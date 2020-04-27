package com.michaelzap94.santabiblia.fragments.ui.tabVerses;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.michaelzap94.santabiblia.BaseActivityTopDrawer;
import com.michaelzap94.santabiblia.DatabaseHelper.ContentDBHelper;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.adapters.DashboardRecyclerViewAdapter;
import com.michaelzap94.santabiblia.adapters.VersesRecyclerViewAdapter;
import com.michaelzap94.santabiblia.models.Book;
import com.michaelzap94.santabiblia.models.Label;
import com.michaelzap94.santabiblia.models.Verse;
import com.michaelzap94.santabiblia.utilities.BookHelper;
import com.michaelzap94.santabiblia.utilities.RecyclerItemClickListener;
import com.michaelzap94.santabiblia.viewmodel.VersesViewModel;

import java.util.ArrayList;

public class VersesFragment extends Fragment implements RecyclerItemClickListener.OnRecyclerClickListener {
    private static final String TAG = "VersesFragment";
    public static final String TAB_VERSES_NUMBER = "tab_verses_number";
    View root;
    private int book_number;
    private String currentBookName;
    private int chapter_number;
    private int verse_number;
    ///////////////////////////////////////////////////////////
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView bottomSheetTextview;
    ///////////////////////////////////////////////////////////
    private ArrayList<Verse> list = new ArrayList();
    private RecyclerView rvView;
    private VersesViewModel viewModel;
    //Instantiate the RecyclerViewAdapter, passing an empty list initially.
    // this data will not be shown until you setAdapter to the RecyclerView view in the Layout
    private VersesRecyclerViewAdapter rvAdapter;
    //==========================================================
    private ArrayList<Label> arrLabels;
    private DashboardRecyclerViewAdapter rvAdapterLabels;
    private RecyclerView rvViewLabels;

    public static VersesFragment newInstance(int book, int chapter, int verse) {
        Log.d(TAG, "VersesFragment: newInstance" + + book + " " + chapter);
        VersesFragment f = new VersesFragment();
        Bundle args = new Bundle();
        args.putInt("book", book);
        args.putInt("chapter", chapter);
        args.putInt("verse", verse);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.book_number = getArguments().getInt("book");
        this.chapter_number = getArguments().getInt("chapter");
        this.verse_number = getArguments().getInt("verse");
        this.currentBookName = BookHelper.getBook(book_number).getName();
        Log.d(TAG, "onCreate: VersesFragment " + chapter_number);
        rvAdapter = new VersesRecyclerViewAdapter( getActivity(), new ArrayList<>());

        //get viewmodel class and properties, pass this context so LifeCycles are handled by ViewModel,
        // in case the Activity is destroyed and recreated(screen roation)
        //ViewModel will help us show the exact same data, and resume the application from when the user left last time.
        viewModel = new ViewModelProvider(this).get(VersesViewModel.class);
        //Use when we need to reload data
        viewModel.fetchData(this.book_number, this.chapter_number);//refresh -> load data
        //viewModel.getUserMutableLiveData().observe(context, verseListUpdateObserver);
        //observerViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: VersesFragment " + this.chapter_number);

        root = inflater.inflate(R.layout.verses_fragment, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: VersesFragment " + this.chapter_number);
        //////////////////////////////////////////////
        this.rvView = (RecyclerView) root.findViewById(R.id.verses_list_view);
        rvView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rvView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvView,VersesFragment.this));
        rvView.setAdapter(rvAdapter);//attach the RecyclerView adapter to the RecyclerView View
        /////////////////////////////////////////
        ///////////////////////////////////////////////////////////
        View bottomSheet = root.findViewById(R.id.bottom_sheet_nestedscrollview);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState){
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        rvAdapterLabels = new DashboardRecyclerViewAdapter(getActivity(), arrLabels);
                        rvViewLabels = (RecyclerView) view.findViewById(R.id.bottom_sheet_recycler_view);
                        rvViewLabels.setLayoutManager(new LinearLayoutManager(VersesFragment.this.getContext()));
//        rvViewLabels.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvView, VersesFragment.this));
                        rvViewLabels.setAdapter(rvAdapterLabels);//attach the RecyclerView adapter to the RecyclerView View
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        if(actionMode!=null){
                            actionMode.finish();
                        }
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                    default:
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.d(TAG, "BottomSheetBehavior liding....");
            }
        });
        ///////////////////////////////////////////////////////////
        observerViewModel();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof BaseActivityTopDrawer) {
            ((BaseActivityTopDrawer) getActivity()).getSupportActionBar().setSubtitle("Capitulo "+this.chapter_number);
        }
    }

    private void observerViewModel() {
        viewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), (verseArrayList) -> {
            Log.d(TAG, "observerViewModel: VersesFragment GOT DATA" + verseArrayList.size() + "in fragment: " + this.chapter_number);
            //WHEN data is created  pass data and set it in the recyclerview VIEW
            rvAdapter.updateVersesRecyclerView(verseArrayList);

            if (VersesFragment.this.verse_number != 0) {
                VersesFragment.this.rvView.scrollToPosition(VersesFragment.this.verse_number - 1);
            }
        });
    }
    //==========================================================================================
    private class ActionModeCallback implements ActionMode.Callback {
        private ActionModeCallback() {
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
//                case R.id.menu_share /*2131624173*/:
//                    Util.share(CapituloFragment.this.getContext(), CapituloFragment.this.mAdapter.getFormatVersiculo(), CapituloFragment.this.libro, CapituloFragment.this.capitulo, CapituloFragment.this.mAdapter.getSelectedVersiculoInc(), CapituloFragment.this.mAdapter.getSelectedVersiculoFin());
//                    CapituloFragment.this.actionMode.finish();
//                    return true;
//                case R.id.menu_fav /*2131624181*/:
//                    View layout = ((LayoutInflater) CapituloFragment.this.getActivity().getSystemService("layout_inflater")).inflate(R.layout.fav_dialog, null);
//                    TextView fav_dig_tv1 = (TextView) layout.findViewById(R.id.fav_dig_tv1);
//                    fav_dig_tv1.setText(CapituloFragment.this.mAdapter.getFormatVersiculo());
//                    fav_dig_tv1.setVisibility(0);
//                    final EditText fav_d_et1 = (EditText) layout.findViewById(R.id.fav_dig_et1);
//                    Button fav_dig_b1 = (Button) layout.findViewById(R.id.fav_dig_b1);
//                    final int vers1 = CapituloFragment.this.mAdapter.getSelectedVersiculoInc();
//                    final int vers2 = CapituloFragment.this.mAdapter.getSelectedVersiculoFin();
//                    fav_dig_b1.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View arg0) {
//                            fav_d_et1.setText(BuildConfig.FLAVOR);
//                        }
//                    });
//                    new Builder(CapituloFragment.this.getActivity()).setTitle("Agregar Favorito").setView(layout).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            DatabaseHelper.getLtHelper(CapituloFragment.this.getActivity()).addFavorito(CapituloFragment.this.libro, CapituloFragment.this.capitulo, vers1, vers2, fav_d_et1.getText().toString().trim(),getContext());
//                            CapituloFragment.this.loadVersiculos();
//                            CapituloFragment.this.getActivity().sendBroadcast(new Intent(FavoritosFragment.action_favoritos));
//                        }
//                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    }).show();
//                    CapituloFragment.this.actionMode.finish();
//                    return true;
//                case R.id.menu_copi /*2131624182*/:
//                    Util.copiar(CapituloFragment.this.getContext(), LibrosHelper.getTitleLibCaps(CapituloFragment.this.libro, CapituloFragment.this.capitulo, CapituloFragment.this.mAdapter.getSelectedVersiculoInc(), CapituloFragment.this.mAdapter.getSelectedVersiculoFin()), CapituloFragment.this.mAdapter.getFormatVersiculo());
//                    CapituloFragment.this.actionMode.finish();
//                    return true;
                case R.id.menu_delete:
                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_verses_selected, menu);
            return true;
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            VersesFragment.this.actionMode = null;
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            VersesFragment.this.rvAdapter.clearSelections();
        }
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: " + position);
        Toast.makeText(getActivity(), "onItemClick" + position, Toast.LENGTH_SHORT).show();

//        arrLabels = ContentDBHelper.getInstance(getActivity()).getAllLabels();
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        //Only keep selecting items if actionMode exists.
        myToggleSelection(position);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: " + position);
        Toast.makeText(getActivity(), "onItemLongClick" + position, Toast.LENGTH_SHORT).show();
        arrLabels = ContentDBHelper.getInstance(getActivity()).getAllLabels();
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        //===============================================================
        //Create the actionMode only on LongClick
        if (VersesFragment.this.actionMode == null) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(VersesFragment.this.actionModeCallback);
        }
        myToggleSelection(position);
    }
    private void myToggleSelection(int idx) {
        if(VersesFragment.this.actionMode != null){
            int items = rvAdapter.toggleSelection(idx);
            if(items > 0){
                String title = BookHelper.getTitleBookAndCaps(this.currentBookName, this.chapter_number, rvAdapter.getSelectedItems());
                actionMode.setTitle(title);
            } else {
                actionMode.finish();
            }
        }
    }
}
