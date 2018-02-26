package com.us.cm.proyectocm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    public static final String CHAT_SERVER_URL = "http://217.216.84.31:3000/";
    private TextView res0;
    private TextView res;
    private TextView res2;
    private Socket mSocket;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        res0=(TextView)findViewById(R.id.console0);
        res=(TextView)findViewById(R.id.console1);
        res2=(TextView)findViewById(R.id.console2);

        res0.append("Server: "+CHAT_SERVER_URL);
        res.append(System.getProperty("line.separator"));
        res2.append(System.getProperty("line.separator"));


        try {
            mSocket = IO.socket(CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        mSocket.on("receive", listen_msg );
        mSocket.on("ackSend", ack );
        mSocket.connect();

        final Button receive = (Button)findViewById(R.id.receive);
        receive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                res2.setText("");
                res2.append("Connection status: ");

                if (mSocket.connected()){
                    res2.append("Connected."+ System.getProperty ("line.separator"));
                    String msg="body msg request";
                    mSocket.emit("request",msg);
                }else{
                    res2.append("Not connected."+ System.getProperty ("line.separator"));
                }

            }
        });

        final Button send = (Button)findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                res.setText("");
                res.append("Connection status: ");

                if (mSocket.connected()){
                    res.append("Connected."+ System.getProperty ("line.separator"));
                    String msg="body msg sent";
                    mSocket.emit("send",msg);
                }else{
                    res.append("Not connected."+ System.getProperty ("line.separator"));
                }

            }
        });
        final Button close = (Button)findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mSocket.connected()){
                    mSocket.disconnect();
                    res.append("Disconnected from server"+ System.getProperty ("line.separator"));
                    res2.append("Disconnected from server"+ System.getProperty ("line.separator"));
                }else{
                    res.setText("Already is disconnected from server"+ System.getProperty ("line.separator"));
                    res2.setText("Already is disconnected from server"+ System.getProperty ("line.separator"));
                }

            }
        });
        final Button connect = (Button)findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!mSocket.connected()){
                    mSocket.connect();
                    res.append("Connected to server"+ System.getProperty ("line.separator"));
                    res2.append("Connected to server"+ System.getProperty ("line.separator"));
                }else{
                    res.setText("Already is connected from server"+ System.getProperty ("line.separator"));
                    res2.setText("Already is connected from server"+ System.getProperty ("line.separator"));
                }

            }
        });
    }
    private Emitter.Listener listen_msg;
    {
        listen_msg = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            String id_msg = "";
                            String text_msg = "";
                            id_msg = data.getString("id");
                            text_msg = data.getString("text");
                            res2.append("id: " + id_msg + System.getProperty("line.separator"));
                            res2.append("text: " + text_msg + System.getProperty("line.separator"));
                        } catch (JSONException e) {
                            res2.append("Error to receive message." + System.getProperty("line.separator"));
                        }

                    }
                });
            }
        };
    }
    private Emitter.Listener ack;
    {
        ack = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            String msg = "";
                            Integer cont = 0;
                            msg = data.getString("status");
                            cont = data.getInt("number");
                            res.append("Server response: " + msg +", number: "+cont + System.getProperty("line.separator"));
                        } catch (JSONException e) {
                            res.append("Error to receive message." + System.getProperty("line.separator"));
                        }

                    }
                });
            }
        };
    }
}
