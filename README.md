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

* ~~[#431172 "Require-Bundle: system.bundle" directive is not working](https://bugs.eclipse.org/bugs/show_bug.cgi?id=431172)~~ (Closed)
* ~~[#432100 Fragment will not be resolved when having Require-Bundle header included](https://bugs.eclipse.org/bugs/show_bug.cgi?id=432100)~~ (Closed)
* ~~[#434670 Running Concierge multiple times will result in problems with URL stream handler](https://bugs.eclipse.org/bugs/show_bug.cgi?id=434670)~~ (Closed)
* ~~[#436724 BundleImpl.checkConflicts fails when Resource.BundleRequirementImpl has no attributes](https://bugs.eclipse.org/bugs/show_bug.cgi?id=436724)~~ (Closed)
* ~~[#438783 org.osgi.framework.namespace has wrong version in System bundle](https://bugs.eclipse.org/bugs/show_bug.cgi?id=438783)~~ (Rejected)
* ~~[#438784 Utils.splitString will fail with arg "" (empty string)](https://bugs.eclipse.org/bugs/show_bug.cgi?id=438784)~~ (Closed)
* ~~[#438786 Concierge.exportSystemBundlePackages will fail with trailing comma](https://bugs.eclipse.org/bugs/show_bug.cgi?id=438786)~~ (Closed)
* ~~[#439184 ClassCastException in Concierge.storeProfile()](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439184)~~ (Closed)
* ~~[#433345 OSGi bootdelegation is not supported](https://bugs.eclipse.org/bugs/show_bug.cgi?id=433345)~~ (Closed)
* ~~[#433346 org.eclipse.osgi.services can NOT be resolved when systempackages property is specified](https://bugs.eclipse.org/bugs/show_bug.cgi?id=433346)~~ (Rejected)
* [#438781 Loading of localized files in bundle will fail due to wrong path](https://bugs.eclipse.org/bugs/show_bug.cgi?id=438781) (Open)
* [#439182 org.osgi.service.condpermadmin package is missing in Concierge](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439182) (Open)
  * Workaround: see patch osgi-permission: make additional bundle with missing packages.
    See https://github.com/JochenHiller/concierge-tests/tree/master/patches/osgi-permission/
* [#439469 ClassCastException in BundleImpl.Revision.BundleClassLoader.findResource1](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439469) (Open)
* [#439470 Bundle activator will be called twice](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439470) (Open)
* [#439492 Concierge is missing pre-registered SAXParserFactory and DocumentBuilderFactory](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439492) (Open)

From Harini Siresena:  
  
* ~~[#437884 Framework system packages incorrectly specifies util.tracker bundle version](https://bugs.eclipse.org/bugs/show_bug.cgi?id=437884)~~ (Closed)


The patched sources are in this repo too. Check source code at https://github.com/JochenHiller/concierge-tests/tree/master/src .
The code patches are marked with conditional compilation based on Concierge.PATCH_JOCHEN.

### Identified bugs in other bundles

* Equinox
  * Equinox Console bundle
    * [#439182 Equinox.console will require condpermadmin which is missing in Concierge](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439182) (Open)
    * [#439180 org.eclipse.equinox.console bundle has wrong version 1.1.0 for "Import-Package: org.osgi.framework.namespace"](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439180) (Open)
      * see also ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=438783~~ (Rejected by Concierge)
      * Workaround: see patch equinox-console: change MANIFEST according.
        See https://github.com/JochenHiller/concierge-tests/tree/master/patches/equinox-console/
    * [#439445 Equinox console bundle has hard dependency to Equinox framework and not to supplement bundle](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439445) (Open)
      * Workaround: see patch equinox-supplement: added missing classes.
        See https://github.com/JochenHiller/concierge-tests/tree/master/patches/equinox-supplement/
  * Equinox Registry
    * Bugs in Concierge:
      * [#438781 Will fail as plugin.properties can not be loaded](https://bugs.eclipse.org/bugs/show_bug.cgi?id=438781) (Open)
      * [#439470 Bundle activator will be called twice](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439470) (Open)
  * Equinox DS
    * no problems in DS, only in dependent bundles

* EMF
  * ~~[#328227 EMF will not run on Felix or other OSGi frameworks](https://bugs.eclipse.org/bugs/show_bug.cgi?id=328227)~~ (Closed)
    * Note: the bundle will raise exceptions during installation. Until now there are just some tests which check 
      whether an EMF example can be used within right classloader.
    * TODO investigate why exceptions will be thrown from EMF
    * TODO provide a more easy way for testing as requested by Ed
* Xtext
  * ~~[#434490 Xtext will not run on other OSGi frameworks, e.g. Concierge](https://bugs.eclipse.org/bugs/show_bug.cgi?id=434490)~~
    * Note: Tests are only about install/resolve, no real functional tests
* Eclipse Kura
  * Tests are until now OK, but not yet completed
  * Identified and close bugs in Concierge:
    * ~~[#436724 BundleImpl.checkConflicts fails when Resource.BundleRequirementImpl has no attributes](https://bugs.eclipse.org/bugs/show_bug.cgi?id=436724)~~ (Closed)
    * ~~[#436725 Eclipse SODA COMM bundle relies on Equinox, does NOT run on Concierge](https://bugs.eclipse.org/bugs/show_bug.cgi?id=436725)~~ (Closed) 
    * ~~[#436729 Bundle org.eclipse.kura.core.configuration refers to Apache Felix SCR](https://bugs.eclipse.org/bugs/show_bug.cgi?id=436729)~~ (Closed) 
* EclipseSmartHome
  * Bugs in Concierge:
    * [#439469 ClassCastException in BundleImpl.Revision.BundleClassLoader.findResource1](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439469) (Open)
    * [#439470 Bundle activator will be called twice](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439470) (Open)
    * [#439492 Concierge is missing pre-registered SAXParserFactory and DocumentBuilderFactory](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439492) (Open) 
  * TODO working on next tests

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

Eclipse SmartHome can be tested using latest snapshot release from `http://download.eclipse.org/tools/orbit/downloads/drops/R20140525021250/repository/plugins`.
The build number has to be changed in EclipseSmartHomeTest like that:

```Java
	private static final String B_ESH(String bundleName) {
		return bundleName + "_0.7.0.201407112057" + ".jar";
	}
```

TODO add missing bundles

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

* Eclipse SmartHome test for o.e.sh.model.core with split packages
* Extend xargs launcher obout wildcard support for simpler startup scripts
* Download all bundles when remote URL is a p2-repo
* Add wildcard capability to installBundle to avoid to specify the version
  * Shall use the latest found version of a bundle
* Use Xtext online repo (check Hudson builds) ==> ask Xtext where to find repo
* Provide a way how EMF (and other bundles) can be used from workspace for testing
  (as requested by Ed Merks)
* Create bug to EMF to adapt other bundles (like EMF-examples) to do NOT require-bundle to Equinox too
