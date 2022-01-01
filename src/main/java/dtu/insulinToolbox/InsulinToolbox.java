package dtu.insulinToolbox;

import java.io.IOException;
import java.text.ParseException;

import com.opencsv.exceptions.CsvValidationException;

import dtu.insulinToolbox.model.Readings;
import dtu.insulinToolbox.reader.FreeStyleLibreReader;

public class InsulinToolbox {

	public static void main(String[] args) throws IOException, CsvValidationException, ParseException {

		FreeStyleLibreReader reader = new FreeStyleLibreReader(args[0]);
		Readings readings = reader.read();
		
		for (DataPoint dp : readings.getAllWithActivityName(DataPoint.ACTIVITY_NAME_RAPID_INSULIN)) {
			boolean hadEatingCorrectionAfter = readings.hasActivityBefore(dp, 45,
				DataPoint.ACTIVITY_NAME_EATING,
				DataPoint.ACTIVITY_NAME_LONG_LASTING_INSULIN);
			boolean hadEatingCorrectionAfter = readings.hasActivityAfter(dp, 180,
				DataPoint.ACTIVITY_NAME_EATING,
				DataPoint.ACTIVITY_NAME_LONG_LASTING_INSULIN);
			if (!hadEatingCorrectionAfter && !hadEatingCorrectionAfter) {
				Double current = readings.getClosestReading(dp);
				Double insulin = dp.getAttribute("units");
				System.out.println("Candidate correction at " + dp.getDate());
				System.out.println("Avg glucose 45 mins before: " + readings.getBefore(dp, 45).getAverageGlucose());
				System.out.println("Gave " + dp.getAttribute("units") + " units of rapid acting");
				System.out.println("Next hour:");
				Double nextHour = readings.getAfter(dp, 60).getAverageGlucose();
				System.out.println("  avg glucose: " + nextHour);
				System.out.println("  insulin decreased of " + (current-nextHour) + " = " + ((current-nextHour)/insulin));
				System.out.println("Next + 1 hour:");
				Double next1Hour = readings.getAfter(dp, 60, 60).getAverageGlucose();
				System.out.println("  avg glucose: " + next1Hour);
				System.out.println("  insulin decreased of " + (current-next1Hour) + " = " + ((current-next1Hour)/insulin));
				System.out.println("Next + 1 hour:");
				Double next2Hour = readings.getAfter(dp, 120, 60).getAverageGlucose();
				System.out.println("  avg glucose: " + next2Hour);
				System.out.println("  insulin decreased of " + (current-next2Hour) + " = " + ((current-next2Hour)/insulin));
				System.out.println("---");
			}
		}

		System.out.println("done");
	}
}
