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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DonorSetupActivity extends AppCompatActivity
{

    private EditText firstname,othernames,phonenumber;
    private Button   SaveInformation;
    private String userType;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;

    private ProgressDialog loadingBar;

    String  current_User_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_setup);

        userType = UserType.getUserType(this);
        Toast.makeText(this, userType, Toast.LENGTH_SHORT).show();


        mAuth= FirebaseAuth.getInstance();
        current_User_ID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Donor").child(current_User_ID);

        firstname = findViewById(R.id.editfirstname);
        othernames = findViewById(R.id.editothernames);
        phonenumber = findViewById(R.id.editphonenumber);
        SaveInformation = findViewById(R.id.save_button);

        loadingBar = new ProgressDialog(this);


        SaveInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SaveDonorInformation();
            }
        });






    }

    private void SaveDonorInformation()
    {
        String FirstName = firstname.getText().toString().trim();
        String OtherNames = othernames.getText().toString().trim();
        String PhoneNumber = phonenumber.getText().toString().trim();



        if (TextUtils.isEmpty(FirstName))
        {
            Toast.makeText(this, "Please provide your first name...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(OtherNames))
        {
            Toast.makeText(this, "Please  provide your other names", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(PhoneNumber))
        {
            Toast.makeText(this, "Please provide your Phone Number", Toast.LENGTH_SHORT).show();

        }
        else
        {

            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait, while we are finalizing your setup account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();

            userMap.put("Firstname", FirstName);
            userMap.put("Othernames", OtherNames);
            userMap.put("Phonenumber", PhoneNumber);
            userMap.put("Donor", userType);

            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {
                        SendUserToHomeActivity();
                        Toast.makeText(DonorSetupActivity.this, "Your account is created successfully", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(DonorSetupActivity.this, "Error Occured" + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                }
            });



        }

    }

    private void SendUserToHomeActivity()
    {
        Intent homeintent = new Intent(DonorSetupActivity.this, HomeActivity.class);
        homeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeintent);

    }
}
