package it.poliba.sisinflab.ontologybrowser;






import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class OntologyBrowserHelper {
	private  ArrayList<FeatureTreeNode> levels;
    private  ArrayList<FeatureTreeNode> currentSublevels;
    private Vector requests;
    private Vector roleRequests;
    private Ontology ontology;
    public OntologyBrowserHelper(String name, String path, String hierarchy){
    	 ontology = new Ontology(name, path, hierarchy);
    	 
    }
    /**
     * Reset nesting to root level
     */
    public void reset() {
        levels = new ArrayList<FeatureTreeNode>();
        levels.add(ontology.getRoot());
        deleteAllRequests();
    }
    /**
     * Delete all requested features
     */
    public void deleteAllRequests() {
        requests = new Vector(0,1); 
        roleRequests = new Vector(0,1); 
    }
    public void checkItem(FeatureTreeNode node){
    	switch(node.getStatus()){
    	case FeatureTreeNode.NOT_CHECKED:
    		requestItem(node);
    		
    		updateLevelStatus(FeatureTreeNode.INNER_CHECKED);
    		break;
    	case FeatureTreeNode.INNER_CHECKED:
    		deleteRequestParent(node);
    		
    		updateLevelStatus(FeatureTreeNode.NOT_CHECKED);
    		break;
    	case FeatureTreeNode.CHECKED:
    		deleteRequest(node);
    		
    		updateLevelStatus(FeatureTreeNode.NOT_CHECKED);
    		break;
    	}
    }
    private void updateLevelStatus(int status){
    	int s = levels.size();
        for (int i=s-1; i>=0; i--) {
            FeatureTreeNode n = (FeatureTreeNode)levels.get(i);
            n.setStatus(status);
        }
    }
    public void requestItem(FeatureTreeNode selectedItem) throws IndexOutOfBoundsException, IllegalArgumentException {
    		
    		
         
            if (isNumberRestrictionRequired(selectedItem)) {
               
               throw new IllegalArgumentException();
            }
            /* The selected item must be added to requests vector if it does not 
             * exist or if it exists as a filler of a different role */ 
            Vector  roleVector=getRoleVector();
            
            int index = requests.indexOf(selectedItem);
           
            if (index<0 || (selectedItem.getType()==FeatureTreeNode.TYPE_CONCEPT && !roleVector.equals(/*(FeatureTreeNode)*/roleRequests.elementAt(index)))) { // role is tested for identity, not equality
               if(!requests.contains(selectedItem))
               {  requests.addElement(selectedItem);
                    roleRequests.addElement(roleVector);
                    }
               else{}
            }
            selectedItem.setStatus(FeatureTreeNode.CHECKED);
       
        
    }
    
	   /**
     * Tell whether the selected item in the current nesting level 
     * requires a number restriction to be specified.
     *
     * @arg itemSelection index of the selected item
     * @returns <code>true</code> if a number restriction is
     * required, <code>false</code>otherwise
     */
    public boolean isNumberRestrictionRequired(FeatureTreeNode selectedItem) throws IndexOutOfBoundsException {
      
        
       
           
            return (selectedItem.getType() == FeatureTreeNode.TYPE_ROLE);
       
    }
    /**
     * Finds the deepest role node in the current browsing path, if any exists.
     *
     * @returns the <code>FeatureTreeNode</code> representing the
     * deepest role node; if no role nodes exist in the current
     * browsing path, <code>null</code> is returned
     */
    private FeatureTreeNode findDeepestRole() {
        int s = levels.size();
        for (int i=s-1; i>=0; i--) {
            FeatureTreeNode n = (FeatureTreeNode)levels.get(i);
            if (n.getType() == FeatureTreeNode.TYPE_ROLE)
                return n;
        }
        return null;
    }
    /**
     * Finds the deepest role node in the current browsing path, if any exists.
     *
     * @returns the <code>FeatureTreeNode</code> representing the
     * deepest role node; if no role nodes exist in the current
     * browsing path, <code>null</code> is returned
     */
    private Vector getRoleVector() {
        Vector roleVector= new Vector();
    	int s = levels.size();
        for (int i=0; i<s; i++) {
            FeatureTreeNode n = (FeatureTreeNode)levels.get(i);
            if (n.getType() == FeatureTreeNode.TYPE_ROLE)
                roleVector.add(n);
        }
        return roleVector;
    }	
	    
		
	 
	 /**
	     * Point nesting level to <code>level</code> node
	     * in the hierarchy
	     */
	    private  void nextLevel(FeatureTreeNode level) {
	    
	        levels.add(level);
	        currentSublevels = null;
	    }

	    /**
	     * Expand the <code>itemSelection</code> item in the current level.
	     *
	     * @arg itemSelection index of the item to be expanded
	     * @returns <code>true</code> if the item can be expanded (i.e. has
	     * children), <code>false</code> otherwise
	     */
	    public  boolean expandItem(int itemSelection) throws IndexOutOfBoundsException {
	        FeatureTreeNode selectedItem = null;
	        try {
	            selectedItem = (FeatureTreeNode) currentSublevels.get(itemSelection);
	           return expandItem(selectedItem);
	        } catch (ArrayIndexOutOfBoundsException e) {
	            throw new IndexOutOfBoundsException(e.getMessage());
	        }
	    }
	    /**
	     * Expand the <code>itemSelection</code> item in the current level.
	     *
	     * @arg itemSelection index of the item to be expanded
	     * @returns <code>true</code> if the item can be expanded (i.e. has
	     * children), <code>false</code> otherwise
	     */
	    public  boolean expandItem(FeatureTreeNode selectedItem) throws IndexOutOfBoundsException {
	        
	 
	           
	            if  (ontology.getChild(selectedItem.getUri()).size() == 0) {
	            	
	            	 return false;
	            } else {
	                nextLevel(selectedItem);
	                return true;
	            }
	        
	    }
	    public FeatureTreeNode getParentNode(){
	    	return levels.get(levels.size()-1);
	    }
	public  boolean expandableItem(int itemSelection) throws IndexOutOfBoundsException {
	        FeatureTreeNode selectedItem = null;

	        try {
	            selectedItem = (FeatureTreeNode) currentSublevels.get(itemSelection); //currentSublevels

	            if  (ontology.getChild(selectedItem.getUri())==null ||ontology.getChild(selectedItem.getUri()).size() == 0) {
	                return false;
	            } else {
	                /*nextLevel(selectedItem);*/
	                return true;
	            }
	        } catch (ArrayIndexOutOfBoundsException e) {e.printStackTrace();
	            throw new IndexOutOfBoundsException(e.getMessage());
	        }
	    }
	    /**
	     * Go to previous level
	     *
	     * @returns <code>false</code> if there is no previus
	     * level, <code>true</code> otherwise.
	     */
	    public boolean previousLevel() {
	        if (levels.isEmpty()) {
	            return false;
	        } else {
	            int s = levels.size();
	            //AGGIUNGO BLOCCO IF
	            if(s > 1){
	             
	                levels.remove(s-1);
	                currentSublevels = null;
	                return true;
	                //return(s > 1);
	            }else return false;
	        }
	    }

	    /**
	     * Represent the current level path in a formatted string
	     *
	     * @returns the string representation of the current level path
	     */
	    public String levelPathToString() {
	        int s = levels.size();
	        StringBuffer sb = new StringBuffer("Path");
	        sb.append(": ");       
	       
	        for (int i=0; i<s ; i++) {
	            FeatureTreeNode level = (FeatureTreeNode) levels.get(i);
	            String name = level.getName();
	            if (!"Thing".equalsIgnoreCase(name)) {
	                sb.append(name);
	                if (i < s-1) {
	                    sb.append(" >> ");
	                }
	            }
	        }
	        return sb.toString();
	    }

	  


	  
	    public void deleteRequest(FeatureTreeNode request){
	    	
	    	Vector  roleVector=getRoleVector();
           
	         boolean del=false;
	         for(int i=0;i<requests.size();i++){
	             
	             FeatureTreeNode req=(FeatureTreeNode)requests.elementAt(i);
	             Vector rol=(Vector)roleRequests.elementAt(i);
	             if(req!=null&&rol!=null){
	                if(req.equals(request)&&rol.equals(roleVector))
	                    del=true;
	             }
	             else if(req!=null){
	                 if(req.equals(request))
	                    del=true;
	             }
	             else if(rol.equals(roleVector))
	                 del=true;
	         if(del){
	             requests.removeElementAt(i);
	             roleRequests.removeElementAt(i);
	             request.setStatus(FeatureTreeNode.NOT_CHECKED);
	             break;
	         }
	             
	             
	         
	         
	                 
	         }
	    }
public void deleteRequestParent(FeatureTreeNode request){
	request.setStatus(FeatureTreeNode.NOT_CHECKED);    	
	ArrayList<FeatureTreeNode> list = ontology.getChild(request.getUri());
	    	for (FeatureTreeNode featureTreeNode : list) {
	    		switch(featureTreeNode.getStatus()){
	    		case FeatureTreeNode.CHECKED:
	    			deleteRequest(featureTreeNode);
	    			break;
	    		case FeatureTreeNode.INNER_CHECKED:
	    			deleteRequestParent(featureTreeNode);
	    			break;
	    		}
				
				
			}
	    	
	    }
	  
	   /**
	     * Add the selected item to user requests, specifying a
	     * number restriction
	     *
	     * @arg itemSelection index of the item to be added
	     * @throws IndexOutOfBoundsException if <code>itemSelection</code>
	     * is outside the bounds of items in the current nesting level
	     * @throws IllegalArgumentException if the selected item doeas
	     * not require a number restriction
	     */
	    public void requestItemWithNumberRestriction(FeatureTreeNode selectedItem, NumberRestriction numberRestriction) throws IndexOutOfBoundsException, IllegalArgumentException {
	       
	        try {
	          
	            System.out.println(selectedItem.getName());
	            if (!isNumberRestrictionRequired(selectedItem)) {
	                throw new IllegalArgumentException("Selected item does not require a number restriction.");
	            }
	            if (!requests.contains(selectedItem)) {
	                selectedItem.setNumberRestriction(numberRestriction);
	              
	                requests.addElement(selectedItem);
	                roleRequests.addElement(null);
	            }
	            else selectedItem.setNumberRestriction(numberRestriction);
	            selectedItem.setStatus(FeatureTreeNode.CHECKED);
	        } catch (ArrayIndexOutOfBoundsException e) {
	            throw new IndexOutOfBoundsException(e.getMessage());
	        }
	    }

	    
	   
	    /**
	     * Represent currently requested items in a 
	     * formatted string
	     * 
	     * 
	     * @returns the string representation of the currently
	     * requested items
	     * @param group 
	     */
	    public String requestedItemsToString() {
	        StringBuffer sb = new StringBuffer();
	        FeatureTreeNode feat;
	        int featType, constrType;
	        int s = requests.size();
	        
	        for (int i=0; i<s; i++) {
	            feat = (FeatureTreeNode) requests.elementAt(i);
	            sb.append("* ");
	            sb.append(feat.toHumanReadableString());
	            if (i<s-1) {
	                sb.append("\n");
	            }
	        }
	        
	        return sb.toString();
	    }

	    
	    public void  buildRequestedFeaturesGroup(/*ChoiceGroup group*/) {
	       try {
			ontology.createIndividual(requests, roleRequests);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    }
	    
	    /*public void updateRequestedFeaturesGroup(ChoiceGroup group, boolean[] selectedFlags) {
	    int len = selectedFlags.length;
	    for (int i=len-1; i>=0; i--) {
	    if (selectedFlags[i] == false) {
	    requests.removeElementAt(i);
	    roleRequests.removeElementAt(i);
	    //group.delete(i);
	    group.delete(i);
	    }
	    }
	    }*/
	    /**
	     * Fill a <code>List</code> GUI component with items in
	     * the current nesting level 
	     *
	     * @arg list the list to be filled
	     */
	    public ArrayList<FeatureTreeNode> buildLevelBrowsingList(/*ChoiceGroup list*/) {//f

	    

	     
	        
	        FeatureTreeNode parent = (FeatureTreeNode) levels.get(levels.size()-1);
	        currentSublevels = ontology.getChild(parent.getUri());
	      
	        Collections.sort(currentSublevels, COMPARATOR);
	        /*list.deleteAll();*/ //f
	        return currentSublevels;
  }
		public static Comparator<FeatureTreeNode> COMPARATOR = new Comparator<FeatureTreeNode>()
			    {
			    // This is where the sorting happens.
			        public int compare(FeatureTreeNode o1, FeatureTreeNode o2)
			        {   if(o2.getType()==o1.getType())
			        	return o2.getName().compareToIgnoreCase(o1.getName())>0?-1:1;
			        	else return  o2.getType()==FeatureTreeNode.TYPE_ROLE?-1:1;
			            
		        }
		    };
		
	   
}
