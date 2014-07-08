# Concierge-Tests

This project contains test cases for the Eclipse OSGi framework Concierge.
It will be committed at GitHub to make it public available during development.
All the code will be contributed to the Eclipse Concierge project when Gerrit is in place.

## How to use this test cases

1. First check out Concierge

```Shell
git clone git://git.eclipse.org/gitroot/concierge/org.eclipse.concierge.git
```
	
2. Check out Concierge tests

```Shell
git clone https://github.com/JochenHiller/concierge-tests.git
```
	
3. Copy all files from concierge-tests to Concierge project (as tests will be running with Concierge project).
It will assume that both projects are checked out in same directory level.

```Shell
cd concierge-tests
./copy-to-concierge.sh
```

## Overview of testing framework

The tests are based on this general concepts:

* Tests are simple JUnit 4 based unit tests
* The OSGi Concierge framework will be started via Concierge based FrameworkLauncher
* An AbstractConciergeTestCase base class will provide helping methods for simple testing
  * start/stop the framework
  * install and start bundles
  * check for resolved bundles
  * add support for calling code in context of a bundle classloader
* All used 3rd party bundles will be retrieved from their corresponding repositories in Internet
* For performance reasons, these bundles will be locally cached
  * The default folder is `./target/localCache`
* The repos can be configured in file `concierge-test.properties`
* Tests can be running using a simple shell for interactive testing
  * just implement the method `stayInShell` returning true
  * the bundle from `./test/plugins/shell-1.0.0.jar` will be used
* Unit tests will be running in an order by specifying `@FixMethodOrder(MethodSorters.NAME_ASCENDING)` on test classes
* The `ConciergeTestSuite` will run all tests
* Actually the tests are focused on installation and resolving bundles. In most cases there are no tests for checking whether the bundle is really working
* Specific bundles will be added to `./target/<your-dir>` where there are no online bundles available
* "Virtual" Bundles can be installed from test case to avoid duplicate code managemet
  * Manifest file can be specified as properties
  * Code can be executed via reflection in bundle classloader
  * see EMF tests as a sample how to do that

## Overview of Failed Tests

* ClassCastException in Concierge.storeProfile()
  * see https://bugs.eclipse.org/bugs/show_bug.cgi?id=439184
* FrameworkLaunchArgsTest
  * test11SystemPackagesTrailingComma: will fail with ArrayIndexOutOfBoundsException when property ends with a ","
    * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=438784
    * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=438786
  * test13SystemPackagesExtraTrailingComma: will fail with ArrayIndexOutOfBoundsException when property ends with a ","
    * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=438784
    * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=438786
* EclipseEquinoxTest
  * test04EquinoxDS: package condpermadmin is missing in Concierge
    * equinox.console will require condpermadmin: Why?
      * https://bugs.eclipse.org/bugs/show_bug.cgi?id=439182
  * test04EquinoxDS: will fail as org.osgi.framework.namespace has v1.0 in Concierge, and 1.1 is expected
    * see https://bugs.eclipse.org/bugs/show_bug.cgi?id=439180, bug in Equinox console bundle
    * see bug for Concierge, which will be rejected: https://bugs.eclipse.org/bugs/show_bug.cgi?id=438783
  * test04EquinoxDS: will fail as o.e.equinox.console has unresolvable dependencies
  * test05EquinoxRegistry: same as DS
    * Will fail as plugin.propeties can not be loaded
      * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=438781
* EclipseSmartHome
  * test10EclipseSmartHome: TODO
* EclipseKuraTest
  * test01Log4j: will fail as fragment can not be resolved.
    * See bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=436724
  * test02Slf4j: will fail as fragment can not be resolved.
    * See bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=436724
  * test10EclipseKura: will  fail as fragment can not be resolved.
    * See bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=436724

### Eclipse SmartHome running on Concierge

TODO

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

#### Open bugs

* Eclipse Kura dependencies over the OSGI R5 Core Specifications
  * Overall issued to track Kura/Concierge port
  * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=433624
* Added bug and patch for Concierge when handling log4j and its fragment
  * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=436724
* Added a bug for Kura: SODA COMM bundle relies on Equinox specific configuration
  * See ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=436725~~ (resolved)
* Added bug for Kura to rely on Apache Felix SCR import packages which makes a hard dependency to an OSGi DS implementation
  * See ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=436729~~ (resolved)

## References for Concierge

For more details see

* http://projects.eclipse.org/projects/rt.concierge
* git://git.eclipse.org/gitroot/concierge/org.eclipse.concierge.git

## TODO

* Download all bundles when remote URL is a p2-repo
* Add wildcard capability to installBundle to avoid to specify the version
  * Shall use the latest found version of a bundle
* Extend xargs launcher obout wildcard support for simpler startup scripts
* Eclipse SmartHome test for o.e.sh.model.core with split packages


## Equinox protocol

* Clone Equinox bundles repo
  * git clone git://git.eclipse.org/gitroot/equinox/rt.equinox.bundles.git -b R4_4_maintenance
* Xtext
  * git clone git://git.eclipse.org/gitroot/tmf/org.eclipse.xtext.git -b v2.6.x_Maintenance

  
Root exception:
java.lang.NullPointerException
	at org.eclipse.core.internal.runtime.ResourceTranslator.getResourceBundle(ResourceTranslator.java:69)
	at org.eclipse.core.internal.runtime.ResourceTranslator.getResourceBundle(ResourceTranslator.java:61)
	at org.eclipse.core.internal.registry.osgi.EclipseBundleListener.addBundle(EclipseBundleListener.java:174)
	at org.eclipse.core.internal.registry.osgi.EclipseBundleListener.processBundles(EclipseBundleListener.java:90)
	at org.eclipse.core.internal.registry.osgi.RegistryStrategyOSGI.onStart(RegistryStrategyOSGI.java:224)
	at org.eclipse.core.internal.registry.ExtensionRegistry.<init>(ExtensionRegistry.java:725)
	at org.eclipse.core.runtime.RegistryFactory.createRegistry(RegistryFactory.java:58)
	at org.eclipse.core.internal.registry.osgi.Activator.startRegistry(Activator.java:137)
	at org.eclipse.core.internal.registry.osgi.Activator.start(Activator.java:56)
	at org.eclipse.osgi.internal.framework.BundleContextImpl$3.run(BundleContextImpl.java:771)
	at org.eclipse.osgi.internal.framework.BundleContextImpl$3.run(BundleContextImpl.java:1)
	at java.security.AccessController.doPrivileged(Native Method)
	at org.eclipse.osgi.internal.framework.BundleContextImpl.startActivator(BundleContextImpl.java:764)
	at org.eclipse.osgi.internal.framework.BundleContextImpl.start(BundleContextImpl.java:721)
	at org.eclipse.osgi.internal.framework.EquinoxBundle.startWorker0(EquinoxBundle.java:936)
	at org.eclipse.osgi.internal.framework.EquinoxBundle$EquinoxModule.startWorker(EquinoxBundle.java:319)
	at org.eclipse.osgi.container.Module.doStart(Module.java:571)
	at org.eclipse.osgi.container.Module.start(Module.java:439)
	at org.eclipse.osgi.container.ModuleContainer$ContainerStartLevel.incStartLevel(ModuleContainer.java:1582)
	at org.eclipse.osgi.container.ModuleContainer$ContainerStartLevel.incStartLevel(ModuleContainer.java:1562)
	at org.eclipse.osgi.container.ModuleContainer$ContainerStartLevel.doContainerStartLevel(ModuleContainer.java:1533)
	at org.eclipse.osgi.container.ModuleContainer$ContainerStartLevel.dispatchEvent(ModuleContainer.java:1476)
	at org.eclipse.osgi.container.ModuleContainer$ContainerStartLevel.dispatchEvent(ModuleContainer.java:1)
	at org.eclipse.osgi.framework.eventmgr.EventManager.dispatchEvent(EventManager.java:230)
	at org.eclipse.osgi.framework.eventmgr.EventManager$EventThread.run(EventManager.java:340)

  