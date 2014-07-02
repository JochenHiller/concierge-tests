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

## Overview of Tests

### Eclipse SmartHome running on Concierge

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

	concierge.test.localDirectories=\
	    <your-dir>/distrib/target/kura-raspberry-pi-jars_0.2.0-SNAPSHOT/kura/plugins:\
	    <your-dir>/distrib/target/kura-raspberry-pi-jars_0.2.0-SNAPSHOT/plugins:\
	    ...

#### Open bugs

* Eclipse Kura dependencies over the OSGI R5 Core Specifications
https://bugs.eclipse.org/bugs/show_bug.cgi?id=433624
* Added bug and patch for Concierge when handling log4j and its fragment
https://bugs.eclipse.org/bugs/show_bug.cgi?id=436724
* Added a bug for Kura: SODA COMM bundle relies on Equinox specific configuration
https://bugs.eclipse.org/bugs/show_bug.cgi?id=436725
* Added bug for Kura to rely on Apache Felix SCR import packages which makes a hard dependency to an OSGi DS implementation
https://bugs.eclipse.org/bugs/show_bug.cgi?id=436729


## References for Concierge

For more details see

* http://projects.eclipse.org/projects/rt.concierge
* git://git.eclipse.org/gitroot/concierge/org.eclipse.concierge.git
