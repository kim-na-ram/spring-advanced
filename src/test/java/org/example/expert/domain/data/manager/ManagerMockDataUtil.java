package org.example.expert.domain.data.manager;

import org.example.expert.domain.data.todo.TodoMockDataUtil;
import org.example.expert.domain.data.user.UserMockDataUtil;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.user.entity.User;

public class ManagerMockDataUtil {
    public static Manager manager() {
        return new Manager(UserMockDataUtil.user(), TodoMockDataUtil.todo());
    }

    public static ManagerSaveRequest managerSaveRequest() {
        return new ManagerSaveRequest(1L);
    }
}
