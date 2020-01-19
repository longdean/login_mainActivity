package com.example.fileupload;
//import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fileupload.utils.OkHttpClientManager;
import com.example.fileupload.utils.Upload_DownLoadFile;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private static final String PATH = "http://192.168.1.103:8080/ssmchapter15/login01";
    //    private static final String PATH2 = "http://192.168.1.103:8080/ssmchapter15/login02";
    private EditText etName, etPassword;
    private Button login;
    private TextView tvMsg;
    final private static int SUSSESS = 1;
    final private static int FAILURE = 2;
    final private static int ERROR = 3;
    RequestQueue requestQueue;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUSSESS:
                    tvMsg.setText("登录成功!");
                    break;
                case FAILURE:
                    tvMsg.setText("登录异常...");
                    break;
                case ERROR:
                    tvMsg.setText("用户名或者密码不正确...");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        login = findViewById(R.id.login);
        tvMsg = findViewById(R.id.tvMsg);
        requestQueue = Volley.newRequestQueue(this);

    }
    private void post(final Map<String, String> params) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, PATH,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("TAG", response);
                            handler.sendEmptyMessage(SUSSESS);
                            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("TAG", error.getMessage(), error);
                            handler.sendEmptyMessage(ERROR);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            handler.sendEmptyMessage(FAILURE);
            e.printStackTrace();
        }
    }

    private void get(final Map<String, String> params) {
        StringBuilder sb = new StringBuilder(PATH);
        try {
            if (!params.isEmpty()) {
                sb.append("?");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    sb.append(entry.getKey() + "=");
                    sb.append(entry.getValue() + "&");
                }
                sb.delete(sb.length() - 1, sb.length());
                Log.i("TEST", sb.toString());
            }

            StringRequest stringRequest = new StringRequest(sb.toString(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("TAG", response);
                            handler.sendEmptyMessage(SUSSESS);
                            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("TAG", error.getMessage(), error);
                            handler.sendEmptyMessage(ERROR);
                        }
                    });
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            handler.sendEmptyMessage(FAILURE);
            e.printStackTrace();
        }
    }

    public void login(View view) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("username", etName.getText().toString());
        params.put("password", etPassword.getText().toString());
        new Thread() {
            @Override
            public void run() {
//                get(params);
                post(params);
            }
        }.start();
    }

    public void upload(View view) {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                } else {
                    //这里就是权限打开之后自己要操作的逻辑
                    new Thread(){
                        @Override
                        public void run() {
                            new Upload_DownLoadFile(MainActivity.this).upload();
                        }
                    }.start();
                }
            }
        }


    }
}
