package com.learnlab.attachment;

import com.learnlab.common.BusinessException;
import com.learnlab.common.ErrorCode;
import com.learnlab.note.Note;
import com.learnlab.note.NoteService;
import com.learnlab.user.AuthService;
import com.learnlab.user.UserAccount;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AttachmentService {

  private final AttachmentRepository attachments;
  private final NoteService noteService;
  private final AuthService authService;
  private final FileStorage fileStorage;

  public AttachmentService(
      AttachmentRepository attachments,
      NoteService noteService,
      AuthService authService,
      FileStorage fileStorage) {
    this.attachments = attachments;
    this.noteService = noteService;
    this.authService = authService;
    this.fileStorage = fileStorage;
  }

  @Transactional
  public AttachmentView upload(Long noteId, MultipartFile file) {
    Note note = noteService.requireOwned(noteId);
    UserAccount user = authService.requireCurrent();
    FileStorage.Stored stored = fileStorage.store(file);
    Attachment attachment = new Attachment();
    attachment.setNote(note);
    attachment.setUploader(user);
    attachment.setOriginalName(stored.originalName());
    attachment.setStoredName(stored.storedName());
    attachment.setContentType(stored.contentType());
    attachment.setSizeBytes(stored.sizeBytes());
    attachments.save(attachment);
    return AttachmentView.from(attachment);
  }

  @Transactional(readOnly = true)
  public Download download(Long id) {
    Attachment attachment = requireOwned(id);
    Resource resource = fileStorage.load(attachment.getStoredName());
    return new Download(resource, attachment.getOriginalName(), attachment.getContentType());
  }

  @Transactional
  public void delete(Long id) {
    Attachment attachment = requireOwned(id);
    fileStorage.delete(attachment.getStoredName());
    attachments.delete(attachment);
  }

  private Attachment requireOwned(Long id) {
    Attachment attachment = attachments.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "附件不存在"));
    noteService.requireOwned(attachment.getNote().getId());
    return attachment;
  }

  public record Download(Resource resource, String filename, String contentType) {
  }
}
