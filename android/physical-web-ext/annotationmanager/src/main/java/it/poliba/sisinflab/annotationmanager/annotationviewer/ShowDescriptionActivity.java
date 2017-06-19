package it.poliba.sisinflab.annotationmanager.annotationviewer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import it.poliba.sisinflab.annotationmanager.R;


public class ShowDescriptionActivity extends Activity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_description, menu);
        return true;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_description);

		Intent intent = getIntent();
		final String root = intent.getStringExtra("root");
        final HashMap<String, ArrayList<String>> map = (HashMap<String, ArrayList<String>>)intent.getSerializableExtra("annotation");

        if(savedInstanceState == null){
            ShowDescriptionFragment fragment = ShowDescriptionFragment.newInstance(root, map);
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            trans.replace(R.id.container, fragment);
            trans.commit();
        }

	}

	public void onResume() {
		super.onResume();
	}
}
