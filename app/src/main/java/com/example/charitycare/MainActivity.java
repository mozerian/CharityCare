package com.example.charitycare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

 private Button JoinNowButton;
 private TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        JoinNowButton = findViewById(R.id.joinButton);
        loginText = findViewById(R.id.loginText);


        JoinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                SendUserToRegisterActivity();

            }
        });
    }

    private void SendUserToRegisterActivity()
    {
        Intent registerIntent = new Intent(MainActivity.this, SelectActivity.class);
        startActivity(registerIntent);
    }

    public void loginActivity(View view)
    {
        startActivity(new Intent(view.getContext(), LoginActivity.class));
    }




}
