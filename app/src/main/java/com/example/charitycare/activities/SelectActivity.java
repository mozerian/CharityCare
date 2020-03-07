package com.example.charitycare.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.charitycare.R;
import com.google.android.material.snackbar.Snackbar;

public class SelectActivity extends AppCompatActivity
{
      private RadioButton radioDonor, radionDisable;
      private SharedPreferences preferences;
      private SharedPreferences.Editor editor;
      private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);


        preferences = this.getSharedPreferences("UserType", Context.MODE_PRIVATE);
        editor = preferences.edit();

        radionDisable = findViewById(R.id.radio_disabled);
        radioDonor = findViewById(R.id.radio_donor);
    }

    public void SendUserToRegister(View view)
    {
        Intent setupActivity = new Intent(SelectActivity.this, RegisterActivity.class);
        if(radionDisable.isChecked()){
            userType = "disable";
            startActivity(setupActivity);
        }else if(radioDonor.isChecked()){
            userType = "donor";
            startActivity(setupActivity);
        } else
        {
            Snackbar.make(view, "Please Select Type of user first", Snackbar.LENGTH_LONG).show();
        }
        editor.putString("USERTYPE", userType);
        editor.apply();

    }

}
