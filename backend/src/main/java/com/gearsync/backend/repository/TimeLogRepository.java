package com.gearsync.backend.repository;

import com.gearsync.backend.model.Project;
import com.gearsync.backend.model.TimeLog;
import com.gearsync.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimeLogRepository extends JpaRepository<TimeLog, Long> {
    List<TimeLog> findByEmployee(User employee);
    List<TimeLog> findByProject(Project project);
}
