package dtu.insulinToolbox.model;

import java.util.Date;
import java.util.LinkedList;

import org.apache.commons.lang3.time.DateUtils;

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
		return getAllWithStringAttributeEqualsTo(DataPoint.ACTIVITY_ATTRIBUTE_NAME, activity);
	}

	public Readings getAllInTimeFrame(Date start, Date end) {
		Readings toReturn = new Readings();
		for (DataPoint dp : this) {
			if ((dp.getDate().equals(start) || dp.getDate().equals(end)) || (dp.getDate().after(start) && dp.getDate().before(end))) {
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
			DateUtils.addMinutes(reference.getDate(), -offsetBefore-minutesDuration),
			DateUtils.addMinutes(reference.getDate(), -offsetBefore));
	}

	public Double getClosestReading(DataPoint reference) {
		// check before
		Date dateBefore = null;
		Double glucoseBefore = 0d;
		for (int i = indexOf(reference); i >= 0; i--) {
			DataPoint dp = get(i);
			Double glucose = dp.getAttribute("glucose");
			if (glucose != null) {
				glucoseBefore = glucose;
				dateBefore = dp.getDate();
				break;
			}
		}
		// check after
		Date dateAfter = null;
		Double glucoseAfter = 0d;
		for (int i = indexOf(reference); i < size(); i++) {
			DataPoint dp = get(i);
			Double glucose = dp.getAttribute("glucose");
			if (glucose != null) {
				glucoseAfter = glucose;
				dateAfter = dp.getDate();
				break;
			}
		}
		
		// comparison
		if (dateAfter != null && dateBefore != null) {
			long minutesBefore = (reference.getDate().getTime() - dateBefore.getTime()) / (60 * 1000);
			long minutesAfter = (dateAfter.getTime() - reference.getDate().getTime()) / (60 * 1000);
			if (minutesBefore < minutesAfter) {
				return glucoseBefore;
			} else {
				return glucoseAfter;
			}
		} else {
			if (dateAfter != null) {
				return glucoseAfter;
			}
			if (dateBefore != null) {
				return glucoseBefore;
			}
		}
		return 0d;
	}
	
	public DataPoint getClosestInsulin(DataPoint reference) {
		// check before
		Date dateBefore = null;
		DataPoint pointBefore = null;
		for (int i = indexOf(reference); i >= 0; i--) {
			DataPoint dp = get(i);
			String name = dp.getActivity();
			if (DataPoint.ACTIVITY_NAME_RAPID_INSULIN.equals(name)) {
				pointBefore = dp;
				dateBefore = dp.getDate();
				break;
			}
		}
		// check after
		Date dateAfter = null;
		DataPoint pointAfter = null;
		for (int i = indexOf(reference); i < size(); i++) {
			DataPoint dp = get(i);
			String name = dp.getActivity();
			if (DataPoint.ACTIVITY_NAME_RAPID_INSULIN.equals(name)) {
				pointAfter = dp;
				dateAfter = dp.getDate();
				break;
			}
		}
		
		// comparison
		if (dateAfter != null && dateBefore != null) {
			long minutesBefore = (reference.getDate().getTime() - dateBefore.getTime()) / (60 * 1000);
			long minutesAfter = (dateAfter.getTime() - reference.getDate().getTime()) / (60 * 1000);
			if (minutesBefore < minutesAfter) {
				return pointBefore;
			} else {
				return pointAfter;
			}
		} else {
			if (dateAfter != null) {
				return pointAfter;
			}
			if (dateBefore != null) {
				return pointBefore;
			}
		}
		return null;
	}
	
	public int countActivity(String ...activityNames) {
		int count = 0;
		for (DataPoint dp : this) {
			for (String activityName : activityNames) {
				if (activityName.equals(dp.getActivity())) {
					return count;
				}
			}
		}
		return count;
	}

	public boolean hasActivity(String ...activityNames) {
		for (DataPoint dp : this) {
			for (String activityName : activityNames) {
				if (activityName.equals(dp.getActivity())) {
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
		double i = 0;
		double totalGlucose = 0d;
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
