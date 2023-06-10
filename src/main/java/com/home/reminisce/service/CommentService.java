package com.home.reminisce.service;

import com.home.reminisce.api.model.CommentRequest;
import com.home.reminisce.model.Comment;

public interface CommentService {
    Comment createComment(CommentRequest comment);

    void deleteComment(Long id);

    Comment updateComment(CommentRequest comment, Long id);
}
