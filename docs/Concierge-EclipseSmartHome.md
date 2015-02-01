# Eclipse SmartHome running on Concierge

## Overview

This page describes the current status to run Eclipse SmartHome on the lightweight OSGi R5 Framework Concierge. As Eclipse SmartHome is the underlying framework for a so named "solution", the solution used to run on top of Eclipse SmartHome is openHAB2. openHAB2 provides a version for minimal-runtime, which does not require EMF/xText. This version is the base for the integration.

The documentation here will use

* the latest openHAB-2.0.0-SNAPSHOT version, using the minimal runtime version (see https://openhab.ci.cloudbees.com/job/openHAB2/)
* the latest Concierge SNAPHOT version (as of 2015-02-01) (see https://hudson.eclipse.org/concierge/job/ConciergeDistribution/)

## How to run Eclipse SmartHome/openHAB2 with Concierge

1. Get openHAB 2.0.0 SNAPSHOT build from https://openhab.ci.cloudbees.com/job/openHAB2/

```script
mkdir openhab2-minimal-runtime
cd openhab2-minimal-runtime
wget https://openhab.ci.cloudbees.com/job/openHAB2/lastSuccessfulBuild/artifact/distribution/target/distribution-2.0.0-SNAPSHOT-minimal-runtime.zip
```

1. Unzip openHAB2 distribution

```script
mkdir openhab2
cd openhab2
unzip ../distribution-2.0.0-SNAPSHOT-minimal-runtime.zip
```

1. Download `start_concierge_debug.sh` script

```script
wget -q https://raw.githubusercontent.com/JochenHiller/concierge-tests/master/patches/openhab2-minimal-runtime/patches/start_concierge_debug.sh
chmod u+x start_concierge_debug.sh
```

1. Start Concierge server

This script will download missing bundles from GitHub repo.

```script
./start_concierge_debug.sh
```

1. Open a browser to http://localhost:8080/, and use "PaperUI" for testing


## Required patches for Eclipse SmartHome

* Equinox Console does work since Equinox Mars R3. We will use version 1.1.100
* Equinox Console requires corresponding supplement bundles, does work since Equinox Mars R3. We will use version 1.6.0
* Equinox ConfigAdmin does work since Equinox Mars R5. We will use 1.1.0 version
* Jetty OSGi Boot Bundle does support Concierge only from Jetty 9.3 on. Until that is available we use a patched version from Jetty 9.2.1.

## Open Issues

* The condition permission admin will be used as service bundle. It would be better to use a framework extension instead
* Some  bundles (org.eclipse.smarthome.io.net, org.eclipse.smarthome.core.transform, org.openhab.core) will use OSGi Declarative Services (DS) components, which does require optional classes, which are not included in minimal-runtime. This does work well, but every DS component which can not be instantiated will create some ERROR log messages. This will flood logs with ERROR messages which are not relevant.
  * Workarounds:
    * a) enable org.eclipse.concierge.log.enabled=true, and set loglevel to 0: org.eclipse.concierge.log.level=0
    * b) Use osgi-over-slf4j logging bundle, and disable logging of Equinox DS (<logger name="org.eclipse.equinox.ds" level="OFF"/>)
    * c) Use Apache SCR instead of Equinox DS (which does not log errors)
* There is a synchronization issue between Equinox DS and Apache Felix FileInstall.
  * Workarounds: 
    * a) Use Apache SCR instead of Equinox DS
    * b) do not use file install, install via xargs directives

## References

* openHAB 2.0.0-SNAPSHOT version: https://openhab.ci.cloudbees.com/job/openHAB2/
* Concierge SNAPSHOT version: https://hudson.eclipse.org/concierge/job/ConciergeDistribution/
