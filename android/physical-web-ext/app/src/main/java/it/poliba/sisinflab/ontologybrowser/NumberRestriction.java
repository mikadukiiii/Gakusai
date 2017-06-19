package it.poliba.sisinflab.ontologybrowser;

public class NumberRestriction implements Cloneable {
	
	public static final int CONSTRAINT_ATLEAST = 0;
    public static final int CONSTRAINT_EXACTLY = 1;
    public static final int CONSTRAINT_ATMOST = 2; 

    public int getSignFrom() {
		return signFrom;
	}
	public void setSignFrom(int signFrom) {
		this.signFrom = signFrom;
	}
	public int getValueFrom() {
		return valueFrom;
	}
	public void setValueFrom(int valueFrom) {
		this.valueFrom = valueFrom;
		if(valueTo<valueFrom)
			valueTo=valueFrom;
	}
	public int getValueTo() {
		return valueTo;
	}
	public void setValueTo(int valueTo) {
		this.valueTo = valueTo;
	}
	public int getSignTo() {
		return signTo;
	}
	public void setSignTo(int signTo) {
		this.signTo = signTo;
	}
	
	@Override
	public NumberRestriction clone()  {
		// TODO Auto-generated method stub
		NumberRestriction numberRestriction;
		try {
			numberRestriction = (NumberRestriction)super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			return null;
		}
		return numberRestriction;
		
	}
	
	public boolean isIntervalEnabled() {
		return intervalEnabled;
	}
	public void setIntervalEnabled(boolean intervalEnabled) {
		this.intervalEnabled = intervalEnabled;
		if(valueTo<valueFrom)
			valueTo=valueFrom;
		signTo= CONSTRAINT_ATMOST;
	}
	public NumberRestriction(){
		signFrom = CONSTRAINT_ATLEAST;
		valueFrom = 1;
		signTo = CONSTRAINT_ATMOST;
		valueTo = 1;
	}
	
	private boolean intervalEnabled;
	private int signFrom;
	private int valueFrom;
	private int valueTo;
	private int signTo;
	
}
