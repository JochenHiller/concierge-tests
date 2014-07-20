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
public class FrameworkLaunchArgsTest extends AbstractConciergeTestCase {

	/**
	 * This test will install a bundle which refers to a class from Java runtime
	 * (<code>javax.imageio</code>). As this <code>javax</code> package is
	 * missing in system packages, it can NOT be used and an exception will be
	 * thrown, which is expected behavior.
	 */
	@Test
	public void test01GetClassFromBootdelegationMissing() throws Exception {
		try {
			startFramework();
			final Map<String, String> manifestEntries = new HashMap<String, String>();
			manifestEntries.put("Bundle-Version", "1.0.0");
			final Bundle bundle = installBundle(
					"concierge.test.test01GetClassFromBootdelegationMissing",
					manifestEntries);
			bundle.start();
			assertBundleResolved(bundle);

			final String className = "javax.imageio.ImageTranscoder";
			RunInClassLoader runner = new RunInClassLoader(bundle);
			try {
				runner.getClass(className);
				Assert.fail("Oops, ClassNotFoundException expected");
			} catch (ClassNotFoundException ex) {
				// OK expected
			}
		} finally {
			stopFramework();
		}
	}

	/**
	 * This test will install a bundle which refers to a class from Java runtime
	 * (<code>javax.imageio</code>). As this <code>javax</code> package is added
	 * to bootdelegation, the class be used which is checked via reflection.
	 */
	@Test
	public void test02GetClassFromBootdelegationOK() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			launchArgs
					.put("org.osgi.framework.bootdelegation", "javax.imageio");
			startFrameworkClean(launchArgs);
			final Map<String, String> manifestEntries = new HashMap<String, String>();
			manifestEntries.put("Bundle-Version", "1.0.0");
			final Bundle bundle = installBundle(
					"concierge.test.test02GetClassFromBootdelegationOK",
					manifestEntries);
			bundle.start();
			assertBundleResolved(bundle);

			final String className = "javax.imageio.ImageTranscoder";
			RunInClassLoader runner = new RunInClassLoader(bundle);
			Class<?> clazz = runner.getClass(className);
			Assert.assertNotNull(clazz);
		} finally {
			stopFramework();
		}
	}

	/**
	 * This test checks whether <code>system.packages</code> can be specified as
	 * launcher args.
	 */
	@Test
	public void test10SystemPackages() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			launchArgs.put("org.osgi.framework.system.packages", "p1,p2,p3");
			startFramework(launchArgs);
		} finally {
			stopFramework();
		}
	}

	/**
	 * This test will fail when property
	 * <code>org.osgi.framework.system.packages</code> will contain a trailing
	 * <code>,</code> (Comma).
	 */
	@Test
	public void test11SystemPackagesTrailingComma() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			launchArgs.put("org.osgi.framework.system.packages", "p1,p2,p3,");
			startFramework(launchArgs);
		} finally {
			stopFramework();
		}
	}

	/**
	 * This test checks whether <code>system.packages.extra</code> can be
	 * specified as launcher args.
	 */
	@Test
	public void test12SystemPackagesExtra() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			launchArgs.put("org.osgi.framework.system.packages.extra",
					"p1,p2,p3");
			startFramework(launchArgs);
		} finally {
			stopFramework();
		}
	}

	/**
	 * This test will fail when property
	 * <code>org.osgi.framework.system.packages.extra</code> will contain a
	 * trailing <code>,</code> (Comma).
	 */
	@Test
	public void test13SystemPackagesExtraTrailingComma() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			launchArgs.put("org.osgi.framework.system.packages.extra",
					"p1,p2,p3,");
			startFramework(launchArgs);
		} finally {
			stopFramework();
		}
	}
}
