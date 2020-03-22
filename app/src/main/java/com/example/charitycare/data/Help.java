package com.example.charitycare.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static com.example.charitycare.data.Constants.DONATE;
import static com.example.charitycare.data.Constants.FIRST_RUN;
import static com.example.charitycare.data.Constants.HELP;
import static com.example.charitycare.data.Constants.MPESA;
import static com.example.charitycare.data.Constants.PAYPAL;

public class Help
{

    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    public Help(Context context)
    {
        preferences = context.getSharedPreferences(HELP ,Context.MODE_PRIVATE );
        editor = preferences.edit();

    }
    public  static MaterialTapTargetPrompt.Builder showPrompt (Activity activity, int id, String title, String subtitle)
    {
        MaterialTapTargetPrompt.Builder mttp = new MaterialTapTargetPrompt.Builder(activity);
        mttp.setTarget(id)
                .setPrimaryText(title)
                .setSecondaryText(subtitle)
                .setBackButtonDismissEnabled(true)
                .show();
        return  mttp;
    }

    public void setFirstRun(){
        if (!getFirstRun()){
            editor.putBoolean(FIRST_RUN, true);
            editor.putBoolean(DONATE, false);
            editor.putBoolean(MPESA, false);
            editor.putBoolean(PAYPAL, false);
            editor.apply();
        }
    }

    public boolean getFirstRun(){
        return preferences.getBoolean(FIRST_RUN,false);

    }
    public  void setIntroDonate(boolean state){
        editor.putBoolean(DONATE, state);
        editor.apply();
    }

    public  void setIntroMpesa(boolean state){
        editor.putBoolean(MPESA, state);
        editor.apply();
    }

    public  void setIntroPaypal(boolean state){
        editor.putBoolean(PAYPAL, state);
        editor.apply();
    }
    public boolean getIntroDonate (){
        return preferences.getBoolean(DONATE, true);
    }

    public boolean getIntroMpesa (){
        return preferences.getBoolean(MPESA, true);
    }

    public boolean getIntroPaypal(){
        return preferences.getBoolean(PAYPAL, true);
    }

}
