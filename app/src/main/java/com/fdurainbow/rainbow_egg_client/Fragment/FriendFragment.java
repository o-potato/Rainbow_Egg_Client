package com.fdurainbow.rainbow_egg_client.Fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fdurainbow.rainbow_egg_client.Adapter.FriendAdapter;
import com.fdurainbow.rainbow_egg_client.Bean.ContentInfo;
import com.fdurainbow.rainbow_egg_client.R;
import com.fdurainbow.rainbow_egg_client.Utils.FriendHttpUtil;

import java.util.List;

import static com.fdurainbow.rainbow_egg_client.Activity.LoginActivity.hostID;


public class FriendFragment extends Fragment{
    private ListView lv;
    public FriendFragment() {
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
                lv.setAdapter(new FriendAdapter(getActivity(),(List<ContentInfo>) msg.obj));
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friend,null);
        lv = view.findViewById(R.id.fd_listview);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FriendHttpUtil rec = new FriendHttpUtil();
                    List<ContentInfo> list_item = rec.httpGet(hostID);
                    if (list_item!=null)
                        handler.sendMessage(handler.obtainMessage(22,list_item));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        return view;
    }
}
