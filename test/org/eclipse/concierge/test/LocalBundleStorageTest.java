package org.eclipse.concierge.test;

import java.io.File;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LocalBundleStorageTest {

	@Test
	public void test01Singleton() {
		LocalBundleStorage inst1 = LocalBundleStorage.getInstance();
		Assert.assertNotNull(inst1);
		LocalBundleStorage inst2 = LocalBundleStorage.getInstance();
		Assert.assertNotNull(inst2);
		Assert.assertSame(inst1, inst2);
	}

	@Test
	public void test02ClearLocalBundleCache() {
		LocalBundleStorage inst = LocalBundleStorage.getInstance();
		inst.clearLocalBundleCache();
	}

	@Test
	public void test03PathElements() {
		LocalBundleStorage inst = LocalBundleStorage.getInstance();
		File[] pathElements = inst
				.pathElements("abc:def:ghi:./target:./src:./test");
		Assert.assertEquals(3, pathElements.length);
		Assert.assertEquals("target", pathElements[0].getName());
		Assert.assertEquals("src", pathElements[1].getName());
		Assert.assertEquals("test", pathElements[2].getName());
	}

	@Test
	public void test10FindLocalBundleFound() {
		LocalBundleStorage inst = LocalBundleStorage.getInstance();
		File f = inst.findLocalBundle("shell-1.0.0.jar");
		Assert.assertEquals("./test/plugins/shell-1.0.0.jar", f.getPath());
	}

	@Test
	public void test11FindLocalBundleNotFound() {
		LocalBundleStorage inst = LocalBundleStorage.getInstance();
		File f = inst.findLocalBundle("SOME-NOT-FOUND.jar");
		Assert.assertNull(f);
	}

	@Test
	public void test12FindRemoteBundleFound() {
		LocalBundleStorage inst = LocalBundleStorage.getInstance();
		String s = inst.findRemoteBundle("javax.xml_1.3.4.v201005080400.jar");
		Assert.assertEquals(
				"http://download.eclipse.org/eclipse/updates/4.4/R-4.4-201406061215/plugins/javax.xml_1.3.4.v201005080400.jar",
				s);
	}

	@Test
	public void test13FindeRemoteBundleNotFound() {
		LocalBundleStorage inst = LocalBundleStorage.getInstance();
		String s = inst.findRemoteBundle("SOME-NOT-FOUND.jar");
		Assert.assertNull(s);
	}

	@Test
	public void test14GetUrlForBundle() {
		LocalBundleStorage inst = LocalBundleStorage.getInstance();
		String s = inst.getUrlForBundle("shell-1.0.0.jar");
		Assert.assertEquals("./test/plugins/shell-1.0.0.jar", s);
	}

	@Test
	public void test15GetUrlForBundle() {
		LocalBundleStorage inst = LocalBundleStorage.getInstance();
		String s = inst.getUrlForBundle("org.apache.felix.metatype-1.0.10.jar");
		Assert.assertEquals(
				"./target/localCache/org.apache.felix.metatype-1.0.10.jar", s);
	}

}
