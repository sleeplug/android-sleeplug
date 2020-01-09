package es.uvigo.teleco.sleeplug;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.preference.Preference;

import es.uvigo.teleco.sleeplug.utils.ConnectThread;

public class SleePlugApplication extends Application {

    private String ip = "192.168.43.137";
    private int port = 8888;

    private static SleePlugApplication sInstance;

    public static SleePlugApplication getInstance() {
        return sInstance;
    }

    private ConnectThread connectThread;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        connectThread = new ConnectThread(ip, port);
        connectThread.execute();
    }

    public ConnectThread getCurrentConnection() {
        return connectThread;
    }

    public void setCurrentBluetoothConnection(ConnectThread connectThread) {
        this.connectThread = connectThread;
    }
}
