package com.learnlab.seed;

import com.learnlab.comment.NoteComment;
import com.learnlab.comment.NoteCommentRepository;
import com.learnlab.course.Course;
import com.learnlab.course.CourseRepository;
import com.learnlab.note.Note;
import com.learnlab.note.NoteRepository;
import com.learnlab.tag.Tag;
import com.learnlab.tag.TagRepository;
import com.learnlab.task.Priority;
import com.learnlab.task.StudyTask;
import com.learnlab.task.StudyTaskRepository;
import com.learnlab.task.TaskStatus;
import com.learnlab.user.Role;
import com.learnlab.user.UserAccount;
import com.learnlab.user.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
@Profile({"dev", "mysql"})
@ConditionalOnProperty(prefix = "learnlab.seed", name = "enabled", havingValue = "true")
public class DevDataSeeder implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

  private final UserAccountRepository users;
  private final CourseRepository courses;
  private final NoteRepository notes;
  private final TagRepository tags;
  private final StudyTaskRepository tasks;
  private final NoteCommentRepository comments;
  private final PasswordEncoder passwordEncoder;

  public DevDataSeeder(
      UserAccountRepository users,
      CourseRepository courses,
      NoteRepository notes,
      TagRepository tags,
      StudyTaskRepository tasks,
      NoteCommentRepository comments,
      PasswordEncoder passwordEncoder) {
    this.users = users;
    this.courses = courses;
    this.notes = notes;
    this.tags = tags;
    this.tasks = tasks;
    this.comments = comments;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(ApplicationArguments args) {
    UserAccount admin = ensureUser("admin", "admin@learnlab.local", "admin123", Role.ADMIN);
    UserAccount ada = ensureUser("ada", "ada@learnlab.local", "learn123", Role.USER);
    if (courses.countByOwnerId(ada.getId()) > 0) {
      log.info("演示数据已存在。学员 ada / learn123 ，管理员 admin / admin123");
      return;
    }
    Tag vue = tag("Vue");
    Tag spring = tag("Spring");
    Tag sql = tag("SQL");

    Course vueCourse = course(ada, "Vue 3 入门", "组件、路由和状态，从页面往下看。");
    Course springCourse = course(ada, "Spring Boot 基础", "接口、分层、安全和数据库。");

    Note componentNote = note(ada, vueCourse, true, "组件是什么", """
        # 组件是什么

        Vue 页面由组件拼起来。每个 `.vue` 文件通常有三块：

        - script：数据和行为
        - template：页面结构
        - style：样式

        打开 `web/src/views/CourseListView.vue`，改一个按钮上的文字，保存后看浏览器会不会自己刷新。
        """, vue);
    note(ada, springCourse, false, "一次请求经过哪些层", """
        # 一次请求经过哪些层

        浏览器调用 `POST /api/courses` 时，后端大致是这条路：

        1. `CourseController` 接收 JSON，并做参数校验
        2. `CourseService` 处理业务，比如“这是不是我的课”
        3. `CourseRepository` 用 JPA 把对象写成 SQL
        4. 数据库里多出一行 `course`

        对应文件都在 `server/src/main/java/com/learnlab/course`。
        """, spring, sql);

    task(ada, vueCourse, "改一处页面文字", "在课程列表页改按钮文案，确认热更新。", TaskStatus.TODO, Priority.MEDIUM, LocalDate.now().plusDays(2));
    task(ada, springCourse, "用 H2 控制台看 course 表", "登录后新建一门课，再打开 H2 控制台数行数。", TaskStatus.DOING, Priority.HIGH, LocalDate.now().minusDays(1));
    task(ada, null, "读完学习指南的前两节", "先跑起来，再顺着文档点开对应文件。", TaskStatus.TODO, Priority.LOW, LocalDate.now().plusDays(7));

    NoteComment comment = new NoteComment();
    comment.setNote(componentNote);
    comment.setAuthor(ada);
    comment.setContent("热更新生效了就在这条评论下面留一句。");
    comments.save(comment);

    log.info("已写入演示数据。学员 ada / learn123 ，管理员 {} / admin123", admin.getUsername());
  }

  private UserAccount ensureUser(String username, String email, String rawPassword, Role role) {
    return users.findByUsername(username).orElseGet(() -> {
      UserAccount user = new UserAccount();
      user.setUsername(username);
      user.setEmail(email);
      user.setPasswordHash(passwordEncoder.encode(rawPassword));
      user.setRole(role);
      user.setEnabled(true);
      return users.save(user);
    });
  }

  private Tag tag(String name) {
    return tags.findByNameIgnoreCase(name).orElseGet(() -> {
      Tag tag = new Tag();
      tag.setName(name);
      return tags.save(tag);
    });
  }

  private Course course(UserAccount owner, String title, String summary) {
    Course course = new Course();
    course.setOwner(owner);
    course.setTitle(title);
    course.setSummary(summary);
    return courses.save(course);
  }

  private Note note(UserAccount author, Course course, boolean pinned, String title, String content, Tag... noteTags) {
    Note note = new Note();
    note.setAuthor(author);
    note.setCourse(course);
    note.setPinned(pinned);
    note.setTitle(title);
    note.setContent(content);
    note.setTags(new LinkedHashSet<>(Set.of(noteTags)));
    return notes.save(note);
  }

  private void task(
      UserAccount assignee,
      Course course,
      String title,
      String detail,
      TaskStatus status,
      Priority priority,
      LocalDate dueDate) {
    StudyTask task = new StudyTask();
    task.setAssignee(assignee);
    task.setCourse(course);
    task.setTitle(title);
    task.setDetail(detail);
    task.setStatus(status);
    task.setPriority(priority);
    task.setDueDate(dueDate);
    tasks.save(task);
  }
}
