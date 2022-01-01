package dtu.insulinToolbox.model;

import java.util.LinkedList;
import java.util.List;

public class Readings extends LinkedList<DataPoint> {

	private static final long serialVersionUID = -378788195584503319L;

	public List<DataPoint> getAllWithActivityName(String activity) {
		return getAllWithStringAttributeEqualsTo(ManualActivity.ACTIVITY_ATTRIBUTE_NAME, activity);
	}
	
	public List<DataPoint> getAllWithStringAttributeEqualsTo(String attributeName, String attributeValue) {
		List<DataPoint> toReturn = new LinkedList<>();
		for (DataPoint dp : this) {
			if (attributeValue.equals(dp.getStringAttribute(attributeName))) {
				toReturn.add(dp);
			}
		}
		return toReturn;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(DataPoint dp : this) {
			sb.append(dp);
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}
}
