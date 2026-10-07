package com.learnlab.schedule;

import com.learnlab.task.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class OverdueScanJob {

  private static final Logger log = LoggerFactory.getLogger(OverdueScanJob.class);
  private final TaskService taskService;

  public OverdueScanJob(TaskService taskService) {
    this.taskService = taskService;
  }

  /** 每天早上 8 点扫一遍逾期任务。页面上的数字来自仪表盘接口，不依赖这次扫描。 */
  @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Shanghai")
  public void scanOverdueTasks() {
    long count = taskService.countAllOverdue();
    log.info("逾期扫描完成，未完成且已过截止日期的任务有 {} 条", count);
  }
}
