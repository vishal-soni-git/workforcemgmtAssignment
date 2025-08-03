package com.railse.workforcemgmt.model;

import java.time.Instant;

import lombok.Data;

@Data
public class TaskComment {
    private Long id;
    private Long taskId;
    private Long userId;
    private String comment;
    private Instant timestamp;
}
