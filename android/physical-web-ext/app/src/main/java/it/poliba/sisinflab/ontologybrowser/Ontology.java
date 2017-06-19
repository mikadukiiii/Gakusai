/*
 * Ontology.java
 *
 * Created on 5 settembre 2006, 12.36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 * MODIFICATO DALLA RIGA 107 ALLA RIGA 116 
 */

package it.poliba.sisinflab.ontologybrowser;



import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.kxml2.io.KXmlParser;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.widget.ImageView;
/**
 *
 * @author Saverio Ieva
 */
public class Ontology {
	private static Vector levels;
    private static Vector currentSublevels;
    private String name;
    private String path;
    private FeatureTreeNode root;
    private String icon;
    private static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();;
	
	private static OWLOntology onto = null;
    final public static HashMap<String, ArrayList<FeatureTreeNode>> tree = new HashMap<String, ArrayList<FeatureTreeNode>>();

    
    /** Creates a new instance of Ontology */
    public Ontology(String name, String path, String hierarchy/*, ImageView icon*//*, String vendor*/) {
        
        setName(name);
        setPath(path);
       parseXML(hierarchy);
       getProperties();
        /*setIcon(icon);*/
        
        //setVendor(vendor);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
    public FeatureTreeNode getRoot(){
    	return root;
    }
   
private  void parseXML(String result){
		
		InputStreamReader xmlReader = null;
        try {
        	byte bytes[] = result.getBytes("UTF-8");
        	
        	ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            xmlReader = new InputStreamReader(bais);
            KXmlParser parser = new KXmlParser();
            parser.setInput(xmlReader);
            parser.next();
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                //parser.next();
                if (parser.getName() != null) {
                    if (parser.getEventType() == XmlPullParser.START_TAG) {
                    	if(root == null) {
                    		root = new FeatureTreeNode(FeatureTreeNode.TYPE_CONCEPT, "Thing", "");//parser.getAttributeValue(0)) ;
                    	
                    		}
                        exploreElement(parser, parser.getName(), "");//parser.getAttributeValue(0)); 
                    }
                }
                parser.next();
            }

            xmlReader.close();
            parser = null;
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (XmlPullParserException ex) {
            ex.printStackTrace();
        }


	}
	
	private  void exploreElement(KXmlParser parser, String elementName, String iri){
		try{
		    ArrayList<FeatureTreeNode> childs = new ArrayList<FeatureTreeNode>();
            tree.put(iri, childs);
			parser.next();
            if(parser.getName() != null){

                while(!(parser.getName().equals(elementName) && parser.getEventType() == XmlPullParser.END_TAG)){

                        if(parser.getEventType() == XmlPullParser.START_TAG){
                            System.out.println(parser.getName() +" è figlio di "+elementName);
                            ArrayList<FeatureTreeNode> childArray = tree.get(iri);
                            String childURI = "";//parser.getAttributeValue(0);
                            String name = parser.getName();
                            FeatureTreeNode newChild = new FeatureTreeNode(FeatureTreeNode.TYPE_CONCEPT, name, childURI);
                            childArray.add(newChild);

                            tree.put(iri, childArray);
                            exploreElement(parser, name, childURI);
                        } else{
                            parser.next();
                        }

                }
            }
		}catch(IOException ex){
			
		}catch(XmlPullParserException ex){
			
		}
	}
	private  void getProperties(){
		
		
		
		
		if(onto!=null)
		manager.removeOntology(onto);
		try {
			if(path.startsWith("http://")){
				IRI iri = IRI.create(path);
				if(manager.getOntology(iri) == null)
				onto = manager.loadOntologyFromOntologyDocument(iri);
			} else{
				File file = new File(path);
				onto = manager.loadOntologyFromOntologyDocument(file);
			}
			Set<OWLObjectProperty> propertiesList = onto.getObjectPropertiesInSignature();
			Iterator<OWLObjectProperty> iterator = propertiesList.iterator();
			while(iterator.hasNext()){
				OWLObjectProperty property = iterator.next();
				Set<OWLClassExpression> range = property.getRanges(onto);
				ArrayList<FeatureTreeNode> list = null;
				if(range!=null &&range.size()>0){
				OWLClassExpression element = range.iterator().next();
				String conceptIRI = element.asOWLClass().getIRI().toString();
				String conceptName = element.asOWLClass().getIRI().getFragment();
				list = tree.get(conceptIRI);
				
				String name = property.getIRI().getFragment();
				String iri = property.getIRI().toString();
				FeatureTreeNode propertyNode = new FeatureTreeNode(FeatureTreeNode.TYPE_ROLE, name, iri);
			
			
				if(list!=null){
					ArrayList<FeatureTreeNode> thing=tree.get(root.getUri());
					thing.remove(new FeatureTreeNode(FeatureTreeNode.TYPE_CONCEPT, conceptName, conceptIRI));
					thing.add(propertyNode);
					ArrayList<FeatureTreeNode> newList = new ArrayList<FeatureTreeNode>();
					for (FeatureTreeNode featureTreeNode : list) {
						newList.add(featureTreeNode.clone());
					}
					tree.put(iri, newList);
				}
				}
				else {
				tree.put(property.getIRI().toString(), new ArrayList<FeatureTreeNode>());
				ArrayList<FeatureTreeNode> thing=tree.get(root.getUri());
				String name = property.getIRI().getFragment();
				String iri = property.getIRI().toString();
				thing.add(new FeatureTreeNode(FeatureTreeNode.TYPE_ROLE, name, iri));
				}
				
			}
			
		}
		catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
	public ArrayList<FeatureTreeNode> getChild(String root){
	    return tree.get(root);
	}

	public  boolean hasChild(String root){
        if (tree.get(root).size()>0)
            return true;
        else
            return false;
    }
	public void createIndividual(  Vector requests, Vector roleRequests) throws OWLOntologyCreationException{
		OWLDataFactory factory = manager.getOWLDataFactory(); 
		if(!requests.isEmpty ()){
			
			
			OWLOntology ontology = manager.createOntology(IRI.create(manager.getOntologyDocumentIRI(onto).toString()+"/Demand"));
			
			 ArrayList<OWLClassExpression> setClassExpressions = new ArrayList<OWLClassExpression>();
			for(int i=0; i<requests.size (); i++){

                FeatureTreeNode element = (FeatureTreeNode) requests.elementAt (i);
                Vector roleVector = (Vector) roleRequests.elementAt(i);
                
                
                if(element.getType ()==FeatureTreeNode.TYPE_ROLE){
                    OWLObjectProperty property = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(element.getUri()));
                    ArrayList<OWLObjectCardinalityRestriction> numRestrictions = writeNumberRestrictions(property, element.getNumberRestriction());
                   
                    if(roleVector!=null)
                    	setClassExpressions.add(writeRoleVector(roleVector, factory.getOWLObjectIntersectionOf(CollectionFactory.createSet(numRestrictions))));
                    else setClassExpressions.addAll(numRestrictions);
               
                   
                
                }else if(element.getType () == FeatureTreeNode.TYPE_CONCEPT){
                	OWLClass owlClass = factory.getOWLClass(IRI.create(element.getUri()));
                	
                   
                    
                    if (roleVector != null && roleVector.size()>0){
                    	
                    	
                        
                       
                         
                        
                        if(roleVector.size()==1){
                        	FeatureTreeNode lastRatom=(FeatureTreeNode)roleVector.lastElement();
                        	 OWLObjectProperty owlObjectProperty = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(lastRatom.getUri()));
                             OWLObjectPropertyExpression restriction = (OWLObjectPropertyExpression) factory.getOWLObjectAllValuesFrom(owlObjectProperty, owlClass);
                         	
                        	ArrayList<OWLObjectCardinalityRestriction> numRestrictions = writeNumberRestrictions(owlObjectProperty, lastRatom.getNumberRestriction());
                        	setClassExpressions.addAll(numRestrictions);
                        	
                        }
                        else 
                        	setClassExpressions.add(writeRoleVector(roleVector,owlClass));
                        
                        
                    }
                    else{
                    	setClassExpressions.add(owlClass);
                       

                    }
                    
                }
              
                OWLObjectIntersectionOf objectIntersectionOf = factory.getOWLObjectIntersectionOf(CollectionFactory.createSet(setClassExpressions));
        	    OWLIndividual inst = factory.getOWLNamedIndividual(IRI.create(manager.getOntologyDocumentIRI(onto).toString()+"/Demand#demand"));
        	    manager.applyChange(new AddAxiom(ontology, factory.getOWLClassAssertionAxiom(objectIntersectionOf, inst)));
        	    try {
					manager.saveOntology(ontology, new SystemOutDocumentTarget());
					manager.removeOntology(ontology);
				} catch (OWLOntologyStorageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
			
		}
		

		
		 
	}
		private ArrayList<OWLObjectCardinalityRestriction> writeNumberRestrictions(OWLObjectPropertyExpression property, NumberRestriction numberRestriction){
			
			ArrayList<OWLObjectCardinalityRestriction> restrictions = new ArrayList<OWLObjectCardinalityRestriction>();
			
			if(numberRestriction!=null)
			{
				
				OWLObjectCardinalityRestriction cardinalityFrom = writeNumberRestriction(property, numberRestriction.getSignFrom(), numberRestriction.getValueFrom());
				restrictions.add(cardinalityFrom);
			}
			if(numberRestriction.isIntervalEnabled()){
				OWLObjectCardinalityRestriction cardinalityTo = writeNumberRestriction(property, numberRestriction.getSignTo(), numberRestriction.getValueTo());
				restrictions.add(cardinalityTo);
			}
			return restrictions;
		}
		private OWLObjectCardinalityRestriction writeNumberRestriction(OWLObjectPropertyExpression property, int sign, int value ){
			OWLDataFactory factory = manager.getOWLDataFactory(); 
			OWLObjectCardinalityRestriction restr = null;
			switch (sign){
			case NumberRestriction.CONSTRAINT_ATLEAST:
				restr = factory.getOWLObjectMinCardinality(value, property);
				break;
			case NumberRestriction.CONSTRAINT_ATMOST:
				 restr = factory.getOWLObjectMaxCardinality(value, property);
				 break;
			case NumberRestriction.CONSTRAINT_EXACTLY:
				restr =factory.getOWLObjectExactCardinality(value, property);
				break;
				
			}
			return restr;
		}
		 private OWLObjectAllValuesFrom appendObjectPropertyExpression(FeatureTreeNode role, OWLClassExpression filler){
			 OWLDataFactory factory = manager.getOWLDataFactory();   
			 OWLObjectProperty owlObjectProperty = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(role.getUri()));
             OWLObjectAllValuesFrom property = factory.getOWLObjectAllValuesFrom(owlObjectProperty, filler);
		     
		       
		       
		        
		        
		        
		        return property;
		    }
        private OWLObjectAllValuesFrom writeRoleVector(Vector roleVector,OWLClassExpression filler){
        	 OWLDataFactory factory = manager.getOWLDataFactory();   
			
        	
        	OWLObjectAllValuesFrom property = null;
            
        	
        	for(int i=roleVector.size()-1;i>=0;i--){
	        	 FeatureTreeNode role = (FeatureTreeNode) roleVector.get(i);
	        
				 OWLObjectProperty owlObjectProperty = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(role.getUri()));
	              property = factory.getOWLObjectAllValuesFrom(owlObjectProperty, filler);
			     filler = property;
	        	
	             
	        }
        	return property;
        }

    

   

}
