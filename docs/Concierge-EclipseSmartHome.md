# Eclipse SmartHome running on Concierge

## Overview

This page describes the current status to run Eclipse SmartHome on the lightweight OSGi R5 Framework Concierge. As Eclipse SmartHome is the underlying framework for a so named "solution", the solution used to run on top of Eclipse SmartHome is openHAB2. openHAB2 provides a version for minimal-runtime, which does not require EMF/xText. This version is the base for the integration.

The documentation here will use

* the latest openHAB-2.0.0-SNAPSHOT version, using the minimal runtime version (see https://openhab.ci.cloudbees.com/job/openHAB2/)
* the latest Concierge  SNAPHOT version (as of 25.01.2015) (see https://hudson.eclipse.org/concierge/job/ConciergeDistribution/)

## How to run Eclipse SmartHome/openHAB2 with Concierge

To run Eclipse SmartHome, you have to use an openHAB2 build, as Eclipse SmartHome does not have its own distribution yet.

0. Get openHAB 2.0.0 SNAPSHOT build from https://openhab.ci.cloudbees.com/job/openHAB2/

```script
mkdir openhab2-minimal-runtime
cd openhab2-minimal-runtime
wget https://openhab.ci.cloudbees.com/job/openHAB2/lastSuccessfulBuild/artifact/distribution/target/distribution-2.0.0-SNAPSHOT-minimal-runtime.zip
```

0. Unzip openHAB2 distribution

```script
mkdir openhab2
cd openhab2
unzip ../distribution-2.0.0-SNAPSHOT-minimal-runtime.zip
```

0. Download `start_concierge_debug.sh` script

```script
wget -q https://raw.githubusercontent.com/JochenHiller/concierge-tests/master/patches/openhab2-minimal-runtime/patches/start_concierge_debug.sh
chmod u+x start_concierge_debug.sh
```

0. Start Concierge server

```script
./start_concierge_debug.sh
```

0. Open a browser to http://localhost:8080/, and use "PaperUI" for testing

## References

* openHAB 2.0.0-SNAPSHOT version: https://openhab.ci.cloudbees.com/job/openHAB2/
* Concierge SNAPSHOT version: https://hudson.eclipse.org/concierge/job/ConciergeDistribution/

## Open Issues

* The condition permission admin will be used as service bundle. It would be better to use a framework extension instead
* Bindings in "addon" folder will not be started reliable, sometime bundles are ony installed, not started
* When manually starting bindings in INSTALLED mode, Java can hang. You have to kill the JavaVM
