package org.example.expert.domain.auth.controller;

import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.auth.service.AuthService;
import org.example.expert.domain.common.exception.InvalidRequestException;
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
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @MockBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("회원가입 테스트 케이스")
    public class SignUp {
        @Test
        @DisplayName("이메일란이 비어있어 회원가입에 실패한다.")
        public void signUp_blankEmail_failure() throws Exception {
            // given
            SignupRequest signupRequest = new SignupRequest(null, "password", "user");

            // when, then
            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("이메일 형식이 유효하지 않아 회원가입에 실패한다.")
        public void signUp_invalidEmail_failure() throws Exception {
            // given
            SignupRequest signupRequest = new SignupRequest("aaaa", "password", "user");

            // when, then
            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("비밀번호란이 비어있어 회원가입에 실패한다.")
        public void signUp_blankPassword_failure() throws Exception {
            // given
            SignupRequest signupRequest = new SignupRequest("aaaa@aaa.com", null, "user");

            // when, then
            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("user 권한란이 비어있어 회원가입에 실패한다.")
        public void signUp_blankUserRole_failure() throws Exception {
            // given
            SignupRequest signupRequest = new SignupRequest("aaaa@aaa.com", "password", null);

            // when, then
            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("회원가입에 성공한다.")
        void signUp_success() throws Exception {
            // given
            SignupRequest signupRequest = new SignupRequest("aaaa@aaa.com", "password", "user");

            SignupResponse signupResponse = new SignupResponse("bearerToken");

            given(authService.signup(any(SignupRequest.class))).willReturn(signupResponse);

            // when, then
            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(signupRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(toJsonString(signupResponse)));
        }
    }

    @Nested
    @DisplayName("로그인 테스트 케이스")
    public class SignIn {
        @Test
        @DisplayName("이메일란이 비어있어 로그인에 실패한다.")
        public void signIn_blankEmail_failure() throws Exception {
            // given
            SigninRequest signinRequest = new SigninRequest(null, "password");

            // when, then
            mockMvc.perform(post("/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(signinRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("이메일 형식이 유효하지 않아 로그인에 실패한다.")
        public void signIn_invalidEmail_failure() throws Exception {
            // given
            SigninRequest signinRequest = new SigninRequest("aaaa", "password");

            // when, then
            mockMvc.perform(post("/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(signinRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("비밀번호란이 비어있어 로그인에 실패한다.")
        public void signIn_blankPassword_failure() throws Exception {
            // given
            SigninRequest signinRequest = new SigninRequest("aaaa@aaa.com", null);

            // when, then
            mockMvc.perform(post("/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(signinRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 인해 로그인에 실패한다.")
        void signIn_notFoundUser_failure() throws Exception {
            // given
            SigninRequest signinRequest = new SigninRequest("aaaa@aaa.com", "password");

            given(authService.signin(any(SigninRequest.class))).willThrow(new InvalidRequestException("가입되지 않은 유저입니다."));

            // when, then
            mockMvc.perform(post("/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(signinRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").exists());
        }

        @Test
        @DisplayName("이메일과 비밀번호가 일치하지 않아서 로그인에 실패한다.")
        void signIn_failure() throws Exception {
            // given
            SigninRequest signinRequest = new SigninRequest("aaaa@aaa.com", "password");

            given(authService.signin(any(SigninRequest.class))).willThrow(new AuthException(""));

            // when, then
            mockMvc.perform(post("/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(signinRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").exists());
        }

        @Test
        @DisplayName("로그인에 성공한다.")
        void signIn_success() throws Exception {
            // given
            SigninRequest signinRequest = new SigninRequest("aaaa@aaa.com", "password");

            SigninResponse signinResponse = new SigninResponse("bearerToken");

            given(authService.signin(any(SigninRequest.class))).willReturn(signinResponse);

            // when, then
            mockMvc.perform(post("/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(signinRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(toJsonString(signinResponse)));
        }
    }

}