package org.example.expert.domain.data.manager;

import org.example.expert.domain.data.todo.TodoMockDataUtil;
import org.example.expert.domain.data.user.UserMockDataUtil;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;

public class ManagerMockDataUtil {
    public static Manager manager() {
        return new Manager(UserMockDataUtil.user(), TodoMockDataUtil.todo());
    }

    public static Manager manager(User user) {
        return new Manager(user, TodoMockDataUtil.todo());
    }

    public static ManagerSaveRequest managerSaveRequest() {
        return new ManagerSaveRequest(1L);
    }

    public static ManagerSaveRequest managerSaveRequest(long todoId) {
        return new ManagerSaveRequest(todoId);
    }

    public static ManagerSaveResponse managerSaveResponse() {
        UserResponse userResponse = UserMockDataUtil.userResponse();
        return new ManagerSaveResponse(1L, userResponse);
    }

    public static ManagerResponse managerResponse() {
        UserResponse userResponse = new UserResponse(1L, "email");
        return new ManagerResponse(1L, userResponse);
    }
}
