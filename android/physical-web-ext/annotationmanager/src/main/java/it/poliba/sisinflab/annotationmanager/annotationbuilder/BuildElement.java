package it.poliba.sisinflab.annotationmanager.annotationbuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import it.poliba.sisinflab.annotationmanager.R;

public class BuildElement extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_listviewlayout);

		Intent intent = getIntent();
		final String root = intent.getStringExtra("root");
		final String rootReq = intent.getStringExtra("root-req");
		
		final ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();		
	    

		if (root.equals("Thing")) {
			this.setTitle("Show Description");
		} 
		else if (root.equals("universal")) {
			this.setTitle("Properties List");
			
			Iterator<String> iterator = BuildSemanticDescription.properties.iterator();
		    while(iterator.hasNext()) {
		    	final HashMap<String, Object> listMap = new HashMap<String, Object>();
				listMap.put("name", iterator.next().split("#")[1]);
				listMap.put("concept", R.drawable.foreach_button);
				listMap.put("image", R.drawable.expander);
				data.add(listMap);
			}
		}
		else if (root.equals("numeric")) {
			this.setTitle("Properties List");
			
			Iterator<String> iterator = BuildSemanticDescription.properties.iterator();
		    while(iterator.hasNext()) {
		    	final HashMap<String, Object> listMap = new HashMap<String, Object>();
				listMap.put("name", iterator.next().split("#")[1]);
				listMap.put("concept", R.drawable.gtr_button);
				listMap.put("image", null); 
				data.add(listMap);
			}
		}
		else if (root.equals("concept")) {

			String rootTbox = intent.getStringExtra("root-tbox");
			this.setTitle("Concepts List: "+rootTbox);
			
			Iterator<OWLClassExpression> iterator = BuildSemanticDescription.tbox.get(rootTbox).iterator();
		    while(iterator.hasNext()) {
		    	
		    	OWLClassExpression cexp =  iterator.next();
		    	OWLClass c = cexp.getClassesInSignature().iterator().next();
		    	
		    	final HashMap<String, Object> listMap = new HashMap<String, Object>();
				listMap.put("name", c.getIRI().getFragment());
				listMap.put("concept", R.drawable.c_button);
				if (BuildSemanticDescription.tbox.get(c.getIRI().getFragment()).size()>0)
					listMap.put("image", R.drawable.expander);
				else
					listMap.put("image", null);
				data.add(listMap);
			}
		}
		else {
			String rootTbox = intent.getStringExtra("root-tbox");
			this.setTitle("Concepts List: "+rootTbox);
			
			Iterator<OWLClassExpression> iterator = BuildSemanticDescription.tbox.get(rootTbox).iterator();
		    while(iterator.hasNext()) {
		    	
		    	OWLClassExpression cexp =  iterator.next();
		    	OWLClass c = cexp.getClassesInSignature().iterator().next();
		    	
		    	final HashMap<String, Object> listMap = new HashMap<String, Object>();
				listMap.put("name", c.getIRI().getFragment());
				listMap.put("concept", R.drawable.not_button);
				if (BuildSemanticDescription.tbox.get(c.getIRI().getFragment()).size()>0)
					listMap.put("image", R.drawable.expander);
				else
					listMap.put("image", null);
				data.add(listMap);
			}
		}

		// String[] from={"name", "surname", "image"}; //dai valori contenuti in
		// queste chiavi
		// int[] to={R.id.personName, R.id.personSurname,
		// R.id.personImage};//agli id delle view

		String[] from = { "name", "image", "concept" }; // dai valori contenuti
														// in queste
		// chiavi
		int[] to = { R.id.name, R.id.type, R.id.Concept };

		// costruzione dell adapter
		SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
				data,// sorgente dati
				R.layout.simple_listviewitem, // layout contenente gli id di "to"
				from, to);

		// utilizzo dell'adapter
		ListView l = (ListView) findViewById(R.id.personListView);
		l.setAdapter(adapter);

		l.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int pos,
					long id) {
                if ((root.equals("concept") || root.equals("negation")) && (data.get(pos).get("image") != null)){
					Intent intent = new Intent(getBaseContext(),
							BuildElement.class);
					intent.putExtra("root", root);
					intent.putExtra("root-tbox", (String)data.get(pos).get("name"));
                    Toast.makeText(getBaseContext(), "OK", Toast.LENGTH_SHORT).show();

                    startActivity(intent);
				}
				else if (root.equals("universal") && (data.get(pos).get("image") != null)){
					
					BuildSemanticDescription.objprop = data.get(pos).get("name").toString();
					String iri = OntologyParser.getDefaultIRI() + "#" + data.get(pos).get("name");
					String range = OntologyParser.getPropertyRange(iri);
					if (range==null)
						range = "Thing";					
					
					Intent intent = new Intent(getBaseContext(),
							BuildElement.class);
					intent.putExtra("root", "concept");
					intent.putExtra("root-tbox", range);
					startActivity(intent);
				}
				
			}
		});
		
		l.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    final int pos, long id) {
            	if (!root.equals("universal")){
	            	final AlertDialog ad = new AlertDialog.Builder(BuildElement.this).create();
	            	ad.setIcon(android.R.drawable.ic_dialog_info);
	            	ad.setTitle("Add to the request");
	            	
	            	String ops = "";
	            	if (root.equals("negation"))
	            		ops = "NOT ";
	            	else if (root.equals("concept") && BuildSemanticDescription.objprop!=null)
	            		ops = "ONLY " + BuildSemanticDescription.objprop + ".";
	            	else if (root.equals("numeric"))
	            		ops = ">=1 ";
	            		
	            	ad.setMessage("Select \"" + ops + data.get(pos).get("name").toString() + "\" ?");
	            	ad.setButton(-1, "Ok", new DialogInterface.OnClickListener() {
	            	      public void onClick(DialogInterface dialog, int which) {
	            	    	  
	            	          if (root.equals("concept")){
	            	        	  if (BuildSemanticDescription.objprop == null)
	            	        		  BuildSemanticDescription.request.put("C#"+(String)data.get(pos).get("name"), null);
	            	        	  else{
	            	        		  ArrayList<String> child = new ArrayList<String>();
	            	        		  child.add(data.get(pos).get("name").toString());
	            	        		  BuildSemanticDescription.request.put("U#"+BuildSemanticDescription.objprop, child);            	        		  
	            	        	  }
	            	          } else if (root.equals("negation")){
	            	        	  BuildSemanticDescription.request.put("N#"+(String)data.get(pos).get("name"), null);
		            	      } else if (root.equals("numeric")){
		        	        	  BuildSemanticDescription.request.put("GTR#1#"+(String)data.get(pos).get("name"), null);
		        	          }
		            	    
	            	       } }); 
	            	ad.setButton(-2, "Cancel", new DialogInterface.OnClickListener() {
		          	      public void onClick(DialogInterface dialog, int which) {
		          	    	  ad.dismiss();
		          	       } }); 
	            	
	            	ad.show();
            	}
            	
                return true;
            }
        }); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.build_element, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_back) {
            finish();


            return super.onOptionsItemSelected(item);
        } else {
            return super.onOptionsItemSelected(item);
        }
	}
	
}
