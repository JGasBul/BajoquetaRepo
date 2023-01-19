package org.example.panel;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.example.panel.ml.ModelUnquant;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.schema.Model;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;



public class MainActivity extends AppCompatActivity {

    MqttAndroidClient client;
    TextView subText;
    TextView subText2;

    TextView led;
    int cont1 = 0;
    int cont2 = 0;

    //IA

    TextView result;
    ImageView imageView;
    ImageView picture;
    int imageSize = 224;
    //------



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        subText = (TextView) findViewById(R.id.subText);
        subText2 = (TextView) findViewById(R.id.subText2);
        led = (TextView) findViewById(R.id.led);

        //IA
        result = findViewById(R.id.output);
        imageView = findViewById(R.id.foto);
        picture = findViewById(R.id.HacerFoto);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {

                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
        //------

        //Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Boolean> datos = new HashMap<>();
        //datos.put("led", true);
        //db.collection("LED").document("led").set(datos);

       // leerFirebase();
        //-------


        String clientId = MqttClient.generateClientId();

        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.194.53:1883", clientId);


        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "connected!!", Toast.LENGTH_LONG).show();
                    setSubscription();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "connection failed!!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TOPIC", topic +" DATA: "+message.toString());
                if (topic.equals("home/mov/tab")) {
                    subText.setText(new String(message.getPayload()));
                    cont1 = 0;
                } else {
                    subText2.setText(new String(message.getPayload()));
                    cont2 = 0;
                }

                if (cont1>10){
                    subText.setText("...");
                }
                if (cont2>10){
                    subText2.setText("...");
                }


                cont1++;
                cont2++;
                leerFirebase();

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }


    //------------------------------------------MQTT-----------------------------------

    public void published(View v) {

        String topic = "home/mov/tab";
        String message = "Prueba de conexión";
        try {
            client.publish(topic, message.getBytes(), 0, false);
            Toast.makeText(this, "Published Message", Toast.LENGTH_SHORT).show();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void setSubscription() {

        try {

            client.subscribe("home/mov/tab", 0);
            client.subscribe("home/mov/tab2", 0);


        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void conn(View v) {

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "connected!!", Toast.LENGTH_LONG).show();
                    setSubscription();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "connection failed!!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void disconn(View v) {

        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "Disconnected!!", Toast.LENGTH_LONG).show();


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Could not diconnect!!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    void leerFirebase(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("LED").document("led");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        if(document.getData().containsValue(true)){

                            led.setText(new String("ON"));

                            //Envio mqtt
                            String topic = "home/led";
                            String message = "1";
                            try {
                                client.publish(topic, message.getBytes(), 0, false);
                               // Toast.makeText(MainActivity.this, "LED ON", Toast.LENGTH_SHORT).show();
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }

                        }else{
                            led.setText(new String("OFF"));
                            //Envio mqtt
                            String topic = "home/led";
                            String message = "0";
                            try {
                                client.publish(topic, message.getBytes(), 0, false);
                                //Toast.makeText(MainActivity.this, "LED OFF", Toast.LENGTH_SHORT).show();
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        }

                    } else {
                        // El documento no existe
                    }
                } else {
                    // Ha ocurrido un error al obtener el documento
                }
            }
        });
    }

    //ACTIVAR DESACTIVAR LED
    public void setLed(View v){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("LED").document("led");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        if(document.getData().containsValue(true)){

                            Map<String, Boolean> datos = new HashMap<>();
                            datos.put("led", false);

                            db.collection("LED").document("led").set(datos);
                            led.setText(new String("OFF"));

                            //Envio mqtt
                            String topic = "home/led";
                            String message = "0";
                            try {
                                client.publish(topic, message.getBytes(), 0, false);
                                Toast.makeText(MainActivity.this, "LED OFF", Toast.LENGTH_SHORT).show();
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }

                        }else{
                            Map<String, Boolean> datos = new HashMap<>();
                            datos.put("led", true);


                            db.collection("LED").document("led").set(datos);
                            led.setText(new String("ON"));

                            //Envio mqtt
                            String topic = "home/led";
                            String message = "1";
                            try {
                                client.publish(topic, message.getBytes(), 0, false);
                                Toast.makeText(MainActivity.this, "LED ON", Toast.LENGTH_SHORT).show();
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        }

                    } else {
                        // El documento no existe
                    }
                } else {
                    // Ha ocurrido un error al obtener el documento
                }
            }
        });


    }

    //IA
    public void classifyImage(Bitmap image){
        try {
            ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());


            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());


            int [] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());


            int pixel = 0;
            for(int i = 0; i < imageSize; i++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);


            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();

            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Pringles", "Nada Detectado", "Azucar", "Tomate Cherry"};
            result.setText(classes[maxPos]);






            model.close();
        } catch (IOException e) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}