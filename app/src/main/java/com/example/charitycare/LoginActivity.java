package com.example.charitycare;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button loginbtn;
    private EditText email,password;

    private ProgressDialog loadingBar;
    private String userType;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();
        loginbtn = findViewById(R.id.loginbutton);
        email = findViewById(R.id.editemail);
        password = findViewById(R.id.editpassword);
        loadingBar= new  ProgressDialog(this);

        userType = UserType.getUserType(this);


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllowingUserToLogin();
            }
        });

    }
//if the user is arleady logged in no need to provide his email and password
    @Override
    protected void onStart() {
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
//if the user is arleady logged in no need to provide his email and password

    private void AllowingUserToLogin()
    {
        String mail = email.getText().toString();
        String pass = password.getText().toString();

        if(TextUtils.isEmpty(mail))
        {

                Toast.makeText(this, "Please Enter your email", Toast.LENGTH_SHORT).show();


        }
        else if(TextUtils.isEmpty(pass))
        {
            Toast.makeText(this, "Please write your password", Toast.LENGTH_SHORT).show();

        }
        else
        {
            loadingBar.setTitle("Login");
            loadingBar.setMessage("Please wait, while we are allowing you to log in");
            loadingBar.show();



            mAuth.signInWithEmailAndPassword(mail,pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful()){

                                if(userType.equals("donor"))
                                {
                                    SendUserToHomeActivity();

                                    Toast.makeText(LoginActivity.this, "you are logged in successfully", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    SendUserToDisableHomeActivity();
                                    Toast.makeText(LoginActivity.this, "you are logged in successfully", Toast.LENGTH_SHORT).show();
                                }


                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error occured" + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

        }
    }

    private void SendUserToDisableHomeActivity()
    {
       Intent disablehome = new Intent(LoginActivity.this, DisableHomeActivity.class);
       disablehome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
       startActivity(disablehome);
    }

    private void SendUserToHomeActivity()
    {
        Intent homeintent = new Intent(LoginActivity.this, HomeActivity.class);
        homeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeintent);
    }
}
