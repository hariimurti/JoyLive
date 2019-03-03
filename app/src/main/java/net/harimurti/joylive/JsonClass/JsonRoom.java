package net.harimurti.joylive.JsonClass;

public class JsonRoom {
    private int errno;
    private String msg;
    private Rooms data;

    public int getErrorNumber() {
        return this.errno;
    }

    public String getMessage() {
        return this.msg;
    }

    public Rooms getData() {
        return this.data;
    }
}