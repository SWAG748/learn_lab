package com.learnlab.note;

import com.learnlab.attachment.Attachment;
import com.learnlab.attachment.AttachmentRepository;
import com.learnlab.attachment.AttachmentView;
import com.learnlab.attachment.FileStorage;
import com.learnlab.common.BusinessException;
import com.learnlab.common.ErrorCode;
import com.learnlab.common.PageResult;
import com.learnlab.course.Course;
import com.learnlab.course.CourseService;
import com.learnlab.tag.Tag;
import com.learnlab.tag.TagService;
import com.learnlab.user.AuthService;
import com.learnlab.user.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class NoteService {

  private final NoteRepository notes;
  private final AttachmentRepository attachments;
  private final CourseService courseService;
  private final TagService tagService;
  private final AuthService authService;
  private final FileStorage fileStorage;

  public NoteService(
      NoteRepository notes,
      AttachmentRepository attachments,
      CourseService courseService,
      TagService tagService,
      AuthService authService,
      FileStorage fileStorage) {
    this.notes = notes;
    this.attachments = attachments;
    this.courseService = courseService;
    this.tagService = tagService;
    this.authService = authService;
    this.fileStorage = fileStorage;
  }

  @Transactional(readOnly = true)
  public PageResult<NoteSummary> search(Long courseId, String keyword, String tag, int page, int size) {
    UserAccount user = authService.requireCurrent();
    Page<Note> result = notes.search(
        courseId,
        user.getId(),
        blankToNull(tag),
        blankToNull(keyword),
        PageRequest.of(Math.max(page, 0), clamp(size)));
    List<NoteSummary> items = result.getContent().stream().map(NoteSummary::from).toList();
    return new PageResult<>(items, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
  }

  @Transactional(readOnly = true)
  public NoteDetail get(Long id) {
    return toDetail(requireOwned(id));
  }

  @Transactional
  public NoteDetail create(NoteRequest request) {
    Note note = new Note();
    note.setAuthor(authService.requireCurrent());
    apply(note, request);
    notes.save(note);
    return toDetail(note);
  }

  @Transactional
  public NoteDetail update(Long id, NoteRequest request) {
    Note note = requireOwned(id);
    apply(note, request);
    return toDetail(note);
  }

  @Transactional
  public void delete(Long id) {
    Note note = requireOwned(id);
    for (Attachment file : attachments.findByNoteIdOrderByIdAsc(note.getId())) {
      fileStorage.delete(file.getStoredName());
    }
    notes.delete(note);
  }

  @Transactional(readOnly = true)
  public Note requireOwned(Long id) {
    Note note = notes.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "笔记不存在"));
    UserAccount user = authService.requireCurrent();
    if (!note.getAuthor().getId().equals(user.getId())) {
      throw new BusinessException(ErrorCode.FORBIDDEN, "这不是你的笔记");
    }
    return note;
  }

  private void apply(Note note, NoteRequest request) {
    Course course = courseService.requireOwned(request.courseId());
    note.setCourse(course);
    note.setTitle(request.title().trim());
    note.setContent(request.content().trim());
    note.setPinned(request.pinned());
    note.setTags(resolveTags(request.tags()));
  }

  private Set<Tag> resolveTags(List<String> rawTags) {
    Set<Tag> tags = new LinkedHashSet<>();
    if (rawTags == null) {
      return tags;
    }
    for (String raw : rawTags) {
      if (raw == null || raw.isBlank()) {
        continue;
      }
      tags.add(tagService.findOrCreate(raw));
    }
    return tags;
  }

  private NoteDetail toDetail(Note note) {
    List<AttachmentView> files = attachments.findByNoteIdOrderByIdAsc(note.getId()).stream()
        .map(AttachmentView::from)
        .toList();
    return new NoteDetail(
        note.getId(),
        note.getTitle(),
        note.getContent(),
        note.isPinned(),
        note.getCourse().getId(),
        note.getCourse().getTitle(),
        note.getAuthor().getUsername(),
        note.getTags().stream().map(Tag::getName).sorted().toList(),
        files,
        note.getCreatedAt(),
        note.getUpdatedAt());
  }

  private static String blankToNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }

  private static int clamp(int size) {
    if (size < 1) {
      return 10;
    }
    return Math.min(size, 50);
  }
}
