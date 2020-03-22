package com.example.charitycare.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.charitycare.R;
import com.example.charitycare.data.Help;

public class HelpFragment extends Fragment {
     Help help;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_help, container, false);
        help = new Help(getActivity());

        Button btnGuide = root.findViewById(R.id.button_open_help);
        btnGuide.setOnClickListener(view -> {
            resetPrefernces();
            Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.nav_home);
        });


        return root;
    }
    public void resetPrefernces(){
         help.setIntroPaypal(false);
         help.setIntroMpesa(false);
         help.setIntroDonate(false);
    }
}