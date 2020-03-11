package com.example.charitycare.Admin;

import android.app.ProgressDialog;
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

import com.example.charitycare.R;
import com.example.charitycare.activities.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminLOginActivity extends AppCompatActivity {

    private Button loginbtn;
    private TextView NotAdmin;
    private FirebaseAuth mAuth;
    private EditText email, password;
    private ProgressDialog loadingBar;
   // private FirebaseDatabase userRef;
     private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        NotAdmin = findViewById(R.id.txtAdmin);


        mAuth = FirebaseAuth.getInstance();
        loginbtn = findViewById(R.id.loginbutton);
        email = findViewById(R.id.editemail);
        password = findViewById(R.id.editpassword);
        NotAdmin = findViewById(R.id.txtAdmin);
        loadingBar = new ProgressDialog(this);

          userRef = FirebaseDatabase.getInstance().getReference().child("Admins");


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

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            SendUserToAdminHome();
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

                                {
                                    SendUserToAdminHome();

                                    Toast.makeText(AdminLOginActivity.this, "you are logged in successfully", Toast.LENGTH_SHORT).show();

                                }
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(AdminLOginActivity.this, "Error occured" + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

        }
    }



       private void SendUserToAdminHome()
    {
        Intent homeintent = new Intent(AdminLOginActivity.this, AdminHomeActivity.class);
        homeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeintent);
    }


    public void loginActivity(View view)
    {
        startActivity(new Intent(view.getContext(), LoginActivity.class));

    }
}

