package net.harimurti.joylive.Classes;

import android.util.Log;

public class AsyncSleep {
    private Listener listener = null;

    public interface Listener {
        default void onStart(){}
        default void onFinish(){}
    }

    public AsyncSleep setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public void start(int second) {
        try {
            listener.onStart();
            Thread.sleep( second * 1000 );
            listener.onFinish();
        }
        catch (InterruptedException e) {
            Log.e("AsyncThread", e.getMessage());
        }
    }
}
