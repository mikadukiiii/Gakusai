package it.poliba.sisinflab.physicalweb;


import android.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.physical_web.physicalweb.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnnotationDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnnotationDialogFragment extends DialogFragment {

    private static final String ARG_NAME = "name";
    private static final String ARG_ANN = "annotation";
    public static final String TAG = "annotation_dialog_fragment";

    private String name;
    private String annotation;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name       Parameter 1.
     * @param annotation Parameter 2.
     * @return A new instance of fragment AnnotationDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnnotationDialogFragment newInstance(String name, String annotation) {
        AnnotationDialogFragment fragment = new AnnotationDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_ANN, annotation);
        fragment.setArguments(args);
        return fragment;
    }

    public AnnotationDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
            annotation = getArguments().getString(ARG_ANN);
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_annotation_dialog, container, false);
        getDialog().setTitle(name);
        getDialog().setCancelable(true);

        TextView annView = (TextView) rootView.findViewById(R.id.ann_view);
        annView.setText(annotation);
        return rootView;
    }


}
