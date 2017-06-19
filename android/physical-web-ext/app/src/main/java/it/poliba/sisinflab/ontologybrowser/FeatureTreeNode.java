/*
 * FeatureTree.java
 *
 * Created on 5 settembre 2006, 19.04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.poliba.sisinflab.ontologybrowser;



/**
 *
 * @author Floriano
 */
public class FeatureTreeNode {
    public static final int TYPE_CONCEPT = 0;
    public static final int TYPE_ROLE = 1;
    
   
    
    
    public static final int NOT_CHECKED = 1;
    public static final int CHECKED = 2;
    public static final int INNER_CHECKED = 3;
    private int type;
    private NumberRestriction numberRestriction;
    private String name;
    private String uri;
    
    private String icon;
    private int status;
    public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	
	  
    public boolean equals(String uri) {
        return (this.uri.equals(uri));
    }
    
    public boolean equals(FeatureTreeNode node) {
        return node.getName().equals(getName())&&node.getUri().equals(getUri())&&node.getType()==(getType());
    }
    @Override
	public boolean equals(Object o) {
		
    	
        if (o instanceof String) 
			return equals((String) o);
		
        else if (o instanceof FeatureTreeNode) 
		
		return equals((FeatureTreeNode)o);
        else return false;
	}
    
	@Override
	protected FeatureTreeNode clone() throws CloneNotSupportedException {
		FeatureTreeNode featureTreeNode = new FeatureTreeNode(type,name,icon);
		return featureTreeNode;
	
		
	}

	/** Creates a new instance of FeatureTree */
    public FeatureTreeNode(int type, String name, String uri) {
        setType(type);
        setName(name==null?"":name);
        setUri(uri==null?"":uri);
        icon = null;
        status = NOT_CHECKED;
       
    }
    
    public FeatureTreeNode(int type, String name, String uri, String icon) {
        this(type, name, uri);
        setIcon(icon);
    }
  

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
 
   
    
    

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    
 
   

    

    public String toHumanReadableString() {
        StringBuffer sb = new StringBuffer();
        if (getType() == FeatureTreeNode.TYPE_ROLE) {
            switch (getNumberRestriction().getSignFrom()) {
                case NumberRestriction.CONSTRAINT_ATLEAST:
                    sb.append(">= " + getNumberRestriction().getValueFrom() + " ");
                    break;
                case NumberRestriction.CONSTRAINT_ATMOST:
                    sb.append("<= " + getNumberRestriction().getValueFrom() + " ");
                    break;
                case NumberRestriction.CONSTRAINT_EXACTLY:
                    sb.append("= " + getNumberRestriction().getValueFrom() + " ");
                    break;
                default:
                    break;
            }
        }
        sb.append(getName());
        return sb.toString();
    }

	public int getStatus() {
		return status;
	}

	public void setStatus(int checked) {
		this.status = checked;
	}
	public boolean isUnchecked(){
		return this.status == NOT_CHECKED;
	}
	public NumberRestriction getNumberRestriction() {
		return numberRestriction;
	}

	public void setNumberRestriction(NumberRestriction numberRestriction) {
		this.numberRestriction = numberRestriction;
	}

	
	
	
	
}
