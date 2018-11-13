package com.example.wesley.idoso;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


public class Conecta extends AppCompatActivity implements CustomHandler.AppReceiver, Serializable {

    private CustomHandler handler;
    TextView status;
    TextView mensagem;
    ListView comodosList;
    String [][] comodos = new String[0][0];



    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_STATUS = 3;

    public static final int COMODO_VAZIO = 0;
    public static final int OBJETO_NO_COMODO = 1;
    public static final int PESSOA_NO_COMODO = 2;
    public static final int ALERTA_RISCO = 3;
    public static final int SAIU_DA_CASA = 4;
    public static final int PODE_ESTAR_NO_BANHEIRO = 5;

    public static final int MENSAGEM_COMODOS = 0;
    public static final int MENSAGEM_STATUS = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conecta);

        Intent it = getIntent();
        String ip = it.getStringExtra("ip");
        String port = it.getStringExtra("port");
        String user = it.getStringExtra("user");

        this.status = (TextView) findViewById(R.id.status);
        this.mensagem = (TextView) findViewById(R.id.mensagem);
        status.setText("Conectando a: "+user+"  -  "+ip+" : "+port);


        handler = new CustomHandler(this);

        final Intent itService = new Intent(this,ConectaService.class);
        itService.putExtra("ip", ip);
        itService.putExtra("port", port);
        itService.putExtra("user", user);
        itService.putExtra("handler", new Messenger(handler));
        startService(itService);

        comodosList = (ListView) findViewById(R.id.listaComodos);

//        Button stop = (Button) findViewById(R.id.stop);
//
//        stop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopService(itService);
//            }
//        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceiveResult(Message message) {



        JSONObject obj = null;
        int tipo = 0;
        int estado = 0;
        String comodo = null;
        ArrayList<String> comodosItens;


        switch (message.what) {
            case STATUS_RUNNING:
                Toast.makeText(this,(String)message.obj,Toast.LENGTH_SHORT).show();
                this.status.setText((String)message.obj);
                break;
            case STATUS_FINISHED:

                    this.finish();
                break;
            case STATUS_ERROR:

                break;
            case STATUS_STATUS:
                obj = (JSONObject) message.obj;

                try {
                    tipo = obj.getInt("tipo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if(tipo == MENSAGEM_STATUS){
                    try {
                        comodo = obj.getString("comodo");
                        estado = obj.getInt("estado");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for(int i = 0; i < comodos.length; i++){
                        if(comodos[i][0].equals(comodo)){
                            switch (estado){
                                case COMODO_VAZIO:
                                    comodos[i][1] = "Comodo vazio";
                                    break;
                                case OBJETO_NO_COMODO:
                                    comodos[i][1] = "Objeto no comodo";

                                    break;
                                case PESSOA_NO_COMODO:
                                    comodos[i][1] = "Pessoa no comodo";

                                    break;
                                case ALERTA_RISCO:
                                    comodos[i][1] = "SOCORRO!";

                                    break;
                                case SAIU_DA_CASA:
                                    comodos[i][1] = "Saiu de casa";

                                    break;
                                case PODE_ESTAR_NO_BANHEIRO:
                                    comodos[i][1] = "Pode estar no banheiro";

                                    break;
                            }
                        }
                    }






                }else if(tipo == MENSAGEM_COMODOS){
                    String[] preComodos = new String[0];
                    try {
                        preComodos = (obj.getString("comodos")).split(",");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    comodos = new String[preComodos.length][];

                    for(int i = 0; i < preComodos.length; i++){
                        comodos[i] = new String [] {preComodos[i],"Comodo Vazio"};
                    }




                }


                comodosItens = new ArrayList<String>();
                for(String[] c : comodos){

                    comodosItens.add(c[0]+" - "+c[1]);
                }


                ArrayAdapter adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, comodosItens);
                comodosList.setAdapter(adaptador);




                break;
            case 4:

                this.mensagem.setText((String)message.obj);


                int notifyID = 1;
                String CHANNEL_ID = "my_channel_01";// The id of the channel.
                CharSequence name = CHANNEL_ID;
                int importancee = NotificationManager.IMPORTANCE_HIGH;

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                NotificationCompat.Builder notificationc = new NotificationCompat.Builder(this);

                notificationc.setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!")
                        .setVibrate(new long[]{150,300,150,600,150,600});


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    NotificationChannel mChannell = new NotificationChannel(CHANNEL_ID, name, importancee);
                    mNotificationManager.createNotificationChannel(mChannell);

                    notificationc.setChannelId(CHANNEL_ID);
                }

                Notification notification = notificationc.build();




                mNotificationManager.notify(notifyID , notification);



                Intent resultIntent = new Intent(this, Alerta.class);
                startActivity(resultIntent);
                break;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }


}
