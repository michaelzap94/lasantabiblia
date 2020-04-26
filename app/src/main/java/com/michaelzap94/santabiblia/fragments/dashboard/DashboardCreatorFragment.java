package com.michaelzap94.santabiblia.fragments.dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.michaelzap94.santabiblia.Dashboard;
import com.michaelzap94.santabiblia.DatabaseHelper.BibleDBHelper;
import com.michaelzap94.santabiblia.DatabaseHelper.ContentDBHelper;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.Settings;
import com.michaelzap94.santabiblia.models.Label;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardCreatorFragment extends Fragment {

    private EditText name;
    private EditText color;
    private Button createButton;

    public DashboardCreatorFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.dashboard_creator_fragment, container, false);
        //============================================================================================
        boolean canGoBack = getActivity().getSupportFragmentManager().getBackStackEntryCount()>0;
        Dashboard.updateCanGoBack(canGoBack, (AppCompatActivity)getActivity());
        //============================================================================================

        name = view.findViewById(R.id.dash_creator_fragment_name_edittext);
        color = view.findViewById(R.id.dash_creator_fragment_color);
        createButton = view.findViewById(R.id.dash_creator_fragment_button);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error = false;
                if (name.getText().toString().trim().equalsIgnoreCase("")) {
                    name.setError("This field can not be blank");
                    error = true;
                }
                if (color.getText().toString().trim().equalsIgnoreCase("")) {
                    color.setError("This field can not be blank");
                    error = true;
                }
                if(!error){
                    String nameValue = name.getText().toString();
                    String colorValue = color.getText().toString();
                    Long idReturned = ContentDBHelper.getInstance(getActivity()).createLabel(nameValue, colorValue);
                    if(idReturned != -1){
                        Toast.makeText(getActivity(), "Insert success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Insert failure", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        return view;
    }

}
