package com.home.reminisce.service;

import com.home.reminisce.model.Comment;

public interface CommentService {
    Comment createComment(Comment comment);

    void deleteComment(Long id);

    Comment updateComment(Comment comment);
}
