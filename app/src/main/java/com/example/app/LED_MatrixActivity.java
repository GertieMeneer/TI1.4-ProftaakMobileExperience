
package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.example.app.builders.MessageBuilder;
import com.example.app.mqtt.Mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.net.ssl.SSLSocketFactory;

public class LED_MatrixActivity extends AppCompatActivity {
    private static final int GRID_SIZE = 8;
    private MessageBuilder messageBuilder = new MessageBuilder();
    private Mqtt mqtt = new Mqtt();
    private MqttClient client;
    public static String currentAnimal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_matrix);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackButton());

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> onSendButton());

        GridLayout gridLayout = findViewById(R.id.grid_layout);
        gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);

        int defaultGrayColor = getResources().getColor(android.R.color.darker_gray);

        // 64 buttons in the grid
        for (int i = 1; i <= GRID_SIZE * GRID_SIZE; i++) {
            Button button = new Button(this);
            button.setText(String.valueOf(i));
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.setGravity(Gravity.CENTER);
            layoutParams.width = 0;
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            layoutParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            button.setLayoutParams(layoutParams);
            button.setBackgroundColor(defaultGrayColor); // Set initial background color
            gridLayout.addView(button);
            button.setOnClickListener(v -> onButtonClick(button));
        }

        try {
            // serverURI in format: "protocol://name:port"
            this.client = new MqttClient(
                    "ssl://7b10c1a6effd49c798757d01597a1663.s2.eu.hivemq.cloud:8883",
                    MqttClient.generateClientId(),
                    new MemoryPersistence());
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName("AndroidPhone");
        mqttConnectOptions.setPassword("Stronk!PasswordSuperAdmin1".toCharArray());
        mqttConnectOptions.setSocketFactory(SSLSocketFactory.getDefault());

        try {
            client.connect(mqttConnectOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            client.subscribe("topic/result");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mqtt.connect();
            mqtt.sendMessage(Info.username, "topic/username");
            mqtt.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            // Called when the client lost the connection to the broker
            public void connectionLost(Throwable cause) {
                System.out.println("client lost connection " + cause);
            }

            @Override
            // Called when a message arrives from MQTT
            public void messageArrived(String topic, MqttMessage message) {
//                Log.d("mqtt", "message arrived: " + message.getPayload());
                if (topic.equals("topic/result")) {
                    if (message.toString().equals("200")) {
                        if (currentAnimal.equals("pig")) {
                            Info.unlockedAnimals.add("pig");
                        }
//                        Log.d("result", "good");
                        runOnUiThread(() -> {
                            goodInput();
                        });
                    } else {
//                        Log.d("result", "wrong");
                        runOnUiThread(() -> {
                            wrongInput();
                        });
                    }
                }
            }

            @Override
            // Called when an outgoing publish is complete
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("delivery complete " + token);
            }
        });
    }

    private void onButtonClick(Button button) {
        String buttonText = button.getText().toString();
        messageBuilder.add(buttonText);
//        Log.d("BUTTON", buttonText);
        messageBuilder.printMessage();

        int defaultGrayColor = getResources().getColor(android.R.color.darker_gray);
        int redColor = getResources().getColor(android.R.color.holo_red_light);

        Drawable buttonBackground = button.getBackground();

        if (buttonBackground instanceof ColorDrawable) {
            int buttonColor = ((ColorDrawable) buttonBackground).getColor();

            if (buttonColor == defaultGrayColor) {
                button.setBackgroundColor(redColor);
            } else if (buttonColor == redColor) {
                button.setBackgroundColor(defaultGrayColor);
            }
        } else {
            button.setBackgroundColor(defaultGrayColor);
        }
    }

    private void onBackButton() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void onSendButton() {
        try {
            mqtt.connect();
            try {
                mqtt.sendMessage(messageBuilder.getMessage(), "topic/matrix");
//                Toast toast = Toast.makeText(this, "Opdracht verstuurd!", Toast.LENGTH_SHORT);
//                toast.show();
            } catch (Exception e) {
                Toast toast = Toast.makeText(this, "Probeer het opnieuw!", Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(this, "Verbinding mislukt!", Toast.LENGTH_SHORT);
            toast.show();
        }
        mqtt.disconnect();
    }

    private void goodInput() {
        Toast toast = Toast.makeText(LED_MatrixActivity.this, "Je hebt het goed!", Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent(LED_MatrixActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void wrongInput() {
        Toast toast = Toast.makeText(LED_MatrixActivity.this, "Helaas, het is fout :(", Toast.LENGTH_SHORT);
        toast.show();
    }
}