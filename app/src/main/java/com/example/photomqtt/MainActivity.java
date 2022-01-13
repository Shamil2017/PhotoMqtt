package com.example.photomqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private  Integer Qos = 0;
    private  String  host, port, topicPub, topicSub, clientID, mes;
    private MqttAndroidClient client;
    TextView subText;
    TextView textView2;
    ImageView imgViewPhoto;
    private final static String FILE_NAME = "content.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        host = "broker.mqttdashboard.com";
        port = "1883";
        //topicSub = "/SONDA/SCHOOL/2";
        topicSub = "PhotoCamera/getphoto";
        topicPub = "PhotoCamera/command/getphoto";
        clientID = MqttClient.generateClientId().toString();
        textView2 = findViewById(R.id.textView2);
        imgViewPhoto = findViewById(R.id.imgViewPhoto);

/*
        OutputStreamWriter osw;
        BufferedWriter bw;
        try {
            FileOutputStream fos = openFileOutput("content1.txt", Context.MODE_PRIVATE);
            osw = new OutputStreamWriter(fos);
            bw = new BufferedWriter(osw);

            bw.write("ghfjghfjhgjfhg");
            bw.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
      /*  InputStreamReader isr;
        BufferedReader br;
        FileInputStream is;

        try {
            is = openFileInput("content1.txt");
            isr =  new InputStreamReader(is);
            br = new BufferedReader(isr);

            String line = "";
            while ((line = br.readLine())!=null)
                Toast.makeText(getApplicationContext(), line, Toast.LENGTH_SHORT).show();
            br.close();
            isr.close();
            is.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s1 = getApplicationContext().getFilesDir().toString();
                //getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();
        Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
        textView2.setText(s1);
*/

    }
  /*

    private void setSubscription(){

        try{

            client.subscribe(topic,0);


        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    public void conn(View v){

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"connected!!",Toast.LENGTH_LONG).show();
                    setSubscription();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"connection failed!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
*/


    private void publishText(String message){

        try {
            client.publish(topicPub, message.getBytes(),0,false);
            Toast.makeText(this,"Published Message",Toast.LENGTH_SHORT).show();
        } catch ( MqttException e) {
            e.printStackTrace();
        }
    }

    private void connect() {

        client =
                new MqttAndroidClient(MainActivity.this, "tcp://" + host + ":"+ port,
                        clientID);
        try {
            IMqttToken token = client.connect();
            Log.d(TAG, "client.connect");
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "onSuccess");
                    Toast.makeText(getApplicationContext(), "onSuccess", Toast.LENGTH_SHORT).show();
                    // отправляем сообщение на данный топик
                    //publishText("!GET");
                    sub();
                    publishText("!GETPHOTO");

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                    Toast.makeText(getApplicationContext(), "onFailure", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {

            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void sub() {
        try {
            client.subscribe(topicSub, Qos);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) { //Called when the client lost the connection to the broker
                }
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d(TAG,"messages: " + new String(message.getPayload()));
                    Log.d(TAG,"topic: " + topic);
                    Toast.makeText(getApplicationContext(), "receive message", Toast.LENGTH_SHORT).show();
                    FileOutputStream fos = null;
                    try {
                        fos = openFileOutput(FILE_NAME, MODE_PRIVATE);

                        fos.write(message.getPayload());
                    }
                    catch(IOException ex) {

                        Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    finally {
                        try {
                            if (fos != null)
                                fos.close();
                        } catch (IOException ex) {

                            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    String fname=new File(getFilesDir(), FILE_NAME).getAbsolutePath();
                    Bitmap bmImg = BitmapFactory.decodeFile(fname);
                    imgViewPhoto.setImageBitmap(bmImg);
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {//Called when a outgoing publish is complete
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }



    public void disconn(View view){

        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"Disconnected!!",Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"Could not diconnect!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void getPhotoClick(View view) {
        try {
            Toast.makeText(getApplicationContext(), "connect", Toast.LENGTH_SHORT).show();
            connect();

        }
        catch (Exception e) {

            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

    }


}