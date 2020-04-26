package com.michaelzap94.santabiblia.fragments.dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.michaelzap94.santabiblia.R;

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

        name = view.findViewById(R.id.dash_creator_fragment_name_edittext);
        color = view.findViewById(R.id.dash_creator_fragment_color);
        createButton = view.findViewById(R.id.dash_creator_fragment_button);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().equalsIgnoreCase("")) {
                    name.setError("This field can not be blank");
                }
                if (color.getText().toString().trim().equalsIgnoreCase("")) {
                    color.setError("This field can not be blank");
                }

            }
        });

        return view;
    }

}
