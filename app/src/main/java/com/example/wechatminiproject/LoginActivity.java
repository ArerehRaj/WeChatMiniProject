package com.example.wechatminiproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText editTextUsername;
    EditText editTextPassword;
    ProgressBar progressBar;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUserNameLogin);
        editTextPassword = findViewById(R.id.editTextUserPasswordLogin);

        progressBar = findViewById(R.id.progressBar2);

        sharedPreferences = this.getSharedPreferences("com.example.wechatminiproject", Context.MODE_PRIVATE);

        Objects.requireNonNull(getSupportActionBar()).hide();

    }

    public void login(View view)
    {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        if(username.isEmpty())
        {
            editTextUsername.setError("Please Enter Your Username.");
            return;
        }

        if(password.isEmpty())
        {
            editTextPassword.setError("Please Enter Your Password.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e == null)
                {
                    showAlert("Login Successful", "Welcome, " + username + "!", false);
                }
                else
                {
                    ParseUser.logOut();
                    showAlert("Login Fail", e.getMessage() + " Please try again", true);
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showAlert(String title, String message, boolean error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                    if (!error) {

                        boolean isNewUser = sharedPreferences.getBoolean("isNewUser",false);
                        Intent intent = null;

                        if(isNewUser)
                        {
                            intent = new Intent(LoginActivity.this,AddInfoActivity.class);
                            intent.putExtra("username",editTextUsername.getText().toString());
                            String email = getIntent().getStringExtra("userEmail");
                            intent.putExtra("userEmail",email);
                        }
                        else
                        {
                            intent = new Intent(LoginActivity.this,HomeActivity.class);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
}