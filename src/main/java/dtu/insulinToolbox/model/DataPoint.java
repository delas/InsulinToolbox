package dtu.insulinToolbox.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DataPoint {

	public static final String ACTIVITY_ATTRIBUTE_NAME = "activity-name";

	public static final String ACTIVITY_NAME_EATING = "eating";
	public static final String ACTIVITY_NAME_LONG_LASTING_INSULIN = "long-lasting-insulin";
	public static final String ACTIVITY_NAME_RAPID_INSULIN = "rapid-acting-insulin";
	public static final String ACTIVITY_NAME_SCAN = "scan";

	private UUID id;
	private Date date;
	private Map<String, Double> numericAttributes;
	private Map<String, String> stringAttributes;
	
	public DataPoint(Date date) {
		this.id = UUID.randomUUID();
		this.date = date;
		this.numericAttributes = new HashMap<String, Double>();
		this.stringAttributes = new HashMap<String, String>();
	}

	public DataPoint(Date date, String activity) {
		this(date);
		setStringAttribute(ACTIVITY_ATTRIBUTE_NAME, activity);
	}
	
	public Date getDate() {
		return date;
	}

	public String getActivity() {
		return getStringAttribute(ACTIVITY_ATTRIBUTE_NAME);
	}
	
	public Set<String> getAttributeNames() {
		return numericAttributes.keySet();
	}
	
	public Double getAttribute(String name) {
		return numericAttributes.get(name);
	}
	
	public void setAttribute(String name, Double value) {
		numericAttributes.put(name, value);
	}
	
	public Set<String> getStringAttributeNames() {
		return stringAttributes.keySet();
	}
	
	public String getStringAttribute(String name) {
		return stringAttributes.get(name);
	}
	
	public void setStringAttribute(String name, String value) {
		stringAttributes.put(name, value);
	}
	
	@Override
	public String toString() {
		if (getActivity() == null) {
			return "Data point, " + date + " " + numericAttributes + " - " + stringAttributes;
		} else {
			return getActivity() + ", " + date + " " + numericAttributes + " - " + stringAttributes;
		}	
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof DataPoint) {
			return id.equals(((DataPoint) other).id);
		}
		return false;
	}
}
