# Concierge-Tests

This project contains test cases for the Eclipse OSGi framework Concierge.
It will be committed at GitHub to make it public available during development.
All the code will be contributed to the Eclipse Concierge project when Gerrit is in place.

## How to use this test cases

1. First check out Concierge 
```Shell
git clone git://git.eclipse.org/gitroot/concierge/org.eclipse.concierge.git
```
2.. Check out Concierge tests 
```Shell
git clone https://github.com/JochenHiller/concierge-tests.git
```
3. Copy all files from concierge-tests to Concierge project (as tests will be running within Concierge project). 
It will assume that both projects are checked out in same directory level. 
```Shell
cd concierge-tests
./copy-to-concierge.sh
```

## Overview of testing framework

The tests are based on these general concepts:

* Tests are simple JUnit 4 based unit tests
* The OSGi Concierge framework will be started via Concierge based FrameworkLauncher
* An AbstractConciergeTestCase base class will provide helping methods for simple testing
  * start/stop the framework
  * install and start bundles
  * check for resolved bundles
  * add support for calling Java code in context of a bundle classloader
* All used 3rd party bundles will be retrieved from their corresponding repositories in Internet
* For performance reasons, these bundles will be locally cached
  * The default folder is `./target/localCache`
* The repositories can be configured in file `./test/concierge-test.properties`
* Tests can run within a simple shell for interactive testing
  * just implement the method `MyTests.stayInShell()` returning true
  * the bundle from `./test/plugins/shell-1.0.0.jar` will be used
* Unit tests will run in an order by specifying `@FixMethodOrder(MethodSorters.NAME_ASCENDING)` on test classes
* The `ConciergeTestSuite` will run all tests
* Actually the tests are focused on installation and resolving bundles. In most cases there are no 
functional tests for checking whether the bundle is really working
* Specific bundles will be added to `./target/<some-dir>` where there are no online bundles available
* "Virtual" Bundles can be installed from test case to avoid duplicate code management
  * Manifest file can be specified as properties
  * Code can be executed via reflection in bundle classloader
  * see EMF tests as a sample how to do that

## Overview of Failed Tests

The unit tests showed some bugs in Concierge and some bugs in bundles installed in Concierge due to 
dependencies, in most cases dependencies to Equinox.

### Identified bugs in Concierge

* ["Require-Bundle: system.bundle" directive is not working:](~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=431172~~) (Closed)
* Fragment will not be resolved when having Require-Bundle header included
  * ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=432100~~ (Closed)
* Running Concierge multiple times will result in problems with URL stream handler
  * ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=434670~~ (Closed)
* BundleImpl.checkConflicts fails when Resource.BundleRequirementImpl has no attributes
  * ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=436724~~ (Closed)
* org.osgi.framework.namespace has wrong version in System bundle
  * ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=438783~~ (Rejected)
* Utils.splitString will fail with arg "" (empty string)
  * ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=438784~~ (Closed)
* Concierge.exportSystemBundlePackages will fail with trailing comma
  * ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=438786~~ (Closed)
* ClassCastException in Concierge.storeProfile()
  * ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=439184~~ (Closed)
* OSGi bootdelegation is not supported
  * ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=433345~~ (Closed)
* org.eclipse.osgi.services can NOT be resolved when systempackages property is specified	2014-04-25
  * ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=433346~~ (Rejected)
* Loading of localized files in bundle will fail due to wrong path
  * https://bugs.eclipse.org/bugs/show_bug.cgi?id=438781 (Open)
* org.osgi.service.condpermadmin package is missing in Concierge
  * https://bugs.eclipse.org/bugs/show_bug.cgi?id=439182 (Open)
  
From Harini Siresena:  
  
* Framework system packages incorrectly specifies util.tracker bundle version
  * ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=437884~~ (Closed)

### Identified bugs in other bundles

* Equinox
  * Equinox Console bundle
    * equinox.console will require condpermadmin which is missing in Concierge
      * https://bugs.eclipse.org/bugs/show_bug.cgi?id=439182
    * org.eclipse.equinox.console bundle has wrong version 1.1.0 for "Import-Package: org.osgi.framework.namespace"
      * see https://bugs.eclipse.org/bugs/show_bug.cgi?id=439180 (Open)
      * see ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=438783~~ (Rejected by Concierge)
    * Equinox console bundle has hard dependency to Equinox framework and not to supplement bundle
      * see https://bugs.eclipse.org/bugs/show_bug.cgi?id=439445 (Open)
    * for further work: use concierge-patch-console bundle as intermediate solution, test case will work now
  * Equinox Registry
    * Will fail as plugin.properties can not be loaded
      * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=438781 (Open)
    * TODO workaround for console first
      * TODO just working on that, will fail with provider already set
```Java
org.osgi.framework.BundleException: Error starting bundle [org.eclipse.equinox.registry-3.5.400.v20140428-1507]
	at org.eclipse.concierge.BundleImpl.activate0(BundleImpl.java:500)
	at org.eclipse.concierge.BundleImpl.activate(BundleImpl.java:452)
	at org.eclipse.concierge.BundleImpl.start(BundleImpl.java:409)
	at org.eclipse.concierge.BundleImpl.start(BundleImpl.java:349)
	at org.eclipse.concierge.test.AbstractConciergeTestCase.installAndStartBundle(AbstractConciergeTestCase.java:131)
	at org.eclipse.concierge.test.AbstractConciergeTestCase.installAndStartBundles(AbstractConciergeTestCase.java:114)
	at org.eclipse.concierge.test.EclipseEquinoxTest.test06EquinoxRegistry(EclipseEquinoxTest.java:205)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:606)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:47)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:44)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:271)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:50)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:309)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:459)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:675)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:382)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)
Caused by: org.eclipse.core.runtime.CoreException: Extension registry provider is already set.
	at org.eclipse.core.internal.registry.RegistryProviderFactory.setDefault(RegistryProviderFactory.java:31)
	at org.eclipse.core.internal.registry.osgi.Activator.startRegistry(Activator.java:142)
	at org.eclipse.core.internal.registry.osgi.Activator.start(Activator.java:56)
	at org.eclipse.concierge.BundleImpl.activate0(BundleImpl.java:475)
	... 29 more
```
   Equinox DS
    * for further work: use concierge-patch-console bundle as intermediate solution
      * TODO just working on that, will fail, see below
```Java
org.osgi.framework.BundleException: Error starting bundle [org.eclipse.equinox.ds-1.4.200.v20131126-2331]
	at org.eclipse.concierge.BundleImpl.activate0(BundleImpl.java:500)
	at org.eclipse.concierge.BundleImpl.activate(BundleImpl.java:452)
	at org.eclipse.concierge.BundleImpl.start(BundleImpl.java:409)
	at org.eclipse.concierge.BundleImpl.start(BundleImpl.java:349)
	at org.eclipse.concierge.test.AbstractConciergeTestCase.installAndStartBundle(AbstractConciergeTestCase.java:131)
	at org.eclipse.concierge.test.AbstractConciergeTestCase.installAndStartBundles(AbstractConciergeTestCase.java:114)
	at org.eclipse.concierge.test.EclipseEquinoxTest.test11EquinoxDS(EclipseEquinoxTest.java:260)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:606)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:47)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:44)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:271)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:50)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:309)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:459)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:675)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:382)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)
Caused by: java.lang.NoClassDefFoundError: org/eclipse/osgi/framework/console/CommandProvider
	at java.lang.ClassLoader.defineClass1(Native Method)
	at java.lang.ClassLoader.defineClass(ClassLoader.java:800)
	at org.eclipse.concierge.BundleImpl$Revision$BundleClassLoader.findOwnClass(BundleImpl.java:2809)
	at org.eclipse.concierge.BundleImpl$Revision$BundleClassLoader.findResource1(BundleImpl.java:2583)
	at org.eclipse.concierge.BundleImpl$Revision$BundleClassLoader.findResource0(BundleImpl.java:2533)
	at org.eclipse.concierge.BundleImpl$Revision$BundleClassLoader.findClass(BundleImpl.java:2350)
	at org.eclipse.concierge.BundleImpl$Revision$BundleClassLoader.loadClass(BundleImpl.java:2332)
	at java.lang.Class.getDeclaredConstructors0(Native Method)
	at java.lang.Class.privateGetDeclaredConstructors(Class.java:2493)
	at java.lang.Class.getConstructor0(Class.java:2803)
	at java.lang.Class.newInstance(Class.java:345)
	at org.eclipse.concierge.BundleImpl.activate0(BundleImpl.java:474)
	... 29 more
Caused by: java.lang.ClassNotFoundException: org.eclipse.osgi.framework.console.CommandProvider
	at org.eclipse.concierge.BundleImpl$Revision$BundleClassLoader.findClass(BundleImpl.java:2353)
	at org.eclipse.concierge.BundleImpl$Revision$BundleClassLoader.loadClass(BundleImpl.java:2332)
	... 41 more
```
* EMF
  * EMF will not run on Felix or other OSGi frameworks
    * see ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=328227~~ (Closed)
    * Note: the bundle will raise exceptions during installation. Until now there are just some teste which check 
      whether an EMF example can be used within right classloader.
    * TODO investigate why exceptions will be thrown from EMF
    * TODO provide a more easy way for testing as requested by Ed
* Xtext
  * Xtext will not run on other OSGi frameworks, e.g. Concierge
    * see ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=434490~~
    * Note: Tests are only about install/resolve, no real functional tests
* Eclipse Kura
  * Tests are until now OK, but not yet completed
  * Identified and close bugs in Concierge:
    * BundleImpl.checkConflicts fails when Resource.BundleRequirementImpl has no attributes
      * ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=436724~~ (Closed)
   * Eclipse SODA COMM bundle relies on Equinox, does NOT run on Concierge
      * ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=436725~~ (Closed)
   * Bundle org.eclipse.kura.core.configuration refers to Apache Felix SCR
     * ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=436729~~ (Closed) 
* EclipseSmartHome
  * TODO

### Eclipse Kura running on Concierge

1. How to build Eclipse Kura

See https://github.com/eclipse/kura for details how to build.
```Shell
// build target platform
$ git clone -b develop https://github.com/eclipse/kura.git
$ cd kura/target-platform
$ mvn clean install
// build kura core
$ cd ../kura/
$ mvn -Dmaven.test.skip=true -f manifest_pom.xml clean install
$ mvn -Dmaven.test.skip=true -f pom_pom.xml -Pweb clean install
$ mvn -Dmaven.test.skip=true -f pom_pom.xml clean install
```
2. Unpack distribution file
```Shell
$ cd distrib/target
$ unzip kura-raspberry-pi-jars_0.2.0-SNAPSHOT.zip
```
3. Add the directory with unpacked files to file test/concierge-test.properties

This will extend the paths to be searched for bundles to lookup into Kura distribution.
If we assume that kura is on same directory level as org.eclipse.concierge,
this configuration can be added:

```INI
concierge.test.localDirectories=\
    ../kura/kura/distrib/target/kura-raspberry-pi-jars_0.2.0-SNAPSHOT/kura/plugins:\
    ../kura/kura/distrib/target/kura-raspberry-pi-jars_0.2.0-SNAPSHOT/plugins:\
    ...
```

### Eclipse SmartHome running on Concierge

TODO

## References for Concierge

For more details see

* Concierge project page: http://projects.eclipse.org/projects/rt.concierge
* Concierge Git repo: git://git.eclipse.org/gitroot/concierge/org.eclipse.concierge.git
* Equinox framework repo: git://git.eclipse.org/gitroot/equinox/rt.equinox.framework.git
  * Checkout luna branch
```Shell
git clone git://git.eclipse.org/gitroot/equinox/rt.equinox.framework.git -b R4_4_maintenance
```

* Equinox bundles repo: git://git.eclipse.org/gitroot/equinox/rt.equinox.bundles.git
  * Checkout luna branch
```Shell
git clone git://git.eclipse.org/gitroot/equinox/rt.equinox.bundles.git -b R4_4_maintenance
``` 
* Xtext repo: git://git.eclipse.org/gitroot/tmf/org.eclipse.xtext.git
```Shell
git clone git://git.eclipse.org/gitroot/tmf/org.eclipse.xtext.git -b v2.6.x_Maintenance
```

## TODO

* programmatic patch of bundles
  * patch Manifest
* Use Xtext online repo (check Hudson builds)
* Equinox console is not running of CommandProvider is missing
* Equinox registry test case
* Download all bundles when remote URL is a p2-repo
* Add wildcard capability to installBundle to avoid to specify the version
  * Shall use the latest found version of a bundle
* Extend xargs launcher obout wildcard support for simpler startup scripts
* Eclipse SmartHome test for o.e.sh.model.core with split packages
* Provide a way how EMF (and other bundles) can be used from workspace for testing
  (as requested by Ed Merks)
