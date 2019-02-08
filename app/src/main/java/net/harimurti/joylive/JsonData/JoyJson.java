package net.harimurti.joylive.JsonData;

public class JoyJson {
    private int errno;
    private String msg;
    private JoyData data;

    public JoyJson(int errno, String msg, JoyData data){
        this.errno = errno;
        this.msg = msg;
        this.data = data;
    }

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