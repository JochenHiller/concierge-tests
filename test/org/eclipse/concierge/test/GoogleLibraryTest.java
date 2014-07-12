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
public class GoogleLibraryTest extends AbstractConciergeTestCase {

	@Test
	public void test01GoogleGuava() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			launchArgs.put("org.eclipse.concierge.debug", "true");
			launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
			startFramework(launchArgs);
			final Bundle bundle = installAndStartBundle("com.google.guava_15.0.0.v201403281430.jar");
			assertBundleResolved(bundle);
			Assert.assertEquals("com.google.guava", bundle.getSymbolicName());
			Assert.assertEquals("15.0.0.v201403281430", bundle.getVersion()
					.toString());
		} finally {
			stopFramework();
		}
	}

	@Test
	public void test02GoogleInject() throws Exception {
		final Map<String, String> launchArgs = new HashMap<String, String>();
		launchArgs.put("org.osgi.framework.system.packages", "javax.inject");
		launchArgs.put("org.eclipse.concierge.debug", "true");
		launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
		startFramework(launchArgs);
		try {
			final Bundle bundle = installAndStartBundle("com.google.inject_3.0.0.v201312141243.jar");
			assertBundleResolved(bundle);
			Assert.assertEquals("com.google.inject", bundle.getSymbolicName());
			Assert.assertEquals("3.0.0.v201312141243", bundle.getVersion()
					.toString());
		} finally {
			stopFramework();
		}
	}

}
