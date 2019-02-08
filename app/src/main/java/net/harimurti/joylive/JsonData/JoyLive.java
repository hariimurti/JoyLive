package net.harimurti.joylive.JsonData;

public class JoyLive {
    int errno;
    String msg;
    Data data;

    public JoyLive(int errno, String msg, Data data){
        this.errno = errno;
        this.msg = msg;
        this.data = data;
    }

    public int getErrno() {
        return this.errno;
    }

    public String getMsg() {
        return this.msg;
    }

    public Data getData() {
        return this.data;
    }
}