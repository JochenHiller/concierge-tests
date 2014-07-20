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
public class EclipseSmartHomeTest extends AbstractConciergeTestCase {

	/** extend bundle name with BUILD tag. */
	private static final String B_ESH(String bundleName) {
		return bundleName + "_0.7.0.201407181607" + ".jar";
	}

	private static final String B_EMF(String bundleName) {
		return bundleName + "_2.10.0.v20140514-1158" + ".jar";
	}

	private static final String B_XTEXT(String bundleName) {
		return bundleName + "_2.6.1.v201406120726" + ".jar";
	}

	@Override
	protected boolean stayInShell() {
		return false;
	}

	@Test
	public void test01EclipseSmartHomeIOServiceDiscovery() throws Exception {
		try {
			startFramework();

			final String[] bundleNames = new String[] {
					// o.e.s.io.servicediscovery and deps
					"org.slf4j.api_1.7.2.v20121108-1250.jar",
					B_ESH("org.eclipse.smarthome.io.servicediscovery"),

			};

			final Bundle[] bundles = installAndStartBundles(bundleNames);
			assertBundlesResolved(bundles);
		} finally {
			stopFramework();
		}
	}

	@Test
	public void test05EclipseSmartHomeConfigCore() throws Exception {
		try {
			startFramework();

			final String[] bundleNames = new String[] {
					"org.slf4j.api_1.7.2.v20121108-1250.jar",
					"com.google.guava_15.0.0.v201403281430.jar",
					"org.apache.commons.io_2.0.1.v201105210651.jar",
					"org.apache.commons.lang_2.6.0.v201404270220.jar",
					"org.apache.felix.metatype-1.0.10.jar",
					"org.apache.felix.configadmin-1.8.0.jar",
					"org.apache.felix.scr-1.8.2.jar",
					B_ESH("org.eclipse.smarthome.config.core"), };

			final Bundle[] bundles = installAndStartBundles(bundleNames);
			assertBundlesResolved(bundles);

			// TODO test does work as Activator will be called AFTER DS activate
			// idea how to test that? Extend test.support bundle about method
			// call list?
			RunInClassLoader runner = new RunInClassLoader(bundles[7]);
			Object o = runner
					.getClassField(
							"org.eclipse.smarthome.config.core.internal.ConfigActivator",
							"configurationAdminTracker");
			Assert.assertNotNull(o);
		} finally {
			stopFramework();
		}
	}

	@Test
	public void test10EclipseSmartHome() throws Exception {
		try {
			final Map<String, String> launchArgs = new HashMap<String, String>();
			launchArgs.put("org.osgi.framework.bootdelegation",
					"javax.xml.parsers,org.xml.sax,org.xml.sax.helpers,"
							+ "javax.xml.transform,javax.script");
			launchArgs
					.put("org.osgi.framework.system.packages.extra",
							"javax.imageio,javax.imageio.metadata,"
									+ "javax.net,javax.net.ssl,"
									+ "javax.naming,javax.sql,"
									+ "javax.security,javax.security.auth,javax.security.cert,"
									+ "javax.crypto,javax.crypto.spec,"
									+ "javax.xml.datatype,javax.xml.namespace,javax.xml.parsers,"
									+ "org.xml.sax,org.xml.sax.helpers,org.xml.sax.ext,"
									+ "org.w3c.dom," + "org.ietf.jgss");
			startFrameworkClean(launchArgs);

			// start slf4j first
			final String[] slf4jBundleNames = new String[] {
					"org.slf4j.api_1.7.2.v20121108-1250.jar",
					"ch.qos.logback.core_1.0.7.v20121108-1250.jar",
					"ch.qos.logback.classic_1.0.7.v20121108-1250.jar",
					"ch.qos.logback.slf4j_1.0.7.v20121108-1250.jar" };
			final Bundle[] slf4jBundles = installBundles(slf4jBundleNames);
			slf4jBundles[0].start();
			// resolve fragment, will resolve logback bundles too
			enforceResolveBundle(slf4jBundles[3]);
			assertBundlesResolved(slf4jBundles);

			final String[] jettyBundleNames = new String[] {
					"org.eclipse.concierge.service.xmlparser_1.0.0.201407191653.jar",
					// "javax.xml_1.3.4.v201005080400.jar",
					"javax.activation_1.1.0.v201211130549.jar",
					// "javax.xml.stream_1.0.1.v201004272200.jar",
					// "javax.xml.bind_2.2.0.v201105210648.jar",
					"javax.servlet_3.0.0.v201112011016.jar",
					"org.eclipse.jetty.util_8.1.14.v20131031.jar",
					"org.eclipse.jetty.io_8.1.14.v20131031.jar",
					"org.eclipse.jetty.http_8.1.14.v20131031.jar",
					"org.eclipse.jetty.continuation_8.1.14.v20131031.jar",
					"org.eclipse.jetty.server_8.1.14.v20131031.jar",
					"org.eclipse.jetty.security_8.1.14.v20131031.jar",
					"org.eclipse.jetty.servlet_8.1.14.v20131031.jar" };
			final Bundle[] jettyBundles = installAndStartBundles(jettyBundleNames);
			assertBundlesResolved(jettyBundles);

			final String[] bundleNames = new String[] {
					// o.e.s.core plus deps, use Felix services instead of
					// Equinox ones
					"org.apache.commons.io_2.0.1.v201105210651.jar",
					"org.apache.commons.lang_2.6.0.v201404270220.jar",
					"org.apache.felix.metatype-1.0.10.jar",
					"org.apache.felix.configadmin-1.8.0.jar",
					"org.apache.felix.eventadmin-1.3.2.jar",
					"org.apache.felix.scr-1.8.2.jar",
					"com.google.guava_15.0.0.v201403281430.jar",

					B_ESH("org.eclipse.smarthome.core"),

					// o.e.s.core.autoupdate and deps
					B_ESH("org.eclipse.smarthome.core.autoupdate"),

					B_ESH("org.eclipse.smarthome.core.library"),

					B_ESH("org.eclipse.smarthome.core.persistence"),

					// o.e.s.io.console plus deps
					"com.google.guava_10.0.1.v201203051515.jar",
					"org.eclipse.equinox.supplement_1.5.100.v20140428-1446.jar",
					"org.eclipse.equinox.common_3.6.200.v20130402-1505.jar",
					B_ESH("org.eclipse.smarthome.io.console"),

					B_EMF("org.eclipse.emf.common"),
					B_EMF("org.eclipse.emf.ecore"),
					B_EMF("org.eclipse.emf.ecore.xmi"),
					B_ESH("org.eclipse.smarthome.core.scheduler"),

					// o.e.s.core.scriptengine plus deps
					"org.slf4j.log4j_1.7.2.v20130115-1340.jar",
					"org.antlr.runtime_3.2.0.v201101311130.jar",
					// xtext and its deps
					B_XTEXT("org.eclipse.xtext.util"),
					"javax.inject_1.0.0.v20091030.jar",
					// com.google.inject inject BEFORE xtext, optional depedency
					"com.google.inject_3.0.0.v201312141243.jar",
					B_XTEXT("org.eclipse.xtext"),
					"org.objectweb.asm_5.0.1.v201404251740.jar",
					B_XTEXT("org.eclipse.xtext.common.types"),
					B_XTEXT("org.eclipse.xtext.xbase.lib"),
					B_XTEXT("org.eclipse.xtend.lib"),
					B_XTEXT("org.eclipse.xtext.xbase"),
					B_ESH("org.eclipse.smarthome.core.scriptengine"),

					// o.e.s.io.net
					"org.apache.commons.codec_1.4.0.v201209201156.jar",
					"org.apache.commons.exec_1.1.0.v201301240602.jar",
					"org.apache.commons.logging_1.1.1.v201101211721.jar",
					"org.apache.commons.httpclient_3.1.0.v201012070820.jar",
					"org.apache.commons.net_3.2.0.v201305141515.jar",
					"org.eclipse.osgi.services_3.4.0.v20140312-2051.jar",
					"org.eclipse.equinox.http.servlet_1.1.300.v20120522-1841.jar",
					"org.eclipse.jetty.osgi.httpservice_8.1.14.v20131031.jar",
					B_ESH("org.eclipse.smarthome.io.net"),

					// o.e.s.core.transform and its deps, needs scriptengine and
					// io.net.exec
					"org.apache.commons.collections_3.2.0.v2013030210310.jar",
					B_ESH("org.eclipse.smarthome.config.core"),
					// B_ESH("org.eclipse.smarthome.core.transform"),

					// o.e.s.io.monitor and deps
					B_ESH("org.eclipse.smarthome.io.monitor"),

					// o.e.s.io.servicediscovery and deps
					B_ESH("org.eclipse.smarthome.io.servicediscovery"),

					// o.e.s.model.core and deps
					"org.eclipse.equinox.registry_3.5.400.v20140428-1507.jar",
					B_ESH("org.eclipse.smarthome.model.core"),
					B_ESH("org.eclipse.smarthome.model.item"),
					B_ESH("org.eclipse.smarthome.model.persistence"),
					B_ESH("org.eclipse.smarthome.model.script"),
					B_ESH("org.eclipse.smarthome.model.rule"),
					B_ESH("org.eclipse.smarthome.model.sitemap"),

					// o.e.s.ui and deps
					B_ESH("org.eclipse.smarthome.core.transform"),
					B_ESH("org.eclipse.smarthome.ui"),
					B_ESH("org.eclipse.smarthome.ui.webapp"), };

			final Bundle[] bundles = installAndStartBundles(bundleNames);
			assertBundlesResolved(bundles);
		} finally {
			stopFramework();
		}
	}
}
