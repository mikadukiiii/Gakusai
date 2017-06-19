package it.poliba.sisinflab.physicalweb;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.physical_web.physicalweb.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link android.app.Fragment} subclass.
 * Use the {@link it.poliba.sisinflab.physicalweb.PerformancesDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerformancesDialogFragment extends DialogFragment {
    private static final String ARG_URL = "url";
    private static final String ARG_RESULTS = "results";

    public static final String TAG = "performances_dialog_fragment";

    private String url;
    private ArrayList<String> results;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param results    Parameter 1.
     * @return A new instance of fragment PerformancesDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerformancesDialogFragment newInstance(String url, ArrayList<String> results) {
        PerformancesDialogFragment fragment = new PerformancesDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RESULTS, (Serializable) results);
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    public PerformancesDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_URL);
            results = (ArrayList<String>) getArguments().getSerializable(ARG_RESULTS);
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_performances_dialog, container, false);
        getDialog().setTitle(url);
        getDialog().setCancelable(true);

        ListView resultsView = (ListView) rootView.findViewById(R.id.results_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, results);
        resultsView.setAdapter(adapter);
        return rootView;
    }

}
