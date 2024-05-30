package com.prj2spring.controller.board;

import com.prj2spring.domain.board.Board;
import com.prj2spring.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService service;

    @PostMapping("add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity add(
            Authentication authentication,
            Board board,
            @RequestParam(value = "files[]", required = false) MultipartFile[] files) throws IOException {

        if (service.validate(board)) {
            service.add(board, files, authentication);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(value = "type", required = false) String searchType,
            @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        return service.list(page, searchType, keyword);
    }

    // /api/board/5
    // /api/board/6
    @GetMapping("{id}")
    public ResponseEntity get(@PathVariable Integer id) {
        Board board = service.get(id);

        if (board == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(board);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity delete(@PathVariable Integer id
            , Authentication authentication) {
        if (service.hasAccess(id, authentication)) {
            service.remove(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("edit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity edit(Board board,
                               @RequestParam(value = "removeFileList[]", required = false)
                               List<String> removeFileList,
                               Authentication authentication) {

        if (!service.hasAccess(board.getId(), authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (service.validate(board)) {
            service.edit(board, removeFileList);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("like")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> like(@RequestBody Map<String, Object> req,
                                    Authentication authentication) {

        return service.like(req, authentication);
    }
}
