/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jochen Hiller
 *******************************************************************************/
package org.eclipse.concierge.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.osgi.framework.Bundle;

/**
 * Test Apache services running in Concierge.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApacheFelixServicesTest extends AbstractConciergeTestCase {

	/**
	 * Apache EventAdmin needs ConfigAdmin and Metatype service.
	 */
	@Test
	public void test01ApacheFelixEventAdminService() throws Exception {
		try {
			startFramework();
			final String[] bundleNames = new String[] {
					"org.apache.felix.metatype-1.0.10.jar",
					"org.apache.felix.configadmin-1.8.0.jar",
					"org.apache.felix.eventadmin-1.3.2.jar", };
			final Bundle[] bundles = installAndStartBundles(bundleNames);
			assertBundlesResolved(bundles);
		} finally {
			stopFramework();
		}
	}

	@Test
	public void test02ApacheFelixDSService() throws Exception {
		try {
			startFramework();
			final String bundleName = "org.apache.felix.scr-1.8.2.jar";
			final Bundle bundle = installAndStartBundle(bundleName);
			assertBundleResolved(bundle);
		} finally {
			stopFramework();
		}
	}

	@Test
	public void test03ApacheFelixGogo() throws Exception {
		try {
			startFramework();
			final String[] bundleNames = new String[] { "org.apache.felix.gogo.runtime_0.10.0.v201209301036.jar", };
			final Bundle[] bundles = installAndStartBundles(bundleNames);
			assertBundlesResolved(bundles);
		} finally {
			stopFramework();
		}
	}

	/**
	 * Equinox DS requires Equinox console, will fail due to missing
	 * resolvements.
	 */
	@Test
	public void test10TestOrderOfActivationOfFelixDS() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			launchArgs.put("org.osgi.framework.bootdelegation",
					"javax.xml.parsers,org.xml.sax");
			launchArgs.put("org.osgi.framework.system.packages.extra",
					"javax.xml.parsers");
			startFrameworkClean(launchArgs);

			final Bundle[] bundles = installAndStartBundles(new String[] {
					"org.eclipse.osgi.services_3.4.0.v20140312-2051.jar",
					"org.apache.felix.scr-1.8.2.jar" });
			assertBundlesResolved(bundles);

			final Bundle bundleUnderTest = installBundle("org.eclipse.concierge.test.support_1.0.0.jar");
			bundleUnderTest.start();
			assertBundleResolved(bundleUnderTest);

			// check the order of Activator and DS activate/deactive calls
			RunInClassLoader runner = new RunInClassLoader(bundleUnderTest);

			/**
			 * this code will essentially do:
			 * 
			 * <pre>
			 * serviceTracker = new ServiceTracker(Activator.getContext(),
			 * 		org.eclipse.concierge.test.support.Service.class.getName(), null);
			 * serviceTracker.open();
			 * return (Service) serviceTracker.getService();
			 * </pre>
			 */
			// first request the service, which should activate component
			// serviceTracker = new
			// ServiceTracker(Activator.getContext(),Service.class.getName(),
			// null);
			Object serviceTracker = runner
					.createInstance(
							"org.osgi.util.tracker.ServiceTracker",
							new String[] { "org.osgi.framework.BundleContext",
									"java.lang.String",
									"org.osgi.util.tracker.ServiceTrackerCustomizer" },
							new Object[] {
									bundleUnderTest.getBundleContext(),
									"org.eclipse.concierge.test.support.Service",
									null });
			Assert.assertEquals("org.osgi.util.tracker.ServiceTracker",
					serviceTracker.getClass().getName());
			// serviceTracker.open();
			Object res1 = runner.callMethod(serviceTracker, "open",
					new Object[] {});
			Assert.assertNull(res1);

			// return (Service) serviceTracker.getService();
			Object res2 = runner.callMethod(serviceTracker, "getService",
					new Object[] {});
			Assert.assertNotNull(res2);
			// instance must be ComponentImpl
			Assert.assertEquals(
					"org.eclipse.concierge.test.support.ComponentImpl", res2
							.getClass().getName());

			// stop bundle again, should call deactivate
			bundleUnderTest.stop();

			// now check the calls
			Object synchronizeList = runner.getClassField(
					"org.eclipse.concierge.test.support.Monitor",
					"callSequence");
			@SuppressWarnings("unchecked")
			List<String> list = new ArrayList<String>(
					(Collection<String>) synchronizeList);
			// 4 entries expected
			Assert.assertEquals(4, list.size());
			for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
				String s = iter.next();
				System.out.println(s);
			}
			Assert.assertEquals("Activator.start", list.get(0));
			Assert.assertEquals("ComponentImpl.activate", list.get(1));
			Assert.assertEquals("ComponentImpl.deactivate", list.get(2));
			Assert.assertEquals("Activator.stop", list.get(3));
		} finally {
			stopFramework();
		}
	}

}
