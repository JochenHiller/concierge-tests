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

/**
 * @author Jochen Hiller
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LauncherArgsTest extends AbstractConciergeTestCase {

	@Test
	public void test01GetClassFromBootdelegationMissing() throws Exception {
		final Map<String, String> launchArgs = new HashMap<String, String>();
		launchArgs.put("org.eclipse.concierge.debug", "true");
		launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
		startFramework(launchArgs);
		final Map<String, String> manifestEntries = new HashMap<String, String>();
		manifestEntries.put("Bundle-Version", "1.0.0");
		final Bundle bundle = installBundle(
				"concierge.test.testGetClassFromBootdelegationMissing",
				manifestEntries);
		enforceResolveBundle(bundle);
		assertBundleResolved(bundle);

		final String className = "javax.imageio.ImageTranscoder";
		RunInClassLoader runner = new RunInClassLoader(bundle);
		try {
			runner.getClass(className);
			Assert.fail("Oops, ClassNotFoundException expected");
		} catch (ClassNotFoundException ex) {
			// OK expected
		}

		stopFramework();
	}

	@Test
	public void test02GetClassFromBootdelegationOK() throws Exception {
		final Map<String, String> launchArgs = new HashMap<String, String>();
		launchArgs.put("org.osgi.framework.bootdelegation", "javax.imageio");
		launchArgs.put("org.eclipse.concierge.debug", "true");
		launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
		startFramework(launchArgs);
		final Map<String, String> manifestEntries = new HashMap<String, String>();
		manifestEntries.put("Bundle-Version", "1.0.0");
		final Bundle bundle = installBundle(
				"concierge.test.testGetClassFromBootdelegationOK",
				manifestEntries);
		enforceResolveBundle(bundle);
		assertBundleResolved(bundle);

		final String className = "javax.imageio.ImageTranscoder";
		RunInClassLoader runner = new RunInClassLoader(bundle);
		Class<?> clazz = runner.getClass(className);
		Assert.assertNotNull(clazz);

		stopFramework();
	}

	@Test
	public void test10SystemPackages() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			launchArgs.put("org.osgi.framework.system.packages",
					"javax.imageio,javax.imageio.metadata");
			launchArgs.put("org.eclipse.concierge.debug", "true");
			launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
			startFramework(launchArgs);
		} finally {
			stopFramework();
		}
	}

	@Test
	public void test10SystemPackagesTrailingComma() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			launchArgs.put("org.osgi.framework.system.packages",
					"javax.imageio,javax.imageio.metadata,");
			launchArgs.put("org.eclipse.concierge.debug", "true");
			launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
			startFramework(launchArgs);
		} finally {
			stopFramework();
		}
	}
}
