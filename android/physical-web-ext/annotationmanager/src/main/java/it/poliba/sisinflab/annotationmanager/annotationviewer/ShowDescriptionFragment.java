package it.poliba.sisinflab.annotationmanager.annotationviewer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import it.poliba.sisinflab.annotationmanager.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ShowDescriptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowDescriptionFragment extends Fragment {

    public static final String TAG = "showDescriptionFragment";

    private static final String ARG_ROOT = "root";
    private static final String ARG_MAP = "map";

    private String root;
    private HashMap<String, ArrayList<String>> map;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param root Parameter 1.
     * @param map Parameter 2.
     * @return A new instance of fragment ShowDescriptionFragment.
     */

    public static ShowDescriptionFragment newInstance(String root, HashMap<String, ArrayList<String>> map) {
        ShowDescriptionFragment fragment = new ShowDescriptionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROOT, root);
        args.putSerializable(ARG_MAP, (Serializable) map);
        fragment.setArguments(args);
        return fragment;
    }

    public ShowDescriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_back) {
            if(getFragmentManager().getBackStackEntryCount() > 1) {
                getFragmentManager().beginTransaction().remove(this).commit();
                getFragmentManager().popBackStack();
            }else{
                getActivity().finish();
            }
            return true;
        } else if (i == R.id.menu_home) {
            getActivity().finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            root = getArguments().getString(ARG_ROOT);
            map = (HashMap<String, ArrayList<String>>) getArguments().getSerializable(ARG_MAP);
        }
        setRetainInstance(true);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_show_description, container, false);

        if (root.equals("Thing")) {
            getActivity().setTitle("Show Description");
        } else {
            getActivity().setTitle(root);
        }

        final ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        final ArrayList<String> child = map.get(root);

        for (int i = 0; i < child.size(); i++) {
            final HashMap<String, Object> listMap = new HashMap<String, Object>();

            if (child.get(i).startsWith("NOT ")) {
                listMap.put("name", child.get(i).substring(4));
                listMap.put("concept", R.drawable.not_button);
            } else if (child.get(i).startsWith("MAX ")) {
                listMap.put("name", child.get(i).substring(4));
                listMap.put("concept", R.drawable.ltr_button);
            } else if (child.get(i).startsWith("MIN ")) {
                listMap.put("name", child.get(i).substring(4));
                listMap.put("concept", R.drawable.gtr_button);
            } else if (child.get(i).startsWith("ONLY ")) {
                listMap.put("name", child.get(i).substring(5));
                listMap.put("concept", R.drawable.foreach_button);
            } else {
                listMap.put("name", child.get(i));
                listMap.put("concept", R.drawable.c_button);
            }

            if (map.get(child.get(i)) != null)
                listMap.put("image", R.drawable.expander);
            else
                listMap.put("image", null);

            data.add(listMap);
        }

        String[] from = { "name", "image", "concept" };
        int[] to = { R.id.name, R.id.expand_button, R.id.type};

        SimpleAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(),
                data,
                R.layout.show_description_item,
                from, to);

        ListView l = (ListView) rootView.findViewById(R.id.list);
        l.setAdapter(adapter);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) {
                if (map.get(child.get(pos)) != null) {
                    ShowDescriptionFragment fragment = ShowDescriptionFragment.newInstance(child.get(pos), map);
                    FragmentTransaction trans = getFragmentManager().beginTransaction();
                    trans.replace(R.id.container, fragment);
                    trans.addToBackStack(null);
                    trans.commit();
                }
            }
        });

        return rootView;
    }

}
