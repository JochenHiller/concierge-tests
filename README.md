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
4. Add the file `lib/commons-io-2.4.jar` to build path
5. For running tests of Eclipse SmartHome: check the version of latest snapshot at 
http://download.eclipse.org/smarthome/updates-nightly/plugins/ and update the version in the
file `EclipseSmartHomeTest.java`


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
* The `ConciergeTestSuite` in package `org.eclipse.concierge.suite` will run all tests
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

### Open bugs in Concierge

* [#439958 Bundle-NativeCode resolve will fail on Mac OS X](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439958) (Open)
* [#440492 shell-1.0.0.jar is not a valid JAR file](https://bugs.eclipse.org/bugs/show_bug.cgi?id=440492) (Open)
* [#440504 XargsFileLauncher: Concierge will be started at the end, by intention?](https://bugs.eclipse.org/bugs/show_bug.cgi?id=440504) (Open)
* [#440505 XargsFileLauncher: support properties, support wildcards](https://bugs.eclipse.org/bugs/show_bug.cgi?id=440505) (Open)

The patched sources are in this repo too. Check source code at https://github.com/JochenHiller/concierge-tests/tree/master/src/org/eclipse/concierge
The code patches are marked with conditional compilation based on Concierge.PATCH_JOCHEN.

### Closed bugs in Concierge

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
* ~~[#438781 Loading of localized files in bundle will fail due to wrong path](https://bugs.eclipse.org/bugs/show_bug.cgi?id=438781)~~ (Closed)
* ~~[#439182 org.osgi.service.condpermadmin package is missing in Concierge](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439182)~~ (Closed)
  * Note: added an extension bundle which provides the missing classes. This needs to be installed first when missing classes are needed (e.g. for Equinox console).
* ~~[#439469 ClassCastException in BundleImpl.Revision.BundleClassLoader.findResource1](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439469)~~ (Closed)
* ~~[#439470 Bundle activator will be called twice](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439470)~~ (Closed)
* ~~[#439492 Concierge is missing pre-registered SAXParserFactory and DocumentBuilderFactory](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439492)~~ (Closed)
  * Note: as commented in bug this missing functionality is added as separate bundle to keep core code of Concierge as small as possible
* ~~[#439751 Component.activate() will be called BEFORE bundle activator will be called](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439751)~~ (Rejected)
  * Note: TODO raise a bug against Apache Felix DS
* ~~[#439947 NullPointerException when resolving a fragment as framework extension bundle](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439947)~~ (Resolved)
* ~~[#439957 Bundle-NativeCode resolve will fail when selection-filter will be used](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439957)~~ (Closed)
* ~~[#439981 Concierge.removeFrameworkListener raise a NullPointerException](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439981)~~ (Closed)
* ~~[#440227 Boot delegation of com.sun.* and sun.* packages](https://bugs.eclipse.org/bugs/show_bug.cgi?id=440227)~~ (Closed)
  * Seems that Equinox and Felix also do not have easy working solutions.
  * See also workarounds at e.g.
    * http://comments.gmane.org/gmane.comp.java.jersey.user/6114
    * https://github.com/tux2323/jersey.sample.osgiservice
* ~~[#437884 Framework system packages incorrectly specifies util.tracker bundle version](https://bugs.eclipse.org/bugs/show_bug.cgi?id=437884)~~ (Closed)

### Identified bugs in other bundles

* Equinox
  * Equinox Console bundle
    * [#439180 org.eclipse.equinox.console bundle has wrong version 1.1.0 for "Import-Package: org.osgi.framework.namespace"](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439180) (Open)
      * see also ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=438783~~ (Rejected by Concierge)
      * Workaround: see patch equinox-console: change MANIFEST according.
        See https://github.com/JochenHiller/concierge-tests/tree/master/patches/equinox-console/
    * [#439445 Equinox console bundle has hard dependency to Equinox framework and not to supplement bundle](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439445) (Open)
      * Workaround: see patch equinox-supplement: added missing classes.
        See https://github.com/JochenHiller/concierge-tests/tree/master/patches/equinox-supplement/
* Jetty
  * Jetty OSGi Boot bundle
    * [#440506 Jetty OSGi boot bundle does not support OSGi framework Eclipse Concierge](https://bugs.eclipse.org/bugs/show_bug.cgi?id=440506) (Open)
      * Note: there is a prebuild version of this bundle with Concierge support available at
        https://github.com/JochenHiller/concierge-tests/blob/master/patches/openhab2/patches/runtime/server/concierge/jetty-osgi-boot-9.2.1.v20140609.jar
* EMF
  * [#328227 EMF will not run on Felix or other OSGi frameworks](https://bugs.eclipse.org/bugs/show_bug.cgi?id=328227) (To be Re-opened)
    Note: the bundle will raise exceptions during installation. Until now there are just some tests which check 
      whether an EMF example can be used within right classloader.
    * Note: have to re-open as due to wrong mechanism leads to NON initalizing plugin.xml's 
    * TODO provide a more easy way for testing as requested by Ed
* Xtext
  * [#434490 Xtext will not run on other OSGi frameworks, e.g. Concierge](https://bugs.eclipse.org/bugs/show_bug.cgi?id=434490) (To be re-opened)
    * Note: Tests are only about install/resolve, no real functional tests
    * Have to re-open as the current implementation will nothing do on Concierge
  * [#439758 org.eclipse.xtext should have require-bundle to com.google.inject as mandatory](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439758) (Open)
    * Workaround: adapt bundle start order to first install com.google.inject, then xtext
* Eclipse Kura
  * All Kura bundles will be installed into Concierge, no problems to install and start
    * One issue with Felix Gogo console, runtime exception
  * No further functional tests
  * Identified and closed bugs in Concierge and Kura bundles:
    * ~~[#436724 BundleImpl.checkConflicts fails when Resource.BundleRequirementImpl has no attributes](https://bugs.eclipse.org/bugs/show_bug.cgi?id=436724)~~ (Closed)
    * ~~[#436725 Eclipse SODA COMM bundle relies on Equinox, does NOT run on Concierge](https://bugs.eclipse.org/bugs/show_bug.cgi?id=436725)~~ (Closed) 
    * ~~[#436729 Bundle org.eclipse.kura.core.configuration refers to Apache Felix SCR](https://bugs.eclipse.org/bugs/show_bug.cgi?id=436729)~~ (Closed) 
    * ~~[#439957 Bundle-NativeCode resolve will fail when selection-filter will be used](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439957)~~ (Closed)
      * Fix available, see bug description
    * [#439958 Bundle-NativeCode resolve will fail on Mac OS X](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439958) (Open)
      * Fix available, see bug description
    * ~~[#439981 Concierge.removeFrameworkListener raise a NullPointerException](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439981)~~ (Closed)

* EclipseSmartHome
  * Bugs in Concierge:
    * [#439469 ClassCastException in BundleImpl.Revision.BundleClassLoader.findResource1](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439469) (Open)
    * ~~[#439470 Bundle activator will be called twice](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439470)~~ (Closed)
    * ~~[#439492 Concierge is missing pre-registered SAXParserFactory and DocumentBuilderFactory](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439492)~~ (Closed) 
  * Issue with Jersey
    * Seems that Equinox and Felix also do not have easy working solutions.
      * See also workarounds at e.g.
      * http://comments.gmane.org/gmane.comp.java.jersey.user/6114
      * https://github.com/tux2323/jersey.sample.osgiservice
  
    * Jersey 1.17 does have OPTIONAL dependencies to Jackson, with wrong Jackson package version (1.9.2, instead of 1.6.0).
      A workaround is to patch Jersey to make two package mandatory without package version:
      * org.codehaus.jackson
      * org.codehaus.jackson.map
   * TODO Real solution: check about latest Jersey 2.x version, and dependencies to Jackson (assume no deps)
  * TODO working on next tests
    * go over all TODOs, sort them
    * major issue: o.e.sh.core.transform, make sep. test
* openHAB
  * Bug in openHAB2
    * [#6 Running openHAB2 on Concierge needs update of Felix FileInstall from 3.2.6 to 3.4.0](https://github.com/openhab/openhab2/issues/6) (Open)
      * Apache Felix FileInstall needs an update to 3.4.0, as this does not require StartLevel service, which is NOT supported by Concierge
      * Workaround: use FileInstall 3.4.0 as part of Concierge patches for openHAB2/Eclipse SmartHome

### Eclipse Kura running on Concierge

1. How to build Eclipse Kura

See https://github.com/eclipse/kura for details how to build.
```Shell
// build target platform
git clone -b develop https://github.com/eclipse/kura.git
cd kura/target-platform
mvn clean install
// build kura core
cd ../kura/
mvn -Dmaven.test.skip=true -f manifest_pom.xml clean install
mvn -Dmaven.test.skip=true -f pom_pom.xml -Pweb clean install
mvn -Dmaven.test.skip=true -f pom_pom.xml clean install
```
2. Unpack distribution file
```Shell
cd distrib/target
unzip kura-raspberry-pi-jars_0.2.0-SNAPSHOT.zip
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

#### You can run Eclipse SmartHome with Concierge this way:

To run Eclipse SmartHome, you have to use an openHAB2 build, as Eclipse SmartHome
does not have its own distribution yet.

1. Get openHAB2 snapshot build from https://openhab.ci.cloudbees.com/job/openHAB2/
```script
wget https://openhab.ci.cloudbees.com/job/openHAB2/lastSuccessfulBuild/artifact/distribution/target/distribution-2.0.0-SNAPSHOT-runtime.zip
wget https://openhab.ci.cloudbees.com/job/openHAB2/lastSuccessfulBuild/artifact/distribution/target/distribution-2.0.0-SNAPSHOT-demo.zip
```
2. Unzip openHAB2 runtime and demo data
```script
mkdir openhab2
cd openhab2
unzip ../distribution-2.0.0-SNAPSHOT-runtime.zip
unzip ../distribution-2.0.0-SNAPSHOT-demo.zip
```
3. Download `start_concierge_debug.sh` script
```script
wget https://raw.githubusercontent.com/JochenHiller/concierge-tests/master/patches/openhab2/patches/start_concierge_debug.sh
chmod u+x start_concierge_debug.sh
```
4. Start Concierge server
```script
./start_concierge_debug.sh
```
5. Open a browser to http://localhost:8080/smarthome.app?sitemap=demo

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
* Eclipse Platform bundles repo: git://git.eclipse.org/gitroot/platform/eclipse.platform.runtime.git
  * Checkout luna branch
```Shell
git clone git://git.eclipse.org/gitroot/platform/eclipse.platform.runtime.git -b R4_4_maintenance
``` 
* Xtext repo: git://git.eclipse.org/gitroot/tmf/org.eclipse.xtext.git
```Shell
git clone git://git.eclipse.org/gitroot/tmf/org.eclipse.xtext.git -b v2.6.x_Maintenance
```

* Apache Felix
  * Felix Website: http://felix.apache.org
  * Source Code Repo: http://svn.apache.org/repos/asf/felix/trunk
* Jetty
  * Source Code Repo: https://git.eclipse.org/r/p/jetty/org.eclipse.jetty.project
  * Source Code Repo: git://git.eclipse.org/gitroot/jetty/rt.equinox.bundles.git
* EMF  
  * Source Code Repo: http://git.eclipse.org/c/emf/org.eclipse.emf.git
  * Source Code Repo: git://git.eclipse.org/gitroot/emf/org.eclipse.emf.git
  
## TODO

* Xtext: Activator, get rid of platform, use OSGi Service tracker instead
* EMF-Core: Change activator to create Implementation only when Plugin is available. Otherwise 
  make the initialization code from activator
      protected BundleActivator createBundle()
      {
        return new Implementation();
      }
* Jersey: test case for Jackson classes
* Extend xargs launcher obout wildcard support for simpler startup scripts
  * Finalize Xargs contribution (multi-line properties, multiple properties, -fwstart option, reformat code)
* Implementation for com.sun.* packages
* Add a bug when running ConciergeExtension test cases: will not cleanup correctly, which
  results in further errors in test cases or in seldom cases to JavaVM crashs
  Seems to be a bug related to bundle jar file which will NOT be closed in all error situations
* Add test cases for Xargs with initLevel
* Add log information for resolveBundle when running into nested resolve calls (IllegalStateException)
* Check Apache Gogo for reported errors/exceptions
* Create bug for Apache Felix DS for order of activation/component
* Download all bundles when remote URL is a p2-repo
* Add wildcard capability to AbstractConciergeTestCase.installBundle to avoid to specify the version
  * Shall use the latest found version of a bundle
* Use Xtext online repo (check Hudson builds) ==> ask Xtext where to find repo
* Provide a way how EMF (and other bundles) can be used from workspace for testing
  (as requested by Ed Merks)
* Create bug to EMF to adapt other bundles (like EMF-examples) to do NOT require-bundle to Equinox too
* Run and test Concierge under Raspberry PI
