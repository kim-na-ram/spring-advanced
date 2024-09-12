package org.example.expert.config;

import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ObjectUtils;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JwtFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("/auth 로 시작하는 url 은 필터를 통과한다.")
    public void filter_auth_success() throws Exception {
        // when, then
        mockMvc.perform(post("/auth/signin"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("JWT 토큰이 없다면 필터를 통과하지 못한다.")
    public void filter_notExistToken_failure() throws Exception {
        // when, then
        mockMvc.perform(get("/todos"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> ObjectUtils.nullSafeEquals(result.getResponse().getErrorMessage(), "JWT 토큰이 필요합니다."));
    }

    @Test
    @DisplayName("유효하지 않은 JWT 토큰으로 인해 필터를 통과하지 못한다.")
    public void filter_invalidToken_failure() throws Exception {
        // given
        String token = "Bearer hhhhhhhjjjjjjjjjkjjjjjjj";

        // when, then
        mockMvc.perform(get("/todos")
                        .header(AUTHORIZATION, token))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(result -> ObjectUtils.nullSafeEquals(result.getResponse().getErrorMessage(), "유효하지 않는 JWT 서명입니다."));
    }

    @Test
    @DisplayName("유효한 토큰으로 필터를 통과한다.")
    public void filter_validToken_success() throws Exception {
        // given
        String token = jwtUtil.createToken(1L, "aaa@aaa.com", UserRole.USER);

        // when, then
        mockMvc.perform(get("/todos")
                        .header(AUTHORIZATION, token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("/admin 로 시작하는 url을 관리자 권한이 없어 필터를 통과하지 못한다.")
    public void filter_accessToAdmin_failure() throws Exception {
        // given
        long userId = 1L;
        String token = jwtUtil.createToken(userId, "aaa@aaa.com", UserRole.USER);

        // when, then
        mockMvc.perform(patch("/admin/users/{userId}", userId)
                        .header(AUTHORIZATION, token))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(result -> ObjectUtils.nullSafeEquals(result.getResponse().getErrorMessage(), "관리자 권한이 없습니다."));
    }

    @Test
    @DisplayName("/admin 로 시작하는 url을 관리자 권한으로 필터를 통과한다.")
    public void filter_accessToAdmin_success() throws Exception {
        // given
        long userId = 1L;
        String token = jwtUtil.createToken(userId, "aaa@aaa.com", UserRole.ADMIN);

        // when, then
        mockMvc.perform(patch("/admin/users/{userId}", userId)
                        .header(AUTHORIZATION, token))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}