package org.swp391_group4_backend.ecosolution.dto.response;

public record AdminStatsResponse(
    long totalCitizens,
    long totalCollectors,
    long pendingTasks,
    long completedTasks
) {}
