package org.example.expert.domain.manager.controller;

import org.example.expert.config.mock.MockAuthUserArgumentResolver;
import org.example.expert.domain.data.manager.ManagerMockDataUtil;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.service.ManagerService;
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
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManagerController.class)
class ManagerControllerTest {
    @MockBean
    private ManagerService managerService;

    @Autowired
    private ManagerController managerController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockAuthUserArgumentResolver mockAuthUserArgumentResolver = new MockAuthUserArgumentResolver();

        mockMvc = MockMvcBuilders.standaloneSetup(managerController)
                .setCustomArgumentResolvers(mockAuthUserArgumentResolver)
                .build();
    }

    @Nested
    @DisplayName("manager 등록 테스트 케이스")
    public class SaveManager {
        @Test
        @DisplayName("매니저 유저 아이디가 비어있어 manager 등록에 실패한다.")
        public void saveManager_blankManagerId_failure() throws Exception {
            // given
            long todoId = 1L;
            ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest();

            // when, then
            mockMvc.perform(post("/todos/{todoId}/managers", todoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(managerSaveRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("manager 등록에 성공한다.")
        public void saveManager_success() throws Exception {
            // given
            long todoId = 1L;
            ManagerSaveRequest managerSaveRequest = ManagerMockDataUtil.managerSaveRequest(1L);

            ManagerSaveResponse managerSaveResponse = ManagerMockDataUtil.managerSaveResponse();

            given(managerService.saveManager(any(), anyLong(), any())).willReturn(managerSaveResponse);

            // when, then
            mockMvc.perform(post("/todos/{todoId}/managers", todoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(toJsonString(managerSaveRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(toJsonString(managerSaveResponse)));
        }
    }

    @Nested
    @DisplayName("manager 목록 조회 테스트 케이스")
    public class GetManagerList {
        @Test
        @DisplayName("manager 목록 조회에 성공한다.")
        public void getManagers_success() throws Exception {
            // given
            long todoId = 1L;

            ManagerResponse managerResponse = ManagerMockDataUtil.managerResponse();
            List<ManagerResponse> list = List.of(managerResponse);

            given(managerService.getManagers(anyLong())).willReturn(list);

            // when, then
            mockMvc.perform(get("/todos/{todoId}/managers", todoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string(toJsonString(list)));
        }
    }

    @Nested
    @DisplayName("manager 삭제 테스트 케이스")
    public class DeleteManager {
        @Test
        @DisplayName("manager 삭제에 성공한다.")
        public void deleteManager_success() throws Exception {
            // given
            long todoId = 1L;
            long managerId = 2L;

            willDoNothing().given(managerService).deleteManager(anyLong(), anyLong(), anyLong());

            // when, then
            mockMvc.perform(delete("/todos/{todoId}/managers/{managerId}", todoId, managerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }
}