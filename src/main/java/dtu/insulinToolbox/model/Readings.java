package dtu.insulinToolbox.model;

import java.util.LinkedList;
import java.util.List;

public class Readings extends LinkedList<DataPoint> {

	private static final long serialVersionUID = -378788195584503319L;
	
	public Readings getAllWithStringAttributeEqualsTo(String attributeName, String attributeValue) {
		Readings toReturn = new Readings();
		for (DataPoint dp : this) {
			if (attributeValue.equals(dp.getStringAttribute(attributeName))) {
				toReturn.add(dp);
			}
		}
		return toReturn;
	}
	
	public Readings getAllWithActivityName(String activity) {
		return getAllWithStringAttributeEqualsTo(ManualActivity.ACTIVITY_ATTRIBUTE_NAME, activity);
	}

	public Readings getAllInTimeFrame(Date start, Date end) {
		Readings toReturn = new Readings();
		for (DataPoint dp : this) {
			if (dp.getDate().after(start) && dp.getDate().before(end)) {
				toReturn.add(dp);
			}
		}
		return toReturn;
	}

	public Readings getBefore(DataPoint reference, int minutesBefore) {
		return getBefore(reference, 0, minutesBefore);
	}

	public Readings getAfter(DataPoint reference, int minutesAfter) {
		return getAfter(reference, 0, minutesAfter);
	}

	public Readings getAfter(DataPoint reference, int offsetAfter, int minutesDuration) {
		return getAllInTimeFrame(
			DateUtils.addMinutes(reference.getDate(), offsetAfter),
			DateUtils.addMinutes(reference.getDate(), offsetAfter + minutesDuration));
	}

	public Readings getBefore(DataPoint reference, int offsetBefore, int minutesDuration) {
		return getAllInTimeFrame(
			DateUtils.addMinutes(reference.getDate(), -offsetBefore),
			DateUtils.addMinutes(reference.getDate(), -offsetBefore-minutesDuration));
	}

	public Double getClosestReading(DataPoint reference) {
		for (int i = indexOf(reference); i >= 0; i--) {
			Double glucose = get(i).getAttribute("glucose");
			if (glucose != null) {
				return glucose;
			}
		}
		return 0;
	}

	public boolean hasActivity(String ...activityNames) {
		for (DataPoint dp : this) {
			for (String activityName : activityNames) {
				if (activityName.equals(dp.getActivityName())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasActivityAfter(DataPoint reference, int minutesAfter, String ...activityNames) {
		return getAfter(reference, minutesAfter).hasActivity(activityNames);
	}

	public boolean hasActivityBefore(DataPoint reference, int minutesBefore, String ...activityNames) {
		return getBefore(reference, minutesBefore).hasActivity(activityNames);
	}

	public Double getAverageGlucose() {
		int i = 0;
		Double totalGlucose = 0;
		for (DataPoint dp : this) {
			Double glucose = dp.getAttribute("glucose");
			if (glucose != null) {
				totalGlucose += glucose;
				i++;
			}
		}
		return totalGlucose / i;
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
