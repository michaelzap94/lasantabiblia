package com.michaelzap94.santabiblia.fragments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelzap94.santabiblia.DatabaseHelper.ContentDBHelper;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.adapters.RecyclerView.VersesLearnedRecyclerView;
import com.michaelzap94.santabiblia.models.VersesMarked;
import com.michaelzap94.santabiblia.utilities.CommonMethods;
import com.michaelzap94.santabiblia.viewmodel.VersesMarkedViewModel;

import java.util.ArrayList;

public class VersesLearned extends DialogFragment {

    private static final String TAG = "VersesLearned";
    private Toolbar toolbar;
    private Context ctx;
    private ArrayList<VersesMarked> list = new ArrayList<>();
    private int versesLearnedSize = 0;
    private RecyclerView rvView;
    private VersesMarkedViewModel viewModel;
    //Instantiate the RecyclerViewAdapter, passing an empty list initially.
    // this data will not be shown until you setAdapter to the RecyclerView view in the Layout
    private VersesLearnedRecyclerView rvAdapter;
    private CoordinatorLayout coordinatorLayout;;

    public static VersesLearned newInstance() {
        return new VersesLearned();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getActivity();
        //itemTouchHelper = new ItemTouchHelper(SwipeToDelete);
        rvAdapter = new VersesLearnedRecyclerView(this.ctx, list, this);
        viewModel = new ViewModelProvider(getActivity()).get(VersesMarkedViewModel.class);
        viewModel.getVersesLearned(1);//refresh -> load data
    }

    private void observerViewModel() {
        viewModel.getVersesMarkedListLearned().observe(getActivity(), (versesMarkedArrayList) -> {
            Log.d(TAG, "observerViewModel: LABEL GOT DATA" + versesMarkedArrayList.size() + "in label: Mem");
            versesLearnedSize = versesMarkedArrayList.size();
            //WHEN data is created  pass data and set it in the updateVersesMarkedViewPager VIEW
            rvAdapter.updateVersesMarkedRecyclerView(versesMarkedArrayList);
            updateTitle();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.verses_marked_dialog_edit, container, false);
        toolbar = rootView.findViewById(R.id.toolbar);
        rvView = rootView.findViewById(R.id.verses_marked_dialog_rv);
        coordinatorLayout = rootView.findViewById(R.id.verses_marked_dialog_coordinatorLayout);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observerViewModel();
        rvView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rvView.setAdapter(rvAdapter);//attach the RecyclerView adapter to the RecyclerView View
        toolbar.setNavigationOnClickListener(v -> {
            dismiss();
        });
//        toolbar.inflateMenu(R.menu.menu_save);
//        toolbar.setOnMenuItemClickListener(item -> {
//            dismiss();
//            return true;
//        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
    @Override
    public void onPause() {
        super.onPause();
        getActivity().getSupportFragmentManager().popBackStack();
        this.dismissAllowingStateLoss();
    }
    private void updateTitle(){
        toolbar.setTitle("Verses Learned: " + versesLearnedSize);
//        toolbar.setSubtitle(titleChapterVerses);
    }

    public void removeFromLearned(String uuid, int position){new VersesLearned.RemoveVersesLearned(position).execute(uuid);}
    private class RemoveVersesLearned extends AsyncTask<String, Void, Boolean> {
        private int position;
        private RemoveVersesLearned(int position) {
            this.position = position;
        }
        protected Boolean doInBackground(String... args) {
            Log.d(TAG, "doInBackground: " + args[0]);
            return ContentDBHelper.getInstance(ctx).editVersesLearned(args[0], 0);
        }
        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if(success){
                rvAdapter.removeItem(this.position);
                versesLearnedSize--;
                updateTitle();
                //refresh the not learned verses
                viewModel.getVersesLearned(0);
            } else {
                Toast.makeText(ctx, "This item could not be deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
