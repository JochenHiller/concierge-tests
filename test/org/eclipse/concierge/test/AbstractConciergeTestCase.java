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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.eclipse.concierge.Factory;
import org.junit.Assert;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.FrameworkWiring;

/**
 * @author Jochen Hiller
 */
public abstract class AbstractConciergeTestCase {

	protected Framework framework = null;
	protected BundleContext bundleContext = null;
	protected LocalBundleStorage localBundleStorage = LocalBundleStorage
			.getInstance();

	public void startFramework() throws Exception {
		// start OSGi framework
		final Map<String, String> launchArgs = new HashMap<String, String>();
		launchArgs.put("org.eclipse.concierge.debug", "true");
		launchArgs.put("org.osgi.framework.storage.clean", "onFirstInit");
		startFramework(launchArgs);
	}

	public void startFramework(Map<String, String> launchArgs) throws Exception {
		// start OSGi framework
		framework = new Factory().newFramework(launchArgs);
		framework.init();
		framework.start();
		bundleContext = framework.getBundleContext();

		if (stayInShell()) {
			installAndStartBundle("./test/plugins/shell-1.0.0.jar");
		}
	}

	public void stopFramework() throws Exception {
		if (stayInShell()) {
			framework.waitForStop(0);
		} else {
			framework.stop();
			FrameworkEvent event = framework.waitForStop(10000);
			Assert.assertEquals(FrameworkEvent.STOPPED, event.getType());
		}
	}

	// Utilities

	/**
	 * Override when a test case should use shell and wait for manual exit of
	 * framework.
	 */
	protected boolean stayInShell() {
		return false;
	}

	protected Bundle[] installBundles(final String[] bundleNames)
			throws BundleException {
		final Bundle[] bundles = new Bundle[bundleNames.length];
		for (int i = 0; i < bundleNames.length; i++) {
			final String url = this.localBundleStorage
					.getURLForBundle(bundleNames[i]);
			bundles[i] = bundleContext.installBundle(url);
		}
		return bundles;
	}

	protected Bundle[] installAndStartBundles(final String[] bundleNames)
			throws BundleException {
		final Bundle[] bundles = new Bundle[bundleNames.length];
		for (int i = 0; i < bundleNames.length; i++) {
			bundles[i] = installAndStartBundle(bundleNames[i]);
		}
		return bundles;
	}

	/**
	 * Install a bundle for given name. Will check whether bundle can be
	 * resolved.
	 */
	protected Bundle installAndStartBundle(final String bundleName)
			throws BundleException {
		final String url = this.localBundleStorage.getURLForBundle(bundleName);
		// System.err.println("installAndStartBundle: " + bundleName);

		final Bundle bundle = bundleContext.installBundle(url);

		if (!isFragmentBundle(bundle)) {
			bundle.start();
		}
		return bundle;
	}

	/**
	 * Enforce to call resolve bundle in Concierge framework for the specified
	 * bundle.
	 */
	protected void enforceResolveBundle(final Bundle bundle) {
		// initiate resolver
		framework.adapt(FrameworkWiring.class).resolveBundles(
				Collections.singleton(bundle));
	}

	/** Returns true when the specified bundle is a fragment. */
	protected boolean isFragmentBundle(final Bundle bundle) {
		return (bundle.adapt(BundleRevision.class).getTypes() & BundleRevision.TYPE_FRAGMENT) != 0;
	}

	/** Checks about Bundle RESOLVED state for all bundles. */
	protected void assertBundlesResolved(final Bundle[] bundles) {
		for (int i = 0; i < bundles.length; i++) {
			assertBundleResolved(bundles[i]);
		}
	}

	/** Checks about Bundle RESOLVED or ACTIVE state. */
	protected void assertBundleResolved(final Bundle bundle) {
		if ((bundle.getState() == Bundle.RESOLVED)
				|| (bundle.getState() == Bundle.ACTIVE)) {
			// all fine
		} else {
			Assert.fail("Bundle " + bundle.getSymbolicName() + " needs to be "
					+ getBundleStateAsString(Bundle.RESOLVED) + " or "
					+ getBundleStateAsString(Bundle.ACTIVE) + " but was "
					+ getBundleStateAsString(bundle.getState()));
		}
	}

	/** Checks about Bundle ACTIVE state. */
	protected void assertBundleActive(final Bundle bundle) {
		if (bundle.getState() == Bundle.ACTIVE) {
			// all fine
		} else {
			Assert.fail("Bundle " + bundle.getSymbolicName() + " needs to be "
					+ getBundleStateAsString(Bundle.ACTIVE) + " but was "
					+ getBundleStateAsString(bundle.getState()));
		}
	}

	/**
	 * This method will install a "pseudo" bundle into the framework. The bundle
	 * will get its <code>META-INF/MANIFEST.MF</code> from given headers. The
	 * bundle will be generared as JarOutputStream and installed from
	 * corresponding InpuStream.
	 */
	protected Bundle installBundle(final String bundleName,
			final Map<String, String> headers) throws IOException,
			BundleException {
		// copy MANIFEST to a jar file in memory
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION,
				"1.0");
		manifest.getMainAttributes().put(
				new Attributes.Name(Constants.BUNDLE_MANIFESTVERSION), "2");
		manifest.getMainAttributes().put(
				new Attributes.Name(Constants.BUNDLE_SYMBOLICNAME), bundleName);

		for (Iterator<Map.Entry<String, String>> iter = headers.entrySet()
				.iterator(); iter.hasNext();) {
			final Map.Entry<String, String> entry = iter.next();
			manifest.getMainAttributes().put(
					new Attributes.Name(entry.getKey()), entry.getValue());
		}
		final JarOutputStream jarStream = new JarOutputStream(out, manifest);
		jarStream.close();
		final InputStream is = new ByteArrayInputStream(out.toByteArray());
		final Bundle b = bundleContext.installBundle(bundleName, is);
		out.close();
		return b;
	}

	/** Returns bundle state as readable string. */
	protected String getBundleStateAsString(final int state) {
		switch (state) {
		case Bundle.INSTALLED:
			return "INSTALLED";
		case Bundle.RESOLVED:
			return "RESOLVED";
		case Bundle.ACTIVE:
			return "ACTIVE";
		case Bundle.STARTING:
			return "STARTING";
		case Bundle.STOPPING:
			return "STOPPING";
		case Bundle.UNINSTALLED:
			return "UNINSTALLED";
		default:
			return "UNKNOWN state: " + state;
		}
	}

	/**
	 * The <code>RunInClassLoader</code> class helps to run code in ClassLoader
	 * of the bundle using Java reflection. This allows to call testing code in
	 * context of a bundle without need to have classes and/or compile code for
	 * testing purposes.
	 */
	static class RunInClassLoader {

		private final Bundle bundle;
		private boolean debug = false;

		public RunInClassLoader(Bundle b) {
			this.bundle = b;
		}

		public void debug(boolean debug) {
			this.debug = debug;
		}

		/**
		 * Get a class references based on bundle classloader.
		 */
		public Class<?> getClass(final String className) throws Exception {
			final Class<?> clazz = this.bundle.loadClass(className);
			return clazz;
		}

		public Object getClassField(final String className,
				final String classFieldName) throws Exception {
			final Class<?> clazz = this.bundle.loadClass(className);
			final Field field = clazz.getField(classFieldName);
			if (!Modifier.isStatic(field.getModifiers())) {
				throw new RuntimeException("Oops, field " + field.toString()
						+ " is not static");
			}
			// get the value of field, as class field object == null
			final Object result = field.get(null);
			return result;
		}

		/**
		 * Call a class method for given class name. The method will be detected
		 * based on arguments.
		 */
		public Object callClassMethod(final String className,
				final String classMethodName, final Object[] args)
				throws Exception {
			final Class<?> clazz = this.bundle.loadClass(className);
			// get parameter types from args
			final Class<?>[] parameterTypes = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				parameterTypes[i] = args[i].getClass();
			}
			final Method method = clazz.getDeclaredMethod(classMethodName,
					parameterTypes);
			if (!Modifier.isStatic(method.getModifiers())) {
				throw new RuntimeException("Oops, method " + method.toString()
						+ " is not static");
			}
			// TODO Maybe set accessible if private?
			final Object result = method.invoke(null, args);
			return result;
		}

		public Object createInstance(String className, final Object[] args)
				throws Exception {
			final Class<?> clazz = this.bundle.loadClass(className);
			dumpDeclaredConstructors(clazz);
			// get parameter types from args
			final Class<?>[] parameterTypes = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				parameterTypes[i] = args[i].getClass();
			}
			final Constructor<?> constructor = clazz
					.getDeclaredConstructor(parameterTypes);
			// TODO Maybe set accessible if private?
			final Object result = constructor.newInstance(args);
			return result;
		}

		/**
		 * Call an instance method for given object. The method will be detected
		 * based on types of arguments.
		 */
		public Object callMethod(final Object obj, final String methodName,
				final Object[] args) throws Exception {
			final Class<?> clazz = obj.getClass();
			final Class<?>[] parameterTypes = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				if (args[i] == null) {
					parameterTypes[i] = Object.class;
				} else {
					parameterTypes[i] = args[i].getClass();
				}
			}
			dumpMethods(clazz);
			dumpDeclaredMethods(clazz);
			final Method method = clazz.getMethod(methodName, parameterTypes);
			if (Modifier.isStatic(method.getModifiers())) {
				throw new RuntimeException("Oops, method " + method.toString()
						+ " is static");
			}
			final Object result = method.invoke(obj, args);
			return result;
		}

		/**
		 * Call an instance method for given object. The method will be detected
		 * based on give parameter types. Needed when args have to precise
		 * argument types, e.g. a method is of type Object, but the args is of
		 * type SomeClass.
		 */
		public Object callMethod(final Object obj, final String methodName,
				final Class<?>[] parameterTypes, final Object[] args)
				throws Exception {
			final Class<?> clazz = obj.getClass();
			dumpMethods(clazz);
			dumpDeclaredMethods(clazz);
			final Method method = clazz.getMethod(methodName, parameterTypes);
			if (Modifier.isStatic(method.getModifiers())) {
				throw new RuntimeException("Oops, method " + method.toString()
						+ " is static");
			}
			final Object result = method.invoke(obj, args);
			return result;
		}

		private void dumpMethods(Class<?> clazz) {
			if (!debug) {
				return;
			}
			System.out.println("dumpMethods: " + clazz.getName());
			final Method[] methods = clazz.getMethods();
			for (int i = 0; i < methods.length; i++) {
				System.out.println(methods[i]);
			}
			System.out.println("==================");
		}

		private void dumpDeclaredMethods(Class<?> clazz) {
			if (!debug) {
				return;
			}
			System.out.println("dumpDeclaredMethods: " + clazz.getName());
			final Method[] methods = clazz.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				System.out.println(methods[i]);
			}
			System.out.println("==================");
		}

		private void dumpDeclaredConstructors(Class<?> clazz) {
			if (!debug) {
				return;
			}
			System.out.println("dumpDeclaredConstructors: " + clazz.getName());
			final Constructor<?>[] constructors = clazz
					.getDeclaredConstructors();
			for (int i = 0; i < constructors.length; i++) {
				System.out.println(constructors[i]);
			}
			System.out.println("==================");
		}
	}
}
