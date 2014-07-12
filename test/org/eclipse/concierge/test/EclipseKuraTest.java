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
 * Tests whether Eclipse Kura can be installed and started in Concierge. There
 * are no tests which check whether it is really working in Concierge.
 * 
 * @author Jochen Hiller
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EclipseKuraTest extends AbstractConciergeTestCase {

	private static final String B_KURA(String bundleName) {
		return bundleName + "_0.2.0-SNAPSHOT.v201407012209" + ".jar";

	}

	@Override
	protected boolean stayInShell() {
		return false;
	}

	/**
	 * This test checks whether the included log4j bundles can be installed and
	 * started.
	 * 
	 * The log4j bundles will be installed first. The log4j bundle will be
	 * started. The fragment log4j.extras will be resolved implicitly.
	 * 
	 * There was a bug identified in Concierge about conflicts checking in
	 * fragments, for more details see <a
	 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=436724"
	 * >https://bugs.eclipse.org/bugs/show_bug.cgi?id=436724</a>.
	 * 
	 * Note: log4j and log4j.extra bundles requires a lot of
	 * <code>javax.*</code> packages, which needs to be added to system extra
	 * packages.
	 */
	@Test
	public void test01Log4j() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			launchArgs
					.put("org.osgi.framework.system.packages.extra",
							"javax.management,javax.naming,javax.xml.parsers,"
									+ "org.w3c.dom,org.xml.sax,org.xml.sax.helpers,"
									+ "javax.jmdns,"
									+ "javax.xml.transform,javax.xml.transform.dom,"
									+ "javax.xml.transform.sax,javax.xml.transform.stream");
			launchArgs.put("org.eclipse.concierge.debug", "true");
			launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
			startFramework(launchArgs);

			// check log4j
			final String[] bundleNames = new String[] { "log4j_1.2.17.jar",
					"log4j.apache-log4j-extras_1.1.0.jar" };
			final Bundle[] bundles = installBundles(bundleNames);
			bundles[0].start();
			assertBundlesResolved(bundles);
		} finally {
			stopFramework();
		}
	}

	/**
	 * This test checks whether the included log4j bundles AND slf4j
	 * implementation can be installed and started.
	 * 
	 * All bundles will be installed first. The log4j bundle and slf4j.api
	 * bundle will be started. By starting the slf4j.api bundle, the fragment
	 * slf4j.log4j bundle will resolved implicitly.
	 * 
	 * There was a bug identified in Concierge about conflicts checking in
	 * fragments, for more details see <a
	 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=436724"
	 * >https://bugs.eclipse.org/bugs/show_bug.cgi?id=436724</a>.
	 * 
	 * Note: log4j/slf4j requires some <code>javax.*</code> packages, which
	 * needs to be added to system extra packages.
	 */
	@Test
	public void test02Slf4j() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			launchArgs.put("org.osgi.framework.system.packages.extra",
					"javax.management,javax.naming,javax.xml.parsers,"
							+ "org.w3c.dom,org.xml.sax,org.xml.sax.helpers");
			launchArgs.put("org.eclipse.concierge.debug", "true");
			launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
			startFramework(launchArgs);

			// check log4j and slf4j
			final String[] bundleNames = new String[] { "log4j_1.2.17.jar",
					"slf4j.api_1.6.4.jar", "slf4j.log4j12_1.6.0.jar" };
			final Bundle[] bundles = installBundles(bundleNames);
			bundles[0].start();
			bundles[1].start();
			assertBundlesResolved(bundles);
		} finally {
			stopFramework();
		}
	}

	/**
	 * This test checks whether soda.comm bundle can be installed and started.
	 * 
	 * There was a bug that bundle does fail on startup with a
	 * NullPointerException because some Java system properties will be used,
	 * which are only set oin Equinox and NOT in Concierge. See <a
	 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=436725"
	 * >https://bugs.eclipse.org/bugs/show_bug.cgi?id=436725</a>
	 */
	@Test
	public void test03SodaComm() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			launchArgs.put("org.eclipse.concierge.debug", "true");
			launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
			startFramework(launchArgs);

			// check Soda Comm
			// When test case fails, these properties have to be set to get
			// BundleActivator working
			// System.setProperty("osgi.install.area", "file:./someValue");
			// System.setProperty("osgi.bundles", "");
			final String[] bundleNames = new String[] { "org.eclipse.soda.dk.comm_1.2.0.jar", };
			final Bundle[] bundles = installAndStartBundles(bundleNames);
			assertBundlesResolved(bundles);
		} finally {
			stopFramework();
		}
	}

	@Test
	public void test04USB() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();

			launchArgs
					.put("org.osgi.framework.system.packages.extra",
					// for javax.usb.common
							"javax.swing,javax.swing.tree,javax.swing.border,javax.swing.event");
			launchArgs.put("org.eclipse.concierge.debug", "true");
			launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
			startFramework(launchArgs);

			// check USB bundles
			final String[] bundleNames = new String[] {
					"javax.usb.api_1.0.2.jar", "javax.usb.common_1.0.2.jar", };
			final Bundle[] bundles = installAndStartBundles(bundleNames);
			assertBundlesResolved(bundles);
		} finally {
			stopFramework();
		}
	}

	@Test
	/**
	 * This test checks install and start of all Kura bundles.
	 * 
	 * These bundles are not yet included:
	 */
	public void test10EclipseKura() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			launchArgs
					.put("org.osgi.framework.system.packages.extra",
							// for log4j and slf4j
							"javax.management,javax.naming,javax.xml.parsers,"
									+ "org.w3c.dom,org.xml.sax,org.xml.sax.helpers,"
									// for jetty
									+ "javax.net,javax.net.ssl,"
									+ "javax.security,javax.security.auth,javax.security.cert,"
									+ "javax.naming,javax.sql,"
									+ "org.ietf.jgss,"
									// for core.configuration
									+ "javax.xml.bind,javax.xml.bind.annotation,"
									+ "javax.xml.bind.annotation.adapters,javax.xml.bind.util,"
									+ "javax.xml.namespace,javax.xml.stream,"
									+ "org.w3c.dom,"
									// for kura.core
									+ "javax.crypto,javax.crypto.spec,"
									// for javax.usb.common
									+ "javax.swing,javax.swing.tree,javax.swing.border,javax.swing.event");
			launchArgs.put("org.eclipse.concierge.debug", "true");
			launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
			startFramework(launchArgs);

			// start log4j and slf4j first
			final String[] log4jBundleNames = new String[] {
					"log4j_1.2.17.jar", "slf4j.api_1.6.4.jar",
					"slf4j.log4j12_1.6.0.jar", };
			final Bundle[] log4jBundles = installBundles(log4jBundleNames);
			log4jBundles[0].start();
			log4jBundles[1].start();
			assertBundlesResolved(log4jBundles);

			final String[] jettyBundleNames = new String[] {
					"javax.servlet_3.0.0.v201112011016.jar",
					"org.eclipse.jetty.util_8.1.3.v20120522.jar",
					"org.eclipse.jetty.io_8.1.3.v20120522.jar",
					"org.eclipse.jetty.http_8.1.3.v20120522.jar",
					"org.eclipse.jetty.continuation_8.1.3.v20120522.jar",
					"org.eclipse.jetty.server_8.1.3.v20120522.jar",
					"org.eclipse.jetty.security_8.1.3.v20120522.jar",
					"org.eclipse.jetty.servlet_8.1.3.v20120522.jar", };
			final Bundle[] jettyBundles = installAndStartBundles(jettyBundleNames);
			assertBundlesResolved(jettyBundles);

			final String[] gogoBundleNames = new String[] {
					"org.apache.felix.gogo.runtime_0.8.0.v201108120515.jar",
					"org.apache.felix.gogo.command_0.8.0.v201108120515.jar",
					"org.apache.felix.gogo.shell_0.8.0.v201110170705.jar", };
			final Bundle[] gogoBundles = installAndStartBundles(gogoBundleNames);
			assertBundlesResolved(gogoBundles);

			final String[] equinoxBundleNames = new String[] {
					"osgi.core-permission-5.0.0.jar",
					"org.eclipse.osgi.services_3.4.0.v20140312-2051.jar",
					"org.eclipse.equinox.supplement_1.5.100.v20140428-1446.jar",
					"org.eclipse.equinox.util_1.0.500.v20130404-1337.jar",
					"org.apache.felix.gogo.runtime_0.10.0.v201209301036.jar",
					"org.eclipse.equinox.console_1.1.0.v20140131-1639.jar",
					"org.eclipse.equinox.ds_1.4.200.v20131126-2331.jar" };
			final Bundle[] equinoxBundles = installBundles(equinoxBundleNames);
			equinoxBundles[0].start();
			assertBundleResolved(equinoxBundles[0]);
			equinoxBundles[1].start();
			assertBundleResolved(equinoxBundles[1]);
			equinoxBundles[2].start();
			assertBundleResolved(equinoxBundles[2]);
			equinoxBundles[3].start();
			assertBundleResolved(equinoxBundles[3]);
			equinoxBundles[4].start();
			assertBundleResolved(equinoxBundles[4]);
			// TODO console does not resolve
			// equinoxBundles[5].start();
			// equinoxBundles[6].start();
			// assertBundleResolved(equinoxBundles[6]);

			final String[] bundleNames = new String[] {
					// o.e.kura.api and deps
					"javax.usb.api_1.0.2.jar",
					"org.eclipse.soda.dk.comm_1.2.0.jar",
					"osgi.cmpn_4.3.0.201111022214.jar",
					"org.eclipse.equinox.io_1.0.400.v20120522-2049.jar",
					B_KURA("org.eclipse.kura.api"),

					// o.e.kura.core and deps
					"mqtt-client_0.4.0.jar",
					"org.hsqldb.hsqldb_2.3.0.jar",
					B_KURA("org.eclipse.kura.core"),

					// o.e.kura.core.cloud and deps
					"org.apache.servicemix.bundles.protobuf-java_2.4.1.1.jar",
					B_KURA("org.eclipse.kura.core.cloud"),

					// o.e.kura.core.comm and deps
					B_KURA("org.eclipse.kura.core.comm"),

					// o.e.kura.core.configuration and deps
					B_KURA("org.eclipse.kura.core.configuration"),

					// o.e.k.core.crypto and deps
					B_KURA("org.eclipse.kura.core.crypto"),

					// o.e.kura.core.deployment and deps
					"org.apache.commons.io_2.4.0.jar",
					B_KURA("org.eclipse.kura.deployment.agent"),
					B_KURA("org.eclipse.kura.core.deployment"),

					// o.e.kura.core.net and deps
					B_KURA("org.eclipse.kura.core.net"),

					// o.e.kura.linux
					"org.apache.commons.net_3.1.0.v201205071737.jar",
					B_KURA("org.eclipse.kura.linux.clock"),

					// o.e.kura.linux.command and deps
					B_KURA("org.eclipse.kura.linux.command"),

					// o.e.kura.net.admin and deps
					B_KURA("org.eclipse.kura.linux.net"),
					// TODO does fail with osgi.util.tracker, Concierge issue?
					// B_KURA("org.eclipse.kura.net.admin")

					// o.e.kura.linux.position
					B_KURA("org.eclipse.kura.linux.position"),

					// o.e.kura.linux.usb
					"javax.usb.common_1.0.2.jar",
					B_KURA("org.eclipse.kura.linux.usb"),
					// o.e.kura.linux.watchdog
					B_KURA("org.eclipse.kura.linux.watchdog"),

					// o.e.kura.web
					"org.apache.commons.fileupload_1.2.2.v20111214-1400.jar",
			// B_KURA("org.eclipse.kura.web"),
			//
			};

			final Bundle[] bundles = installAndStartBundles(bundleNames);
			assertBundlesResolved(bundles);
		} finally {
			stopFramework();
		}
	}
}
