package dev.stanislavskyi.feedback_bot_vgr.telegram.state;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserSession {

    private UserState state;
    private String selectedRole;
    private String selectedAutoServiceBranch;

    public UserSession() {
        this.state = UserState.NEW_USER;
    }

}
