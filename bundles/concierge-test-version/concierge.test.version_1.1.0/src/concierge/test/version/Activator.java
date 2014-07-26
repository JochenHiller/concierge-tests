package concierge.test.version;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class Activator implements BundleActivator {

	public void start(BundleContext bundleContext) throws Exception {
		log(bundleContext, "start()");
	}

	public void stop(BundleContext bundleContext) throws Exception {
		log(bundleContext, "stop()");
	}

	private void log(BundleContext bundleContext, String msg) {
		String wholeMsg = "[" + bundleContext.getBundle().getSymbolicName()
				+ ":" + bundleContext.getBundle().getVersion() + "] "
				+ this.getClass().getName() + "." + msg;
		ServiceReference<LogService> sref = bundleContext
				.getServiceReference(LogService.class);
		if (sref != null) {
			LogService service = bundleContext.getService(sref);
			service.log(LogService.LOG_INFO, wholeMsg);
			bundleContext.ungetService(sref);
		} else {
			System.out.println(wholeMsg);
		}
	}
}
