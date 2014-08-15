package org.eclipse.concierge.compat.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Tests the XargsLauncher with its pattern replacement and wildcard support.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class XargsFileLauncherTest {

	@Test
	public void test01Constructor() {
		new XargsFileLauncher();
	}

	@Test
	public void test02ReplaceVariable() {
		XargsFileLauncher l = new XargsFileLauncher();
		Map<String, String> p = new HashMap<String, String>();
		Assert.assertEquals("${prop1}", l.replaceVariable("${prop1}", p));
		Assert.assertEquals("XXX${prop1}YYY",
				l.replaceVariable("XXX${prop1}YYY", p));
		p.put("prop1", "value1");
		Assert.assertEquals("value1", l.replaceVariable("${prop1}", p));
		Assert.assertEquals("XXXvalue1YYY",
				l.replaceVariable("XXX${prop1}YYY", p));
	}

	@Test
	public void test03ReplaceMultipleVariables() {
		XargsFileLauncher l = new XargsFileLauncher();
		Map<String, String> p = new HashMap<String, String>();
		Assert.assertEquals("${p1}", l.replaceVariable("${p1}", p));
		Assert.assertEquals("XXX${p1}YYY", l.replaceVariable("XXX${p1}YYY", p));
		p.put("p1", "v1");
		p.put("p2", "v2");
		Assert.assertEquals("v1v2", l.replaceVariable("${p1}${p2}", p));
		Assert.assertEquals("XXXv1YYYv2ZZZ",
				l.replaceVariable("XXX${p1}YYY${p2}ZZZ", p));
		Assert.assertEquals("v1v1", l.replaceVariable("${p1}${p1}", p));
		Assert.assertEquals("XXXv1YYYv1ZZZ",
				l.replaceVariable("XXX${p1}YYY${p1}ZZZ", p));
	}

	@Test
	@Ignore
	public void test10ReplaceVariablesPerformance() {
		XargsFileLauncher l = new XargsFileLauncher();
		Map<String, String> p = new HashMap<String, String>();
		p.put("p1", "v1");
		p.put("p2", "v2");

		final int N = 500000;

		System.gc();
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < N; i++) {
			Assert.assertEquals("v1v2", l.replaceVariable("${p1}${p2}", p));
			Assert.assertEquals("XXXv1YYYv2ZZZ",
					l.replaceVariable("XXX${p1}YYY${p2}ZZZ", p));
		}
		long endTime = System.currentTimeMillis();
		System.out.println("test10ReplaceVariablesPerformance: " + N
				+ " runs in " + (endTime - startTime) + " ms.");
	}

	@Test
	public void test20GetPropertiesFromXargsFileEmpty() throws IOException {
		Map<String, String> props = processProperties("");
		Assert.assertEquals(0, props.size());
	}

	@Test
	public void test21GetPropertiesFromXargsFileOneLine() throws IOException {
		Map<String, String> props = processProperties("-Dprop=value");
		Assert.assertEquals(1, props.size());
		Assert.assertEquals("value", props.get("prop"));
	}

	@Test
	public void test21GetPropertiesFromXargsFileTwoLines() throws IOException {
		Map<String, String> props = processProperties("-Dprop1=value1\n-Dprop2=value2");
		Assert.assertEquals(2, props.size());
		Assert.assertEquals("value1", props.get("prop1"));
		Assert.assertEquals("value2", props.get("prop2"));
	}

	@Test
	public void test22GetPropertiesFromXargsSplitOverMultipleLines()
			throws IOException {
		Map<String, String> props = processProperties("-Dprop=value1\\\n value2");
		Assert.assertEquals(1, props.size());
		Assert.assertEquals("value1value2", props.get("prop"));
	}

	@Test
	public void test23GetPropertiesFromXargsSplitOverTwoLinesWithComments()
			throws IOException {
		Map<String, String> props = processProperties("-Dprop=value1\\ # comment \n value2  # comment");
		Assert.assertEquals(1, props.size());
		Assert.assertEquals("value1value2", props.get("prop"));
	}

	@Test
	public void test24GetPropertiesFromXargsSplitOverMultipleLinesWithComments()
			throws IOException {
		Map<String, String> props = processProperties("-Dprop=v1\\ # c \n      v2 \\ # c \n\t\tv3 \\ #\n v4 \\ # comment \n v5 # c");
		Assert.assertEquals(1, props.size());
		Assert.assertEquals("v1v2v3v4v5", props.get("prop"));
	}

	// private helper methods

	private Map<String, String> processProperties(String s) throws IOException {
		XargsFileLauncher l = new XargsFileLauncher();
		File f = createFileFromString(s);
		Map<String, String> props = l.getPropertiesFromXargsFile(f);
		return props;
	}

	private File createFileFromString(final String initXargs)
			throws IOException {
		final File file = File.createTempFile("xargs-", ".xargs");
		final FileOutputStream fos = new FileOutputStream(file);
		final PrintStream ps = new PrintStream(fos);
		ps.println(initXargs);
		ps.close();
		fos.close();
		return file;
	}

}
