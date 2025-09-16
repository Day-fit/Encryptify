package pl.dayfit.encryptify.stats.dto;

public record StatisticsDto (
        long folderCount,
        long fileCount,
        long totalSpaceUsed,
        ActivityResponseDto lastActivity
) {}
