package com.railse.workforcemgmt.service;

import java.util.List;

import com.railse.workforcemgmt.dto.AssignByReferenceRequest;
import com.railse.workforcemgmt.dto.TaskCreateRequest;
import com.railse.workforcemgmt.dto.TaskFetchByDateRequest;
import com.railse.workforcemgmt.dto.TaskManagementDto;
import com.railse.workforcemgmt.dto.UpdateTaskRequest;
import com.railse.workforcemgmt.model.TaskActivity;
import com.railse.workforcemgmt.model.TaskComment;
import com.railse.workforcemgmt.model.enums.Priority;

public interface TaskManagementService {
    List<TaskManagementDto> createTasks(TaskCreateRequest request);

    List<TaskManagementDto> updateTasks(UpdateTaskRequest request);

    String assignByReference(AssignByReferenceRequest request);

    List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request);

    TaskManagementDto findTaskById(Long id);

    // New features
    List<TaskManagementDto> fetchTasksByPriority(Priority priority);

    TaskManagementDto updateTaskPriority(Long taskId, Priority priority);

    TaskComment addComment(Long taskId, Long userId, String comment);

    List<TaskComment> getComments(Long taskId);

    List<TaskActivity> getActivities(Long taskId);
}

