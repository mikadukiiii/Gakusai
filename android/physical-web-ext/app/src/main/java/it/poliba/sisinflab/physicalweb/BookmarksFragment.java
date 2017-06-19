package it.poliba.sisinflab.physicalweb;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.physical_web.physicalweb.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarksFragment extends Fragment {

    UserData ud;
    ArrayAdapter<String> adapter;

    public BookmarksFragment() {
        // Required empty public constructor
    }

    public static BookmarksFragment newInstance() {
        return new BookmarksFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        getActivity().getActionBar().setTitle(R.string.bookmarks);
        ud = UserData.load(getActivity());
        ListView bookmarksList = (ListView) rootView.findViewById(R.id.list);
        bookmarksList.setEmptyView(rootView.findViewById(android.R.id.empty));
        bookmarksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ud.getBookmarks().remove(position);
                ud.save(getActivity());
                adapter.notifyDataSetChanged();
            }
        });
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ud.getBookmarks());
        bookmarksList.setAdapter(adapter);
        return rootView;
    }
/*
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_bookmarks).setVisible(false);
    }
    */
}
