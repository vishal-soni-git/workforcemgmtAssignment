package com.railse.workforcemgmt.repository;

import java.util.List;
import java.util.Optional;

import com.railse.workforcemgmt.model.TaskManagement;
import com.railse.workforcemgmt.model.enums.ReferenceType;

public interface TaskRepository {
    Optional<TaskManagement> findById(Long id);

    TaskManagement save(TaskManagement task);

    List<TaskManagement> findAll();

    List<TaskManagement> findByReferenceIdAndReferenceType(Long referenceId, ReferenceType referenceType);

    List<TaskManagement> findByAssigneeIdIn(List<Long> assigneeIds);
}
