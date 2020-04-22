package com.michaelzap94.santabiblia.fragments.ui.tabBooks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.adapters.BooksRecyclerViewAdapter;
import com.michaelzap94.santabiblia.models.Book;
import com.michaelzap94.santabiblia.utilities.BookHelper;

import java.util.ArrayList;

public class BooksFragment extends Fragment {
    private static final String TAB_NAME_CHAPTERS = "tab_name_chapters";
    private ArrayList<Book> bookArrayList;

    private RecyclerView rvView;
    //Instantiate the RecyclerViewAdapter, passing an empty list initially.
    // this data will not be shown until you setAdapter to the RecyclerView view in the Layout
    private BooksRecyclerViewAdapter rvAdapter;

    public static Fragment newInstance(int index) {
        BooksFragment fragment = new BooksFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TAB_NAME_CHAPTERS, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getArguments().getInt(TAB_NAME_CHAPTERS)){
            case 0:
                bookArrayList = BookHelper.getbooksOT();
                break;
            case 1:
                bookArrayList = BookHelper.getbooksNT();
                break;
            case 2:
                bookArrayList = new ArrayList();
                break;
            default:
                bookArrayList = new ArrayList();
                break;
        }
        rvAdapter = new BooksRecyclerViewAdapter(getActivity(), bookArrayList);
    }

    //Inflate the fragment to put data in it.
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Layout inflater takes an XML file as input and builds the View objects from it.
        View root = inflater.inflate(R.layout.book_fragment, container, false);

        this.rvView = (RecyclerView) root.findViewById(R.id.book_list_recycler_view);
        rvView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvView.setAdapter(rvAdapter);//attach the RecyclerView adapter to the RecyclerView View
        rvView.addItemDecoration(new DividerItemDecoration(rvView.getContext(), DividerItemDecoration.VERTICAL));

        return root;
    }
}
