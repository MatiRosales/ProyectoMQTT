package cl.isisur.mqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity {

    private MqttAndroidClient client;
    private final MemoryPersistence persistence = new MemoryPersistence();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), "tcp://209.126.106.184:1883", "SensorSantos", persistence);
            mqttAndroidClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                System.out.println("Conexión perdida");
               }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("Mensaje recibido: " + topic + ": " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("Entrega completada");
            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Conexión exitosa ");
                    try {
                        System.out.println("Suscribiendose a /santos/android");
                        mqttAndroidClient.subscribe("santos/android/nego", 0);
                        System.out.println("Suscrito a  /santos/android");
                        System.out.println("Publicando mensaje");
                        mqttAndroidClient.publish("/santos/android/nego", new MqttMessage("Prueba, HOLA MUNDO..!".getBytes()));
                    } catch (MqttException ex) {

                    }
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("Falló la conexión");
                    System.out.println("Problema: " + exception.toString());
                }
            });
        } catch (MqttException ex) {
            System.out.println(ex.toString());
        }
    }
}
