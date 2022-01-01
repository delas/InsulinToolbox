package dtu.insulinToolbox.model;

import java.util.Date;

public class ManualActivity extends DataPoint {

	private String activity;
	
	public ManualActivity(Date date, String activity) {
		super(date);
		this.activity = activity;
	}

	public String getActivty() {
		return activity;
	}
	

	@Override
	public String toString() {
		return "Manual activity, " + activity + ", " + date + " " + numericAttributes + " - " + stringAttributes;
	}
}
