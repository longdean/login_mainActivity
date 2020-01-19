package com.example.fileupload.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Environment;
import android.util.Log;
import android.util.TimeUtils;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.fileupload.Main2Activity;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class Upload_DownLoadFile {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private Context context;

    public Upload_DownLoadFile(Context context) {
        this.context = context;
    }

    private static final String TAG = "Upload_DownLoadFile";
    final private String uploadUrl = "http://192.168.1.103:8080/ssmchapter16/fileUpload";

    final private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .build();

    //文件上传
    public void upload() {
        File file = new File(Environment.getExternalStorageDirectory(), "IMG_0867.JPG");
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", "lda")
                .addFormDataPart("uploadfile", "test_image", fileBody)
                .build();


        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e(TAG, "文件上传失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonString = response.body().string();
                Log.d(TAG, " upload jsonString =" + jsonString);
                Log.i(TAG, "文件上传成功");
            }
        });
    }

    //文件下载
    public void download(final String url) {
        final long startTime = System.currentTimeMillis();
        Log.i("DOWNLOAD", "startTime=" + startTime);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                e.printStackTrace();
                Log.i("DOWNLOAD", "download failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    String mSDCardPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();//SD卡路径
                    String appPath = context.getApplicationContext().getFilesDir().getAbsolutePath();//此APP的files路径
                    File dest = new File(mSDCardPath, url.substring(url.lastIndexOf("=") + 1));
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    assert response.body() != null;
                    bufferedSink.writeAll(response.body().source());

                    bufferedSink.close();
                    Log.i("filePath", mSDCardPath + "/" + url.substring(url.lastIndexOf("=") + 1));
                    Log.i("DOWNLOAD", "download success");
                    Log.i("DOWNLOAD", "totalTime=" + (System.currentTimeMillis() - startTime));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("DOWNLOAD", "download failed");
                } finally {
                    if (bufferedSink != null) {
                        bufferedSink.close();
                    }
                }
            }
        });
    }


    public void downloadFile() {
        verifyStoragePermissions((Activity) context);
        String downloadUrl = "http://192.168.1.103:8080/ssmchapter16/download?filename=茶卡盐湖.jpg";
        downloadFile(downloadUrl);
    }

    //下载文件
    private void downloadFile(final String url) {
        new Thread() {
            public void run() {
                download(url);
            }
        }.start();
    }

    //请求权限代码
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    //测试OKhttp3的get请求代码---start
    public void testGet() {
//        String url = "http://wwww.baidu.com";
        final Request request = new Request.Builder()
                .url(uploadUrl)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response.body().string());
                Log.i(TAG, "访问成功");
            }
        });
    }
    //测试OKhttp3的get请求代码---end
}
