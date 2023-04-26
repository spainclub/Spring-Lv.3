package com.sparta.project.Controller;

import com.sparta.project.Dto.PostRequestDto;
import com.sparta.project.Dto.PostResponseDto;
import com.sparta.project.Service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    //게시글 작성
    @PostMapping("/posts")
    public PostResponseDto createPost(@RequestBody PostRequestDto postrequestDto, HttpServletRequest httpServletRequest) {
        return postService.createPost(postrequestDto, httpServletRequest);
    }

    //전체 게시글 조회
    @GetMapping("/posts")
    public List<PostResponseDto> getPosts() {
        return postService.getPosts();
    }

    //선택한 게시글 조회
    @GetMapping("/posts/{postId}")
    public PostResponseDto getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    //게시글 수정
    @PutMapping("/posts/{postId}")
    public PostResponseDto updatePost(@PathVariable Long postId, @RequestBody PostRequestDto postrequestDto, HttpServletRequest httpServletRequest) {
        return postService.updatePost(postId, postrequestDto, httpServletRequest);
    }

    //게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public String deletePost(@PathVariable Long postId, HttpServletRequest httpServletRequest) {
        return postService.deletePost(postId, httpServletRequest);
    }


}