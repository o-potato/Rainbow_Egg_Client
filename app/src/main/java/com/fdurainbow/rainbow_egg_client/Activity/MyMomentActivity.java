package com.fdurainbow.rainbow_egg_client.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.fdurainbow.rainbow_egg_client.Adapter.ViewAdapter;
import com.fdurainbow.rainbow_egg_client.Bean.Dynamic;
import com.fdurainbow.rainbow_egg_client.Bean.PraiseDetail;
import com.fdurainbow.rainbow_egg_client.Bean.PraiseOrCollectMsg;
import com.fdurainbow.rainbow_egg_client.Bean.ReceiveInfo;
import com.fdurainbow.rainbow_egg_client.Activity.LoginActivity;
import com.fdurainbow.rainbow_egg_client.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyMomentActivity extends AppCompatActivity {

    ListView lv_view;
    ImageView iv_back;
    List<Dynamic> list_item;
    ViewAdapter adapter = new ViewAdapter();
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){

            adapter.setDatas((List<Dynamic>)msg.obj);
            adapter.setInflater(MyMomentActivity.this);
            if(adapter == null) Log.v("emptyAdapter","empty");
            lv_view.setAdapter(adapter);

            adapter.setOnItemCollectListener(new ViewAdapter.onItemCollectListener() {
                @Override
                public void onCollectClick(final int i) {
                    PraiseOrCollectMsg msg = new PraiseOrCollectMsg();
                    msg.setDynamicID(list_item.get(i).getDynamicID());
                    msg.setHostID(1);
                    String jsonstr = new Gson().toJson(msg);
                    RequestBody body = RequestBody.create(MediaType.parse("application/json"),jsonstr);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://10.176.50.27:8888/findcollect")
                            .post(body)
                            .build();
                    client.newCall(request).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String json = response.body().string();
                            Log.v("accepet",json);
                            list_item.get(i).setIsCollected(Integer.parseInt(json));
                            //adapter.notifyDataSetChanged();
                            Message message = new Message();
                            message.what = 0;
                            handlerPra.sendMessage(message);
                        }
                    });

                }
            });

            adapter.setOnItemCommentClickListener(new ViewAdapter.onItemCommentListener() {
                @Override
                public void onCommentClick(int i) {
                    comment(i);
                }
            });

            adapter.setOnItemPraiseClickListener(new ViewAdapter.onItemPraiseListener() {
                @Override
                public void onPraiseClick(final int i) {
                    PraiseOrCollectMsg msg = new PraiseOrCollectMsg();
                    msg.setDynamicID(list_item.get(i).getDynamicID());
                    msg.setHostID(1);
                    String jsonstr = new Gson().toJson(msg);
                    RequestBody body = RequestBody.create(MediaType.parse("application/json"),jsonstr);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://10.176.50.27:8888/findpraise")
                            .post(body)
                            .build();
                    client.newCall(request).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String json = response.body().string();
                            Gson gson = new Gson();
                            PraiseDetail pra = gson.fromJson(json,PraiseDetail.class);
                            list_item.get(i).setHasPraised(pra.haspriased);
                            list_item.get(i).setIsPraised(pra.isprased);
                            //adapter.notifyDataSetChanged();
                            Message message = new Message();
                            message.what = 0;
                            handlerPra.sendMessage(message);
                        }
                    });

                }
            });
        }
    };

    Handler handlerPra = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == 0){
                adapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_my_moment);
        lv_view = findViewById(R.id.lv_view);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    ReceiveInfo rec = new ReceiveInfo();//Log.v("1234","1234");
                    list_item = rec.ReiceiveMoment(LoginActivity.hostID);
                    handler.sendMessage(handler.obtainMessage(22,list_item));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        iv_back = (ImageView)findViewById(R.id.iv_menu);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyMomentActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void comment(int i){
        Dynamic dy = list_item.get(i);
        Intent commentDetail = new Intent(MyMomentActivity.this, CommentDetail.class);
        commentDetail.putExtra("mDynamic",dy);
        startActivity(commentDetail);
    }

}