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
		
		System.out.println(readings);
		
		System.out.println("done");
	}
}
