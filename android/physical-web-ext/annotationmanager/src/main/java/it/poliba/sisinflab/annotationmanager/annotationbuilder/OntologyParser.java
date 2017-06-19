package it.poliba.sisinflab.annotationmanager.annotationbuilder;

import org.kxml2.io.KXmlParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


public class OntologyParser {
    
    final public static HashMap<String, ArrayList<String>> tree = new HashMap<String, ArrayList<String>>();
    final public static HashMap<String, String> propertyList = new HashMap<String, String>();
    private static HashMap<String, Set<OWLClassExpression>> tbox = null;
    
    private static String onto_iri = null;
    
    public static String getDefaultIRI(){
    	return onto_iri;
    }
    
    public static HashMap<String, Set<OWLClassExpression>> getTBox() {
		return tbox;
	}
    
	public static Set<String> getPropertylist() {
		return propertyList.keySet();
	}
	
	public static String getPropertyRange(String property) {
		return propertyList.get(property);
	}
	
	public static void parseXML(String result){
		
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
                        exploreElement(parser, parser.getName()); 
                    }
                }
                parser.next();
            }
            
            xmlReader.close();
            parser = null;
            
        } catch (IOException ex) {
            
        } catch (XmlPullParserException ex) {
            
        }

	}
	
	private static void exploreElement(KXmlParser parser, String elementName){
		try{
		    ArrayList<String> childs = new ArrayList<String>();
            tree.put(elementName, childs);
			parser.next();
			while(!(parser.getName().equals(elementName) && parser.getEventType() == XmlPullParser.END_TAG)){
				if(parser.getName() != null){
					if(parser.getEventType() == XmlPullParser.START_TAG){
						//System.out.println(parser.getName() +" � figlio di "+elementName);
					    ArrayList<String> new_child = tree.get(elementName);
					    new_child.add(parser.getName());
					    tree.put(elementName, new_child);
						exploreElement(parser, parser.getName());
					} else{
						parser.next();
					}	 
				}
			}
		}catch(IOException ex){
			
		}catch(XmlPullParserException ex){
			
		}
	}
	
	public static ArrayList<String> getChild(String root){
	    return tree.get(root);
	}
	
	public static boolean hasChild(String root){
        if (tree.get(root).size()>0)
            return true;
        else
            return false;
    }
	
	public static void parseOntology(String onto_name){
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();		
		OWLOntology onto = null;
		try {
			if(onto_name.startsWith("http://")){
				IRI iri = IRI.create(onto_name);
				onto = manager.loadOntologyFromOntologyDocument(iri);
			} else{
				File file = new File(onto_name);
				onto = manager.loadOntologyFromOntologyDocument(file);
			}
			
			onto_iri = onto.getOntologyID().getOntologyIRI().toString();
			
			parseTBox(onto);
			parseProperties(onto);
			
		}
		catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void parseProperties(OWLOntology onto){
		Set<OWLObjectProperty> propertiesList = onto.getObjectPropertiesInSignature();
		Iterator<OWLObjectProperty> iterator = propertiesList.iterator();
		while(iterator.hasNext()){
			OWLObjectProperty property = iterator.next();
			Set<OWLClassExpression> range = property.getRanges(onto);
			ArrayList<String> list = null;
			if(range!=null &&range.size()>0){
			OWLClassExpression element = range.iterator().next();
			String concept = element.asOWLClass().getIRI().getFragment();
			list = tree.get(concept);
			String iri = property.getIRI().toString();
			propertyList.put(iri, concept);
			if(list!=null){
				ArrayList<String> thing=tree.get("Thing");
				thing.remove(concept);
				//tree.remove(concept);
			}
			}
			else 
			propertyList.put(property.getIRI().toString(), null);
		}
	}

	public static boolean hasPropertyRange(String property) {
		String range = propertyList.get(property);
		if(range!=null)
			return true;
		else return false;
	}

	public static boolean hasItemChild(String string, String type) {
		// TODO Auto-generated method stub
		if(type.equals("Concept"))
			return hasChild(string);
		else 
		return hasPropertyRange(string);
	}
	
	public static void parseTBox(OWLOntology onto){
		
		tbox = new HashMap<String, Set<OWLClassExpression>>();
		
			Set<OWLClass> classList = onto.getClassesInSignature();
			Iterator<OWLClass> iterator = classList.iterator();
			while(iterator.hasNext()){
				OWLClass c = iterator.next();
				
				/*
				 * Per ogni classe nell'ontologia ricavo le sottoclassi dirette attraverso il metodo getSubClasses(onto)
				 */
				Set<OWLClassExpression> set = tbox.get(c.getIRI().getFragment());
				if (set == null){
					tbox.put(c.getIRI().getFragment(), c.getSubClasses(onto));	
				}
				else{
					Iterator<OWLClassExpression> subc = c.getSubClasses(onto).iterator();
					while(subc.hasNext()){
						OWLClassExpression sub = subc.next();
						if(!set.contains(sub)){
							set.add(sub);
							//tbox.put(c.getIRI().getFragment(), child);
						}
					}
				}	

				/*
				 * Verifico inoltre se la classe possiede una descrizione da cui estrarre eventuali concetti padre
				 */
				Set<OWLClassExpression> exp = c.getSuperClasses(onto);
				Iterator<OWLClassExpression> itr = exp.iterator();
				while(itr.hasNext()){
					OWLClassExpression cl = itr.next();
					if (cl.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF){
						Iterator<OWLClassExpression> father = cl.asConjunctSet().iterator();
						while(father.hasNext()){
							OWLClassExpression fc = father.next();
							if (fc.getClassExpressionType() == ClassExpressionType.OWL_CLASS){
								Set<OWLClassExpression> child = tbox.get(fc.asOWLClass().getIRI().getFragment());
								if (child == null){
									child = new TreeSet<OWLClassExpression>();
									child.add(c);
									tbox.put(fc.asOWLClass().getIRI().getFragment(), child);
								}
								else{
									if(!child.contains(c)){
										child.add(c);
										//tbox.put(fc.asOWLClass().getIRI().getFragment(), child);
									}
								}	
							}
						}
					}
				}
				
			}			
		}
	
}
