package dtu.insulinToolbox;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;

import com.opencsv.exceptions.CsvValidationException;

import dtu.insulinToolbox.model.DataPoint;
import dtu.insulinToolbox.model.Readings;
import dtu.insulinToolbox.reader.FreeStyleLibreReader;

public class InsulinToolbox {

	public static void main(String[] args) throws IOException, CsvValidationException, ParseException {

		FreeStyleLibreReader reader = new FreeStyleLibreReader(args[0]);
		Readings readings = reader.read();
		DecimalFormat df = new DecimalFormat("#.##");
		
		for (DataPoint dp : readings.getAllWithActivityName(DataPoint.ACTIVITY_NAME_RAPID_INSULIN)) {
			boolean hadEatingCorrectionBefore = readings.hasActivityBefore(dp, 45,
				DataPoint.ACTIVITY_NAME_EATING,
				DataPoint.ACTIVITY_NAME_LONG_LASTING_INSULIN);
			boolean hadEatingCorrectionAfter = readings.hasActivityAfter(dp, 180,
				DataPoint.ACTIVITY_NAME_EATING,
				DataPoint.ACTIVITY_NAME_LONG_LASTING_INSULIN);
			if (!hadEatingCorrectionBefore && !hadEatingCorrectionAfter) {
				Double current = readings.getClosestReading(dp);
				Double insulin = dp.getAttribute("units");
				System.out.println("Candidate correction at " + dp.getDate());
				System.out.println("Avg glucose 45 mins before: " + df.format(readings.getBefore(dp, 45).getAverageGlucose()));
				System.out.println("Glucose was " + current);
				System.out.println("Gave " + insulin + " units of rapid insulin");
				System.out.println("from t to t + 1h:");
				Double nextHour = readings.getAfter(dp, 60).getAverageGlucose();
				System.out.println("  avg glucose: " + df.format(nextHour));
				System.out.println("  insulin decreased of " + df.format(current-nextHour) + ", 1 unit decreased of " + df.format((current-nextHour)/insulin));
				System.out.println("from t + 1h to t + 2h:");
				Double next1Hour = readings.getAfter(dp, 60, 60).getAverageGlucose();
				System.out.println("  avg glucose: " + df.format(next1Hour));
				System.out.println("  insulin decreased of " + df.format(current-next1Hour) + ", 1 unit decreased of  " + df.format((current-next1Hour)/insulin));
				System.out.println("from t + 2h to t + 3h:");
				Double next2Hour = readings.getAfter(dp, 120, 60).getAverageGlucose();
				System.out.println("  avg glucose: " + df.format(next2Hour));
				System.out.println("  insulin decreased of " + df.format(current-next2Hour) + ", 1 unit decreased of " + df.format((current-next2Hour)/insulin));
				System.out.println("from t + 3h to t + 4h:");
				Double next3Hour = readings.getAfter(dp, 180, 60).getAverageGlucose();
				System.out.println("  avg glucose: " + df.format(next3Hour));
				System.out.println("  insulin decreased of " + df.format(current-next3Hour) + ", 1 unit decreased of " + df.format((current-next3Hour)/insulin));

				System.out.println("---");
			}
		}

		System.out.println("done");
	}
}
