package dtu.insulinToolbox.reader;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import dtu.insulinToolbox.model.DataPoint;
import dtu.insulinToolbox.model.Readings;

public class FreeStyleLibreReader {

	private String file;
	private static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	
	public FreeStyleLibreReader(String file) {
		this.file = file;
	}
	
	public Readings read() throws IOException, CsvValidationException, ParseException {
		Readings readings = new Readings();
		
		Reader reader = Files.newBufferedReader(Paths.get(file));
		CSVReader csvReader = new CSVReader(reader);
		String[] nextRecord;
		int lines = 0;
		while ((nextRecord = csvReader.readNext()) != null) {
			lines++;
			if (lines <= 2) continue;
			
			DataPoint dp = null;
			Date date = formatter.parse(nextRecord[2]);
			String type = nextRecord[3];
			if ("0".equals(type)) {
				dp = parseAutomaticReading(date, nextRecord[4]);
			} else {
				dp = parseManualActivity(date, nextRecord[5], nextRecord[7], nextRecord[12], nextRecord[9]);
				if (dp == null) {
					continue;
				}
			}
			dp.setStringAttribute("device", nextRecord[0]);
			dp.setStringAttribute("serial-number", nextRecord[1]);
			
			readings.add(dp);
		}
		csvReader.close();
		
		Collections.sort(readings, new Comparator<DataPoint>() {
			@Override
			public int compare(DataPoint o1, DataPoint o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});
		
		return readings;
	}
	
	private static DataPoint parseAutomaticReading(Date date, String glucose) {
		DataPoint dp = new DataPoint(date);
		dp.setAttribute("glucose", Double.parseDouble(glucose));
		return dp;
	}
	
	private static DataPoint parseManualActivity(Date date, String scan, String rapidInsulin, String longlastingInsulin, String carbs) {
		DataPoint dp = null;
		if (!"".equals(scan)) {
			dp = new DataPoint(date, DataPoint.ACTIVITY_NAME_SCAN);
			dp.setAttribute("glucose", Double.parseDouble(scan));
		} else if (!"".equals(rapidInsulin)) {
			dp = new DataPoint(date, DataPoint.ACTIVITY_NAME_RAPID_INSULIN);
			dp.setAttribute("units", Double.parseDouble(rapidInsulin));
		} else if (!"".equals(longlastingInsulin)) {
			dp = new DataPoint(date, DataPoint.ACTIVITY_NAME_LONG_LASTING_INSULIN);
			dp.setAttribute("units", Double.parseDouble(longlastingInsulin));
		} else if (!"".equals(carbs)) {
			dp = new DataPoint(date, DataPoint.ACTIVITY_NAME_EATING);
			dp.setAttribute("carbs", Double.parseDouble(carbs));
		} else {
			return null;
		}
		return dp;
	}
}
