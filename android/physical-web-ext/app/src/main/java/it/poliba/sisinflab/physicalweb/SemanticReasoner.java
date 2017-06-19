package it.poliba.sisinflab.physicalweb;

import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyAlreadyExistsException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import it.poliba.sisinflab.owl.owlapi.MicroReasoner;
import it.poliba.sisinflab.owl.owlapi.MicroReasonerFactory;
import it.poliba.sisinflab.owl.sod.hlds.Abduction;
import it.poliba.sisinflab.owl.sod.hlds.Contraction;
import it.poliba.sisinflab.owl.sod.hlds.Item;

/**
 * Created by giorgio on 13/02/15.
 */
public class SemanticReasoner {

    private static MicroReasoner reasoner;
    private static OWLOntologyManager manager;
    private IRI demand;
    private OWLOntology onto_request;
    private Set<IRI> ontologies;
    private static final String TAG = "SemanticReasoner";
    public static final double UNDEFINED_SCORE = -1;
    public static int keepCount = 0;

    public static final String DEFAULT_REQ = Environment.getExternalStorageDirectory().getPath() + File.separator + "PhysicalWeb" + File.separator + "Scenario1.owl";

    public void loadOntology(final String path) throws OWLOntologyCreationException {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    File onto_path = new File(path);
                    manager = OWLManager.createOWLOntologyManager();
                    OWLDataFactory owlDataFactory = OWLManager.getOWLDataFactory();
                    //Load ontology from an input file
                    OWLOntology onto = manager.loadOntologyFromOntologyDocument(onto_path);
                    ontologies = onto.getDirectImportsDocuments();
                    //Create the factory
                    MicroReasonerFactory reasonerFactory = new MicroReasonerFactory();
                    //Return an instance of OWLReasoner class that represents our Mini-ME reasoner
                    reasoner = reasonerFactory.createMicroReasoner(onto, manager);

                    //Log.i(TAG, "Ontology loaded");
                    Iterator it = reasoner.getSupplyIndividuals().iterator();
                    demand = (IRI) it.next();
                    Item temp = reasoner.retrieveSupplyIndividual(demand);
                    demand = reasoner.loadDemand(temp);
                    //Log.i(TAG, "Request loaded");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

            }
        }.execute();

    }

    public IRI loadRequest(String path) throws OWLOntologyCreationException, OWLOntologyAlreadyExistsException {
        File file = new File(path);
        onto_request = manager.loadOntologyFromOntologyDocument(file);
        Set<IRI> requests = reasoner.loadDemand(onto_request);
        Iterator<IRI> iter = requests.iterator();
        IRI request = null;
        while (iter.hasNext()) {
            request = iter.next();
        }
        //Log.i(TAG, "Request loaded");
        return request;
    }

    public HashMap<String, Object> loadResource(InputStream annotation) throws OWLOntologyCreationException {
        HashMap<String, Object> supply = new HashMap<String, Object>();

        OWLOntology onto_supply = manager.loadOntologyFromOntologyDocument(annotation);
        for (OWLAnnotation prop : onto_supply.getIndividualsInSignature().iterator().next().getAnnotations(onto_supply)) {
            if (prop.getProperty().getIRI().toString().endsWith("icon")) {
                String url = prop.getValue().toString();
                url = url.replace("\"", "");
                //Log.i(TAG, "Icon found: " + url);
                supply.put("icon", url);
            } else if (prop.getProperty().getIRI().toString().endsWith("url")) {
                String url = prop.getValue().toString();
                url = url.replace("\"", "");
                //Log.i(TAG, "URL found: " + url);
                supply.put("url", url);
            }
        }

        Set<IRI> suppOntos = onto_supply.getDirectImportsDocuments();
        boolean find = false;
        if(ontologies != null){
            for (IRI onto : suppOntos) {

                if (ontologies.contains(onto)) {
                    find = true;
                } else {
                    find = false;
                    break;
                }
            }
        }
        if (find) {
            Set<IRI> supplies = reasoner.loadSupply(onto_supply);
            Iterator<IRI> iter = supplies.iterator();
            IRI supply_iri = null;
            while (iter.hasNext()) {
                supply_iri = iter.next();
            }
            //Log.i(TAG, "Supply loaded");
            supply.put("iri", supply_iri);
        }
        return supply;
    }

    public Contraction contraction(IRI supply, IRI demand) {
        //Log.i(TAG, "Compute contraction between " + supply.getFragment() + " and " + demand.getFragment());
        Contraction result = reasoner.contraction(supply, demand);
        //Log.i(TAG, "Keep: " + result.K.toString());
        //Log.i(TAG, "Give-up: " + result.G.toString());
        return result;
    }

    public Abduction abduction(IRI supply, IRI demand) {
        //Log.i(TAG, "Compute abduction between " + supply.getFragment() + " and " + demand.getFragment());
        Abduction result = null;
        result = reasoner.abduction(supply, demand);
        //Log.i(TAG, "H: " + result.H.toString());


        return result;
    }

    public double calculateScore(IRI supply, IRI demand, String refUrl, UserData data) {

        double score;

        if (reasoner.checkCompatibility(supply, demand)) {
            //Log.i(TAG, "Compatibility");
            Abduction abd = abduction(supply, demand);
            //Log.i(TAG, "Abd penalty " + String.valueOf(abd.penalty));

            Item top = new Item(IRI.create("http://www.w3.org/2002/07/owl#Thing"));
            IRI topIRI = reasoner.loadSupply(top);
            Abduction max = abduction(topIRI, demand);
            //Log.i(TAG, "Max penalty " + String.valueOf(max.penalty));
            score = (1 - (abd.penalty / max.penalty)) * 100;

        } else {
            //Log.i(TAG, "Incompatibility");
            Contraction cnt = contraction(supply, demand);
            //Log.i(TAG, "Contr penalty " + String.valueOf(cnt.penalty));

            Item keep = new Item(IRI.create("Keep" + keepCount), cnt.K);
            keepCount++;
            IRI keepIRI = reasoner.loadDemand(keep);

            Abduction abd = abduction(supply, keepIRI);
            //Log.i(TAG, "Abd penalty " + String.valueOf(abd.penalty));

            Item top = new Item(IRI.create("http://www.w3.org/2002/07/owl#Thing"));
            IRI topIRI = reasoner.loadSupply(top);
            Abduction max = abduction(topIRI, keepIRI);

            //Log.i(TAG, "TOTAL penalty " + String.valueOf(max.penalty));

            score = (1 - ((abd.penalty / max.penalty) + (cnt.penalty / max.penalty))) * 100;


        }
        if(data.getBookmarks().contains(refUrl)){
            //se è un segnalibro, aumenta del 10%
            score += 10;
        }else if(data.getSpam().contains(refUrl)){
            //se è spam, diminuisci del 50%
            score -= 50;
        }else{
            int count = 0;
            for(String url : data.getHistory()){
                if(url.equalsIgnoreCase(refUrl))
                    count++;
            }
            //ogni 3 click, aumenta del 5% per un massimo del 20%
            int plus = (Math.round(count/3)) * 5;
            if(plus > 15)
                plus = 15;
            score += plus;
        }

        if(score > 100)
            score = 100;
        else if(score < 0){
            score = 0;
        }
        return score;
    }

    public MicroReasoner getReasoner() {
        return reasoner;
    }

    public void setReasoner(MicroReasoner reasoner) {
        this.reasoner = reasoner;
    }

    public IRI getDemand() {
        return demand;
    }

    public void setDemand(IRI demand) {
        this.demand = demand;
    }
}
