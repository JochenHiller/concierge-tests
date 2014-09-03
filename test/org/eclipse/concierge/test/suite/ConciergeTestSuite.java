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
package org.eclipse.concierge.test.suite;

import org.eclipse.concierge.MultiMapTest;
import org.eclipse.concierge.SplitStringTest;
import org.eclipse.concierge.compat.service.XargsFileLauncherTest;
import org.eclipse.concierge.test.ConciergeExtensionsTest;
import org.eclipse.concierge.test.ConciergeServicesTest;
import org.eclipse.concierge.test.ConciergeXargsTest;
import org.eclipse.concierge.test.FrameworkLaunchArgsTest;
import org.eclipse.concierge.test.OSGiFrameworkBasicTest;
import org.eclipse.concierge.test.integration.ApacheFelixServicesTest;
import org.eclipse.concierge.test.integration.EclipseEMFTest;
import org.eclipse.concierge.test.integration.EclipseEquinoxTest;
import org.eclipse.concierge.test.integration.EclipseJettyTest;
import org.eclipse.concierge.test.integration.EclipseKuraTest;
import org.eclipse.concierge.test.integration.EclipsePlatformTest;
import org.eclipse.concierge.test.integration.EclipseSmartHomeTest;
import org.eclipse.concierge.test.integration.GoogleLibraryTest;
import org.eclipse.concierge.test.integration.JavaxLibrariesTest;
import org.eclipse.concierge.test.integration.Slf4jLibraryV172Test;
import org.eclipse.concierge.test.util.LocalBundleStorageTest;
import org.eclipse.concierge.test.util.SyntheticBundleBuilderTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Overall Concierge test suite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ LocalBundleStorageTest.class,
		SyntheticBundleBuilderTest.class, MultiMapTest.class,
		SplitStringTest.class, XargsFileLauncherTest.class,
		ConciergeXargsTest.class, FrameworkLaunchArgsTest.class,
		OSGiFrameworkBasicTest.class, ConciergeServicesTest.class,
		ConciergeExtensionsTest.class, JavaxLibrariesTest.class,
		Slf4jLibraryV172Test.class, GoogleLibraryTest.class,
		ApacheFelixServicesTest.class, EclipseEquinoxTest.class,
		EclipseJettyTest.class, EclipsePlatformTest.class,
		EclipseEMFTest.class, EclipseSmartHomeTest.class,
		EclipseKuraTest.class, })
public class ConciergeTestSuite {

}
