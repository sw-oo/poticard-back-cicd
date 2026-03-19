package org.example.porti.chat.message.model;

public enum ContentsType {
    TEXT, IMAGE, DOC, USER_LEFT, USER_ENTER;

    public String getTypeMsg(String userName) {
        return getTypeMsg(userName, null);
    }

    public String getTypeMsg(String userName, String contents) {
        return switch (this) {
            case USER_ENTER -> userName + "님이 입장하셨습니다.";
            case USER_LEFT  -> userName + "님이 나갔습니다.";
            case TEXT       -> contents;
            case DOC, IMAGE -> "파일을 보냈습니다.";
        };
    }
}
