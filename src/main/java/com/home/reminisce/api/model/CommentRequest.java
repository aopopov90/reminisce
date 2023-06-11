package com.home.reminisce.api.model;

public record CommentRequest(Long sessionId, String text, Integer categoryId) {
}
