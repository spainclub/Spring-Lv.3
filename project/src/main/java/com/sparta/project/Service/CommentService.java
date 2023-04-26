package com.sparta.project.Service;

import com.sparta.project.Dto.CommentRequestDto;
import com.sparta.project.Dto.CommentResponseDto;
import com.sparta.project.Entity.Comment;
import com.sparta.project.Entity.Post;
import com.sparta.project.Entity.User;
import com.sparta.project.Entity.UserRoleEnum;
import com.sparta.project.Repository.CommentRepository;
import com.sparta.project.Repository.PostRepository;
import com.sparta.project.Repository.UserRepository;
import com.sparta.project.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    //댓글 작성
    @Transactional
    public CommentResponseDto createComment(Long postId, CommentRequestDto commentRequestDto, HttpServletRequest httpServletRequest) {
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
            Post post = postRepository.findById(postId).orElseThrow(
                    () -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
            );

            Comment comment = new Comment(commentRequestDto, user, post);
            post.addComment(comment);
            comment.setUser(user);

            commentRepository.save(comment);
            CommentResponseDto commentResponseDto = new CommentResponseDto(comment);
            return commentResponseDto;
        }
        else {
            throw new IllegalArgumentException("토큰이 존재하지 않습니다." + HttpStatus.BAD_REQUEST);
        }
    }

    //댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto commentRequestDto, HttpServletRequest httpServletRequest) {
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
            Comment comment = commentRepository.findById(commentId).orElseThrow(
                    () -> new IllegalArgumentException("댓글이 존재하지 않습니다.")
            );

            //관리자 권한
            if (user.getRole() == UserRoleEnum.ADMIN) {
                comment.updateComment(commentRequestDto, user);
                return new CommentResponseDto(comment);
            }
            //or 조건으로 하나로 합칠수 있
            String name = claims.getSubject();
            if (name.equals(comment.getUsername())) {
                comment.updateComment(commentRequestDto, user);
                return new CommentResponseDto(comment);
            } else {
                throw new IllegalArgumentException("권한이 없습니다." + HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new IllegalArgumentException("토큰이 존재하지 않습니다."  + HttpStatus.BAD_REQUEST);
        }
    }

    //댓글 삭제
    @Transactional
    public String deleteComment(Long id, HttpServletRequest httpServletResponse) {
        String token = jwtUtil.resolveToken(httpServletResponse);
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
            Comment comment = commentRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
            );

            //관리자 권한
            if (user.getRole() == UserRoleEnum.ADMIN) {
                commentRepository.deleteById(id);
                return "삭제 완료";
            }

            String name = claims.getSubject();
            if (name.equals(comment.getUsername())) {
                commentRepository.deleteById(id);
                return "삭제 완료";
            }
            else { throw new IllegalArgumentException("권한이 없습니다." + HttpStatus.BAD_REQUEST); }
        }
        else { throw new IllegalArgumentException("토큰이 존재하지 않습니다." + HttpStatus.BAD_REQUEST); }
    }

}
