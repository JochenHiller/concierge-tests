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

import org.eclipse.concierge.test.util.SyntheticBundleBuilder;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.osgi.framework.Bundle;

/**
 * @author Jochen Hiller
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConciergeExtensionsTest extends AbstractConciergeTestCase {

	/**
	 * This test will install a fragment bundle to framework
	 * org.eclipse.concierge.
	 * 
	 * TODO when this test fails, other tests will fail later. Assumption is
	 * that in case of exception, resources will NOT be cleaned up correctly. we
	 * have to check all open() methods for bundles, whether they will be
	 * closed() in finally.
	 * 
	 * <pre>
	 * java.lang.NullPointerException
	 * 	at org.eclipse.concierge.Concierge$CapabilityRegistry.getByValue(Concierge.java:4744)
	 * 	at org.eclipse.concierge.RFC1960Filter.filterWithIndex(RFC1960Filter.java:1157)
	 * 	at org.eclipse.concierge.Concierge$9.findProviders(Concierge.java:2231)
	 * 	at org.eclipse.concierge.Concierge$ResolverImpl.resolveResource(Concierge.java:2748)
	 * 	at org.eclipse.concierge.Concierge$ResolverImpl.resolve0(Concierge.java:2560)
	 * 	at org.eclipse.concierge.Concierge.resolve(Concierge.java:2206)
	 * 	at org.eclipse.concierge.Concierge.resolveBundles(Concierge.java:1968)
	 * 	at org.eclipse.concierge.test.AbstractConciergeTestCase.enforceResolveBundle(AbstractConciergeTestCase.java:160)
	 * 	at org.eclipse.concierge.test.ConciergeExtensionsTest.test01FragmentBundleOfConcierge(ConciergeExtensionsTest.java:40)
	 * </pre>
	 */
	@Test
	@Ignore
	public void test01FrameworkExtensionFragmentOfConcierge() throws Exception {
		try {
			startFramework();

			// install pseudo bundle
			SyntheticBundleBuilder builder = SyntheticBundleBuilder.newBuilder();
			builder.bundleSymbolicName(
					"concierge.test.test01FrameworkExtensionFragmentOfConcierge")
					.addManifestHeader("Bundle-Version", "1.0.0")
					.addManifestHeader("Fragment-Host",
							"org.eclipse.concierge; extension:=framework");
			final Bundle bundleUnderTest = installBundle(builder);
			enforceResolveBundle(bundleUnderTest);
			assertBundleResolved(bundleUnderTest);
		} finally {
			stopFramework();
		}
	}

	/**
	 * This test will install a fragment bundle to framework system.bundle.
	 * 
	 * TODO when this test fails, other tests will fail later. Assumption is
	 * that in case of exception, resources will NOT be cleaned up correctly. we
	 * have to check all open() methods for bundles, whether they will be
	 * closed() in finally.
	 * 
	 * <pre>
	 * java.lang.NullPointerException
	 * 	at org.eclipse.concierge.Concierge$CapabilityRegistry.getByValue(Concierge.java:4744)
	 * 	at org.eclipse.concierge.RFC1960Filter.filterWithIndex(RFC1960Filter.java:1157)
	 * 	at org.eclipse.concierge.Concierge$9.findProviders(Concierge.java:2231)
	 * 	at org.eclipse.concierge.Concierge$ResolverImpl.resolveResource(Concierge.java:2748)
	 * 	at org.eclipse.concierge.Concierge$ResolverImpl.resolve0(Concierge.java:2560)
	 * 	at org.eclipse.concierge.Concierge.resolve(Concierge.java:2206)
	 * 	at org.eclipse.concierge.Concierge.resolveBundles(Concierge.java:1968)
	 * 	at org.eclipse.concierge.test.AbstractConciergeTestCase.enforceResolveBundle(AbstractConciergeTestCase.java:160)
	 * 	at org.eclipse.concierge.test.ConciergeExtensionsTest.test01FragmentBundleOfConcierge(ConciergeExtensionsTest.java:40)
	 * </pre>
	 */
	@Test
	@Ignore
	public void test02FrameworkExtensionFragmentOfSystemBundle()
			throws Exception {
		try {
			startFramework();

			// install pseudo bundle
			SyntheticBundleBuilder builder = SyntheticBundleBuilder.newBuilder();
			builder.bundleSymbolicName(
					"concierge.test.test02FrameworkExtensionFragmentOfSystemBundle")
					.addManifestHeader("Bundle-Version", "1.0.0")
					.addManifestHeader("Fragment-Host",
							"system.bundle; extension:=framework");
			final Bundle bundleUnderTest = installBundle(builder);
			enforceResolveBundle(bundleUnderTest);
			assertBundleResolved(bundleUnderTest);
		} finally {
			stopFramework();
		}
	}

	/**
	 * This test will install the extension for permissions. As this is actually
	 * a normal bundle, no fragment, check for that it is a fragment as reminder
	 * to change the extension bundle to a fragment later.
	 */
	@Test
	@Ignore
	public void test10ConciergeExtensionPermission() throws Exception {
		try {
			startFramework();

			final Bundle frameworkExtensionBundle = installBundle("org.eclipse.concierge.extension.permission_1.0.0.201408052201.jar");
			enforceResolveBundle(frameworkExtensionBundle);
			assertBundleResolved(frameworkExtensionBundle);

			// check for tests: the extension must be a fragment
			Assert.assertTrue(
					"Check code: permission extension is not a fragment",
					isFragmentBundle(frameworkExtensionBundle));

			// install pseudo bundle
			SyntheticBundleBuilder builder = SyntheticBundleBuilder.newBuilder();
			builder.bundleSymbolicName(
					"concierge.test.test02FrameworkExtensionFragmentOfSystemBundle")
					.addManifestHeader("Bundle-Version", "1.0.0")
					.addManifestHeader("Import-Package",
							"org.osgi.service.condpermadmin, org.osgi.service.permissionadmin");
			final Bundle bundleUnderTest = installBundle(builder);
			enforceResolveBundle(bundleUnderTest);
			assertBundleResolved(bundleUnderTest);
		} finally {
			stopFramework();
		}
	}

}
