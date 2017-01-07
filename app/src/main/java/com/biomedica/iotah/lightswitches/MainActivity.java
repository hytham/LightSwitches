package com.biomedica.iotah.lightswitches;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "MQTT";
    MqttConnectOptions options;
    MqttAndroidClient client;
    ToggleButton tbSwitchLight,tbSwitchMediaSet;
    TextView txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tbSwitchLight=(ToggleButton)findViewById(R.id.tbSwitchLight);
        tbSwitchMediaSet=(ToggleButton) findViewById(R.id.tbSwitchMediaSet);

        tbSwitchLight.setOnCheckedChangeListener(this);
        tbSwitchMediaSet.setOnCheckedChangeListener(this);

        txtStatus=(TextView) findViewById(R.id.txtStatus);

        options = new MqttConnectOptions();


        options.setUserName("haiwa80");
        options.setPassword("HythamReem1980#".toCharArray());

        String clientId = "";
        client =
                new MqttAndroidClient(this.getApplicationContext(), this.getApplicationContext().getResources().getString(R.string.MQTTUrl),
                        clientId);

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    txtStatus.setText("");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                    txtStatus.setText("Fail to send a command");

                }
            });
        } catch (MqttException e) {
            txtStatus.setText("Fail to send a command with MQTT error : "+ e.getMessage() );
        }


        getStatus();
    }

    @Override
    protected void onStart(){
        super.onStart();

    }


    @Override
    protected void onResume()
    {
        getStatus();
    }

    private void SendMessage(String Message,String topic){

        String payload = Message;
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        String msg="";

        switch (buttonView.getId()){
            case(R.id.tbSwitchLight):
                msg="light";
                break;
            case(R.id.tbSwitchMediaSet):
                msg="mediaset";
                break;
        }


        if(isChecked)
            SendMessage("0",msg);
        else
            SendMessage("1",msg);
    }

    private void getStatus(){



        RestClient restClient=new RestClient();
        try {
            String res=restClient.GET(new JSONObject(), getApplicationContext().getResources().getString(R.string.StatusUrl));//  "http://biomedica-bioinformatics.azurewebsites.net/api/IOT_STATUS_MST");
            JSONObject json=new JSONObject(res);
            JSONArray arr=json.getJSONArray("IOT_STATUS_MST");
            for(int i=0;i<arr.length();i++){
                JSONObject j= (JSONObject) arr.get(i);
                String name=j.get("item")+"-"+j.get("num");
                String v=j.getString("status");
                switch (name)
                {
                    case("light-1"):
                        tbSwitchLight.setChecked(v=="1");
                        break;
                    case("mediaset-1"):

                        break;
                }

            }


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
