package com.example.wechatminiproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    EditText editTextUserEmail;
    EditText editTextUserName;
    EditText editTextUserPassword;
    EditText editTextUserConfirm;

    ProgressBar progressBar;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        if(ParseUser.getCurrentUser() != null)
        {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        editTextUserName = findViewById(R.id.editTextUserName);
        editTextUserEmail = findViewById(R.id.editTextUserEmail);
        editTextUserPassword = findViewById(R.id.editTextUserPassword);
        editTextUserConfirm = findViewById(R.id.editTextUserConfirmPassword);

        progressBar = findViewById(R.id.progressBar);

        sharedPreferences = this.getSharedPreferences("com.example.wechatminiproject", Context.MODE_PRIVATE);

        ParseInstallation.getCurrentInstallation().saveInBackground();

    }

    public void goToLogin(View view)
    {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void signUp(View view)
    {
        String email = editTextUserEmail.getText().toString();
        String username = editTextUserName.getText().toString();
        String password = editTextUserPassword.getText().toString();
        String confirm = editTextUserConfirm.getText().toString();

        if(email.isEmpty())
        {
            editTextUserEmail.setError("Please Enter Your Email ID.");
            return;
        }

        if(!email.contains("@somaiya.edu"))
        {
            editTextUserEmail.setError("Please Enter Your Somaiya Email ID.");
            return;
        }

        if(username.isEmpty())
        {
            editTextUserName.setError("Please Enter Your Username.");
            return;
        }

        if(password.isEmpty())
        {
            editTextUserPassword.setError("Please Enter Your Password.");
            return;
        }

        if(confirm.isEmpty())
        {
            editTextUserConfirm.setError("Please Enter Your Confirmation Password.");
            return;
        }

        if(!password.equals(confirm))
        {
            editTextUserPassword.setError("Both Passwords Should Match.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    ParseUser.logOut();
                    progressBar.setVisibility(View.INVISIBLE);
                    sharedPreferences.edit().putBoolean("isNewUser",true).apply();
                    showAlert("Account Created Successfully!", "Please verify your email before Login", false);
                }
                else
                {
                    ParseUser.logOut();
                    showAlert("Error Account Creation failed", "Account could not be created" + " :" + e.getMessage(), true);
                }
            }
        });
    }

    private void showAlert(String title, String message, boolean error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                    if (!error) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("userEmail",editTextUserEmail.getText().toString());
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
}