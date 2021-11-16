package com.fdurainbow.rainbow_egg_client.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fdurainbow.rainbow_egg_client.Activity.CommentDetail;
import com.fdurainbow.rainbow_egg_client.Adapter.ViewAdapter;
import com.fdurainbow.rainbow_egg_client.Bean.Dynamic;
import com.fdurainbow.rainbow_egg_client.Bean.PraiseDetail;
import com.fdurainbow.rainbow_egg_client.Bean.PraiseOrCollectMsg;
import com.fdurainbow.rainbow_egg_client.Bean.ReceiveInfo;
import com.fdurainbow.rainbow_egg_client.R;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.fdurainbow.rainbow_egg_client.Activity.LoginActivity.hostID;


public class ChannelFragment extends Fragment {
    public ChannelFragment() {
        // Required empty public constructor
    }
    ListView lv_view;
    List<Dynamic> list_item;
    SwipeRefreshLayout swipeRefreshLayout;
    ViewAdapter adapter = new ViewAdapter();
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            adapter.setDatas((List<Dynamic>)msg.obj);
            adapter.setInflater(getActivity());
            lv_view.setAdapter(adapter);

            swipeRefreshLayout.setRefreshing(false);

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
                            .url("http://10.176.50.27:8080/findcollect")
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
                    System.out.println("");
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
                            .url("http://10.176.50.27:8080/findpraise")
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
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_view,container,false);
        lv_view = view.findViewById(R.id.lv_view);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            ReceiveInfo rec = new ReceiveInfo();//Log.v("1234","1234");
                            list_item = rec.ReiceiveDynamic(hostID);
                            //Log.v("getinfo",list_item.get(0).toString());
                            if(!list_item.isEmpty())
                                handler.sendMessage(handler.obtainMessage(22,list_item));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    ReceiveInfo rec = new ReceiveInfo();//Log.v("1234","1234");
                    list_item = rec.ReiceiveDynamic(hostID);
                    //Log.v("getinfo",list_item.get(0).toString());
                    if(!list_item.isEmpty())
                        handler.sendMessage(handler.obtainMessage(22,list_item));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        return view;
    }

    private void comment(int i){
        Dynamic dy = list_item.get(i);
        Intent commentDetail = new Intent(getActivity(), CommentDetail.class);
        commentDetail.putExtra("mDynamic",dy);
        startActivity(commentDetail);
    }

}
