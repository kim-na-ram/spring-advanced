package org.example.expert.domain.user.controller;

import org.example.expert.domain.data.user.UserMockDataUtil;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.service.UserAdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.example.expert.utils.TestUtils.toJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAdminController.class)
class UserAdminControllerTest {
    @MockBean
    private UserAdminService userAdminService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("user 권한 변경 테스트 케이스")
    public class ChangeUserRole {
        @Test
        @DisplayName("user 권한 변경에 성공한다.")
        public void changeUserRole_success() throws Exception {
            // given
            long userId = 1L;
            UserRoleChangeRequest userRoleChangeRequest = UserMockDataUtil.userRoleChangeRequest_toUser();

            willDoNothing().given(userAdminService).changeUserRole(anyLong(), any());

            // when, then
            mockMvc.perform(patch("/admin/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(userRoleChangeRequest)))
                    .andExpect(status().isOk());
        }
    }
}