package es.uvigo.teleco.sleeplug.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectThread extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "ConnectThread";

    private ProgressDialog dialog;
    private Context context;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String ip;
    private int port;
    private boolean connectSuccess = false;
    private String responseMessage;

    public ConnectThread(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public ConnectThread(Context context, String ip, int port) {
        this(ip, port);
        this.context = context;
    }

    public boolean isConnectSuccess() {
        return connectSuccess;
    }

    public void setConnectSuccess(boolean connectSuccess) {
        this.connectSuccess = connectSuccess;
    }

    public Socket getSocket() {
        return this.socket;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "debugging :)");
        startConnection();
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... voids) {
        super.onProgressUpdate(voids);
    }

    @Override
    protected void onPostExecute(Void result) {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connectSuccess = true;
            //dialog.dismiss();
            super.onPostExecute(result);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void startConnection() {
        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public String write(String msg) {
        try {
            Log.d(TAG, msg);
            out.println(msg);
            return "OK";
        } catch (Exception e) {
            return "KO";
        }
    }

    public String writeAndReadLine(String msg) {
        try {
            Log.d(TAG, msg);
            out.println(msg);
            String response = in.readLine();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return response;
        } catch (Exception e) {
            return "KO";
        }
    }

    public String readLine() {
        try {
            return in.readLine();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return "KO";
        }
    }
}
