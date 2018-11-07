package com.example.wesley.idoso;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;



public class ConectaService extends IntentService {


    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_STATUS = 3;


    String ip;
    int port;
    Socket socket;
    Boolean ativo = true;
    char message;
    Messenger handler;

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public ConectaService() {
        super("ConectaServiceThread");
    }



    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.

        this.ip =  intent.getStringExtra("ip");
        this.port = intent.getIntExtra("port",5000);

        handler = intent.getParcelableExtra("handler");



        try {

            InetAddress serverAddr = InetAddress.getByName(this.ip);



            this.socket = new Socket(serverAddr, this.port);




        } catch (UnknownHostException e1) {
            e1.printStackTrace();

        } catch (IOException e1) {

            e1.printStackTrace();

        }


        BufferedReader stdIn = null;
        PrintWriter out;


        try {
            stdIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);
            out.print("2");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read data
//        DataInputStream inputStream = null;
//        try {
//            inputStream = new DataInputStream(socket.getInputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        Message msg = new Message();
        msg.obj = "Conectado com: "+this.ip+":"+this.port;
        msg.what = STATUS_RUNNING;
        try {
            handler.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        while(this.ativo){
            message = '.';

            try {
                message = (char)stdIn.read();



            } catch (IOException e) {
                e.printStackTrace();
                message = '.';

            }


            Log.e("while", String.valueOf(message));


            msg = new Message();
            msg.obj = "Conectado com: "+this.ip+":"+this.port;
            if(message == '0'){
                msg.obj = "Nenhum movimento";
            }
            if(message == '2'){
                msg.obj = "Objeto no Local";
            }
            if(message == '1'){
                msg.obj = "Pessoa no Local";
            }
            msg.what = STATUS_STATUS;

            if(message == '3'){
                msg.obj = "SOCORROOO!!!!";

                msg.what = 4;

                Intent alerta = new Intent(ConectaService.this, Alerta.class);
                startActivity(alerta);
            }

            try {
                handler.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        // Shut down socket
        try {
            this.socket.shutdownInput();
            this.socket.shutdownOutput();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }






    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            this.ativo = false;
            this.socket.shutdownInput();
            this.socket.shutdownOutput();
            this.socket.close();


            Message msg = new Message();
            msg.what = this.STATUS_FINISHED;

            try {
                handler.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}






















