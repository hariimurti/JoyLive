package net.harimurti.joylive.Api;

public class JsonUser {
    private int errno;
    private String msg;
    private JoyUser data;

    public int getErrorNumber() {
        return this.errno;
    }

    public String getMessage() {
        return this.msg;
    }

    public JoyUser getData() {
        return this.data;
    }
}