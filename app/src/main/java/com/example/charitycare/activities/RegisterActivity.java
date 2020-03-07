package com.example.charitycare.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.charitycare.HomeActivity;
import com.example.charitycare.R;
import com.example.charitycare.data.UserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailAddress, pass, passwordConfirm;
    private Button RegisterButton;
   // private String email, password;

    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;

    private String userType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userType = UserType.getUserType(this);
       // Toast.makeText(this, userType, Toast.LENGTH_SHORT).show();

        mAuth= FirebaseAuth.getInstance();
        emailAddress = findViewById(R.id.email);
        pass = findViewById(R.id.editPassword);
        passwordConfirm = findViewById(R.id.editPasswordConfirm);
        RegisterButton = findViewById(R.id.register_button);

        loadingBar = new ProgressDialog(this);

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                CreateNewAccount();
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            if(userType.equals("donor"))
            {
                SendUserToHomeActivity();
            }

            else
            {
                SendUserToDisableHomeActivity();
            }

        }

    }

    private void SendUserToDisableHomeActivity()
    {
        Intent disablehome = new Intent(RegisterActivity.this, DisableHomeActivity.class);
        disablehome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(disablehome);
    }

    private void SendUserToHomeActivity()
    {
        Intent homeintent = new Intent(RegisterActivity.this, HomeActivity.class);
        homeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeintent);
    }
    private void CreateNewAccount()
    {
        String email = emailAddress.getText().toString().trim();
        String password = pass.getText().toString().trim();
        String  confirmpassword = passwordConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please write your email...", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
            
        }
        else if (TextUtils.isEmpty(confirmpassword))
        {
            Toast.makeText(this, "Please confirm your password...", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(confirmpassword))
        {
            Toast.makeText(this, "Your password do not match...", Toast.LENGTH_SHORT).show();

        }
        else
        {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we are creating your new account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(RegisterActivity.this, "You are authenticated successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                if(userType.equals("donor"))
                                {
                                    startActivity(new Intent(getApplicationContext(), DonorSetupActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)

                                    );
                                    finish();

                                }
                                else
                                {
                                    Intent disableIntent = new Intent( RegisterActivity.this, DisableSetupActivity.class);
                                    disableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(disableIntent);
                                    finish();
                                   // startActivity(new Intent(getApplicationContext(), DisableSetupActivity.class));
                                }
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "error occured.." + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    //public void registerUser(View view){
     //   email = emailAddress.getText().toString().trim();
   // }




}
