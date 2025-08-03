package com.railse.workforcemgmt.model;

import java.time.Instant;

import lombok.Data;

@Data
public class TaskActivity {
    private Long id;
    private Long taskId;
    private String activity;
    private Instant timestamp;
}

