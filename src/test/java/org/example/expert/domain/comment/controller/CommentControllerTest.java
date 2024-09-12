package org.example.expert.domain.comment.controller;

import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.data.comment.CommentMockDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.example.expert.utils.TestUtils.toJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {
    @MockBean
    private CommentService commentService;

    @Autowired
    private CommentController commentController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Nested
    @DisplayName("comment 등록 테스트 케이스")
    public class SaveComment {
        @Test
        @DisplayName("내용란이 비어있어 comment 등록에 실패한다.")
        void saveComment_blankContents_failure() throws Exception {
            // given
            long todoId = 1L;
            CommentSaveRequest commentSaveRequest = new CommentSaveRequest();

            // when, then
            mockMvc.perform(post("/todos/{todoId}/comments", todoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(commentSaveRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("comment 등록에 성공한다.")
        void saveComment_success() throws Exception {
            // given
            long todoId = 1L;
            CommentSaveRequest commentSaveRequest = CommentMockDataUtil.commentSaveRequest();

            CommentSaveResponse commentSaveResponse = CommentMockDataUtil.commentSaveResponse();

            given(commentService.saveComment(any(AuthUser.class), anyLong(), any(CommentSaveRequest.class)))
                    .willReturn(commentSaveResponse);

            // when, then
            mockMvc.perform(post("/todos/{todoId}/comments", todoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(commentSaveRequest)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("comment 목록 조회 테스트 케이스")
    public class GetCommentList {
        @Test
        @DisplayName("comment 목록 조회에 성공한다.")
        public void getComments_success() throws Exception {
            // given
            long todoId = 1L;

            CommentResponse commentResponse = CommentMockDataUtil.commentResponse();
            List<CommentResponse> commentResponseList = List.of(commentResponse);

            given(commentService.getComments(anyLong())).willReturn(commentResponseList);

            // when, then
            mockMvc.perform(get("/todos/{todoId}/comments", todoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }
}