package com.fdurainbow.rainbow_egg_client.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fdurainbow.rainbow_egg_client.Activity.LoginActivity;
import com.fdurainbow.rainbow_egg_client.Activity.MainActivity;
import com.fdurainbow.rainbow_egg_client.Bean.SubmitMoment;
import com.fdurainbow.rainbow_egg_client.NineGrid.FullyGridLayoutManager;
import com.fdurainbow.rainbow_egg_client.NineGrid.GridImageAdapter;
import com.fdurainbow.rainbow_egg_client.R;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.Permission;
import com.luck.picture.lib.permissions.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AnnounceFragment extends Fragment {


    public AnnounceFragment() {
        // Required empty public constructor
    }

    private int maxSelectNum = 9;
    private List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter;
    private RecyclerView mRecyclerView;
    private PopupWindow pop;
    private EditText ed_content;
    private SubmitMoment sub = new SubmitMoment();
    private Button btn_submit;
    private String dynamicID;


    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.fragment_announce,null);
        mRecyclerView = view1.findViewById(R.id.recycler);
        ed_content = view1.findViewById(R.id.content_et);
        btn_submit = view1.findViewById(R.id.send_btn);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this,selectList.get(0).getPath(),Toast.LENGTH_SHORT).show();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                String DateTime = simpleDateFormat.format(date);
                sub.setTime(DateTime);
                sub.setMom_content(ed_content.getText().toString());
                sub.setHostID(Integer.toString(LoginActivity.hostID));
                String jsonstr = new Gson().toJson(sub);
                System.out.println(jsonstr);
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonstr);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://10.176.50.27:8080/momentadd")
                        .post(body)//
                        .build();
                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onResponse(Call arg0, Response arg1) throws IOException {
                        // TODO Auto-generated method stub
                        dynamicID = arg1.body().string();

                        for(LocalMedia media : selectList){
                            OkHttpClient client = new OkHttpClient();
                            String path = media.getPath();
                            File file = new File(path);Log.v("Image",path);
                            MultipartBody body = new MultipartBody.Builder()
                                    .addFormDataPart("dynamicID", dynamicID)
                                    .addFormDataPart("file","img01.png",RequestBody.create(MediaType.parse("image/png"),file))
                                    .addFormDataPart("ss","",RequestBody.create(MediaType.parse("image/png"),file))
                                    .build();
                            Request request = new Request.Builder()
                                    .url("http://10.176.50.27:8080/uploadimage")
                                    .post(body)
                                    .build();
                            client.newCall(request).enqueue(new Callback() {

                                @Override
                                public void onResponse(Call arg0, Response arg1) throws IOException {
                                    // TODO Auto-generated method stub
                                    Handler handler=new Handler(Looper.getMainLooper());
                                    handler.post(new Runnable(){
                                        public void run(){
                                            Toast.makeText(getActivity(),"?????????????????????",Toast.LENGTH_SHORT).show();
                                            MainActivity mActivity = (MainActivity) getActivity();
                                            mActivity.reloadFragment(1);
                                            mActivity.setTab(0);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call arg0, IOException arg1) {
                                    // TODO Auto-generated method stub

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call arg0, IOException arg1) {
                        // TODO Auto-generated method stub
                        Log.v("receiveM","hhh");
                    }
                });
            }
        });

        initWidget();
        return view1;
    }

    private void initWidget() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(getActivity(), onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // ???????????? ???????????????????????????
                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(getActivity()).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // ????????????
                            PictureSelector.create(getActivity()).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // ????????????
                            PictureSelector.create(getActivity()).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {

        @SuppressLint("CheckResult")
        @Override
        public void onAddPicClick() {
            //??????????????????
            RxPermissions rxPermission = new RxPermissions(getActivity());
            rxPermission.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Permission>() {
                        @Override
                        public void accept(Permission permission) {
                            if (permission.granted) {// ???????????????????????????
                                //??????????????????????????????????????????dialog
                                showPop();

                                //????????????????????????????????????????????? ????????????????????????
//                                showAlbum();
                            } else {
                                Toast.makeText(getActivity(), "??????", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    };

    private void showAlbum() {
        //?????????????????????????????????
        PictureSelector.create(getActivity())
                .openGallery(PictureMimeType.ofImage())// ??????.PictureMimeType.ofAll()?????????.ofImage()?????????.ofVideo()?????????.ofAudio()
                .maxSelectNum(maxSelectNum)// ????????????????????????
                .minSelectNum(1)// ??????????????????
                .imageSpanCount(4)// ??????????????????
                .selectionMode(PictureConfig.MULTIPLE)// ?????? or ??????PictureConfig.MULTIPLE : PictureConfig.SINGLE
                .previewImage(true)// ?????????????????????
                .isCamera(true)// ????????????????????????
                .isZoomAnim(true)// ?????????????????? ???????????? ??????true
                //.setOutputCameraPath("/CustomPath")// ???????????????????????????
                .enableCrop(true)// ????????????
                .compress(true)// ????????????
                //.sizeMultiplier(0.5f)// glide ?????????????????? 0~1?????? ????????? .glideOverride()??????
                .glideOverride(160, 160)// glide ???????????????????????????????????????????????????????????????????????????????????????
                .withAspectRatio(1, 1)// ???????????? ???16:9 3:2 3:4 1:1 ????????????
                //.selectionMedia(selectList)// ????????????????????????
                //.previewEggs(false)// ??????????????? ????????????????????????????????????(???????????????????????????????????????????????????)
                //.cropCompressQuality(90)// ?????????????????? ??????100
                //.compressMaxKB()//???????????????kb compressGrade()???Luban.CUSTOM_GEAR??????
                //.compressWH() // ??????????????? compressGrade()???Luban.CUSTOM_GEAR??????
                //.cropWH()// ???????????????????????????????????????????????????????????????
                .rotateEnabled(false) // ???????????????????????????
                .scaleEnabled(true)// ?????????????????????????????????
                //.recordVideoSecond()//?????????????????? ??????60s
                .forResult(PictureConfig.CHOOSE_REQUEST);//????????????onActivityResult code
    }

    private void showPop() {
        View bottomView = View.inflate(getActivity(), R.layout.layout_bottom_dialog, null);
        TextView mAlbum = bottomView.findViewById(R.id.tv_album);
        TextView mCamera = bottomView.findViewById(R.id.tv_camera);
        TextView mCancel = bottomView.findViewById(R.id.tv_cancel);

        pop = new PopupWindow(bottomView, -1, -2);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.5f;
        getActivity().getWindow().setAttributes(lp);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp =getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
        pop.setAnimationStyle(R.style.main_menu_photo_anim);
        pop.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_album:
                        //??????
                        PictureSelector.create(getActivity())
                                .openGallery(PictureMimeType.ofImage())
                                .maxSelectNum(maxSelectNum)
                                .minSelectNum(1)
                                .imageSpanCount(4)
                                .selectionMode(PictureConfig.MULTIPLE)
                                .forResult(PictureConfig.CHOOSE_REQUEST);
                        break;
                    case R.id.tv_camera:
                        //??????
                        PictureSelector.create(getActivity())
                                .openCamera(PictureMimeType.ofImage())
                                .forResult(PictureConfig.CHOOSE_REQUEST);
                        break;
                    case R.id.tv_cancel:
                        //??????
                        //closePopupWindow();
                        break;
                }
                closePopupWindow();
            }
        };

        mAlbum.setOnClickListener(clickListener);
        mCamera.setOnClickListener(clickListener);
        mCancel.setOnClickListener(clickListener);
    }

    public void closePopupWindow() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            pop = null;
        }
    }


    public void show(List<LocalMedia> receive ) {

                selectList.addAll(receive);
                //Log.v("ImageInfo",images.toString());

                //selectList = PictureSelector.obtainMultipleResult(data);

                // ?????? LocalMedia ??????????????????path
                // 1.media.getPath(); ?????????path
                // 2.media.getCutPath();????????????path????????????media.isCut();?????????true
                // 3.media.getCompressPath();????????????path????????????media.isCompressed();?????????true
                // ????????????????????????????????????????????????????????????????????????????????????
                adapter.setList(selectList);
                adapter.notifyDataSetChanged();
            }
}
