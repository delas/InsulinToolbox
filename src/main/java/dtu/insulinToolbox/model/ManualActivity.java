package dtu.insulinToolbox.model;

import java.util.Date;

public class ManualActivity extends DataPoint {

	public static final String ACTIVITY_ATTRIBUTE_NAME = "activity-name";
	
	public ManualActivity(Date date, String activity) {
		super(date);
		setStringAttribute(ACTIVITY_ATTRIBUTE_NAME, activity);
	}

	public String getActivty() {
		return getStringAttribute(ACTIVITY_ATTRIBUTE_NAME);
	}
	

	@Override
	public String toString() {
		return "Manual activity, " + getActivty() + ", " + date + " " + numericAttributes + " - " + stringAttributes;
	}
}
