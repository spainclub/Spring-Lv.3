package com.sparta.project.Service;

import com.sparta.project.Dto.PostRequestDto;
import com.sparta.project.Dto.PostResponseDto;
import com.sparta.project.Entity.Post;
import com.sparta.project.Entity.User;
import com.sparta.project.Entity.UserRoleEnum;
import com.sparta.project.Repository.PostRepository;
import com.sparta.project.Repository.UserRepository;
import com.sparta.project.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    //    게시글 작성
    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto, HttpServletRequest httpServletRequest) {
        String token = jwtUtil.resolveToken(httpServletRequest);
        Claims claims;
        if (token != null) {
            // Token 검증
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }
            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );
            Post post = new Post(requestDto, user);
            postRepository.save(post);
            PostResponseDto postResponseDto = new PostResponseDto(post);
            return postResponseDto;
        }
        else {
            throw new IllegalArgumentException("토큰이 존재하지 않습니다." + HttpStatus.BAD_REQUEST);
        }
    }

    //전체 게시글 조회
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPosts() {
        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        List<Post> allByOrderByCreatedAtDesc = postRepository.findAllByOrderByCreatedAtDesc();
        for (Post post : allByOrderByCreatedAtDesc) {
            postResponseDtos.add(new PostResponseDto(post));
        }
        return postResponseDtos;

    }
    //return postRepository.findAllByOrderByCreatedAtDesc().stream().map(PostResponseDto::new).collect(Collectors.toList());

    //선택한 게시글 조회
    @Transactional
    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );
        PostResponseDto postResponseDto = new PostResponseDto(post);
        return postResponseDto;
    }

    //게시글 수정
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto, HttpServletRequest httpServletRequest) {
        String token = jwtUtil.resolveToken(httpServletRequest);
        Claims claims;
        // 토큰이 있는 경우에만 게시글 수정가능
        if (token != null) {
            // Token 검증
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }
            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );
            Post post = postRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
            );

            //관리자 권한
            if (user.getRole() == UserRoleEnum.ADMIN) {
                post.update(requestDto);
                return new PostResponseDto(post);
            }

            String name = claims.getSubject();
            if (name.equals(post.getUsername())) {
                post.update(requestDto);
                return new PostResponseDto(post);
            } else {
                throw new IllegalArgumentException("권한이 없습니다." + HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new IllegalArgumentException("토큰이 존재하지 않습니다." + HttpStatus.BAD_REQUEST);
        }
    }

    //게시글 삭제
    public String deletePost(Long Id, HttpServletRequest httpServletRequest) {
        String token = jwtUtil.resolveToken(httpServletRequest);
        Claims claims;
        // 토큰이 있는 경우에만 게시글 수정가능
        if (token != null) {
            // Token 검증
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }
            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );
            Post post = postRepository.findById(Id).orElseThrow(
                    () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
            );

            // 관리자 권한
            if (user.getRole() == UserRoleEnum.ADMIN) {
                postRepository.deleteById(Id);
                return "삭제 완료";
            }

            String name = claims.getSubject();
            if (name.equals(post.getUsername())) {
                postRepository.deleteById(Id);
                return "삭제 완료";
            }
            else { throw new IllegalArgumentException("권한이 없습니다." + HttpStatus.BAD_REQUEST); }
        }
        else { throw new IllegalArgumentException("토큰이 존재하지 않습니다." + HttpStatus.BAD_REQUEST); }
    }

}