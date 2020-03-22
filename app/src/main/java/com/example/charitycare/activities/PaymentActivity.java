package com.example.charitycare.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.charitycare.HomeActivity;
import com.example.charitycare.R;
import com.example.charitycare.Services.Apiclient;
import com.example.charitycare.data.Help;

import butterknife.ButterKnife;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class PaymentActivity extends AppCompatActivity {
    Intent intent;
    TextView txtUsername, txtAmount, txtphone;
    String amount, username, phone;
    Button btnmpesa, btnpaypal;
     private Help help;

    private Apiclient mApiclient;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        intent = getIntent();

        help = new Help(this);
        amount = intent.getStringExtra("amount");
        username = intent.getStringExtra("username");
        phone = intent.getStringExtra("phone");
        txtUsername = findViewById(R.id.username);
        txtAmount = findViewById(R.id.amount);
        txtphone = findViewById(R.id.phone);


        txtUsername.setText(username);
        txtAmount.setText(amount);
        txtphone.setText(phone);


        btnmpesa = findViewById(R.id.btn_Mpesa);
        btnpaypal = findViewById(R.id.btn_Paypal);

        if(!help.getIntroMpesa()) {
            MaterialTapTargetPrompt.Builder mttp = help.showPrompt(this, R.id.btn_Mpesa, "M-Pesa", "Tap here to pay with M-pesa");
            mttp.setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                @Override
                public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state) {

                    if (state == MaterialTapTargetPrompt.STATE_DISMISSED
                            || state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                        help.setIntroMpesa(true);
                        showIntroPaypal();

                    }
                }
            });
        }
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        mApiclient = new Apiclient();
        mApiclient.setIsDebug(true);

        getAccessToken();


    }

    public void showIntroPaypal(){
        if(!help.getIntroPaypal()) {
            MaterialTapTargetPrompt.Builder mttp = Help.showPrompt(PaymentActivity.this, R.id.btn_Paypal, "Paypal", "Tap here to pay with Paypal");
            mttp.setPromptStateChangeListener((prompt, state) -> {
                if (state == MaterialTapTargetPrompt.STATE_DISMISSED
                        || state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                    help.setIntroPaypal(true);
                    startActivity(new Intent(PaymentActivity.this, HomeActivity.class));

                }

            });
        }

    }

    private void getAccessToken() {

    }
}