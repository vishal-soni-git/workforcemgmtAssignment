package com.railse.workforcemgmt.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.railse.workforcemgmt.dto.AssignByReferenceRequest;
import com.railse.workforcemgmt.dto.TaskCreateRequest;
import com.railse.workforcemgmt.dto.TaskFetchByDateRequest;
import com.railse.workforcemgmt.dto.TaskManagementDto;
import com.railse.workforcemgmt.dto.UpdateTaskRequest;
import com.railse.workforcemgmt.exception.ResourceNotFoundException;
import com.railse.workforcemgmt.mapper.ITaskManagementMapper;
import com.railse.workforcemgmt.model.TaskActivity;
import com.railse.workforcemgmt.model.TaskComment;
import com.railse.workforcemgmt.model.TaskManagement;
import com.railse.workforcemgmt.model.enums.Priority;
import com.railse.workforcemgmt.model.enums.Task;
import com.railse.workforcemgmt.model.enums.TaskStatus;
import com.railse.workforcemgmt.repository.TaskRepository;
import com.railse.workforcemgmt.service.TaskManagementService;

@Service
public class TaskManagementServiceImpl implements TaskManagementService {

    private final TaskRepository taskRepository;
    private final ITaskManagementMapper taskMapper;

    private final AtomicLong commentIdCounter = new AtomicLong(0);
    private final AtomicLong activityIdCounter = new AtomicLong(0);

    public TaskManagementServiceImpl(TaskRepository taskRepository, ITaskManagementMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskManagementDto findTaskById(Long id) {
        TaskManagement task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return taskMapper.modelToDto(task);
    }

    @Override
    public List<TaskManagementDto> createTasks(TaskCreateRequest createRequest) {
        List<TaskManagement> createdTasks = new ArrayList<>();
        for (TaskCreateRequest.RequestItem item : createRequest.getRequests()) {
            TaskManagement newTask = new TaskManagement();
            newTask.setReferenceId(item.getReferenceId());
            newTask.setReferenceType(item.getReferenceType());
            newTask.setTask(item.getTask());
            newTask.setAssigneeId(item.getAssigneeId());
            newTask.setPriority(item.getPriority());
            newTask.setTaskDeadlineTime(item.getTaskDeadlineTime());
            newTask.setStatus(TaskStatus.ASSIGNED);
            newTask.setDescription("New task created.");
            createdTasks.add(taskRepository.save(newTask));
        }
        return taskMapper.modelListToDtoList(createdTasks);
    }

    @Override
    public List<TaskManagementDto> updateTasks(UpdateTaskRequest updateRequest) {
        List<TaskManagement> updatedTasks = new ArrayList<>();
        for (UpdateTaskRequest.RequestItem item : updateRequest.getRequests()) {
            TaskManagement task = taskRepository.findById(item.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + item.getTaskId()));

            if (item.getTaskStatus() != null) {
                task.setStatus(item.getTaskStatus());
            }
            if (item.getDescription() != null) {
                task.setDescription(item.getDescription());
            }
            updatedTasks.add(taskRepository.save(task));
        }
        return taskMapper.modelListToDtoList(updatedTasks);
    }

    @Override
    public String assignByReference(AssignByReferenceRequest request) {
        List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());
        List<TaskManagement> existingTasks = taskRepository.findByReferenceIdAndReferenceType(request.getReferenceId(), request.getReferenceType());

        for (Task taskType : applicableTasks) {
            List<TaskManagement> tasksOfType = existingTasks.stream()
                    .filter(t -> t.getTask() == taskType && t.getStatus() != TaskStatus.COMPLETED)
                    .collect(Collectors.toList());

            // --- BUG FIX #1 ---
            // Assign only one task to the assignee and cancel any duplicates.
            if (!tasksOfType.isEmpty()) {
                boolean firstAssigned = false;
                for (TaskManagement taskToUpdate : tasksOfType) {
                    if (!firstAssigned) {
                        taskToUpdate.setAssigneeId(request.getAssigneeId());
                        taskToUpdate.setStatus(TaskStatus.ASSIGNED);
                        firstAssigned = true;
                    } else {
                        // Cancel duplicate tasks to prevent duplicates
                        taskToUpdate.setStatus(TaskStatus.CANCELLED);
                    }
                    taskRepository.save(taskToUpdate);
                }
            } else {
                // Create a new task if none exist for this type
                TaskManagement newTask = new TaskManagement();
                newTask.setReferenceId(request.getReferenceId());
                newTask.setReferenceType(request.getReferenceType());
                newTask.setTask(taskType);
                newTask.setAssigneeId(request.getAssigneeId());
                newTask.setStatus(TaskStatus.ASSIGNED);
                taskRepository.save(newTask);
            }
        }
        return "Tasks assigned successfully for reference " + request.getReferenceId();
    }

    @Override
    public List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request) {
        List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());

        // --- BUG FIX #2 ---
        // Filter out CANCELLED tasks and filter tasks by deadline time between startDate and endDate.
        List<TaskManagement> filteredTasks = tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.CANCELLED)
                .filter(task -> {
                    Long startDate = request.getStartDate();
                    Long endDate = request.getEndDate();
                    if (startDate != null && endDate != null) {
                        Long deadline = task.getTaskDeadlineTime();
                        return deadline != null && deadline >= startDate && deadline <= endDate;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        return taskMapper.modelListToDtoList(filteredTasks);
    }

    // --- FEATURE 1: Fetch Tasks by Priority ---
    @Override
    public List<TaskManagementDto> fetchTasksByPriority(Priority priority) {
        List<TaskManagement> allTasks = taskRepository.findAll();
        List<TaskManagement> filtered = allTasks.stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
        return taskMapper.modelListToDtoList(filtered);
    }

    // --- FEATURE 2: Update Task Priority ---
    @Override
    public TaskManagementDto updateTaskPriority(Long taskId, Priority priority) {
        TaskManagement task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        task.setPriority(priority);
        taskRepository.save(task);
        return taskMapper.modelToDto(task);
    }

    // --- FEATURE 3: Add Comment to Task ---
    @Override
    public TaskComment addComment(Long taskId, Long userId, String comment) {
        TaskManagement task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        TaskComment newComment = new TaskComment();
        newComment.setId(commentIdCounter.incrementAndGet());
        newComment.setTaskId(taskId);
        newComment.setUserId(userId);
        newComment.setComment(comment);
        newComment.setTimestamp(Instant.now());
        task.getComments().add(newComment);

        // Add an activity log for comment addition
        TaskActivity activity = new TaskActivity();
        activity.setId(activityIdCounter.incrementAndGet());
        activity.setTaskId(taskId);
        activity.setActivity("Comment added by User " + userId);
        activity.setTimestamp(Instant.now());
        task.getActivities().add(activity);

        taskRepository.save(task);
        return newComment;
    }

    // --- FEATURE 3: Get Comments for Task ---
    @Override
    public List<TaskComment> getComments(Long taskId) {
        TaskManagement task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        return task.getComments();
    }

    // --- FEATURE 3: Get Activity History for Task ---
    @Override
    public List<TaskActivity> getActivities(Long taskId) {
        TaskManagement task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        return task.getActivities();
    }
}
