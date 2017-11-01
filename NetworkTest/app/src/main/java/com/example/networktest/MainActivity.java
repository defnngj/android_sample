package com.example.networktest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView responseText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sendRequest = (Button) findViewById(R.id.send_request);
        responseText = (TextView) findViewById(R.id.response_text);
        sendRequest.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.send_request){
            HttpUtil.sendOkHttpRequests("https://github.com/timeline.json", new okhttp3.Callback(){
                @Override
                public void onResponse(Call call, Response response) throws IOException{
                    //得到服务器返回的具体内容
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);
                }

                @Override
                public void onFailure(Call call,IOException e){
                    // 在这是对异常情况进行处理
                    e.printStackTrace();
                }
            });
            //sendRequestWithOkHttp();
        }
    }

    private void sendRequestWithOkHttp(){
        // 开启线程来发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("https://github.com/timeline.json").build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里进进UI 操作，将结果显示在界面上
                responseText.setText(response);
            }
        });
    }

    private void parseJSONWithJSONObject(String jsonData){
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            String message = jsonObject.getString("message");
            String doc_url = jsonObject.getString("documentation_url");
            Log.e("MainActivity","message is " + message);
            Log.e("MainActivity", "documentation_url is "+ doc_url);

            /** JSON 数组的处理方式
            JSONArray jsonArray = new JSONArray(jsonData);
            for(int i=0; i <jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String message = jsonObject.getString("message");
                String documentation_url = jsonObject.getString("documentation_url");
                Log.e("MainActivity","message is " + message);
                Log.e("MainActivity", "documentation_url is "+ documentation_url);
            }
             */
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 使用Gson处理JSON数据
    private void parseJSONWithGSON(String jsonData){
        Gson gson = new Gson();
        App app = gson.fromJson(jsonData, new TypeToken<App>(){}.getType());
        Log.e("MainActivity","message is " +  app.getMessage());
        Log.e("MainActivity", "documentation_url is "+ app.getDocURL());

        // JSON 数组的处理方式
        /**
        List<App> appList = gson.fromJson(jsonData, new TypeToken<List<App>>(){}.getType());
        for (App apps: appList){
            Log.e("MainActivity","message is " +  apps.getMessage());
            Log.e("MainActivity", "documentation_url is "+ apps.getDocURL());
        }
         */


    }
}
