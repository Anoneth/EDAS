package com.example.edas.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.edas.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    EditText editTextLogin;
    EditText editTextPassword;

    final static String SERVER_ADDRESS = "ya.ru";
    final static String SERVER_ADDRESS_AUTH = "/auth";
    final static String SERVER_ADDRESS_SAVE = "/save";
    final static String SERVER_ADDRESS_LOAD = "/load";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassword = findViewById(R.id.editTextPassword);

        Button buttonLogin = findViewById(R.id.buttonLogin);

        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isOk = true;
                if (TextUtils.isEmpty(editTextLogin.getText())) {
                    isOk = false;
                    editTextLogin.setError(getString(R.string.not_empty));
                }
                if (TextUtils.isEmpty(editTextPassword.getText())) {
                    isOk = false;
                    editTextPassword.setError(getString(R.string.not_empty));
                }
                if (isOk) {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("login", editTextLogin.getText());
                        json.put("password", editTextPassword.getText());
                        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                        String url = "http://ptsv2.com/t/0k7dp-1579713956/post";
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("onResponse", response.toString());
                                if (response.has("token")) {
                                    Intent result = new Intent();
                                    try {
                                        result.putExtra("token", response.getString("token"));
                                    } catch (Exception ex) {}
                                    setResult(RESULT_OK, result);
                                    dialog.cancel();
                                    finish();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(LoginActivity.this, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                                Log.e("onResponse", error.getMessage());
                                dialog.cancel();
                            }
                        });
                        queue.add(request);
                        dialog.setMessage(getString(R.string.wait));
                        dialog.setCancelable(false);
                        dialog.show();
                    } catch (Exception ex) {
                        Log.e("buttonLogin", ex.getMessage());
                    }
                }

            }
        });

    }

}
