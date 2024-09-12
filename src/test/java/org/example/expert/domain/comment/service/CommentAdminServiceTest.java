package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentAdminServiceTest {
    @InjectMocks
    private CommentAdminService commentAdminService;

    @Mock
    private CommentRepository commentRepository;

    @Nested
    @DisplayName("comment 삭제 테스트 케이스")
    public class DeleteComment {
        @Test
        @DisplayName("comment 삭제에 성공한다.")
        public void deleteComment_success() {
            // given
            long commentId = 1L;

            // when
            commentAdminService.deleteComment(commentId);

            // then
            verify(commentRepository, times(1)).deleteById(commentId);
        }
    }
}