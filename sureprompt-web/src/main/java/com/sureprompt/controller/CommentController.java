package com.sureprompt.controller;

import com.sureprompt.dto.CommentDto;
import com.sureprompt.security.CustomOAuth2User;
import com.sureprompt.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/prompts/{id}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<?> addComment(
            @PathVariable Long id, 
            @RequestBody Map<String, String> payload, 
            @AuthenticationPrincipal CustomOAuth2User user) {
            
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String body = payload.get("body");
        if (body == null || body.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Comment body is required");
        }
        
        CommentDto comment = commentService.addComment(id, user.getId(), body);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long id, 
            @PathVariable Long commentId, 
            @AuthenticationPrincipal CustomOAuth2User user) {
            
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            commentService.deleteComment(commentId, user.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
