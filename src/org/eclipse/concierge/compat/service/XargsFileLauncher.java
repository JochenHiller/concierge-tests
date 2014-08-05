/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jan S. Rellermeyer, IBM Research - initial API and implementation
 *******************************************************************************/
package org.eclipse.concierge.compat.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.concierge.BundleImpl;
import org.eclipse.concierge.BundleImpl.Revision;
import org.eclipse.concierge.Concierge;
import org.eclipse.concierge.Factory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.wiring.BundleRevision;

public class XargsFileLauncher {

	/**
	 * process an init.xargs-style file.
	 * 
	 * @param file
	 *            the file.
	 * @return the startlevel.
	 * @throws BundleException
	 * @throws FileNotFoundException
	 * @throws Throwable
	 *             if something goes wrong. For example, if strict startup is
	 *             set and the installation of a bundle fails.
	 */
	public Concierge processXargsFile(final File file) throws BundleException,
			FileNotFoundException {
		final Concierge concierge;
		Map<String, String> passedProperties = new HashMap<String, String>();

		if (Concierge.PATCH_JOCHEN) {
			passedProperties = getPropertiesFromXargsFile(file);
			concierge = (Concierge) new Factory()
					.newFramework(passedProperties);
		} else {
			concierge = (Concierge) new Factory()
					.newFramework(getPropertiesFromXargsFile(file));
		}
		concierge.init();

		final BundleContext context = concierge.getBundleContext();

		int maxLevel = 1;

		if (Concierge.PATCH_JOCHEN) {
			// change: start first
			concierge.start();
		}

		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));

		try {
			final HashMap<String, Bundle> memory = new HashMap<String, Bundle>(
					0);
			String token;
			int initLevel = 1;

			while ((token = reader.readLine()) != null) {
				token = token.trim();
				if (token.equals("")) {
					continue;
				} else if (token.charAt(0) == '#') {
					continue;
				} else if (token.startsWith("-initlevel")) {
					token = getArg(token, 10);
					initLevel = Integer.parseInt(token);
					if (initLevel > maxLevel) {
						maxLevel = initLevel;
					}
					continue;
				} else if (token.startsWith("-all")) {
					final File files[];
					final File jardir = new File(new URL(
							concierge.BUNDLE_LOCATION).getFile());
					files = jardir.listFiles(new FilenameFilter() {
						public boolean accept(File arg0, String arg1) {
							return arg1.toLowerCase().endsWith(".jar")
									|| arg1.toLowerCase().endsWith(".zip");
						}
					});
					if (files == null) {
						System.err.println("NO FILES FOUND IN "
								+ concierge.BUNDLE_LOCATION);
						break;
					}

					for (int i = 0; i < files.length; i++) {
						if (files[i].isDirectory()) {
							continue;
						}
						final BundleImpl b = (BundleImpl) context
								.installBundle(files[i].getName());
						b.setStartLevel(initLevel);
						final Revision rev = (Revision) b
								.adapt(BundleRevision.class);
						if (!rev.isFragment()) {
							b.start();
						}
					}
					continue;
				} else if (token.startsWith("-istart")) {
					token = getArg(token, 7);
					if (Concierge.PATCH_JOCHEN) {
						token = replaceVariable(token, passedProperties);
						token = resolveWildcardName(token);
					}
					final BundleImpl bundle = (BundleImpl) context
							.installBundle(token);
					bundle.setStartLevel(initLevel);
					bundle.start();
				} else if (token.startsWith("-install")) {
					token = getArg(token, 8);
					if (Concierge.PATCH_JOCHEN) {
						token = replaceVariable(token, passedProperties);
						token = resolveWildcardName(token);
					}
					final BundleImpl bundle = (BundleImpl) context
							.installBundle(token);
					bundle.setStartLevel(initLevel);
					memory.put(token, bundle);
				} else if (token.startsWith("-start")) {
					token = getArg(token, 6);
					if (Concierge.PATCH_JOCHEN) {
						token = replaceVariable(token, passedProperties);
						token = resolveWildcardName(token);
					}
					final Bundle bundle = (Bundle) memory.remove(token);
					if (bundle == null) {
						System.err.println("Bundle " + token
								+ " is marked to be started but has not been "
								+ "installed before. Ignoring the command !");
					} else {
						bundle.start();
					}
				}
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException ioe) {

			}
			if (Concierge.PATCH_JOCHEN) {
				// yet started
			} else {
				concierge.start();
			}
		}

		return concierge;
	}

	public Map<String, String> getPropertiesFromXargsFile(final File file)
			throws FileNotFoundException {
		final Map<String, String> properties = new HashMap<String, String>();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));

		try {
			String token;
			while ((token = reader.readLine()) != null) {
				token = token.trim();
				if (token.equals("")) {
					continue;
				} else if (token.charAt(0) == '#') {
					continue;
				} else if (token.startsWith("-D")) {
					token = getArg(token, 2);
					int comment = token.indexOf("#");
					if (comment != -1) {
						token = token.substring(0, comment).trim();
					}
					// get key and value
					int pos = token.indexOf("=");
					if (pos > -1) {
						String key = token.substring(0, pos);
						String value = token.substring(pos + 1);
						// handle multiline properties
						while (value.endsWith("\\")) {
							token = reader.readLine();
							comment = token.indexOf("#");
							if (comment != -1) {
								token = token.substring(0, comment).trim();
							}
							value = value.substring(0, value.length() - 1)
									.trim() + token;
						}
						if (Concierge.PATCH_JOCHEN) {
							value = replaceVariable(value, properties);
						}
						properties.put(key, value);
					}
					continue;
				} else if (token.startsWith("-profile")) {
					token = getArg(token, 8);
					properties.put("ch.ethz.systems.concierge.profile", token);
					continue;
				} else if (token.equals("-init")) {
					properties.put(Constants.FRAMEWORK_STORAGE_CLEAN,
							Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
				} else if (token.startsWith("-startlevel")) {
					token = getArg(token, 11);
					properties.put(Constants.FRAMEWORK_BEGINNING_STARTLEVEL,
							token);
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException ioe) {

			}
		}
		return properties;
	}

	/**
	 * get the argument from a start list entry.
	 * 
	 * @param entry
	 *            the entry.
	 * @param cmdLength
	 *            length of command.
	 * @return the argument.
	 */
	private static String getArg(final String entry, final int cmdLength) {
		// strip command
		final String str = entry.substring(cmdLength);
		// strip comments
		int pos = str.indexOf("#");
		return pos > -1 ? str.substring(0, pos).trim() : str.trim();
	}

	/**
	 * Replace all ${var} entries via its value of properties.
	 */
	private String replaceVariable(String line,
			final Map<String, String> properties) {
		// search property from beginning
		String s = line;
		while (s.matches(".*\\$\\{.*")) {
			final String propertyName = s.substring(s.indexOf("${") + 2,
					s.indexOf("}"));
			final String propertyValue = properties.get(propertyName);
			// replace variable
			if (propertyValue != null) {
				line = line.replaceAll("\\$\\{" + propertyName + "\\}",
						propertyValue);
			}
			// goto next property
			s = s.substring(s.indexOf("}") + 1);
		}
		return line;
	}

	/**
	 * Resolve bundle names with wildcards included.
	 */
	private String resolveWildcardName(final String bundleName) {
		if (!bundleName.contains("*")) {
			return bundleName;
		}
		// TODO how to check http protocol?
		final File dir = new File(bundleName.substring(0,
				bundleName.lastIndexOf("/")));
		// try to use a file filter
		final FileFilter filter = new FileFilter() {
			public boolean accept(final File pathname) {
				final String preStar = bundleName.substring(0,
						bundleName.lastIndexOf("*"));
				final String postStar = bundleName.substring(bundleName
						.lastIndexOf("*") + 1);
				return pathname.getPath().startsWith(preStar)
						&& pathname.getPath().endsWith(postStar);
			}
		};
		final File foundFiles[] = dir.listFiles(filter);
		if ((foundFiles == null) || foundFiles.length == 0) {
			return bundleName; // use default name in case nothing found
		} else if (foundFiles.length == 1) {
			return foundFiles[0].getPath(); // exact match
		} else if (foundFiles.length > 1) {
			final ArrayList<String> sortedFiles = new ArrayList<String>();
			for (int i = 0; i < foundFiles.length; i++) {
				sortedFiles.add(foundFiles[i].getPath());
			}
			Collections.sort(sortedFiles);
			Collections.reverse(sortedFiles);
			return sortedFiles.get(0);
		}
		return bundleName;
	}
}
