package com.prj2spring.controller.comment;

import com.prj2spring.domain.comment.Comment;
import com.prj2spring.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {
    final CommentService service;

    @PostMapping("add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity addComment(@RequestBody Comment comment,
                                     Authentication authentication) {

        if (service.validate(comment)) {
            service.add(comment, authentication);

            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("list/{boardId}")
    public List<Comment> list(@PathVariable Integer boardId) {
        return service.list(boardId);
    }
}
