package com.ashvanee.notificationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String TAG = "Ashvanee";
    String URL_LOGIN_REGISTRATION = "http://192.168.1.34/Notification/signin_signup.php";

    static String SHARED_PREF_NAME = "net.softglobe.fcmphpmysql";

    EditText emailtxt, passwordtxt;
    ProgressBar progressBar;
    String token,email,password;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailtxt = findViewById(R.id.tv_email);
        passwordtxt = findViewById(R.id.password);
        progressBar = findViewById(R.id.progress);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                         token = task.getResult();
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        button = findViewById(R.id.submitbtn);
        button.setOnClickListener(view -> {
            email = emailtxt.getText().toString();
            password = passwordtxt.getText().toString();
            if (TextUtils.isEmpty(email)){
                Toast.makeText(MainActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)){
                Toast.makeText(MainActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            } else {
                LoginRegister(token,email,password);
            }
        });

       /* //Creating notification channel for devices on and above Android O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            channel.enableLights(true);
            channel.enableVibration(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }*/
    }

    private void LoginRegister(String token, String email, String password) {

        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN_REGISTRATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.v(TAG,response);
                            JSONObject jsonObject = new JSONObject(response);
                            progressBar.setVisibility(View.GONE);

                            if (jsonObject.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                SharedPreferences preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("emailkey", email);
                                editor.putBoolean("loggedIn", true);
                                editor.apply();

                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
//                            e.printStackTrace();
                            Log.v(TAG,"1 Registration Error!  "+ e.toString());
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                },
                error -> {
                    Log.v(TAG,"2 Registration Error!  "+error);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Time out please try again ",Toast.LENGTH_LONG).show();
//                        MainActivity.preferenceConfig.displayToast("Time out please try again ");
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("token", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("loggedIn",false);
        if (isLoggedIn){
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}