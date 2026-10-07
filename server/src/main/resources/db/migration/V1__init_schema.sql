-- LearnLab 第一版表结构。
-- Flyway 按文件名版本顺序执行，执行过的脚本记在 flyway_schema_history，不会重复跑。
-- 这份 SQL 同时照顾 H2（MODE=MySQL）和 MySQL 8：不要写 ENGINE、UNSIGNED 这类只有一边认识的语法。

-- 用户。表名不用 user，因为 user 在不少数据库里是保留字。
create table app_user (
  id bigint not null auto_increment,
  username varchar(40) not null,
  email varchar(120) not null,
  password_hash varchar(100) not null,
  role varchar(20) not null,
  enabled boolean not null,
  created_at timestamp not null,
  updated_at timestamp not null,
  primary key (id),
  constraint uk_app_user_username unique (username),
  constraint uk_app_user_email unique (email)
);

-- 课程。一个用户拥有多门课程。
create table course (
  id bigint not null auto_increment,
  title varchar(120) not null,
  summary varchar(500),
  owner_id bigint not null,
  created_at timestamp not null,
  updated_at timestamp not null,
  primary key (id),
  constraint fk_course_owner foreign key (owner_id) references app_user (id)
);

-- 标签。和笔记是多对多，名字全局唯一。
create table tag (
  id bigint not null auto_increment,
  name varchar(40) not null,
  created_at timestamp not null,
  updated_at timestamp not null,
  primary key (id),
  constraint uk_tag_name unique (name)
);

-- 笔记。属于一门课程，由一个作者编写。
create table note (
  id bigint not null auto_increment,
  title varchar(160) not null,
  content text not null,
  pinned boolean not null,
  course_id bigint not null,
  author_id bigint not null,
  created_at timestamp not null,
  updated_at timestamp not null,
  primary key (id),
  constraint fk_note_course foreign key (course_id) references course (id),
  constraint fk_note_author foreign key (author_id) references app_user (id)
);

-- 笔记和标签的中间表。联合主键保证同一对关系不会插两次。
create table note_tag (
  note_id bigint not null,
  tag_id bigint not null,
  primary key (note_id, tag_id),
  constraint fk_note_tag_note foreign key (note_id) references note (id) on delete cascade,
  constraint fk_note_tag_tag foreign key (tag_id) references tag (id)
);

-- 学习任务。course_id 允许为空，表示还没归到某门课。
create table study_task (
  id bigint not null auto_increment,
  title varchar(160) not null,
  detail varchar(1000),
  status varchar(20) not null,
  priority varchar(20) not null,
  due_date date,
  course_id bigint,
  assignee_id bigint not null,
  created_at timestamp not null,
  updated_at timestamp not null,
  primary key (id),
  constraint fk_task_course foreign key (course_id) references course (id),
  constraint fk_task_assignee foreign key (assignee_id) references app_user (id)
);

-- 笔记评论。表名不用 comment，避免和 SQL 关键字撞车。
create table note_comment (
  id bigint not null auto_increment,
  content varchar(1000) not null,
  note_id bigint not null,
  author_id bigint not null,
  created_at timestamp not null,
  updated_at timestamp not null,
  primary key (id),
  constraint fk_comment_note foreign key (note_id) references note (id) on delete cascade,
  constraint fk_comment_author foreign key (author_id) references app_user (id)
);

-- 笔记附件。磁盘上的文件名是 stored_name，original_name 只用于下载时显示。
create table attachment (
  id bigint not null auto_increment,
  original_name varchar(255) not null,
  stored_name varchar(255) not null,
  content_type varchar(120),
  size_bytes bigint not null,
  note_id bigint not null,
  uploader_id bigint not null,
  created_at timestamp not null,
  updated_at timestamp not null,
  primary key (id),
  constraint uk_attachment_stored unique (stored_name),
  constraint fk_attachment_note foreign key (note_id) references note (id) on delete cascade,
  constraint fk_attachment_uploader foreign key (uploader_id) references app_user (id)
);

create index idx_course_owner on course (owner_id);
create index idx_note_course on note (course_id);
create index idx_note_author on note (author_id);
create index idx_task_assignee_status on study_task (assignee_id, status);
create index idx_comment_note on note_comment (note_id);
create index idx_attachment_note on attachment (note_id);
