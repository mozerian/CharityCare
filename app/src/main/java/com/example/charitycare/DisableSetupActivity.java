package com.example.charitycare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;;

    public class DisableSetupActivity extends AppCompatActivity /*implements RadioGroup.OnCheckedChangeListener*/{
        private EditText fullnames, phonenumber, nextofkin, relationship, amount;
        private RadioGroup gender, status, course, help;
    private RadioButton male, female, othersex, single, married, accident, birth, illness, medical, training, specify;

    private Button medicalButton,certificateBtton;
    final static int medicalcert = 1;
        final static int birthcert = 1;

    private Button SaveInformation;
    private String userType;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private StorageReference provestorage;

    private ProgressDialog loadingBar;

    String currentUserID;
        String userGender = "";
        String majorCourse = "";
        String kindOfHelp = "";
        String maritaStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_disable);

        userType = UserType.getUserType(this);
        Toast.makeText(this, userType, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Disabled").child(currentUserID);
        provestorage = FirebaseStorage.getInstance().getReference().child("SupportingDocuments");
        fullnames = findViewById(R.id.edit_fullnames);
        phonenumber = findViewById(R.id.edit_phonenumber);
        nextofkin = findViewById(R.id.edit_nextofKin);
        relationship = findViewById(R.id.edit_relationshipNextKin);
        amount = findViewById(R.id.edit_amount);

        gender = findViewById(R.id.group_gender);
        status = findViewById(R.id.group_status);
        course = findViewById(R.id.major_course);
        help = findViewById(R.id.radio_help);

        medicalButton = findViewById(R.id.medical_button);
        certificateBtton = findViewById(R.id.certificate_button);

        SaveInformation = findViewById(R.id.save_button);

        loadingBar = new ProgressDialog(this);


        medicalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,medicalcert);

            }
        });
        certificateBtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,birthcert);

            }
        });





        SaveInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveAccountInformation();
            }
        });


    }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
        {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == medicalcert && resultCode == RESULT_OK && data !=null)
            {
                Uri imageUri = data.getData();
                if (requestCode == RESULT_OK)
                {
                    //Uri resultUri = resu
                }
            }
            if (requestCode == birthcert && resultCode == RESULT_OK && data !=null)
            {
                Uri image2Uri = data.getData();

            }
            StorageReference filepath = provestorage.child(currentUserID + ".jpg");
           // filepath.putFile()

        }




        public void MaritalStatusButtonClicked(View view)
  {


      boolean checked = ((RadioButton) view).isChecked();

      switch (view.getId())
      {
          case R.id.radio_single:
          if (checked)
              maritaStatus ="Single";
          break;
          case R.id.radio_married:
              if (checked)
                  maritaStatus ="Married";
              break;

      }
  }
        public void GenderButtonClicked(View view)
        {


            boolean checked = ((RadioButton) view).isChecked();

            switch (view.getId())
            {
                case R.id.radio_female:
                    if (checked)
                        userGender ="female";
                    break;
                case R.id.radio_male:
                    if (checked)
                        userGender ="male";
                    break;
                case R.id.radio_other:
                    if (checked)
                        userGender ="other";
                    break;

            }
        }

        public void  MajorCourseButtonClicked(View view)
  {

      boolean checked = ((RadioButton) view).isChecked();

      switch (view.getId())
      {
          case R.id.radio_accident:
              if (checked)
                  majorCourse ="Accident";
              break;
          case R.id.radio_birth:
              if (checked)
                  majorCourse ="Birth";
              break;
          case R.id.radio_illness:
              if (checked)
                  majorCourse ="Illness";
              break;
      }

  }
        public void kindofHelpButtonClicked(View view)
        {

            boolean checked = ((RadioButton) view).isChecked();

            switch (view.getId())
            {
                case R.id.radio_medical:
                    if (checked)
                        kindOfHelp ="Medical";
                    break;
                case R.id.radio_male:
                    if (checked)
                        kindOfHelp ="Training";
                    break;
                case R.id.radio_other:
                    if (checked)
                        kindOfHelp ="Other";
                    break;
            }
        }
    private void SaveAccountInformation() {
        String FullNames = fullnames.getText().toString().trim();
        String PhoneNumber = phonenumber.getText().toString().trim();
        String NextofKin = nextofkin.getText().toString().trim();
        String Relationship = relationship.getText().toString().trim();
        String Amount = amount.getText().toString().trim();

        if (TextUtils.isEmpty(FullNames)) {
            Toast.makeText(this, "Please Provide Your Full Names...", Toast.LENGTH_SHORT).show();

        }
        if (TextUtils.isEmpty(PhoneNumber)) {
            Toast.makeText(this, "Please Provide Your Phone number...", Toast.LENGTH_SHORT).show();

        }
        if (TextUtils.isEmpty(NextofKin)) {
            Toast.makeText(this, "Please Provide the Name of your Next of Kin...", Toast.LENGTH_SHORT).show();

        }
        if (TextUtils.isEmpty(Relationship)) {
            Toast.makeText(this, "please provide the relationship status with your next of kin...", Toast.LENGTH_SHORT).show();

        }
        if (gender.getCheckedRadioButtonId() == -1)
        {
            Toast.makeText(this, "Please select Sex...", Toast.LENGTH_SHORT).show();
        }
        if (status.getCheckedRadioButtonId() == -1)
        {
            Toast.makeText(this, "Please select Marital status...", Toast.LENGTH_SHORT).show();
        }
        if (course.getCheckedRadioButtonId() == -1)
        {
            Toast.makeText(this, "Please select Your course of disability...", Toast.LENGTH_SHORT).show();
        }
        if (help.getCheckedRadioButtonId() == -1)
        {
            Toast.makeText(this, "Please select kind of help you need...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(Amount)) {
            Toast.makeText(this, "Please provide the amount in need of...", Toast.LENGTH_SHORT).show();

        }


        else {
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait, while we are finalizing your setup account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            final HashMap userMap = new HashMap();

            userMap.put("fullnames", FullNames);
            userMap.put("phonenumber", PhoneNumber);
            userMap.put("NextOfKin", NextofKin);
            userMap.put("Relationship", Relationship);
            userMap.put("amount", Amount);
            userMap.put("Disable", userType);
            userMap.put("Gender",userGender);
            userMap.put("MaritalStatus",maritaStatus);
            userMap.put("Course",majorCourse);
            userMap.put("Help",kindOfHelp);

            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        SendUserToDisableHomeActivity();
                        Toast.makeText(DisableSetupActivity.this, "You have successfuly send your data...", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(DisableSetupActivity.this, "Error ocurred:" + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }


                }
            });

        }

    }
    private void SendUserToDisableHomeActivity() {
        Intent disablehomeintent = new Intent(DisableSetupActivity.this, DisableHomeActivity.class);
        disablehomeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(disablehomeintent);

    }
}
