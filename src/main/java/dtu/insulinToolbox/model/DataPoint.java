package dtu.insulinToolbox.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataPoint {

	protected Date date;
	protected Map<String, Double> numericAttributes;
	protected Map<String, String> stringAttributes;
	
	public DataPoint(Date date) {
		this.date = date;
		this.numericAttributes = new HashMap<String, Double>();
		this.stringAttributes = new HashMap<String, String>();
	}
	
	public Date getDate() {
		return date;
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
		return "Data point, " + date + " " + numericAttributes + " - " + stringAttributes;
	}
}
