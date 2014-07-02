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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Overall Concierge test suite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ LocalBundleStorageTest.class,
		RequireBundleSystemBundleTest.class, Slf4jLibraryV172Test.class,
		JavaxLibrariesTest.class, FrameworkLaunchArgsTest.class,
		GoogleLibraryTest.class, ApacheFelixServicesTest.class,
		EclipseEquinoxTest.class, EclipseJettyTest.class, EclipseEMFTest.class,
		EclipseSmartHomeTest.class, EclipseKuraTest.class })
public class ConciergeTestSuite {

}
