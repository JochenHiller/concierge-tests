/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
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

/**
 * Test which tries to install some bundles with basic assumptions.
 * 
 * TODO add more tests and combinations for Bundle-NativeCode, for more OS/ARCH
 * types
 * 
 * @author Jochen Hiller
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OSGiFrameworkBasicTest extends AbstractConciergeTestCase {

	@Test
	public void test01InstallAndStartManifestWithRequireBundleSystemBundle()
			throws Exception {
		startFramework();
		final Map<String, String> manifestEntries = new HashMap<String, String>();
		manifestEntries.put("Bundle-Version", "1.0.0");
		manifestEntries.put("Require-Bundle", "system.bundle");
		final Bundle bundle = installBundle(
				"concierge.test.test01InstallManifestWithRequireBundleSystemBundle",
				manifestEntries);
		bundle.start();
		assertBundleActive(bundle);
		stopFramework();
	}

	@Test
	public void test02InstallAndStartManifestWithImportPackageOSGiNamespae()
			throws Exception {
		startFramework();
		final Map<String, String> manifestEntries = new HashMap<String, String>();
		manifestEntries.put("Bundle-Version", "1.0.0");
		manifestEntries.put("Import-Package",
				"org.osgi.framework.namespace;version=\"[1.0,2.0)\"");
		final Bundle bundle = installBundle(
				"concierge.test.test02InstallManifestWithNamespae",
				manifestEntries);
		bundle.start();
		assertBundleActive(bundle);
		stopFramework();
	}

	@Test
	public void test10InstallAndStartJavaXmlJarFileWithRequireBundleSystemBundle()
			throws Exception {
		startFramework();
		// See OSGi core spec, 10.2.3.7: getSymbolicName()
		// Returns the symbolic name of this Framework. The symbolic name is
		// unique for the implementation of the framework. However, the symbolic
		// name "system.bundle" must be recognized as an alias to the
		// implementation-defined symbolic name since this Framework is also a
		// System Bundle.

		// We get a javax.xml jar file from Orbit which has such a directive
		String url = "http://archive.eclipse.org/tools/orbit/downloads/drops/R20140114142710/repository/plugins/javax.xml_1.3.4.v201005080400.jar";
		final Bundle bundle = bundleContext.installBundle(url);
		// resolve, check if resolved
		enforceResolveBundle(bundle);
		assertBundleResolved(bundle);
		// start, check if active
		bundle.start();
		assertBundleActive(bundle);
		stopFramework();
	}

	/**
	 * This test will checks whether Activator.start()/stop() will be called
	 * only once. Does work.
	 */
	@Test
	public void test20TestNoOfCallsOfActivator() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			// needed as bundle is using these packages from runtime
			launchArgs.put("org.osgi.framework.bootdelegation",
					"javax.xml.parsers,org.xml.sax");
			startFrameworkClean(launchArgs);

			final Bundle[] bundles = installAndStartBundles(new String[] { "org.eclipse.osgi.services_3.4.0.v20140312-2051.jar", });
			assertBundlesResolved(bundles);

			final Bundle bundleUnderTest = installAndStartBundle("org.eclipse.concierge.test.support_1.0.0.jar");
			assertBundleResolved(bundleUnderTest);

			RunInClassLoader runner = new RunInClassLoader(bundleUnderTest);
			Object o;

			// check if 1x started, 0x stopped
			o = runner.getClassField(
					"org.eclipse.concierge.test.support.Monitor",
					"noOfCallsOfStart");
			Assert.assertEquals(1, ((Integer) o).intValue());
			o = runner.getClassField(
					"org.eclipse.concierge.test.support.Monitor",
					"noOfCallsOfStop");
			Assert.assertEquals(0, ((Integer) o).intValue());

			// now stop the bundle
			bundleUnderTest.stop();

			// check if 1x started, 1x stopped
			o = runner.getClassField(
					"org.eclipse.concierge.test.support.Monitor",
					"noOfCallsOfStart");
			Assert.assertEquals(1, ((Integer) o).intValue());
			o = runner.getClassField(
					"org.eclipse.concierge.test.support.Monitor",
					"noOfCallsOfStop");
			Assert.assertEquals(1, ((Integer) o).intValue());

		} finally {
			stopFramework();
		}
	}

	@Test
	public void test30BundleNativeCodeMacOSX_X86_64() throws Exception {
		try {
			startFramework();

			// install pseudo bundle
			final Map<String, String> manifestEntries = new HashMap<String, String>();
			manifestEntries.put("Bundle-Version", "1.0.0");
			manifestEntries
					.put("Bundle-NativeCode",
							"lib/native/someNative.so; osname=MacOSX; processor=x86_64");
			final Bundle bundleUnderTest = installBundle(
					"concierge.test.test30BundleNativeCodeMacOSX",
					manifestEntries);
			enforceResolveBundle(bundleUnderTest);
			final boolean resolved = isBundleResolved(bundleUnderTest);
			Assert.assertEquals(isMacOSX() && isX86_64(), resolved);
		} finally {
			stopFramework();
		}
	}

	@Test
	public void test31BundleNativeCodeMacOSX_X86_64_WithSelectionFilter()
			throws Exception {
		try {
			startFramework();

			// install pseudo bundle
			final Map<String, String> manifestEntries = new HashMap<String, String>();
			manifestEntries.put("Bundle-Version", "1.0.0");
			manifestEntries
					.put("Bundle-NativeCode",
							"lib/native/someNative.so; osname=Linux; processor=ARM; selection-filter=\"(&(kura.arch=armv7_hf))\","
									+ "lib/native/otherNative.so; osname=MacOSX; processor=x86_64");
			final Bundle bundleUnderTest = installBundle(
					"concierge.test.test30BundleNativeCodeMacOSX",
					manifestEntries);
			enforceResolveBundle(bundleUnderTest);
			final boolean resolved = isBundleResolved(bundleUnderTest);
			Assert.assertEquals(isMacOSX() && isX86_64(), resolved);
		} finally {
			stopFramework();
		}
	}

	@Test
	public void test32BundleNativeCodeMacOSX_X86() throws Exception {
		try {
			startFramework();

			// install pseudo bundle
			final Map<String, String> manifestEntries = new HashMap<String, String>();
			manifestEntries.put("Bundle-Version", "1.0.0");
			manifestEntries.put("Bundle-NativeCode",
					"lib/native/someNative.so; osname=MacOSX; processor=x86");
			final Bundle bundleUnderTest = installBundle(
					"concierge.test.test32BundleNativeCodeMacOSX_X86",
					manifestEntries);
			enforceResolveBundle(bundleUnderTest);
			final boolean resolved = isBundleResolved(bundleUnderTest);
			Assert.assertEquals(isMacOSX() && isX86(), resolved);
		} finally {
			stopFramework();
		}
	}

	private boolean isMacOSX() {
		String osname = System.getProperty("os.name");
		return "Mac OS X".equals(osname);
	}

	private boolean isX86_64() {
		String osarch = System.getProperty("os.arch");
		return "x86_64".equals(osarch);
	}

	private boolean isX86() {
		String osarch = System.getProperty("os.arch");
		return "x86".equals(osarch);
	}
}
