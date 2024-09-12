package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.data.comment.CommentMockDataUtil;
import org.example.expert.domain.data.manager.ManagerMockDataUtil;
import org.example.expert.domain.data.todo.TodoMockDataUtil;
import org.example.expert.domain.data.user.UserMockDataUtil;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private CommentService commentService;

    @Nested
    @DisplayName("comment 등록 테스트 케이스")
    class SaveComment {
        @Test
        @DisplayName("todo가 존재하지 않아 comment 등록에 실패한다.")
        public void saveComment_notFoundTodo_failure() {
            // given
            long todoId = 1;
            AuthUser authUser = UserMockDataUtil.authUser();
            CommentSaveRequest request = CommentMockDataUtil.commentSaveRequest();

            given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> commentService.saveComment(authUser, todoId, request)
                    );

            // then
            assertEquals("Todo not found", exception.getMessage());
        }

        @Test
        @DisplayName("todo 담당 매니저가 아니라 comment 등록에 실패한다.")
        public void saveComment_InvalidManager_failure() {
            // given
            long todoId = 1;
            AuthUser authUser = UserMockDataUtil.authUser();
            CommentSaveRequest request = CommentMockDataUtil.commentSaveRequest();

            Todo todo = TodoMockDataUtil.todo();
            ReflectionTestUtils.setField(todo, "managers", List.of());

            given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));

            // when
            InvalidRequestException exception =
                    assertThrows(InvalidRequestException.class,
                            () -> commentService.saveComment(authUser, todoId, request)
                    );

            // then
            assertEquals("댓글 작성은 담당 매니저만 가능합니다.", exception.getMessage());
        }

        @Test
        @DisplayName("comment를 정상적으로 등록한다.")
        public void saveComment_success() {
            // given
            long todoId = 1;
            AuthUser authUser = UserMockDataUtil.authUser();
            CommentSaveRequest request = CommentMockDataUtil.commentSaveRequest();

            Todo todo = TodoMockDataUtil.todo();
            Manager manager = ManagerMockDataUtil.manager(UserMockDataUtil.user());
            ReflectionTestUtils.setField(todo, "managers", List.of(manager));

            Comment comment = CommentMockDataUtil.comment();

            given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
            given(commentRepository.save(any())).willReturn(comment);

            // when
            CommentSaveResponse result = commentService.saveComment(authUser, todoId, request);

            // then
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("comment 목록 조회 테스트 케이스")
    class GetCommentList {
        @Test
        @DisplayName("comment 목록을 정상적으로 조회한다.")
        public void getComments_success() {
            // given
            long todoId = 1L;

            Comment comment1 = CommentMockDataUtil.comment();
            Comment comment2 = CommentMockDataUtil.comment();
            List<Comment> commentList = List.of(comment1, comment2);

            given(commentRepository.findByTodoIdWithUser(anyLong())).willReturn(commentList);

            // when
            List<CommentResponse> result = commentService.getComments(todoId);

            // then
            assertEquals(2, result.size());
        }
    }
}
