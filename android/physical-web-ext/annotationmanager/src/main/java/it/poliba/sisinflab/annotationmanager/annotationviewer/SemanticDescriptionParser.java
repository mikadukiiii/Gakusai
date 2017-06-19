package it.poliba.sisinflab.annotationmanager.annotationviewer;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class SemanticDescriptionParser {
	
	public static HashMap<String, ArrayList<String>> parse(String annotation){
		
		HashMap<String, ArrayList<String>> data = new HashMap<String, ArrayList<String>>();
		String root = "Thing";
		data.put(root, new ArrayList<String>());
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto;
		try {
			onto = manager.loadOntologyFromOntologyDocument(new ByteArrayInputStream(annotation.getBytes()));
			Set<OWLNamedIndividual> individuals = onto.getIndividualsInSignature();
			
			for (OWLNamedIndividual ind : individuals) {				
				Set<OWLClassExpression> express = ind.getTypes(onto);
				for (OWLClassExpression exp : express) {
					exploreItem(data, root, exp);					
				}
			}
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return data;
	}

	private static void exploreItem(HashMap<String, ArrayList<String>> data, String root, OWLClassExpression exp) {
			
		if(exp.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF){									
			Set<OWLClassExpression> andClasses = exp.asConjunctSet();
			for(OWLClassExpression desc : andClasses){								
				exploreItem(data, root, desc);
			}
		}	
		else if(exp.getClassExpressionType() == ClassExpressionType.OBJECT_ALL_VALUES_FROM){
			String name = "ONLY " + ((OWLObjectAllValuesFrom)exp).getProperty().asOWLObjectProperty().getIRI().getFragment();
			OWLClassExpression filler = ((OWLObjectAllValuesFrom)exp).getFiller();
			
			addChild(data, root, name);
			data.put(name, new ArrayList<String>());
			exploreItem(data, name, filler);	
		}
		else if(exp.getClassExpressionType() == ClassExpressionType.OBJECT_MAX_CARDINALITY){
			String name = "MAX " + ((OWLObjectMaxCardinality)exp).getCardinality() + " " 
					+((OWLObjectMaxCardinality)exp).getProperty().asOWLObjectProperty().getIRI().getFragment();
			//OWLClassExpression filler = ((OWLObjectMaxCardinality)exp).getFiller();
			addChild(data, root, name);							
			//exploreItem(data, name, filler);	
		}
		else if(exp.getClassExpressionType() == ClassExpressionType.OBJECT_MIN_CARDINALITY){
			String name = "MIN " + ((OWLObjectMinCardinality)exp).getCardinality() + " " 
					+((OWLObjectMinCardinality)exp).getProperty().asOWLObjectProperty().getIRI().getFragment();
			//OWLClassExpression filler = ((OWLObjectMinCardinality)exp).getFiller();
			addChild(data, root, name);	
		}
		else if(exp.getClassExpressionType() == ClassExpressionType.OBJECT_EXACT_CARDINALITY){
			String name = "EXACTLY " + ((OWLObjectExactCardinality)exp).getCardinality() + " " 
					+((OWLObjectExactCardinality)exp).getProperty().asOWLObjectProperty().getIRI().getFragment();
			//OWLClassExpression filler = ((OWLObjectExactCardinality)exp).getFiller();
			addChild(data, root, name);	
		}
		else if(exp.getClassExpressionType() == ClassExpressionType.OBJECT_COMPLEMENT_OF){
			String name = "NOT " + ((OWLObjectComplementOf)exp).getOperand().asOWLClass().getIRI().getFragment();
			addChild(data, root, name);	
		}
		else if(exp.getClassExpressionType() == ClassExpressionType.OWL_CLASS){
			String name = ((OWLClass)exp).getIRI().getFragment();
			addChild(data, root, name);	
		}
		
	}
	
	private static void addChild(HashMap<String, ArrayList<String>> data, String root, String name){
		ArrayList<String> child = null;
		if (data.get(root) != null)			
			child = data.get(root);
		else
			child = new ArrayList<String>();
		
		child.add(name);	
	}

}
