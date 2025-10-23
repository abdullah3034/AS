package com.gearsync.backend.dto;

public class CustomerDashboardDto {
    private long totalVehicles;
    private long totalAppointments;
    private long upcomingAppointments;
    private long projectsRequested;
    private long projectsInProgress;
    private long projectsCompleted;

    public long getTotalVehicles() { return totalVehicles; }
    public void setTotalVehicles(long totalVehicles) { this.totalVehicles = totalVehicles; }
    public long getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(long totalAppointments) { this.totalAppointments = totalAppointments; }
    public long getUpcomingAppointments() { return upcomingAppointments; }
    public void setUpcomingAppointments(long upcomingAppointments) { this.upcomingAppointments = upcomingAppointments; }
    public long getProjectsRequested() { return projectsRequested; }
    public void setProjectsRequested(long projectsRequested) { this.projectsRequested = projectsRequested; }
    public long getProjectsInProgress() { return projectsInProgress; }
    public void setProjectsInProgress(long projectsInProgress) { this.projectsInProgress = projectsInProgress; }
    public long getProjectsCompleted() { return projectsCompleted; }
    public void setProjectsCompleted(long projectsCompleted) { this.projectsCompleted = projectsCompleted; }
}


