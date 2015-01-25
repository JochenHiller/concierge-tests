# Eclipse SmartHome running on Concierge

## Overview

This page describes the current status to run Eclipse SmartHome on the lightweight OSGi R5 Framework Concierge. As Eclipse SmartHome is the underlying framework for a so named "solution", the solution used to run on top of Concierge is openHAB2. openHAB2 provides a minimal version which does not require EMF/xText which is the base for this integration.

The documentation here will use

* the openHAB 2.0.0-alpha1 release, using the minimal runtime version (see https://github.com/openhab/openhab2/releases)
* Concierge latest SNAPHOT version (as of 25.01.2015) (see https://hudson.eclipse.org/concierge/job/ConciergeDistribution/)

## How to run openHAB2 (using Eclipse SmartHome) with Concierge

To run Eclipse SmartHome, you have to use an openHAB2 build, as Eclipse SmartHome does not have its own distribution yet.

0. Get openHAB 2.0.0-alpha1 release build from https://github.com/openhab/openhab2/releases
```script
mkdir openhab2-minimal-runtime
cd openhab2-minimal-runtime
wget -q https://github.com/openhab/openhab2/releases/download/2.0.0-alpha1/distribution-2.0.0-alpha1-minimal-runtime.zip
```
0. Unzip openHAB2 distribution
```script
mkdir openhab2
cd openhab2
unzip ../distribution-2.0.0-alpha1-minimal-runtime.zip
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
0. Open a browser to http://localhost:8080/smarthome.app?sitemap=demo

## References

* openHAB 2.0.0-alpha1 release: https://github.com/openhab/openhab2/releases
* Concierge Latest SNAPSHOT release: https://hudson.eclipse.org/concierge/job/ConciergeDistribution/

## Open Issues

* The condition permission admin will be used as service bundle. It would be better to use a framework extension instead
