package org.example.expert.domain.comment.controller;

import org.example.expert.domain.comment.service.CommentAdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentAdminController.class)
class CommentAdminControllerTest {
    @MockBean
    private CommentAdminService commentAdminService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("comment 삭제 테스트 케이스")
    public class DeleteComment {
        @Test
        @DisplayName("comment 삭제에 성공한다.")
        public void deleteComment_delete_failure() throws Exception {
            // given
            long commentId = 1L;

            willDoNothing().given(commentAdminService).deleteComment(commentId);

            // when, then
            mockMvc.perform(delete("/admin/comments/{commentId}", commentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }
}