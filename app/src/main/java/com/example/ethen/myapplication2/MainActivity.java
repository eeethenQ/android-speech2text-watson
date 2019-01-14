package com.example.ethen.myapplication2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    public static final int SHOW_RESPONSE = 0;

    private SpeechToText speechService;
    private TextView textView;

    // 新建一个Handler对象，在这里接受message，然后更新Textview控件的内容
    private Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_RESPONSE:
                    String response = (String) msg.obj;
                    textView.setText(response);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Strict Mode to get the permission to read files
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }


        speechService = initSpeechToTextService();

        //textview
        textView = findViewById(R.id.text_to_show);

        // Button upload
        Button button1 = findViewById(R.id.button_1);
        Button button2 = findViewById(R.id.button_2);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Toast.makeText(MainActivity.this, "You click on Button UPLOAD",
                //        Toast.LENGTH_SHORT).show();
                sendRequest();
                Toast.makeText(MainActivity.this, "You upload!",
                        Toast.LENGTH_SHORT).show();

            }
        });

        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

    }

    private void sendRequest(){
        new Thread(new Runnable(){

            @Override
            public void run(){
                try {
                    RecognizeOptions recognizeOptions = new RecognizeOptions.Builder()
                            .audio(new FileInputStream("/storage/emulated/0/Android/data/com.example/test.mp3"))
                            .contentType("audio/mp3")
                            .model("en-US_BroadbandModel")
                            .build();

                    BaseRecognizeCallback baseRecognizeCallback =
                            new BaseRecognizeCallback() {

                                @Override
                                public void onTranscription
                                        (SpeechRecognitionResults speechRecognitionResults) {
                                    //System.out.println(speechRecognitionResults);

                                    String text = speechRecognitionResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                                    Log.d("SpeechService", "get result!");

                                    Message message = new Message();
                                    message.what = SHOW_RESPONSE;
                                    message.obj = text;
                                    handler.sendMessage(message);
                                }

                                @Override
                                public void onDisconnected() {
                                    System.exit(0);
                                }

                            };

                    speechService.recognizeUsingWebSocket(recognizeOptions,
                            baseRecognizeCallback);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private SpeechToText initSpeechToTextService() {
        String apiKey = getString(R.string.speech_text_iam_apikey);
        IamOptions options = new IamOptions.Builder()
                .apiKey(apiKey)
                .build();
        SpeechToText service = new SpeechToText(options);
        service.setEndPoint(getString(R.string.speech_text_url));
        return service;
    }
}
