package com.fdurainbow.rainbow_egg_client.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fdurainbow.rainbow_egg_client.Bean.ContentInfo;
import com.fdurainbow.rainbow_egg_client.Bean.MyImageView;
import com.fdurainbow.rainbow_egg_client.Bean.ReceiveInfo;
import com.fdurainbow.rainbow_egg_client.Fragment.AnnounceFragment;
import com.fdurainbow.rainbow_egg_client.Fragment.FriendFragment;
import com.fdurainbow.rainbow_egg_client.Fragment.ChannelFragment;
import com.fdurainbow.rainbow_egg_client.R;
import com.fdurainbow.rainbow_egg_client.Utils.MyselfUtil;
import com.google.android.material.navigation.NavigationView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

import static com.fdurainbow.rainbow_egg_client.Activity.LoginActivity.hostID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //UI Object
    private TextView txt_topbar;
    private TextView txt_channel;
    private TextView txt_message;
    private TextView txt_setting;
    private FrameLayout ly_content;

    //Fragment Object
    private ChannelFragment channelF;
    private AnnounceFragment announceF;
    private FriendFragment settingF;
    private FragmentManager fManager;

    //drawer layout
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    ImageView menu;

    private MyImageView user_image;
    private TextView user_name;
    private TextView user_sign;
    private ContentInfo MyInfo;

    Handler handlerPra = new Handler(){
        @Override
        public void handleMessage(Message msg){
            MyInfo = (ContentInfo) msg.obj;

            if(MyInfo.getAvatar() != null)
                user_image.setImageURL("http://10.176.50.27:8080/uploadavatar/"+MyInfo.getAvatar());
            else
                user_image.setImageResource(R.drawable.touxiang);
            user_name.setText(MyInfo.getNick());
//            user_sign.setText(MyInfo.getSign());
            user_sign.setText("");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        fManager = getSupportFragmentManager();
        bindViews();
        txt_channel.performClick();

        initView();

        follow_fd();
    }

    private void initView(){
        //????????????
        drawerLayout = findViewById(R.id.activity_na);
        navigationView = findViewById(R.id.nav);
        menu = findViewById(R.id.iv_menu);

        View headerView = navigationView.getHeaderView(0);
        user_image = headerView.findViewById(R.id.iv_menu_user);
        user_name = headerView.findViewById(R.id.tv_menu_user);
        user_sign = headerView.findViewById(R.id.tv_menu_usersign);

//        user_image.setImageResource(R.drawable.touxiang);
//        user_name.setText("lpz");
//        user_sign.setText("");

        //??????????????????
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    MyselfUtil rec = new MyselfUtil();
                    MyInfo = rec.httpGet(hostID);
                    handlerPra.sendMessage(handlerPra.obtainMessage(22,MyInfo));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        //???????????????
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //?????????????????????????????????
                if (drawerLayout.isDrawerOpen(navigationView)){
                    drawerLayout.closeDrawer(navigationView);
                }else{
                    drawerLayout.openDrawer(navigationView);
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.menu_item1:
                        Intent intent  = new Intent(MainActivity.this,MyCollectActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.menu_item2:
                        Intent intent2  = new Intent(MainActivity.this,MyLikeActivity.class);
                        startActivity(intent2);
                        finish();
                        break;
                    case R.id.menu_item3:
                        Intent intent3  = new Intent(MainActivity.this,CommentActivity.class);
                        startActivity(intent3);
                        finish();
                        break;
                    case R.id.menu_item4:
                        Intent intent4  = new Intent(MainActivity.this,MyMomentActivity.class);
                        startActivity(intent4);
                        finish();
                        break;
                }
                return true;
            }

        });

    }

    //????????????
    private void follow_fd(){
        ImageView follow_fd = (ImageView)findViewById(R.id.iv_follow_fd);
        follow_fd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,FollowActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //Fragment
    private void bindViews() {
        txt_topbar = findViewById(R.id.txt_topbar);
        txt_channel = findViewById(R.id.txt_channel);
        txt_message = findViewById(R.id.txt_message);
        txt_setting = findViewById(R.id.txt_setting);
        ly_content = findViewById(R.id.ly_content);

        txt_channel.setOnClickListener(this);
        txt_message.setOnClickListener(this);
        txt_setting.setOnClickListener(this);
    }

    private void setSelected(){
        txt_channel.setSelected(false);
        txt_message.setSelected(false);
        txt_setting.setSelected(false);
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(channelF != null)fragmentTransaction.hide(channelF);
        if(announceF != null)fragmentTransaction.hide(announceF);
        if(settingF != null)fragmentTransaction.hide(settingF);
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        switch (view.getId()){
            case R.id.txt_channel:
                setSelected();
                txt_topbar.setText(R.string.tab_menu_normal);
                txt_channel.setSelected(true);
                if(channelF == null){
                    channelF = new ChannelFragment();
                    fTransaction.add(R.id.ly_content,channelF);
                }else{
                    fTransaction.show(channelF);
                }break;
            case R.id.txt_message:
                setSelected();
                txt_topbar.setText(R.string.tab_menu_message);
                txt_message.setSelected(true);
                if(announceF == null){
                    announceF = new AnnounceFragment();
                    fTransaction.add(R.id.ly_content,announceF);
                }else{
                    fTransaction.show(announceF);
                }break;
            case R.id.txt_setting:
                setSelected();
                txt_topbar.setText(R.string.tab_menu_setting);
                txt_setting.setSelected(true);
                if(settingF == null){
                    settingF = new FriendFragment();
                    fTransaction.add(R.id.ly_content,settingF);
                }else{
                    fTransaction.show(settingF);
                }break;
        }
        fTransaction.commit();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> images;
        Log.v("ImageInfo","image");
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {// ????????????????????????

                //images = PictureSelector.obtainMultipleResult(data);
                //selectList.addAll(images);
                Log.v("ImageInfo","-------------------------------");

                //PictureSelector.obtainMultipleResult(data);
                announceF.show(PictureSelector.obtainMultipleResult(data));

                // ?????? LocalMedia ??????????????????path
                // 1.media.getPath(); ?????????path
                // 2.media.getCutPath();????????????path????????????media.isCut();?????????true
                // 3.media.getCompressPath();????????????path????????????media.isCompressed();?????????true
                // ????????????????????????????????????????????????????????????????????????????????????
                //adapter.setList(selectList);
                //adapter.notifyDataSetChanged();
            }
        }
    }

    public void setTab(int fragmentId) {
        switch (fragmentId) {
            case 0:
                txt_channel.performClick();
                break;
            case 1:
                txt_message.performClick();
                break;
            case 2:
                txt_setting.performClick();
                break;
            default: break;
        }
    }

    public void reloadFragment(int fragmentId) {
        switch (fragmentId) {
            case 0:
                break;
            case 1:
                fManager.beginTransaction().remove(announceF).commit();
                announceF = null;
                txt_message.performClick();
                break;
            case 2:
                break;
            default: break;
        }
    }
}

