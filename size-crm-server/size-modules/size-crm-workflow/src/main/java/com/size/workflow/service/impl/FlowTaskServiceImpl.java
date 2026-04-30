package com.size.workflow.service.impl;

import com.size.common.core.domain.Result;
import com.size.common.workflow.OrphanContractStubRequest;
import com.size.common.workflow.WorkflowContractSnapshot;
import com.size.common.workflow.WorkflowReceivableSnapshot;
import com.size.common.workflow.WorkflowUserDisplayVO;
import com.size.workflow.feign.RemoteInternalCrmWorkflowClient;
import com.size.workflow.feign.RemoteInternalSysUserClient;
import com.size.workflow.model.dto.TaskCompleteDTO;
import com.size.workflow.service.IFlowTaskService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Comment;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.IdentityLinkType;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 工作流任务编排：Camunda + 内部 Feign 补业务展示
 */
@Service
public class FlowTaskServiceImpl implements IFlowTaskService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RemoteInternalSysUserClient remoteInternalSysUserClient;

    @Autowired
    private RemoteInternalCrmWorkflowClient remoteInternalCrmWorkflowClient;

    @Override
    public String startProcess(String processDefinitionKey, String businessKey, String userId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("initiator", userId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
        return processInstance.getId();
    }

    @Override
    public List<Map<String, Object>> listMyTasks(String userId, List<String> roleKeys, String roleKeysCsv) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "10001";
        }
        List<String> effectiveRoleKeys = mergeRoleKeys(roleKeys, roleKeysCsv);
        Map<String, Task> taskMap = new HashMap<>();

        List<Task> userTasks = taskService.createTaskQuery()
                .or()
                .taskCandidateUser(userId)
                .taskAssignee(userId)
                .endOr()
                .orderByTaskCreateTime()
                .desc()
                .list();
        for (Task t : userTasks) {
            taskMap.put(t.getId(), t);
        }

        if (!effectiveRoleKeys.isEmpty()) {
            List<Task> roleTasks = taskService.createTaskQuery()
                    .taskCandidateGroupIn(effectiveRoleKeys)
                    .orderByTaskCreateTime()
                    .desc()
                    .list();
            for (Task t : roleTasks) {
                taskMap.put(t.getId(), t);
            }
        }

        List<Task> tasks = new ArrayList<>(taskMap.values());
        tasks.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Task task : tasks) {
            Map<String, Object> map = new HashMap<>();
            map.put("taskId", task.getId());
            map.put("taskName", task.getName());
            map.put("processInstanceId", task.getProcessInstanceId());
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            String businessKey = processInstance != null ? processInstance.getBusinessKey() : null;
            map.put("businessKey", businessKey);
            map.put("createTime", task.getCreateTime());
            map.putAll(buildBusinessDetail(businessKey));
            result.add(map);
        }
        return result;
    }

    private static List<String> mergeRoleKeys(List<String> roleKeys, String roleKeysCsv) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        if (roleKeys != null) {
            for (String s : roleKeys) {
                if (s != null && !s.isBlank()) {
                    set.add(s.trim());
                }
            }
        }
        if (roleKeysCsv != null && !roleKeysCsv.isBlank()) {
            for (String part : roleKeysCsv.split(",")) {
                String t = part.trim();
                if (!t.isEmpty()) {
                    set.add(t);
                }
            }
        }
        return new ArrayList<>(set);
    }

    @Override
    public void completeTask(TaskCompleteDTO dto) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", dto.approved());

        String taskId = dto.taskId();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new IllegalArgumentException("任务不存在或已被处理");
        }

        List<String> roleKeys = dto.roleKeys() != null ? dto.roleKeys() : List.of();
        assertCanCompleteTask(task, dto.userId(), roleKeys);

        String processInstanceId = task.getProcessInstanceId();
        String businessKey = resolveBusinessKeyByProcessInstance(processInstanceId);

        if (task.getAssignee() == null || task.getAssignee().isBlank()) {
            taskService.claim(taskId, dto.userId());
        }

        String comment = dto.comment();
        if (comment != null && !comment.isEmpty()) {
            identityService.setAuthenticatedUserId(dto.userId());
            try {
                taskService.createComment(taskId, processInstanceId, comment);
            } finally {
                identityService.setAuthenticatedUserId(null);
            }
        }

        identityService.setAuthenticatedUserId(dto.userId());
        try {
            taskService.complete(taskId, variables);
        } finally {
            identityService.setAuthenticatedUserId(null);
        }

        syncBusinessAuditStatusAfterComplete(businessKey, dto.approved(), processInstanceId);
    }

    @Override
    public List<Map<String, Object>> getProgress(String businessKey) {
        List<String> businessKeys = buildBusinessKeys(businessKey);
        String processInstanceId = resolveProcessInstanceId(businessKeys);
        if (processInstanceId == null) {
            return new ArrayList<>();
        }

        List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceEndTime()
                .asc()
                .list();
        List<Task> runningTasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .orderByTaskCreateTime()
                .asc()
                .list();

        Map<String, HistoricTaskInstance> historicByDefKey = new HashMap<>();
        for (HistoricTaskInstance task : historicTasks) {
            if (task.getEndTime() == null) {
                continue;
            }
            HistoricTaskInstance current = historicByDefKey.get(task.getTaskDefinitionKey());
            if (current == null || compareDate(task.getEndTime(), current.getEndTime()) > 0) {
                historicByDefKey.put(task.getTaskDefinitionKey(), task);
            }
        }

        Map<String, Task> runningByDefKey = new HashMap<>();
        for (Task task : runningTasks) {
            runningByDefKey.put(task.getTaskDefinitionKey(), task);
        }

        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (processInstance == null) {
            return new ArrayList<>();
        }

        BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processInstance.getProcessDefinitionId());
        if (modelInstance == null) {
            return new ArrayList<>();
        }

        Collection<org.camunda.bpm.model.bpmn.instance.Process> processes =
                modelInstance.getModelElementsByType(org.camunda.bpm.model.bpmn.instance.Process.class);
        org.camunda.bpm.model.bpmn.instance.Process bpmnProcess = processes.stream()
                .filter(org.camunda.bpm.model.bpmn.instance.Process::isExecutable)
                .findFirst()
                .orElse(null);
        if (bpmnProcess == null && !processes.isEmpty()) {
            bpmnProcess = processes.iterator().next();
        }
        if (bpmnProcess == null) {
            return new ArrayList<>();
        }

        List<UserTask> userTaskDefs = new ArrayList<>(bpmnProcess.getChildElementsByType(UserTask.class));
        List<Map<String, Object>> progressList = new ArrayList<>();

        for (UserTask userTask : userTaskDefs) {
            String taskDefKey = userTask.getId();

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("taskDefinitionKey", taskDefKey);
            map.put("taskName", userTask.getName());

            if (historicByDefKey.containsKey(taskDefKey)) {
                HistoricTaskInstance his = historicByDefKey.get(taskDefKey);
                map.put("taskId", his.getId());
                map.put("assignee", formatAssigneeForProgress(his.getAssignee()));
                map.put("createTime", his.getStartTime());
                map.put("endTime", his.getEndTime());
                map.put("status", "已审批");
                List<Comment> comments = taskService.getTaskComments(his.getId());
                if (!comments.isEmpty()) {
                    map.put("comment", comments.get(0).getFullMessage());
                }
            } else if (runningByDefKey.containsKey(taskDefKey)) {
                Task running = runningByDefKey.get(taskDefKey);
                map.put("taskId", running.getId());
                map.put("assignee", formatAssigneeForProgress(running.getAssignee()));
                map.put("createTime", running.getCreateTime());
                map.put("endTime", null);
                map.put("status", "待审批");
            } else {
                map.put("taskId", null);
                map.put("assignee", null);
                map.put("createTime", null);
                map.put("endTime", null);
                map.put("status", "未审批");
            }

            progressList.add(map);
        }
        return progressList;
    }

    private List<String> buildBusinessKeys(String businessKey) {
        List<String> businessKeys = new ArrayList<>();
        businessKeys.add(businessKey);
        if (!businessKey.contains(":")) {
            businessKeys.add("contract:" + businessKey);
        }
        return businessKeys;
    }

    private String resolveProcessInstanceId(List<String> businessKeys) {
        for (String key : businessKeys) {
            ProcessInstance runtime = runtimeService.createProcessInstanceQuery()
                    .processInstanceBusinessKey(key)
                    .singleResult();
            if (runtime != null) {
                return runtime.getId();
            }
            List<HistoricProcessInstance> histList = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceBusinessKey(key)
                    .orderByProcessInstanceStartTime()
                    .desc()
                    .listPage(0, 1);
            if (!histList.isEmpty()) {
                return histList.get(0).getId();
            }
        }
        return null;
    }

    private int compareDate(Date left, Date right) {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return -1;
        }
        if (right == null) {
            return 1;
        }
        return left.compareTo(right);
    }

    private Map<String, Object> buildBusinessDetail(String businessKey) {
        Map<String, Object> detail = new HashMap<>();
        detail.put("bizType", "UNKNOWN");
        detail.put("bizId", null);
        detail.put("bizNo", businessKey == null ? "-" : businessKey);
        detail.put("bizTitle", "审批单");
        detail.put("bizAmount", null);
        detail.put("bizApplicant", "未知申请人");
        detail.put("bizSummary", "未查询到业务详情");
        detail.put("customerId", null);
        detail.put("customerName", null);
        detail.put("ownerUserId", null);
        detail.put("signDate", null);
        detail.put("startDate", null);
        detail.put("endDate", null);
        detail.put("opportunityId", null);
        detail.put("contractId", null);
        detail.put("returnDate", null);
        detail.put("payType", null);

        if (businessKey == null || !businessKey.contains(":")) {
            return detail;
        }
        String[] arr = businessKey.split(":", 2);
        if (arr.length != 2) {
            return detail;
        }
        String type = arr[0].toUpperCase(Locale.ROOT);
        Long bizId;
        try {
            bizId = Long.parseLong(arr[1]);
        } catch (Exception ignored) {
            return detail;
        }

        detail.put("bizType", type);
        detail.put("bizId", bizId);
        detail.put("bizTitle", type + "审批");

        if ("CONTRACT".equals(type)) {
            ensureOrphanContract(bizId, businessKey);
            WorkflowContractSnapshot row = unwrap(remoteInternalCrmWorkflowClient.getContractSnapshot(bizId));
            if (row != null) {
                detail.put("bizNo", row.getContractNo());
                detail.put("bizTitle", row.getName());
                detail.put("bizAmount", row.getTotalAmount());
                detail.put("ownerUserId", row.getOwnerUserId());
                detail.put("customerId", row.getCustomerId());
                detail.put("opportunityId", row.getOpportunityId());
                detail.put("signDate", row.getSignDate());
                detail.put("startDate", row.getStartDate());
                detail.put("endDate", row.getEndDate());
                detail.put("bizSummary", row.getRemark());
                detail.put("bizApplicant", resolveUserDisplay(row.getOwnerUserId()));
                detail.put("customerName", resolveCustomerName(row.getCustomerId()));
            }
        } else if ("RECEIVABLE".equals(type)) {
            WorkflowReceivableSnapshot row = unwrap(remoteInternalCrmWorkflowClient.getReceivableSnapshot(bizId));
            if (row != null) {
                detail.put("bizNo", row.getReceivableNo());
                detail.put("bizTitle", "回款审批");
                detail.put("bizAmount", row.getAmount());
                detail.put("ownerUserId", row.getOwnerUserId());
                detail.put("customerId", row.getCustomerId());
                detail.put("contractId", row.getContractId());
                detail.put("returnDate", row.getReturnDate());
                detail.put("payType", row.getPayType());
                detail.put("bizSummary", row.getRemark());
                detail.put("bizApplicant", resolveUserDisplay(row.getOwnerUserId()));
                detail.put("customerName", resolveCustomerName(row.getCustomerId()));
            }
        }

        return detail;
    }

    private String resolveBusinessKeyByProcessInstance(String processInstanceId) {
        if (processInstanceId == null) {
            return null;
        }
        ProcessInstance runtime = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (runtime != null) {
            return runtime.getBusinessKey();
        }
        HistoricProcessInstance historic = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        return historic != null ? historic.getBusinessKey() : null;
    }

    private void assertCanCompleteTask(Task task, String userId, List<String> roleKeys) {
        String assignee = task.getAssignee();
        if (assignee != null && !assignee.isBlank()) {
            if (!userId.equals(assignee)) {
                throw new IllegalArgumentException("该任务已由他人签收，您无权办理");
            }
            return;
        }
        List<IdentityLink> links = taskService.getIdentityLinksForTask(task.getId());
        boolean allowed = false;
        for (IdentityLink link : links) {
            if (!IdentityLinkType.CANDIDATE.equals(link.getType())) {
                continue;
            }
            if (userId.equals(link.getUserId())) {
                allowed = true;
                break;
            }
            if (link.getGroupId() != null && roleKeys != null) {
                for (String rk : roleKeys) {
                    if (link.getGroupId().equals(rk)) {
                        allowed = true;
                        break;
                    }
                }
            }
            if (allowed) {
                break;
            }
        }
        if (!allowed) {
            throw new IllegalArgumentException("您不是该任务的候选人（或角色不在候选组内），无权办理");
        }
    }

    private void syncBusinessAuditStatusAfterComplete(String businessKey, boolean approved, String processInstanceId) {
        if (businessKey == null || !businessKey.contains(":")) {
            return;
        }
        if (!approved) {
            applyBizAuditStatus(businessKey, "REJECTED");
            return;
        }
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (pi == null) {
            applyBizAuditStatus(businessKey, "APPROVED");
        } else {
            applyBizAuditStatus(businessKey, "AUDITING");
        }
    }

    private void applyBizAuditStatus(String businessKey, String status) {
        String[] arr = businessKey.split(":", 2);
        if (arr.length != 2) {
            return;
        }
        String type = arr[0].toUpperCase(Locale.ROOT);
        Long bizId;
        try {
            bizId = Long.parseLong(arr[1]);
        } catch (Exception ignored) {
            return;
        }

        if ("CONTRACT".equals(type)) {
            try {
                remoteInternalCrmWorkflowClient.updateContractAuditStatus(bizId, status);
            } catch (Exception ignored) {
                // 业务服务不可达时跳过同步，避免流程节点被卡死
            }
        } else if ("RECEIVABLE".equals(type)) {
            try {
                remoteInternalCrmWorkflowClient.updateReceivableAuditStatus(bizId, status);
            } catch (Exception ignored) {
            }
        }
    }

    private void ensureOrphanContract(Long contractId, String businessKey) {
        if (contractId == null) {
            return;
        }
        try {
            OrphanContractStubRequest body = new OrphanContractStubRequest();
            body.setId(contractId);
            body.setBusinessKey(businessKey);
            remoteInternalCrmWorkflowClient.orphanContractStub(body);
        } catch (Exception ignored) {
        }
    }

    private String formatAssigneeForProgress(String assigneeUserId) {
        if (assigneeUserId == null || assigneeUserId.isBlank()) {
            return null;
        }
        return resolveUserDisplay(assigneeUserId);
    }

    private String resolveUserDisplay(Object userIdObj) {
        if (userIdObj == null) {
            return "未知申请人";
        }
        Long uid = toLong(userIdObj);
        if (uid == null) {
            return String.valueOf(userIdObj);
        }
        try {
            Result<WorkflowUserDisplayVO> res = remoteInternalSysUserClient.getDisplay(uid);
            WorkflowUserDisplayVO u = unwrap(res);
            if (u == null) {
                return String.valueOf(userIdObj);
            }
            if (u.getNickname() != null && !u.getNickname().isEmpty()) {
                return u.getNickname();
            }
            if (u.getUsername() != null && !u.getUsername().isEmpty()) {
                return u.getUsername();
            }
        } catch (Exception ignored) {
        }
        return String.valueOf(userIdObj);
    }

    private String resolveCustomerName(Object customerIdObj) {
        if (customerIdObj == null) {
            return null;
        }
        Long cid = toLong(customerIdObj);
        if (cid == null) {
            return null;
        }
        try {
            return unwrap(remoteInternalCrmWorkflowClient.getCustomerName(cid));
        } catch (Exception ignored) {
            return null;
        }
    }

    private static <T> T unwrap(Result<T> r) {
        if (r == null || r.getCode() != Result.SUCCESS) {
            return null;
        }
        return r.getData();
    }

    private static Long toLong(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Number n) {
            return n.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(o).trim());
        } catch (Exception e) {
            return null;
        }
    }
}
