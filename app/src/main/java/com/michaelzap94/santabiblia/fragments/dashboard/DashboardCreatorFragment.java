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
    private String nameVal = null;
    private String colorVal = null;
    private int idValue = -1;
    private boolean editMode = false;

    public DashboardCreatorFragment() {
        // Required empty public constructor
    }

    public DashboardCreatorFragment(String name, String color, int idValue) {
        this.nameVal = name;
        this.colorVal = color;
        this.idValue = idValue;
        this.editMode = true;
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

        if(nameVal != null && colorVal != null && editMode){
            name.setText(nameVal);
            color.setText(colorVal);
        }


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
                    boolean insertSuccess = false;
                    if(editMode && idValue > 0){
                        insertSuccess = ContentDBHelper.getInstance(getActivity()).editLabel(nameValue, colorValue, idValue);
                    } else {
                        insertSuccess = ContentDBHelper.getInstance(getActivity()).createLabel(nameValue, colorValue);
                    }

                    if(insertSuccess){
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
