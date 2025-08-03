package com.railse.workforcemgmt.model;

import java.util.ArrayList;
import java.util.List;

import com.railse.workforcemgmt.model.enums.Priority;
import com.railse.workforcemgmt.model.enums.ReferenceType;
import com.railse.workforcemgmt.model.enums.Task;
import com.railse.workforcemgmt.model.enums.TaskStatus;

import lombok.Data;

@Data
public class TaskManagement {
    private Long id;
    private Long referenceId;
    private ReferenceType referenceType;
    private Task task;
    private String description;
    private TaskStatus status;
    private Long assigneeId;
    private Long taskDeadlineTime;
    private Priority priority;

    private List<TaskComment> comments = new ArrayList<>();
    private List<TaskActivity> activities = new ArrayList<>();
}

