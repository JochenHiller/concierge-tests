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

	@Override
	protected boolean stayInShell() {
		return false;
	}

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

			final String bundleName = "org.eclipse.osgi.services_3.4.0.v20140312-2051.jar";
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
			launchArgs.put("org.osgi.framework.system.packages.extra",
					"com.example.some.package");
			launchArgs.put("org.eclipse.concierge.debug", "true");
			launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
			startFramework(launchArgs);

			final String bundleName = "org.eclipse.osgi.services_3.4.0.v20140312-2051.jar";
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
	 * BundleRequirement{Import-Package org.eclipse.osgi.framework.console}
	 * BundleRequirement{Import-Package org.eclipse.osgi.report.resolution; version="[1.0,2.0)"}]
	 * BundleRequirement{Import-Package org.osgi.framework.namespace;version="1.1.0"}]
	 * - requires patch in Concierge, specifies 1.0
	 * </pre>
	 */
	@Test
	public void test04EquinoxDS() throws Exception {
		final Map<String, String> launchArgs = new HashMap<String, String>();
		launchArgs.put("org.eclipse.concierge.debug", "true");
		launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
		try {
			startFramework(launchArgs);
			final String[] bundleNames = new String[] {
					"org.eclipse.osgi.services_3.4.0.v20140312-2051.jar",
					"org.eclipse.equinox.supplement_1.5.100.v20140428-1446.jar",
					"org.eclipse.equinox.util_1.0.500.v20130404-1337.jar",
					"org.apache.felix.gogo.runtime_0.10.0.v201209301036.jar",
					// required by Equinox Console, is not optional
					"osgi.core-condpermadmin-5.0.0.jar",
					"org.eclipse.equinox.console_1.1.0.v20140131-1639.jar",
					"org.eclipse.equinox.ds_1.4.200.v20131126-2331.jar" };
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
					"org.eclipse.osgi.services_3.4.0.v20140312-2051.jar",
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

	/**
	 * This test will fail due to console issue.
	 * 
	 * <pre>
	 * org.osgi.framework.BundleException: Resolution failed [
	 * BundleRequirement{Import-Package org.eclipse.osgi.framework.console}, 
	 * BundleRequirement{Import-Package org.eclipse.osgi.report.resolution; version="[1.0,2.0)"}, 
	 * BundleRequirement{Import-Package org.eclipse.osgi.service.environment}, 
	 * BundleRequirement{Import-Package org.eclipse.osgi.util}, 
	 * BundleRequirement{Import-Package org.osgi.framework.namespace;version="1.1.0"}]
	 * - fix in Concierge
	 * </pre>
	 * 
	 * When console plugin will NOT be included, the registry bundle complains:
	 * 
	 * <pre>
	 *  ERROR in org.eclipse.concierge.BundleImpl$Revision$BundleClassLoader@3cb201fd:
	 *  java.lang.NoClassDefFoundError: org/eclipse/osgi/framework/console/CommandProvider
	 *  ...	 
	 *  Caused by: java.lang.ClassNotFoundException: org.eclipse.osgi.framework.console.CommandProvider
	 *  ...
	 * </pre>
	 */
	@Test
	public void test07EquinoxRegistry() throws Exception {
		final Map<String, String> launchArgs = new HashMap<String, String>();
		launchArgs.put("org.osgi.framework.system.packages.extra",
				"javax.xml.parsers,org.xml.sax,org.xml.sax.helpers");
		launchArgs.put("org.eclipse.concierge.debug", "true");
		launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
		try {
			startFramework(launchArgs);
			final String[] bundleNames = new String[] {
					"org.eclipse.osgi.services_3.4.0.v20140312-2051.jar",
					"org.eclipse.equinox.util_1.0.500.v20130404-1337.jar",
					"org.apache.felix.gogo.runtime_0.10.0.v201209301036.jar",
					// required by Equinox Console, is not optional
					"osgi.core-condpermadmin-5.0.0.jar",
					// "org.eclipse.equinox.console_1.1.0.v20140131-1639.jar",
					"org.eclipse.equinox.supplement_1.5.100.v20140428-1446.jar",
					"org.eclipse.equinox.common_3.6.200.v20130402-1505.jar",
					"org.eclipse.equinox.registry_3.5.400.v20140428-1507.jar" };
			final Bundle[] bundles = installAndStartBundles(bundleNames);
			assertBundlesResolved(bundles);
		} finally {
			stopFramework();
		}
	}
}
