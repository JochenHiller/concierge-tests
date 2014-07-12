package org.eclipse.concierge.test.support;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public static BundleContext context;
	public static int noOfCallsOfStart = 0;
	public static int noOfCallsOfStop = 0;

	public static void clean() {
		context = null;
		noOfCallsOfStart = 0;
		noOfCallsOfStop = 0;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		noOfCallsOfStart++;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		noOfCallsOfStop++;
		Activator.context = null;
	}

}
