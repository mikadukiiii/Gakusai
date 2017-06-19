package it.poliba.sisinflab.ontologybrowser;



import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import org.physical_web.physicalweb.R;

public class OntologyBrowser extends FragmentActivity {

    private OntologyBrowserHelper ontologyBrowserHelper;
    AlertDialog alertDialog;
	ProgressBar working = null;
	String ontology = "";
	String name = "";
    String path = "";
   
    
   
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listviewlayout);
       
        Intent intent = getIntent(); 
        
        String hierarchy = intent.getStringExtra("hierarchy");
        String name = intent.getStringExtra("name");
        String path = intent.getStringExtra("path");   	
        this.setTitle("Path: ");
        ontologyBrowserHelper =  new OntologyBrowserHelper(name, path, hierarchy); 
        //Questa è la lista che rappresenta la sorgente dei dati della listview
        //ogni elemento è una mappa(chiave->valore)
        ontologyBrowserHelper.reset();
        buildLevel();
    }
    
    
    /*private void buildNumericalRestriction(final FeatureTreeNode node){
    	
    	
    	 
    	NumericalRestrictionDialog dialog = NumericalRestrictionDialog.newInstance(new NumericalRestrictionDialogListener() {
				
				@Override
				public void onDialogPositiveClick(DialogFragment dialog) {
					ontologyBrowserHelper.requestItemWithNumberRestriction(node, ((NumericalRestrictionDialog)dialog).getNumberRestriction());
				
				
					dialog.getDialog().cancel();
					buildLevel();
				}
				
				@Override
				public void onDialogNegativeClick(DialogFragment dialog) {
					dialog.getDialog().cancel();
					
				}
			}, node.getNumberRestriction());
    	        

        dialog.show(getSupportFragmentManager(), "Numer restriction dialog");
    	
    	 
    }*/
    private void buildLevel(){
   
    	
final ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();
        
        final ArrayList<FeatureTreeNode> child = ontologyBrowserHelper.buildLevelBrowsingList();
       
        for(int i=0;i<child.size();i++){     
                final HashMap<String,Object> listMap=new HashMap<String, Object>();
                
                //creiamo una mappa di valori                             
                listMap.put("name", child.get(i).getName()); // per la chiave name,l'informazine sul nome
               
              
                //listMap.put("surname", "ClassURI" + i);// per la chiave surnaname, l'informazione sul cognome
                if(child.get(i).getType()==FeatureTreeNode.TYPE_CONCEPT)
                	listMap.put("type",R.drawable.c_button);
                else if(child.get(i).getNumberRestriction()==null || child.get(i).isUnchecked())
                	listMap.put("type",R.drawable.properties);
                else listMap.put("type",null);
              
                listMap.put("numRestr", child.get(i) );
                if (ontologyBrowserHelper.expandableItem(i)){
                    listMap.put("expandable", R.drawable.expander); // per la chiave image, inseriamo la risorsa dell immagine
                    
                    
                }                  
                else {
                    listMap.put("expandable", null);
                    
                }
                
                listMap.put("status", child.get(i));        
                data.add(listMap);  //aggiungiamo la mappa di valori alla sorgente dati
        }
        
       
       
        	
        //String[] from={"name", "surname", "image"}; //dai valori contenuti in queste chiavi
        //int[] to={R.id.personName, R.id.personSurname, R.id.personImage};//agli id delle view
        
        String[] from={"name", "expandable", "type", "status", "numRestr"}; //dai valori contenuti in queste chiavi
        //int[] to={R.id.personName, R.id.personImage, R.id.Concept, R.id.Checkbox, R.id.numRestrLbl};
        /*OntologyListViewBinder ontologyListViewBinder = new OntologyListViewBinder();
        ontologyListViewBinder.setRestrButtonListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FeatureTreeNode node = (FeatureTreeNode) v.getTag();
				buildNumericalRestriction(node);
				
				
			}
		});
        ontologyListViewBinder.setThreeStateCheckBoxListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FeatureTreeNode node = (FeatureTreeNode) v.getTag();
				requestItem(node);
				((ThreeStateCheckBox)v).setState(node.getStatus());
				
			}
		});
        
        //costruzione dell adapter
        SimpleAdapter adapter=new SimpleAdapter(
                        getApplicationContext(),
                        data,//sorgente dati
                        R.layout.listviewitem, //layout contenente gli id di "to"
                        from,
                        to);
       
        //utilizzo dell'adapter
      
        //adapter.setViewBinder(ontologyListViewBinder);
        
        ListView l = (ListView) findViewById(R.id.personListView);
        l.setAdapter(adapter);
        
        l.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) {
                // TODO Auto-generated method stub
              
                	if(ontologyBrowserHelper.expandItem(pos))
                		buildLevel();
                    //Intent intent = new Intent(OntoTree.this, OntoTree.class);
                    //intent.putExtra("root", (String)data.get(pos).get("uri")); 
                    //startActivity(intent);
                	
                
            }          
        });
        
        Button button = (Button) findViewById(R.id.backbtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {                     
               ontologyBrowserHelper.previousLevel();
               buildLevel();
            }
        });
        
        Button home = (Button) findViewById(R.id.homebtn);
        home.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ontologyBrowserHelper.buildRequestedFeaturesGroup();
			
				
			}
		});*/
    }
    public void requestItem(FeatureTreeNode node){
        try{
            
            ontologyBrowserHelper.checkItem(node);//(screen.svgList.getFocusedIndex());
           
           
            
        }catch(IllegalArgumentException iae){
        	//buildNumericalRestriction(node);
           //ontologyBrowserHelper.expandItem(node);
           //buildLevel();
        }finally{
            
        }
    }
    public void onResume(){
    	super.onResume();
    	if(Closing.exit == true){
    		finish();
    	}
    }
}
