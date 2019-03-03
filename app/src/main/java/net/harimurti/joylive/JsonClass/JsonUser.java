package net.harimurti.joylive.JsonClass;

public class JsonUser {
    private int errno;
    private String msg;
    private User data;

    public int getErrorNumber() {
        return this.errno;
    }

    public String getMessage() {
        return this.msg;
    }

    public User getData() {
        return this.data;
    }
}