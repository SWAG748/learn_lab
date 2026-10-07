package com.learnlab.dashboard;

import com.learnlab.note.NoteSummary;
import com.learnlab.task.TaskView;

import java.util.List;

public record DashboardView(
    long courseCount,
    long noteCount,
    long todoCount,
    long doingCount,
    long doneCount,
    long overdueCount,
    List<NoteSummary> recentNotes,
    List<TaskView> overdueTasks
) {
}
