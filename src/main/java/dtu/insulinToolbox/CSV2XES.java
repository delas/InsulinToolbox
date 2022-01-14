package dtu.insulinToolbox;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlSerializer;

import com.opencsv.exceptions.CsvValidationException;

import dtu.insulinToolbox.model.DataPoint;
import dtu.insulinToolbox.model.Readings;
import dtu.insulinToolbox.reader.FreeStyleLibreReader;

public class CSV2XES {

	public static final Double THRESHOLD_HYPER = 10d;
	public static final Double THRESHOLD_HYPO = 4d;
	
	private static final SimpleDateFormat CASE_ID_FORMAT = new SimpleDateFormat("YMMdd");
	private static XFactory xesFactory = new XFactoryNaiveImpl();
	
	public static void main(String[] args) throws CsvValidationException, IOException, ParseException {
		
		if (args.length != 2) {
			System.err.println("use: java -jar name.jar INPUT.csv OUTPUT.xes");
			System.exit(1);
		}
		
		System.out.println("Parsing data...");
		FreeStyleLibreReader reader = new FreeStyleLibreReader(args[0]);
		Readings readings = reader.read();

		System.out.println("Constructing raw log...");
		Map<String, LinkedList<DataPoint>> logNoFormat = new HashMap<String, LinkedList<DataPoint>>();
		Set<String> signaledHyper = new HashSet<String>();
		Set<String> signaledHypo = new HashSet<String>();
		for (DataPoint dp : readings) {
			String caseId = CASE_ID_FORMAT.format(dp.getDate());
			if (!logNoFormat.containsKey(caseId)) {
				logNoFormat.put(caseId, new LinkedList<DataPoint>());
			}
			
			if (dp.getActivity() == null) {
				Double glucose = dp.getGlucose();
				// hyper
				if (glucose >= THRESHOLD_HYPER) {
					if (!signaledHyper.contains(caseId)) {
						DataPoint dp2 = new DataPoint(dp.getDate(), "hyper-started");
						dp2.setGlucose(glucose);
						logNoFormat.get(caseId).add(dp2);
						signaledHyper.add(caseId);
					}
				}
				if (glucose < THRESHOLD_HYPER && signaledHyper.contains(caseId)) {
					signaledHyper.remove(caseId);
				}
				
				// hypo
				if (glucose <= THRESHOLD_HYPO) {
					if (!signaledHypo.contains(caseId)) {
						DataPoint dp2 = new DataPoint(dp.getDate(), "hypo-started");
						dp2.setGlucose(glucose);
						logNoFormat.get(caseId).add(dp2);
						signaledHypo.add(caseId);
					}
				}
				if (glucose > THRESHOLD_HYPO && signaledHypo.contains(caseId)) {
					signaledHypo.remove(caseId);
				}
				
			} else {
				logNoFormat.get(caseId).add(dp);
			}
		}
		
		System.out.println("Constructing XES log...");
		XLog log = xesFactory.createLog();
		log.getExtensions().add(XConceptExtension.instance());
		log.getExtensions().add(XTimeExtension.instance());
		log.getClassifiers().add(new XEventNameClassifier());
		for (String caseId : logNoFormat.keySet()) {
			XTrace trace = xesFactory.createTrace();
			XConceptExtension.instance().assignName(trace, caseId);
			for (DataPoint dp : logNoFormat.get(caseId)) {
				XEvent event = xesFactory.createEvent();
				XConceptExtension.instance().assignName(event, dp.getActivity());
				XTimeExtension.instance().assignTimestamp(event, dp.getDate());
				event.getAttributes().put("glucose", xesFactory.createAttributeContinuous("glucose", readings.getClosestReading(dp), null));
				
				if (DataPoint.ACTIVITY_NAME_EATING.equals(dp.getActivity())) {
					event.getAttributes().put("carbs", xesFactory.createAttributeContinuous("carbs", dp.getAttribute("carbs"), null));
				}
				
				if (DataPoint.ACTIVITY_NAME_LONG_LASTING_INSULIN.equals(dp.getActivity()) || DataPoint.ACTIVITY_NAME_RAPID_INSULIN.equals(dp.getActivity())) {
					event.getAttributes().put("units", xesFactory.createAttributeContinuous("units", dp.getAttribute("units"), null));
				}
				
				trace.add(event);
			}
			log.add(trace);
		}
		
		System.out.println("Serializing XES log...");
		XSerializer serializer = new XesXmlSerializer();
		serializer.serialize(log, new FileOutputStream(args[1]));
		
		System.out.println("done");
	}

}
