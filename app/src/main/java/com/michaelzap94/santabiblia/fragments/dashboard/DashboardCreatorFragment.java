package com.michaelzap94.santabiblia.fragments.dashboard;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.michaelzap94.santabiblia.Dashboard;
import com.michaelzap94.santabiblia.DatabaseHelper.ContentDBHelper;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.adapters.RecyclerView.LabelColorCreatorRecyclerView;
import com.michaelzap94.santabiblia.models.Label;
import com.michaelzap94.santabiblia.viewmodel.VersesMarkedViewModel;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardCreatorFragment extends Fragment implements LabelColorCreatorRecyclerView.ItemListener{

//    class GridAdapterColors extends BaseAdapter {
//        private static final String TAG = "GridAdapterColors";
//        private final String[] colors = {"#696969","#bada55","#7fe5f0","#ff0000","#ff80ed","#407294","#cbcba9","#ffffff","#420420","#133337","#065535","#c0c0c0","#5ac18e","#666666","#dcedc1","#000000","#f7347a","#576675","#ffc0cb","#696966","#ffe4e1","#008080","#ffd700","#e6e6fa","#ff7373","#ffa500","#00ffff","#40e0d0","#d3ffce","#b0e0e6","#f0f8ff","#0000ff","#c6e2ff","#003366","#faebd7","#fa8072","#7fffd4","#800000","#ffff00","#eeeeee","#800080","#cccccc","#ffb6c1","#00ff00","#fff68f","#f08080","#c39797","#20b2aa","#333333","#ffc3a0","#66cdaa","#4ca3dd","#ff6666","#ff7f50","#468499","#ff00ff","#f6546a","#ffdab9","#00ced1","#c0d6e4","#660066","#008000","#afeeee","#0e2f44","#990000","#088da5","#8b0000","#cbbeb5","#101010","#f5f5f5","#b6fcd5","#808080","#b4eeb4","#daa520","#6897bb","#ffff66","#f5f5dc","#000080","#dddddd","#81d8d0","#66cccc","#794044","#8a2be2","#ff4040"};
//        private Context mContext;
//        private int selected = -1;
//        private TextView color_txtview_info;
//        public GridAdapterColors(Context context, TextView color_txtview_info) {
//            this.mContext = context;
//            this.color_txtview_info = color_txtview_info;
//        }
//
//        public int getColorSelected(){
//            return selected;
//        }
//
//        @Override
//        public int getCount() {
//            return colors.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return colors[position];
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHolder viewHolder;
//            if(convertView == null) {
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.dashboard_creator_grid_color, parent, false);
//				viewHolder = new ViewHolder(convertView);
//				convertView.setTag(viewHolder);
//            } else {
//				viewHolder = (ViewHolder) convertView.getTag();
//			}
//
//            //FloatingActionButton colorButton = (FloatingActionButton) convertView.findViewById(R.id.dash_creator_grid_color_item);
//
//            viewHolder.colorButton.setTag(position);
//            viewHolder.colorButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colors[position])));
//
//            viewHolder.colorButton.setOnClickListener(v -> {
//                //int tag = (int) v.getTag();
//                Log.d(TAG, "getView: position "+position);
//                Log.d(TAG, "getView: selected "+selected);
//                Log.d(TAG, "getView: parent "+parent);
//                Log.d(TAG, "getView: "+parent.getChildAt(selected));
//                //if(tag == position) {
//                    FloatingActionButton selectedColorButton = (FloatingActionButton) v;
//
//                    if(selected != -1) {
//
//                        FloatingActionButton existingColorView = (FloatingActionButton) parent.getChildAt(selected);
//                        existingColorView.setImageResource(0);
//                        selectedColorButton.setImageResource(R.drawable.ic_check_circle);
//                        selected = position;
//                    } else {
//                        selectedColorButton.setImageResource(R.drawable.ic_check_circle);
//                        selected = position;
//                    }
//                    this.color_txtview_info.setText(" "+colors[position] + " ");
//                    this.color_txtview_info.setError(null);
//                    this.color_txtview_info.setBackgroundColor(Color.parseColor(colors[position]));
//                //}
//
//            });
//            return convertView;
//        }
//		private class ViewHolder {
//			final FloatingActionButton colorButton;
//
//			ViewHolder(View v) {
//				this.colorButton = (FloatingActionButton) v.findViewById(R.id.dash_creator_grid_color_item);
//			}
//
//		}
//    }
    private final String[] colors = {"#696969","#bada55","#7fe5f0","#ff0000","#ff80ed","#407294","#cbcba9","#ffffff","#420420","#133337","#065535","#c0c0c0","#5ac18e","#666666","#dcedc1","#000000","#f7347a","#576675","#ffc0cb","#696966","#ffe4e1","#008080","#ffd700","#e6e6fa","#ff7373","#ffa500","#00ffff","#40e0d0","#d3ffce","#b0e0e6","#f0f8ff","#0000ff","#c6e2ff","#003366","#faebd7","#fa8072","#7fffd4","#800000","#ffff00","#eeeeee","#800080","#cccccc","#ffb6c1","#00ff00","#fff68f","#f08080","#c39797","#20b2aa","#333333","#ffc3a0","#66cdaa","#4ca3dd","#ff6666","#ff7f50","#468499","#ff00ff","#f6546a","#ffdab9","#00ced1","#c0d6e4","#660066","#008000","#afeeee","#0e2f44","#990000","#088da5","#8b0000","#cbbeb5","#101010","#f5f5f5","#b6fcd5","#808080","#b4eeb4","#daa520","#6897bb","#ffff66","#f5f5dc","#000080","#dddddd","#81d8d0","#66cccc","#794044","#8a2be2","#ff4040"};
    private TextInputLayout name;
    private TextView color_txtview_info;
    private Button createButton;
    private String nameVal = null;
    private String colorVal = null;
    private int idValue = -1;
    private boolean editMode = false;
    private RecyclerView recyclerView;
    private VersesMarkedViewModel viewModel;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(VersesMarkedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.dashboard_creator_fragment, container, false);
        name = view.findViewById(R.id.dash_creator_fragment_name_edittext);
        color_txtview_info = view.findViewById(R.id.dash_creator_fragment_color_txtview_info);
        createButton = view.findViewById(R.id.dash_creator_fragment_button);
        LabelColorCreatorRecyclerView adapter = new LabelColorCreatorRecyclerView(getActivity(), colors, editMode, this);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 5, GridLayoutManager.VERTICAL, false);
        //============================================================================================
        boolean canGoBack = getActivity().getSupportFragmentManager().getBackStackEntryCount()>0;
        Dashboard.updateCanGoBack(canGoBack, (AppCompatActivity)getActivity());
        //============================================================================================
//        GridView gV = (GridView) view.findViewById(R.id.dash_creator_gridview);
//        // GridAdapter (Pass context and files list)
//        GridAdapterColors adapter = new GridAdapterColors(getActivity(), color_txtview_info);
//        gV.setAdapter(adapter);
        //==========================================================================================
        recyclerView = (RecyclerView) view.findViewById(R.id.dash_creator_grid_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);


        if(nameVal != null && colorVal != null && editMode){
            name.getEditText().setText(nameVal);
            color_txtview_info.setText(colorVal);
            int position = Arrays.asList(colors).indexOf(colorVal);
            adapter.setSelected(position);
            onItemClick(position);
            createButton.setText("Edit Label");
        }


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error = false;
                if (name.getEditText().getText().toString().trim().equalsIgnoreCase("")) {
                    name.getEditText().setError("This field can not be blank");
                    error = true;
                }
                if (adapter.getColorSelected() < 0) {
                    color_txtview_info.requestFocus();
                    color_txtview_info.setError("Please select a color");
                    error = true;
                }
                if(!error){
                    String nameValue = name.getEditText().getText().toString();
                    String colorValue = color_txtview_info.getText().toString().trim();
                    if(editMode && idValue > 0){
                        viewModel.updateOrCreateLabel(nameValue, colorValue, idValue);
                    } else {
                        viewModel.updateOrCreateLabel(nameValue, colorValue, -1);
                    }
                    //go back to Label fragment
//                    getActivity().getSupportFragmentManager().popBackStack();
//                    //go back to All Labels fragments
//                    getActivity().getSupportFragmentManager().popBackStack();
                    // Reload current fragment
                    ((Dashboard) getActivity()).refreshLabelFragmentAfterEdit(new Label(idValue, nameValue, colorValue, 0));

                }

            }
        });

        return view;
    }

    @Override
    public void onItemClick(int position) {
        this.color_txtview_info.setText(" "+colors[position] + " ");
        this.color_txtview_info.setError(null);
        this.color_txtview_info.setBackgroundColor(Color.parseColor(colors[position]));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof Dashboard) {
            if(editMode) {
                ((Dashboard) getActivity()).getSupportActionBar().setTitle("Edit Label");
            } else {
                ((Dashboard) getActivity()).getSupportActionBar().setTitle("New Label");
            }
        }
    }

}
