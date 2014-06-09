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

import java.util.HashMap;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.osgi.framework.Bundle;

/**
 * Tests for using Equinox service implementations within Concierge framework.
 * 
 * @author Jochen Hiller
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EclipseEquinoxTest extends AbstractConciergeTestCase {

	/**
	 * Just load OSGi services compendium.
	 */
	@Test
	public void test01EquinoxOSGiServices() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			launchArgs.put("org.eclipse.concierge.debug", "true");
			launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
			startFramework(launchArgs);

			final String bundleName = "org.eclipse.osgi.services_3.3.100.v20130513-1956.jar";
			final Bundle bundle = installAndStartBundle(bundleName);
			assertBundleResolved(bundle);
		} finally {
			stopFramework();
		}
	}

	/**
	 * Just load OSGi services compendium but system packages are defined.
	 */
	@Test
	public void test02EquinoxOSGiServicesWithSystemPackages() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			// the value of this property does not matter. If the property is
			// NOT set this test will work
			launchArgs.put("org.osgi.framework.system.packages.extras",
					"com.example.some.package");
			launchArgs.put("org.eclipse.concierge.debug", "true");
			launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
			startFramework(launchArgs);

			final String bundleName = "org.eclipse.osgi.services_3.3.100.v20130513-1956.jar";
			final Bundle bundle = installAndStartBundle(bundleName);
			assertBundleResolved(bundle);
		} finally {
			stopFramework();
		}
	}

	/**
	 * Equinox DS requires bundles Equinox-Util, and org.osgi.service.cm (from
	 * org.eclipse.osgi.services).
	 * 
	 * But it fails with missing requirements:
	 * 
	 * <pre>
	 * COULD NOT RESOLVE REQUIREMENT BundleRequirement{Import-Package org.eclipse.osgi.framework.log;version="1.0.0"} CANDIDATES WERE []
	 * COULD NOT RESOLVE REQUIREMENT BundleRequirement{Import-Package org.eclipse.osgi.service.debug;version="1.0"} CANDIDATES WERE []
	 * COULD NOT RESOLVE REQUIREMENT BundleRequirement{Import-Package org.eclipse.osgi.service.environment;version="1.2.0"} CANDIDATES WERE []
	 * COULD NOT RESOLVE REQUIREMENT BundleRequirement{Import-Package org.eclipse.osgi.util} CANDIDATES WERE []
	 * </pre>
	 */
	@Test
	public void test03EquinoxSupplement() throws Exception {
		final Map<String, String> launchArgs = new HashMap<String, String>();
		launchArgs.put("org.eclipse.concierge.debug", "true");
		launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
		try {
			startFramework(launchArgs);
			final String bundleName = "org.eclipse.equinox.supplement_1.5.100.v20140428-1446.jar";
			final Bundle bundle = installAndStartBundle(bundleName);
			assertBundleResolved(bundle);
		} finally {
			stopFramework();
		}
	}

	/**
	 * Equinox DS requires bundles some bundles.
	 * <ul>
	 * <li>Equinox-Supplement</li>
	 * <li>Equinox-Console -> Felix Runtime</li>
	 * <li>DS -> CondPermAdmin, PermissionAdmin (which is NOT in Concierge,
	 * added per separate JAR file)</li>
	 * </ul>
	 * 
	 * But it fails with missing requirements:
	 * 
	 * <pre>
	 * COULD NOT RESOLVE REQUIREMENT BundleRequirement{Import-Package org.eclipse.osgi.framework.console} CANDIDATES WERE []
	 * COULD NOT RESOLVE REQUIREMENT BundleRequirement{Import-Package org.eclipse.osgi.service.resolver} CANDIDATES WERE []
	 * </pre>
	 * 
	 * This code would have to be added to Equinox-Supplement.
	 */
	@Test
	@Ignore
	public void test04EquinoxDS() throws Exception {
		final Map<String, String> launchArgs = new HashMap<String, String>();
		launchArgs.put("org.eclipse.concierge.debug", "true");
		launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
		try {
			startFramework(launchArgs);
			final String[] bundleNames = new String[] {
					"osgi.core-condpermadmin-5.0.0.jar",
					"org.eclipse.osgi.services_3.3.100.v20130513-1956.jar",
					"org.eclipse.equinox.supplement_1.5.100.v20140428-1446.jar",
					"org.eclipse.equinox.util_1.0.500.v20130404-1337.jar",
					"org.apache.felix.gogo.runtime_0.10.0.v201209301036.jar",
					"org.eclipse.equinox.console_1.0.100.v20130429-0953.jar",
					"org.eclipse.equinox.ds_1.4.101.v20130813-1853.jar" };
			final Bundle[] bundles = installAndStartBundles(bundleNames);
			assertBundlesResolved(bundles);
		} finally {
			stopFramework();
		}
	}

	/**
	 * Equinox Event requires bundles Equinox-Util and Equinox-Supplement.
	 * 
	 * TODO test does work alone, does fail in test suite
	 */
	@Test
	public void test05EquinoxEvent() throws Exception {
		final Map<String, String> launchArgs = new HashMap<String, String>();
		launchArgs.put("org.eclipse.concierge.debug", "true");
		launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
		try {
			startFramework(launchArgs);
			final String[] bundleNames = new String[] {
					"org.eclipse.osgi.services_3.3.100.v20130513-1956.jar",
					"org.eclipse.equinox.supplement_1.5.100.v20140428-1446.jar",
					"org.eclipse.equinox.util_1.0.500.v20130404-1337.jar",
					"org.eclipse.equinox.event_1.3.100.v20140115-1647.jar" };
			final Bundle[] bundles = installAndStartBundles(bundleNames);
			assertBundlesResolved(bundles);
		} finally {
			stopFramework();
		}
	}

	@Test
	public void test06EquinoxCommon() throws Exception {
		final Map<String, String> launchArgs = new HashMap<String, String>();
		launchArgs.put("org.eclipse.concierge.debug", "true");
		launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
		try {
			startFramework(launchArgs);
			final String[] bundleNames = new String[] {
					"org.eclipse.equinox.supplement_1.5.100.v20140428-1446.jar",
					"org.eclipse.equinox.common_3.6.200.v20130402-1505.jar" };
			final Bundle[] bundles = installAndStartBundles(bundleNames);
			assertBundlesResolved(bundles);
		} finally {
			stopFramework();
		}
	}
}
