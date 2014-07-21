package org.eclipse.concierge.test.support;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.xml.sax.SAXException;

public class Activator implements BundleActivator {

	public static BundleContext bundleContext;

	public void start(BundleContext bundleContext) throws Exception {
		Activator.bundleContext = bundleContext;
		Monitor.noOfCallsOfStart++;
		Monitor.addCall("Activator.start");
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Monitor.addCall("Activator.stop");
		Monitor.noOfCallsOfStop++;
		Activator.bundleContext = null;
	}

	/**
	 * This method will check whether a SAXParserFacory and SAXParser can be
	 * created.
	 * 
	 * Assumption:
	 * <ol>
	 * <li>bundle does NOT import javax.xml.parsers, org.xm.sax packages</li>
	 * <li>Java runtime packages have to be used</li>
	 * <li>This requires "org.osgi.framework.bootdelegation" to include these
	 * packages</li>
	 * </ol>
	 */
	public static void checkSAXParserFactory()
			throws ParserConfigurationException, SAXException {
		ServiceTracker<SAXParserFactory, Object> tracker = new ServiceTracker<SAXParserFactory, Object>(
				bundleContext, SAXParserFactory.class.getName(), null);
		tracker.open();
		Monitor.saxParserFactory = (SAXParserFactory) tracker.getService();
		System.out.println(Monitor.saxParserFactory);
		Monitor.saxParser = Monitor.saxParserFactory.newSAXParser();
		System.out.println(Monitor.saxParser);
	}

}
