# Concierge-Tests

This project contains test cases for the Eclipse OSGi framework Concierge.
It will be committed at GitHub to make it public available during development.
All the code will be contributed to the Eclipse Concierge project when Gerrit is in place.

## How to use this test cases

1. First check out Concierge

	git clone git://git.eclipse.org/gitroot/concierge/org.eclipse.concierge.git
	
1. Check out Concierge tests

	git clone https://github.com/JochenHiller/concierge-tests.git
	
1. Copy all files from concierge-tests to Concierge project (as tests will be running with Concierge project)

	cd concierge-tests
	./copy-to-concierge.sh

## Overview of testing framework

The tests are based on this general concepts:

* Tests are simple JUnit 4 bqsed unit tests
* The OSGi Concierge framework will be started via Concierge based FrameworLauncher
* An AbstractConciergeTestCase base class will provide helping methods for simple testing
* All used 3rd party bundles will be retrieved from their corresponding repositories in Internet
* For performance reasons, these bundles will be locally cached
  * The default folder is ./target/localCache
* The repos can be configured in file "concierge-test.properties"
* Tests can be running using a simple shell for interactive testing
  * just implement the method stayInShell returning true
  * the bundle from ./test/plugins/shell-1.0.0.jar will be used
* Unit tests will be running in an order by specifying @FixMethodOrder(MethodSorters.NAME_ASCENDING) on test classes
* The ConciergeTestSuite will run all tests
* Actually the tests are focused on installation and resolving bundles. In most cases there are no tests for checking whether the bundle is really working
* Specific bundles will be added to ./target/<your-dir> where there are no online bundles available

## Overview of Failed Tests

* FrameworkLaunchArgsTest
  * test11SystemPackagesTrailingComma: will fail with ArrayIndexOutOfBoundsException when property ends with a ","
  * test13SystemPackagesExtraTrailingComma: will fail with ArrayIndexOutOfBoundsException when property ends with a ","
* EclipseEquinoxTest
  * test04EquinoxDS: will fail as o.e.equinox.console has unresolvable dependencies
  * test04EquinoxRegistry: will fail as o.e.equinox.console has unresolvable dependencies
* EclipseSmartHome
  * test10EclipseSmartHome:
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

	// build target platform
	$ git clone -b develop https://github.com/eclipse/kura.git
	$ cd kura/target-platform
	$ mvn clean install
	// build kura core
	$ cd ../kura/
	$ mvn -Dmaven.test.skip=true -f manifest_pom.xml clean install
	$ mvn -Dmaven.test.skip=true -f pom_pom.xml -Pweb clean install
	$ mvn -Dmaven.test.skip=true -f pom_pom.xml clean install

2. Unpack distribution file

	$ cd distrib/target
	$ unzip kura-raspberry-pi-jars_0.2.0-SNAPSHOT.zip
	
3. Add the directory with unpacked files to file test/concierge-test.properties

This will extend the paths to be searched for bundles to lookup into Kura distribution.
If we assume that kura is on same directory level as org.eclipse.concierge,
this configuration can be added:

	concierge.test.localDirectories=\
	    ../kura/kura/distrib/target/kura-raspberry-pi-jars_0.2.0-SNAPSHOT/kura/plugins:\
	    ../kura/kura/distrib/target/kura-raspberry-pi-jars_0.2.0-SNAPSHOT/plugins:\
	    ...

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
