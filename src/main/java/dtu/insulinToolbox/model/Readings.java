package dtu.insulinToolbox.model;

import java.util.LinkedList;

public class Readings extends LinkedList<DataPoint> {

	private static final long serialVersionUID = -378788195584503319L;

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
