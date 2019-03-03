package net.harimurti.joylive.Api;

public class JsonRoom {
    private int errno;
    private String msg;
    private JoyData data;

    public int getErrorNumber() {
        return this.errno;
    }

    public String getMessage() {
        return this.msg;
    }

    public JoyData getData() {
        return this.data;
    }
}