package com.sparta.project.Controller;

import com.sparta.project.Dto.CommentRequestDto;
import com.sparta.project.Dto.CommentResponseDto;
import com.sparta.project.Service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    //댓글 작성
    @PostMapping("/api/comment/{postId}")
    public CommentResponseDto createComment(@PathVariable Long postId, @RequestBody CommentRequestDto commentRequestDto, HttpServletRequest httpServletRequest) {
        return commentService.createComment(postId, commentRequestDto, httpServletRequest);
    }

    //댓글 수정
    @PutMapping("/api/comment/{commentId}")
    public CommentResponseDto updateComment(@PathVariable Long commentId, @RequestBody CommentRequestDto commentRequestDto, HttpServletRequest httpServletRequest) {
        return commentService.updateComment(commentId, commentRequestDto, httpServletRequest);
    }

    //댓글 삭제
    @DeleteMapping("/api/comment/{commentId}")
    public String deleteComment(@PathVariable Long commentId, HttpServletRequest httpServletRequest) {
        return commentService.deleteComment(commentId, httpServletRequest);
    }

}
