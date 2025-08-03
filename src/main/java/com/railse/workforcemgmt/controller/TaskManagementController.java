package com.railse.workforcemgmt.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.railse.workforcemgmt.dto.AssignByReferenceRequest;
import com.railse.workforcemgmt.dto.TaskCommentRequest;
import com.railse.workforcemgmt.dto.TaskCreateRequest;
import com.railse.workforcemgmt.dto.TaskFetchByDateRequest;
import com.railse.workforcemgmt.dto.TaskManagementDto;
import com.railse.workforcemgmt.dto.UpdateTaskRequest;
import com.railse.workforcemgmt.model.TaskComment;
import com.railse.workforcemgmt.model.enums.Priority;
import com.railse.workforcemgmt.model.response.Response;
import com.railse.workforcemgmt.service.TaskManagementService;

@RestController
@RequestMapping("/task-mgmt")
public class TaskManagementController {

    private final TaskManagementService taskManagementService;

    public TaskManagementController(TaskManagementService taskManagementService) {
        this.taskManagementService = taskManagementService;
    }

    // Get a task by ID
    @GetMapping("/{id}")
    public TaskManagementDto getTaskById(@PathVariable Long id) {
        return taskManagementService.findTaskById(id);
    }

    // Create multiple tasks
    @PostMapping("/create")
    public List<TaskManagementDto> createTasks(@RequestBody TaskCreateRequest request) {
        return taskManagementService.createTasks(request);
    }

    // Update task(s)
    @PostMapping("/update")
    public List<TaskManagementDto> updateTasks(@RequestBody UpdateTaskRequest request) {
        return taskManagementService.updateTasks(request);
    }

    // Assign task(s) by reference bug #1
    @PostMapping("/assign-by-ref")
    public String assignByReference(@RequestBody AssignByReferenceRequest request) {
        return taskManagementService.assignByReference(request);
    }

    // Fetch tasks by date (Fixes bug #2 in service layer)
    @PostMapping("/fetch-by-date")
    public List<TaskManagementDto> fetchByDate(@RequestBody TaskFetchByDateRequest request) {
        return taskManagementService.fetchTasksByDate(request);
    }

    // Feature #1
     @GetMapping("/priority")
    public ResponseEntity<List<TaskManagementDto>> getTasksByPriority(
            @RequestParam("priority") Priority priority) {
        List<TaskManagementDto> tasks = taskManagementService.fetchTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }

    //Feature #2
      @PutMapping("/{taskId}/priority")
    public ResponseEntity<TaskManagementDto> updateTaskPriority(
        @PathVariable Long taskId,
        @RequestParam("priority") Priority priority) {
       TaskManagementDto updatedTask = taskManagementService.updateTaskPriority(taskId, priority);
       return ResponseEntity.ok(updatedTask);
}


    //Feature #3
     @PostMapping("/comments")
    public Response<String> addComments(@RequestBody TaskCommentRequest request) {
         taskManagementService.addComment(request.getTaskId(),request.getUserId(),request.getComment());
         return new Response<>("Comment added successfully.");
    }

    @GetMapping("/comments/{taskId}")
    public Response<List<TaskComment>> getComments(@PathVariable Long taskId) {
        List<TaskComment> comments = taskManagementService.getComments(taskId);
        return new Response<>(comments);
    }

}

