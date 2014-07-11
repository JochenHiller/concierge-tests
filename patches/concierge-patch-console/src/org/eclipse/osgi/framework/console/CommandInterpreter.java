/*******************************************************************************
 * Copyright (c) 2003, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.console;

import java.util.Dictionary;
import org.osgi.framework.Bundle;

/**	
 * A command interpreter is a shell that can interpret command
 * lines. This object is passed as parameter when a CommandProvider
 * is invoked.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @since 3.1
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CommandInterpreter {
	/**
	 * Get the next argument in the input.  If no arguments are left then null is returned.
	 *
	 * E.g. if the commandline is hello world, the _hello method
	 * will get "world" as the first argument.
	 * @return the next argument or null if no arguments are left.
	 */
	public String nextArgument();

	/**
	 * Execute a command line as if it came from the end user
	 * and return the result.
	 * @param cmd The command line to execute.
	 * @return the result of the command.
	 */
	public Object execute(String cmd);

	/**
	 * Prints an object to the outputstream
	 *
	 * @param o	the object to be printed
	 */
	public void print(Object o);

	/**
	 * Prints an empty line to the outputstream
	 */
	public void println();

	/**
	 * Prints an object to the output medium (appended with newline character).
	 * <p>
	 * If running on the target environment the user is prompted with '--more'
	 * if more than the configured number of lines have been printed without user prompt.
	 * That way the user of the program has control over the scrolling.
	 * <p>
	 * For this to work properly you should not embedded "\n" etc. into the string.
	 *
	 * @param	o	the object to be printed
	 */
	public void println(Object o);

	/**
	 * Print a stack trace including nested exceptions.
	 * @param t The offending exception
	 */
	public void printStackTrace(Throwable t);

	/**
	 * Prints the given dictionary sorted by keys.
	 *
	 * @param dic	the dictionary to print
	 * @param title	the header to print above the key/value pairs
	 */
	public void printDictionary(Dictionary<?, ?> dic, String title);

	/**
	 * Prints the given bundle resource if it exists
	 *
	 * @param bundle	the bundle containing the resource
	 * @param resource	the resource to print
	 */
	public void printBundleResource(Bundle bundle, String resource);
}
