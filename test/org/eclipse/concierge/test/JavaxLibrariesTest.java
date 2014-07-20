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

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * @author Jochen Hiller
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JavaxLibrariesTest extends AbstractConciergeTestCase {

	/**
	 * Bundle install of javax.activation will fail due to missing javax.imageio
	 * packages, which is OK. We will check for correct exception content.
	 */
	@Test
	public void test01JavaxActivationJavaxImageIOMissing() throws Exception {
		startFramework();
		try {
			installAndStartBundle("javax.activation_1.1.0.v201211130549.jar");
		} catch (BundleException ex) {
			// we will expect a resolution failed exception
			Assert.assertTrue("Bundle will not resolve", ex.getMessage()
					.contains("Resolution failed"));
			Assert.assertTrue("Bundle will not resolve", ex.getMessage()
					.contains("javax.imageio"));
			Assert.assertTrue("Bundle will not resolve", ex.getMessage()
					.contains("javax.imageio.metadata"));
		}
		stopFramework();
	}

	/**
	 * Bundle install of javax.activation will work when these packages are
	 * configured as Java system packages when starting OSGi framework AND boot
	 * delegation is set.
	 */
	@Test
	public void test02JavaxActivationJavaxImageIOAsSystemPackageExtraWithBootdelegation()
			throws Exception {
		final Map<String, String> launchArgs = new HashMap<String, String>();
		launchArgs.put("org.osgi.framework.system.packages.extra",
				"javax.imageio,javax.imageio.metadata");
		launchArgs.put("org.osgi.framework.bootdelegation",
				"javax.imageio,javax.imageio.metadata");
		startFramework(launchArgs);
		final Bundle bundle = installAndStartBundle("javax.activation_1.1.0.v201211130549.jar");
		assertBundleResolved(bundle);
		stopFramework();
	}

	/**
	 * Bundle install of javax.activation will work when these packages are
	 * configured as Java system packages when starting OSGi framework.
	 */
	@Test
	public void test03JavaxActivationJavaxImageIOAsSystemPackageExtra()
			throws Exception {
		final Map<String, String> launchArgs = new HashMap<String, String>();
		launchArgs.put("org.osgi.framework.system.packages.extra",
				"javax.imageio,javax.imageio.metadata");
		startFramework(launchArgs);
		final Bundle bundle = installAndStartBundle("javax.activation_1.1.0.v201211130549.jar");
		assertBundleResolved(bundle);
		stopFramework();
	}

	/**
	 * javax.mail does need activation and javax.net.ssl system packages.
	 */
	@Test
	public void test10JavaxMail() throws Exception {
		final Map<String, String> launchArgs = new HashMap<String, String>();
		launchArgs.put("org.osgi.framework.system.packages.extra",
				"javax.imageio,javax.imageio.metadata,javax.net.ssl");
		startFramework(launchArgs);
		final String[] bundleNames = new String[] {
				"javax.activation_1.1.0.v201211130549.jar",
				"javax.mail_1.4.0.v201005080615.jar" };
		final Bundle[] bundles = installAndStartBundles(bundleNames);
		assertBundlesResolved(bundles);
		stopFramework();
	}

	@Test
	public void test11JavaxXmlJars() throws Exception {
		final Map<String, String> launchArgs = new HashMap<String, String>();
		launchArgs.put("org.osgi.framework.system.packages.extra",
				"javax.imageio,javax.imageio.metadata");
		startFramework(launchArgs);
		final String[] bundleNames = new String[] {
				"javax.xml_1.3.4.v201005080400.jar",
				"javax.activation_1.1.0.v201211130549.jar",
				"javax.xml.stream_1.0.1.v201004272200.jar",
				"javax.xml.bind_2.2.0.v201105210648.jar" };
		final Bundle[] bundles = installAndStartBundles(bundleNames);
		assertBundlesResolved(bundles);
		stopFramework();
	}

	/**
	 * This test can reproduce an error in Concierge:
	 * 
	 * <pre>
	 * java.lang.ClassCastException: org.eclipse.concierge.Concierge cannot be cast to org.eclipse.concierge.BundleImpl$Revision
	 * 	at org.eclipse.concierge.BundleImpl$Revision$BundleClassLoader.findResource1(BundleImpl.java:2570)
	 * </pre>
	 * 
	 * To debug, set a breakpoint to ClassCastException.
	 */
	@Test
	public void test12JavaxXmlWireToSystemBundleFails() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			startFramework(launchArgs);
			final String[] bundleNames = new String[] { "javax.xml_1.3.4.v201005080400.jar", };
			final Bundle[] bundles = installAndStartBundles(bundleNames);
			assertBundlesResolved(bundles);

			// install pseudo bundle
			final Map<String, String> manifestEntries = new HashMap<String, String>();
			manifestEntries.put("Bundle-Version", "1.0.0");
			manifestEntries.put("Import-Package", "org.xml.sax");
			final Bundle bundle = installBundle(
					"concierge.test.test12JavaxXMLWireToSystemBundleFails",
					manifestEntries);

			// create class from SAX parser
			RunInClassLoader runner = new RunInClassLoader(bundle);
			Object ex = runner.createInstance("org.xml.sax.SAXException",
					new Object[] {});
			Assert.assertNotNull(ex);
		} finally {
			stopFramework();
		}
	}

	@Test
	public void test20Jackson() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			launchArgs.put("org.osgi.framework.system.packages",
					"javax.imageio,javax.imageio.metadata");
			startFrameworkClean(launchArgs);

			final String[] bundleNames = new String[] {
					"javax.xml_1.3.4.v201005080400.jar",
					"javax.activation_1.1.0.v201211130549.jar",
					"javax.xml.stream_1.0.1.v201004272200.jar",
					"javax.xml.bind_2.2.0.v201105210648.jar",
					"org.codehaus.jackson.core_1.6.0.v20101005-0925.jar",
					"org.codehaus.jackson.mapper_1.6.0.v20101005-0925.jar" };
			final Bundle[] bundles = installAndStartBundles(bundleNames);
			assertBundlesResolved(bundles);
		} finally {
			stopFramework();
		}
	}
}
