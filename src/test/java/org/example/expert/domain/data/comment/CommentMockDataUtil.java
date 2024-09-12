package org.example.expert.domain.data.comment;

import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.data.todo.TodoMockDataUtil;
import org.example.expert.domain.data.user.UserMockDataUtil;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;

public class CommentMockDataUtil {
    public static Comment comment() {
        User user = UserMockDataUtil.userFromAuthUser();
        Todo todo = TodoMockDataUtil.todo();
        return new Comment("contents", user, todo);
    }

    public static CommentSaveRequest commentSaveRequest() {
        return new CommentSaveRequest("contents");
    }
    
    public static CommentSaveResponse commentSaveResponse() {
        return new CommentSaveResponse(1L, "contents", UserMockDataUtil.userResponse());
    }

    public static CommentResponse commentResponse() {
        return new CommentResponse(1L, "contents", UserMockDataUtil.userResponse());
    }

}
