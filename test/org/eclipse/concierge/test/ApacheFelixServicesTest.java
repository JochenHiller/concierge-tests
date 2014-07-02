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

}
