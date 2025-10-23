package com.gearsync.backend.controller;

import com.gearsync.backend.model.Project;
import com.gearsync.backend.model.ProjectStatus;
import com.gearsync.backend.model.ProjectType;
import com.gearsync.backend.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/customer/projects")
public class CustomerProjectController {

    private final ProjectService projectService;

    public CustomerProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<Project>> list(Authentication authentication,
                                              @RequestParam(value = "status", required = false) List<ProjectStatus> statuses,
                                              @RequestParam(value = "type", required = false) List<ProjectType> types,
                                              @RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(projectService.listMyProjects(authentication.getName(), statuses, types, page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Project> get(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(projectService.getMyProject(authentication.getName(), id));
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Project> create(Authentication authentication,
                                          @RequestParam("vehicleId") Long vehicleId,
                                          @RequestBody Project payload) {
        return ResponseEntity.ok(projectService.createMyProject(authentication.getName(), vehicleId, payload));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Project> update(Authentication authentication,
                                          @PathVariable Long id,
                                          @RequestBody Project payload) {
        return ResponseEntity.ok(projectService.updateMyProject(authentication.getName(), id, payload));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
        projectService.deleteMyProject(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}


