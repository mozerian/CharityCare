package com.example.charitycare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.charitycare.R;

public class PaymentActivity extends AppCompatActivity {
    Intent intent;
    TextView txtUsername, txtAmount;
    String amount, username;
    Button btnmpesa,btnpaypal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        intent = getIntent();

        amount = intent.getStringExtra("amount");
        username = intent.getStringExtra("username");
        txtUsername = findViewById(R.id.username);
        txtAmount = findViewById(R.id.amount);

        txtUsername.setText(username);
        txtAmount.setText(amount);

        btnmpesa = findViewById(R.id.btn_Mpesa);
        btnpaypal = findViewById(R.id.btn_Paypal);


    }
}
