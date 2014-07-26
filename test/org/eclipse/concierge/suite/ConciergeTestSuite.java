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
package org.eclipse.concierge.suite;

import org.eclipse.concierge.test.ApacheFelixServicesTest;
import org.eclipse.concierge.test.ConciergeExtensionsTest;
import org.eclipse.concierge.test.ConciergeServicesTest;
import org.eclipse.concierge.test.ConciergeXargsTest;
import org.eclipse.concierge.test.EclipseEMFTest;
import org.eclipse.concierge.test.EclipseEquinoxTest;
import org.eclipse.concierge.test.EclipseJettyTest;
import org.eclipse.concierge.test.EclipseKuraTest;
import org.eclipse.concierge.test.EclipseSmartHomeTest;
import org.eclipse.concierge.test.FrameworkLaunchArgsTest;
import org.eclipse.concierge.test.GoogleLibraryTest;
import org.eclipse.concierge.test.JavaxLibrariesTest;
import org.eclipse.concierge.test.LocalBundleStorageTest;
import org.eclipse.concierge.test.OSGiFrameworkBasicTest;
import org.eclipse.concierge.test.Slf4jLibraryV172Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Overall Concierge test suite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ LocalBundleStorageTest.class,
		OSGiFrameworkBasicTest.class, ConciergeExtensionsTest.class,
		ConciergeServicesTest.class, ConciergeXargsTest.class,
		Slf4jLibraryV172Test.class, JavaxLibrariesTest.class,
		FrameworkLaunchArgsTest.class, GoogleLibraryTest.class,
		ApacheFelixServicesTest.class, EclipseEquinoxTest.class,
		EclipseJettyTest.class, EclipseEMFTest.class,
		EclipseSmartHomeTest.class, EclipseKuraTest.class })
public class ConciergeTestSuite {

}
