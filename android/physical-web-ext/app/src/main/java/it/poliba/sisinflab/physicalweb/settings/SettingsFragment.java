package it.poliba.sisinflab.physicalweb.settings;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.physical_web.physicalweb.R;
import org.semanticweb.owlapi.model.OWLOntologyAlreadyExistsException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import it.poliba.sisinflab.annotationmanager.annotationviewer.SemanticDescriptionParser;
import it.poliba.sisinflab.annotationmanager.annotationviewer.ShowDescriptionActivity;
import it.poliba.sisinflab.filebrowser.BrowseFileActivity;
import it.poliba.sisinflab.physicalweb.AnnotationDownloader;
import it.poliba.sisinflab.physicalweb.SemanticReasoner;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationImpl;

/**
 * @author Giorgio
 *         Fragment delle impostazioni
 */

public class SettingsFragment extends PreferenceFragment {

    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
        /*Preference credits = (Preference)findPreference(getResources().getString(R.string.credits_key));
        credits.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
	        @Override
	        public boolean onPreferenceClick(Preference arg0) { 
	            startActivity(new Intent(arg0.getContext(), AboutActivity.class));   
	            return true;
	        }
        });*/
        /*Preference ontology_path = (Preference)findPreference(getResources().getString(R.string.ontology_path_key));
        ontology_path.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent intent = new Intent(getActivity(), BrowseFileActivity.class);
                startActivityForResult(intent, SettingsActivity.PICK_ONTOLOGY);
                return true;
            }
        });*/

        Preference request_path = (Preference) findPreference(getResources().getString(R.string.request_path_key));
        request_path.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent intent = new Intent(getActivity(), BrowseFileActivity.class);
                startActivityForResult(intent, SettingsActivity.PICK_REQUEST);
                return true;
            }
        });
        Preference feedback = (Preference) findPreference(getResources().getString(R.string.send_feedback_key));
        feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                sendEmail(getResources().getString(R.string.app_name));
                return true;
            }
        });

        Preference visualizeDemand = (Preference) findPreference(getResources().getString(R.string.visualize_request_key));
        visualizeDemand.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String path = sp.getString(getResources().getString(R.string.request_path_key), SemanticReasoner.DEFAULT_REQ);
                final String annotation = AnnotationDownloader.readFile(path);
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        HashMap<String, ArrayList<String>> parsedAnn = SemanticDescriptionParser.parse(annotation);
                        Intent intent = new Intent(getActivity(), ShowDescriptionActivity.class);
                        intent.putExtra("root", "Thing");
                        intent.putExtra("annotation", (Serializable) parsedAnn);
                        startActivity(intent);
                        return null;
                    }

                }.execute();
                return true;
            }
        });


    }


    public void sendEmail(String subject) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

		/* Fill it with Data */
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email)});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);


		/* Send it off to the Activity-Chooser */
        startActivity(emailIntent);
    }

    public void pickFile(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("application/xml");

        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("OK");

        if (requestCode == SettingsActivity.PICK_ONTOLOGY) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    String filePath = data.getStringExtra("path");
                    SemanticReasoner semanticReasoner = new SemanticReasoner();
                    semanticReasoner.loadOntology(filePath);
                    Toast.makeText(getActivity(), R.string.ontology_parsing_success, Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                    editor.putString(getString(R.string.ontology_path_key), filePath);
                    editor.commit();
                }catch (OWLOntologyAlreadyExistsException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), R.string.ontology_parsing_success, Toast.LENGTH_SHORT).show();

                }catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), R.string.ontology_parsing_error, Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == SettingsActivity.PICK_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String filePath = data.getStringExtra("path");
                SemanticReasoner semanticReasoner = new SemanticReasoner();
                try {


                    //semanticReasoner.loadOntology(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getResources().getString(R.string.ontology_path_key), SemanticReasoner.DEFAULT_ONTO));
                    semanticReasoner.loadRequest(filePath);
                    Toast.makeText(getActivity(), R.string.request_parsing_success, Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                    editor.putString(getString(R.string.request_path_key), filePath);
                    editor.commit();
                }catch (OWLOntologyAlreadyExistsException e) {
                        e.printStackTrace();
                    Toast.makeText(getActivity(), R.string.request_parsing_success, Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                    editor.putString(getString(R.string.request_path_key), filePath);
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), R.string.request_parsing_error, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
