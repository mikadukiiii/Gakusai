package it.poliba.sisinflab.annotationmanager.annotationbuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.semanticweb.owlapi.model.OWLClassExpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import it.poliba.sisinflab.annotationmanager.R;


public class BuildSemanticDescription extends Activity {
	
	ListView l = null;
	
	static Set<String> properties = null;
	static HashMap<String, Set<OWLClassExpression>> tbox = null;
	static String type = null;
	static String objprop;
	String rootReq = null;
	
	public static TreeMap<String, ArrayList<String>> request = null;
	
	String onto_path = "";

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.build_semantic_description, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_add) {
            final String[] items = {"Concept", "Negation",
                    "Universal Quantifier", "Numeric Restriction"};
            final Integer[] icons = new Integer[]{R.drawable.c_button, R.drawable.not_button, R.drawable.foreach_button, R.drawable.gtr_button};
            ListAdapter adapter = new ArrayAdapterWithIcon(this, items, icons);
            new AlertDialog.Builder(this).setTitle("Add to Request")
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {

                                case 0: {
                                    type = "C";
                                    Intent intent = new Intent(getBaseContext(),
                                            BuildElement.class);
                                    intent.putExtra("root", "concept");
                                    intent.putExtra("root-req", rootReq);
                                    intent.putExtra("root-tbox", "Thing");
                                    startActivity(intent);
                                    break;
                                }

                                case 1: {
                                    type = "N";
                                    Intent intent = new Intent(getBaseContext(),
                                            BuildElement.class);
                                    intent.putExtra("root", "negation");
                                    intent.putExtra("root-req", rootReq);
                                    intent.putExtra("root-tbox", "Thing");
                                    startActivity(intent);
                                    break;
                                }

                                case 2: {
                                    type = "U";
                                    Intent intent = new Intent(getBaseContext(),
                                            BuildElement.class);
                                    intent.putExtra("root", "universal");
                                    intent.putExtra("root-req", rootReq);
                                    startActivity(intent);
                                    break;
                                }

                                case 3: {
                                    type = "GTR";
                                    Intent intent = new Intent(getBaseContext(),
                                            BuildElement.class);
                                    intent.putExtra("root", "numeric");
                                    intent.putExtra("root-req", rootReq);
                                    startActivity(intent);
                                    break;
                                }

                                default:
                                    break;
                            }
                        }
                    }).show();
            objprop = null;


            return super.onOptionsItemSelected(item);
        } else {
            return super.onOptionsItemSelected(item);
        }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_listviewlayout);
        onto_path = getIntent().getStringExtra("onto_path");
		OntologyParser.parseOntology(onto_path);
		properties = OntologyParser.getPropertylist();
		tbox = OntologyParser.getTBox();
		
		request = new TreeMap<String, ArrayList<String>>();
		
		objprop = null;
		
		Intent intent = getIntent();
		rootReq = intent.getStringExtra("root-req");
				
		//setAdapter();
	}
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first

	    setAdapter();
	}
	
	private void setAdapter(){
		
		final ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		
		Iterator<String> iterator = request.keySet().iterator();
	    while(iterator.hasNext()) {
	    	
	    	String k = iterator.next();	    		    	
	    	final HashMap<String, Object> listMap = new HashMap<String, Object>();
	    	
	    	String[] names = k.split("#");
	    	
	    	if (names[0].equals("C")){	    	
				listMap.put("name", names[1]);
				listMap.put("concept", R.drawable.c_button);
				listMap.put("image", null); 
			}
	    	else if (names[0].equals("N")){	    	
				listMap.put("name", names[1]);
				listMap.put("concept", R.drawable.not_button);
				listMap.put("image", null); 
			} 
	    	else if (names[0].equals("U")){	    	
				listMap.put("name", names[1]);
				listMap.put("concept", R.drawable.foreach_button);
				if (request.get(k).size()>0)
					listMap.put("image", R.drawable.expander);
				else
					listMap.put("image", null); 
			}
	    	else if (names[0].equals("GTR")){	    	
				listMap.put("name", names[1] + " " + names[2]);
				listMap.put("concept", R.drawable.gtr_button);
				listMap.put("image", null); 
			}
	    	
			data.add(listMap);
		}
	    
	    String[] from = { "name", "image", "concept" }; // dai valori contenuti

		int[] to = { R.id.personName, R.id.personImage, R.id.Concept };
		
		// costruzione dell adapter
		SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
		data,// sorgente dati
		R.layout.simple_listviewitem, // layout contenente gli id di "to"
		from, to);
		
		// utilizzo dell'adapter
		l = (ListView) findViewById(R.id.personListView);
		l.setAdapter(adapter);
	}
}