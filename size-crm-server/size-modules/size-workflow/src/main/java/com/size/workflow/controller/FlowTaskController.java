package com.size.workflow.controller;

import com.size.common.core.domain.Result;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Locale;

import org.flowable.engine.HistoryService;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.engine.task.Comment;
import org.flowable.common.engine.impl.identity.Authentication;

/**
 * 工作流任务处理接口
 */
@RestController
@RequestMapping("/workflow/task")
public class FlowTaskController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 发起流程
     */
    @PostMapping("/start")
    public Result<String> startProcess(@RequestParam String processDefinitionKey,
                                       @RequestParam String businessKey,
                                       @RequestParam String userId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("initiator", userId);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
        return Result.ok(processInstance.getId(), "流程启动成功");
    }

    /**
     * 查询我的待办任务 (结合用户ID和角色)
     */
    @GetMapping("/myTasks")
    public Result<List<Map<String, Object>>> myTasks(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) List<String> roleKeys) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "10001";
        }
        // 查询：分配给该用户的个人任务 OR 分配给该用户角色的组任务
        // 注意：Flowable TaskQuery 的条件默认是 AND，这里需要做并集，否则会出现“要点两次/看似未移除”的错觉。
        Map<String, Task> taskMap = new HashMap<>();

        List<Task> userTasks = taskService.createTaskQuery()
                .taskCandidateOrAssigned(userId)
                .orderByTaskCreateTime().desc()
                .list();
        for (Task t : userTasks) {
            taskMap.put(t.getId(), t);
        }

        if (roleKeys != null && !roleKeys.isEmpty()) {
            List<Task> roleTasks = taskService.createTaskQuery()
                    .taskCandidateGroupIn(roleKeys)
                    .orderByTaskCreateTime().desc()
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
            // 获取业务ID
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            String businessKey = processInstance != null ? processInstance.getBusinessKey() : null;
            map.put("businessKey", businessKey);
            map.put("createTime", task.getCreateTime());
            map.putAll(buildBusinessDetail(businessKey));
            result.add(map);
        }
        return Result.ok(result);
    }

    /**
     * 完成审批
     */
    @PostMapping("/complete")
    public Result<Boolean> completeTask(
            @RequestParam(required = false) String taskId,
            @RequestParam(required = false) Boolean approved,
            @RequestParam(required = false) String comment,
            @RequestBody(required = false) Map<String, Object> body) {
        try {
            // 兼容 query/form 与 json body 两种传参方式，避免 400 参数绑定失败
            if ((taskId == null || taskId.isEmpty()) && body != null && body.get("taskId") != null) {
                taskId = String.valueOf(body.get("taskId"));
            }
            if (approved == null && body != null && body.get("approved") != null) {
                approved = Boolean.parseBoolean(String.valueOf(body.get("approved")));
            }
            if ((comment == null || comment.isEmpty()) && body != null && body.get("comment") != null) {
                comment = String.valueOf(body.get("comment"));
            }

            if (taskId == null || taskId.isEmpty() || approved == null) {
                return Result.fail("参数缺失: taskId/approved");
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("approved", approved);

            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                return Result.fail("任务不存在或已被处理");
            }

            String processInstanceId = task.getProcessInstanceId();
            String businessKey = resolveBusinessKeyByProcessInstance(processInstanceId);

            if (comment != null && !comment.isEmpty()) {
                // Flowable 评论建议设置当前认证用户
                Authentication.setAuthenticatedUserId(String.valueOf(task.getAssignee() != null ? task.getAssignee() : "10001"));
                taskService.addComment(taskId, processInstanceId, comment);
                Authentication.setAuthenticatedUserId(null);
            }

            taskService.complete(taskId, variables);
            syncBusinessAuditStatus(businessKey, approved);
            return Result.ok(true, "审批完成");
        } catch (Exception e) {
            return Result.fail("审批失败: " + e.getMessage());
        }
    }

    /**
     * 查询审批进度
     */
    @GetMapping("/progress/{businessKey}")
    public Result<List<Map<String, Object>>> getProgress(@PathVariable String businessKey) {
        List<String> businessKeys = buildBusinessKeys(businessKey);
        String processInstanceId = resolveProcessInstanceId(businessKeys);
        if (processInstanceId == null) {
            return Result.ok(new ArrayList<>());
        }

        List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceEndTime().asc()
                .list();
        List<Task> runningTasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .orderByTaskCreateTime().asc()
                .list();

        Map<String, HistoricTaskInstance> historicByDefKey = new HashMap<>();
        for (HistoricTaskInstance task : historicTasks) {
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
            return Result.ok(new ArrayList<>());
        }

        BpmnModel model = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        if (model == null || model.getMainProcess() == null) {
            return Result.ok(new ArrayList<>());
        }
        Process process = model.getMainProcess();
        List<Map<String, Object>> progressList = new ArrayList<>();

        // 以流程定义中的用户任务为准，输出“全节点”审批状态
        for (FlowElement flowElement : process.getFlowElements()) {
            if (!(flowElement instanceof UserTask)) {
                continue;
            }
            UserTask userTask = (UserTask) flowElement;
            String taskDefKey = userTask.getId();

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("taskDefinitionKey", taskDefKey);
            map.put("taskName", userTask.getName());

            if (historicByDefKey.containsKey(taskDefKey)) {
                HistoricTaskInstance his = historicByDefKey.get(taskDefKey);
                map.put("taskId", his.getId());
                map.put("assignee", his.getAssignee());
                map.put("createTime", his.getCreateTime());
                map.put("endTime", his.getEndTime());
                map.put("status", "已审批");
                List<Comment> comments = taskService.getTaskComments(his.getId());
                if (!comments.isEmpty()) {
                    map.put("comment", comments.get(0).getFullMessage());
                }
            } else if (runningByDefKey.containsKey(taskDefKey)) {
                Task running = runningByDefKey.get(taskDefKey);
                map.put("taskId", running.getId());
                map.put("assignee", running.getAssignee());
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
        return Result.ok(progressList);
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
            HistoricProcessInstance historic = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceBusinessKey(key)
                    .orderByProcessInstanceStartTime()
                    .desc()
                    .listPage(0, 1)
                    .stream()
                    .findFirst()
                    .orElse(null);
            if (historic != null) {
                return historic.getId();
            }
        }
        return null;
    }

    private int compareDate(Date left, Date right) {
        if (left == null && right == null) return 0;
        if (left == null) return -1;
        if (right == null) return 1;
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
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT contract_no, name, total_amount, owner_user_id, customer_id, opportunity_id, sign_date, start_date, end_date, remark FROM crm_contract WHERE id = ? LIMIT 1",
                    bizId
            );
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.get(0);
                detail.put("bizNo", row.get("contract_no"));
                detail.put("bizTitle", row.get("name"));
                detail.put("bizAmount", row.get("total_amount"));
                detail.put("ownerUserId", row.get("owner_user_id"));
                detail.put("customerId", row.get("customer_id"));
                detail.put("opportunityId", row.get("opportunity_id"));
                detail.put("signDate", row.get("sign_date"));
                detail.put("startDate", row.get("start_date"));
                detail.put("endDate", row.get("end_date"));
                detail.put("bizSummary", row.get("remark"));
                detail.put("bizApplicant", resolveUserDisplay(row.get("owner_user_id")));
                detail.put("customerName", resolveCustomerName(row.get("customer_id")));
            }
        } else if ("RECEIVABLE".equals(type)) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT receivable_no, amount, owner_user_id, customer_id, contract_id, return_date, pay_type, remark FROM crm_receivable WHERE id = ? LIMIT 1",
                    bizId
            );
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.get(0);
                detail.put("bizNo", row.get("receivable_no"));
                detail.put("bizTitle", "回款审批");
                detail.put("bizAmount", row.get("amount"));
                detail.put("ownerUserId", row.get("owner_user_id"));
                detail.put("customerId", row.get("customer_id"));
                detail.put("contractId", row.get("contract_id"));
                detail.put("returnDate", row.get("return_date"));
                detail.put("payType", row.get("pay_type"));
                detail.put("bizSummary", row.get("remark"));
                detail.put("bizApplicant", resolveUserDisplay(row.get("owner_user_id")));
                detail.put("customerName", resolveCustomerName(row.get("customer_id")));
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

    private void syncBusinessAuditStatus(String businessKey, boolean approved) {
        if (businessKey == null || !businessKey.contains(":")) {
            return;
        }
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

        String status = approved ? "APPROVED" : "REJECTED";
        if ("CONTRACT".equals(type)) {
            jdbcTemplate.update("UPDATE crm_contract SET audit_status = ?, update_time = NOW() WHERE id = ?", status, bizId);
        } else if ("RECEIVABLE".equals(type)) {
            jdbcTemplate.update("UPDATE crm_receivable SET audit_status = ?, update_time = NOW() WHERE id = ?", status, bizId);
        }
    }

    private void ensureOrphanContract(Long contractId, String businessKey) {
        if (contractId == null) {
            return;
        }
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM crm_contract WHERE id = ?",
                Integer.class,
                contractId
        );
        if (count != null && count > 0) {
            return;
        }

        Long customerId = jdbcTemplate.query(
                "SELECT id FROM crm_customer ORDER BY create_time ASC LIMIT 1",
                rs -> rs.next() ? rs.getLong(1) : 1L
        );
        jdbcTemplate.update(
                "INSERT INTO crm_contract (id, tenant_id, customer_id, opportunity_id, name, contract_no, total_amount, sign_date, start_date, end_date, owner_user_id, process_instance_id, audit_status, remark, create_by, create_time, update_by, update_time, del_flag) " +
                        "VALUES (?, 1, ?, NULL, ?, ?, 0, CURDATE(), CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 1, NULL, 'AUDITING', ?, 'system', NOW(), 'system', NOW(), 0)",
                contractId,
                customerId,
                "历史流程补录合同-" + contractId,
                "AUTO-" + contractId,
                "由流程业务键自动补录: " + businessKey
        );
    }

    private String resolveUserDisplay(Object userIdObj) {
        if (userIdObj == null) {
            return "未知申请人";
        }
        List<Map<String, Object>> users = jdbcTemplate.queryForList(
                "SELECT nickname, username FROM sys_user WHERE id = ? LIMIT 1",
                userIdObj
        );
        if (users.isEmpty()) {
            return String.valueOf(userIdObj);
        }
        Object nickname = users.get(0).get("nickname");
        Object username = users.get(0).get("username");
        if (nickname != null && !String.valueOf(nickname).isEmpty()) {
            return String.valueOf(nickname);
        }
        if (username != null && !String.valueOf(username).isEmpty()) {
            return String.valueOf(username);
        }
        return String.valueOf(userIdObj);
    }

    private String resolveCustomerName(Object customerIdObj) {
        if (customerIdObj == null) {
            return null;
        }
        List<Map<String, Object>> customers = jdbcTemplate.queryForList(
                "SELECT name FROM crm_customer WHERE id = ? LIMIT 1",
                customerIdObj
        );
        if (customers.isEmpty()) {
            return null;
        }
        Object name = customers.get(0).get("name");
        return name == null ? null : String.valueOf(name);
    }
}
