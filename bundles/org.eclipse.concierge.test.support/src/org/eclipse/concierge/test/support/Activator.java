package org.eclipse.concierge.test.support;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.xml.sax.SAXException;

public class Activator implements BundleActivator {

	public static BundleContext context;
	public static int noOfCallsOfStart = 0;
	public static int noOfCallsOfStop = 0;
	public static SAXParserFactory saxParserFactory;
	public static SAXParser saxParser;

	public static void clean() {
		context = null;
		noOfCallsOfStart = 0;
		noOfCallsOfStop = 0;
		saxParserFactory = null;
		saxParser = null;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		noOfCallsOfStart++;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		noOfCallsOfStop++;
		Activator.context = null;
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
				context, SAXParserFactory.class.getName(), null);
		tracker.open();
		saxParserFactory = (SAXParserFactory) tracker.getService();
		System.out.println(saxParserFactory);
		saxParser = saxParserFactory.newSAXParser();
		System.out.println(saxParser);
	}

}
