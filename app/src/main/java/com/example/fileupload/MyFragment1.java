package com.example.fileupload;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fileupload.utils.OkHttpClientManager;
import com.example.fileupload.utils.Upload_DownLoadFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.Response;

public class MyFragment1 extends Fragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button upload = Objects.requireNonNull(getActivity()).findViewById(R.id.upload);
        Button download = getActivity().findViewById(R.id.download);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        new Upload_DownLoadFile(getContext()).upload();
                    }
                }.start();
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Upload_DownLoadFile(getContext()).downloadFile();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout1, container, false);
        return view;

    }

//    private void uploadImageFile() {
//        final String uploadUrl = "http://192.168.1.103:8080/ssmchapter16/fileUpload";
//        final String downloadUrl = "http://192.168.1.103:8080/ssmchapter16/download";
//        File file = new File(Environment.getExternalStorageDirectory(), "IMG_0867.JPG");
//        try {
//            Response response = OkHttpClientManager.post(uploadUrl, file, "uploadfile");
//            if (response.isSuccessful()) {
//                Toast.makeText(getActivity(), "文件上传成功!", Toast.LENGTH_SHORT).show();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
