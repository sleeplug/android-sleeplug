package es.uvigo.teleco.sleeplug.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionReceiver extends BroadcastReceiver {

    private static final String ACTION_STOP = "stop";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION_STOP:
                break;
        }
    }
}
