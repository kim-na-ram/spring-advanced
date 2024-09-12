package org.example.expert.domain.user.controller;

import org.example.expert.config.mock.MockAuthUserArgumentResolver;
import org.example.expert.domain.data.user.UserMockDataUtil;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.service.UserService;
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

import static org.example.expert.utils.TestUtils.toJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockAuthUserArgumentResolver mockAuthUserArgumentResolver = new MockAuthUserArgumentResolver();

        this.mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(mockAuthUserArgumentResolver)
                .build();
    }

    @Nested
    @DisplayName("user 목록 조회 테스트 케이스")
    public class GetUser {
        @Test
        @DisplayName("user 목록 조회에 성공한다.")
        public void getUser_success() throws Exception {
            // given
            long userId = 1L;

            UserResponse userResponse = UserMockDataUtil.userResponse();

            given(userService.getUser(anyLong())).willReturn(userResponse);

            // when, then
            mockMvc.perform(get("/users/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string(toJsonString(userResponse)));
        }
    }

    @Nested
    @DisplayName("비밀번호 변경 테스트 케이스")
    public class ChangePassword {
        @Test
        @DisplayName("user 비밀번호 변경에 성공한다.")
        public void changePassword_success() throws Exception {
            // given
            UserChangePasswordRequest userChangePasswordRequest = UserMockDataUtil.userChangePasswordRequest();

            willDoNothing().given(userService).changePassword(anyLong(), any(UserChangePasswordRequest.class));

            // when, then
            mockMvc.perform(put("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(userChangePasswordRequest)))
                    .andExpect(status().isOk());
        }
    }
}