package com.railse.workforcemgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TaskCommentRequest {
    @JsonProperty("task_id")
    private Long taskId;
    @JsonProperty("user_id")
    private Long userId;
    private String comment;

}

