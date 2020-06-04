package com.example.charitycare.activities;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.charitycare.HomeActivity;
import com.example.charitycare.R;
import com.example.charitycare.Services.Apiclient;
import com.example.charitycare.data.Help;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import butterknife.ButterKnife;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class PaymentActivity extends AppCompatActivity {
    Intent intent;
    TextView txtUsername, txtAmount, txtphone;
    EditText edit_amountToPay;
    String amount, username, phone;
    Button btnmpesa, btnpaypal,btncopy;
     private Help help;
     Context context;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;

    private Apiclient mApiclient;
    private ProgressDialog mProgressDialog;
    private ProgressDialog loadingBar;
    String  current_User_ID;

    private ClipboardManager myClipboard;
    private ClipData myClip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        intent = getIntent();

        mAuth= FirebaseAuth.getInstance();
        current_User_ID = mAuth.getCurrentUser().getUid();
       // UsersRef = FirebaseDatabase.getInstance().getReference().child("Payment");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("payment").child(current_User_ID);



        help = new Help(this);
        amount = intent.getStringExtra("amount");
        username = intent.getStringExtra("username");
        phone = intent.getStringExtra("phone");
        txtUsername = findViewById(R.id.username);
        txtAmount = findViewById(R.id.amount);
        txtphone = findViewById(R.id.phone);
        edit_amountToPay = findViewById(R.id.edit_amount);
        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);


        txtUsername.setText(username);
        txtAmount.setText(amount);
        txtphone.setText(phone);


        btnmpesa = findViewById(R.id.btn_Mpesa);
        btnpaypal = findViewById(R.id.btn_Paypal);
        btncopy = findViewById(R.id.btn_copy);

        loadingBar = new ProgressDialog(this);

        btncopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String text = txtphone.getText().toString();
                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(), "Text Copied",
                        Toast.LENGTH_SHORT).show();

            }
        });

        btnmpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SaveAmountPaidInformation();

            }
        });
         /*
*/
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

    private void SaveAmountPaidInformation()
    {
            String AmountPayable = edit_amountToPay.getText().toString().trim();
            String Username = txtUsername.getText().toString().trim();
            String Phonenumber = txtphone.getText().toString().trim();


            if (TextUtils.isEmpty(AmountPayable))
            {
            Toast.makeText(this, "Please Enter the amount You wish to contribute", Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(Username))
            {
                Toast.makeText(this, "No username", Toast.LENGTH_SHORT).show();
            }

            else if (TextUtils.isEmpty(Phonenumber))
            {
                Toast.makeText(this, "No phone number", Toast.LENGTH_SHORT).show();

            }
            else
            {

               loadingBar.setTitle("Saving");
               loadingBar.setMessage("Please wait...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                HashMap userMap = new HashMap();
                userMap.put("AmountPaid", AmountPayable);
                userMap.put("UsernamePaidFor", Username);
                userMap.put("PhoneNumberPaidFor", Phonenumber);

                UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if (task.isSuccessful())
                        {
                            //SendUserToHomeActivity();
                            Toast.makeText(PaymentActivity.this, "Click Again to Confirm Payment", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                        else
                        {
                            String message = task.getException().getMessage();
                            Toast.makeText(PaymentActivity.this, "Error Occured" + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }

                    }
                });



            }

        btnmpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent simtoolKitLaunchTent  = getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.android.stk");

                if(simtoolKitLaunchTent!=null)
                {
                    startActivity(simtoolKitLaunchTent);
                }
                else{
                    Toast.makeText(PaymentActivity.this,"No application to support this action",Toast.LENGTH_LONG).show();
                }
            }
        });

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