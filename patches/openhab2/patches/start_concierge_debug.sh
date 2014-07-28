#!/bin/sh

cd `dirname $0`


# ENABLE that for development purposes, will speed up
if [ true == true ] ; then
  cp -R ../../concierge-tests/patches/openhab2/patches/* .
  rm -rf storage
  rm userdata/logs/*.log
fi

# will download all needed files if not existing
if [ ! -d runtime/server/concierge ] ; then
  mkdir -p runtime/server/concierge
  for f in jetty-osgi-boot-9.2.1.v20140609.jar openhab2.xargs org.apache.felix.fileinstall-3.4.0.jar org.eclipse.concierge-5.0.0-SNAPSHOT-patched.jar org.eclipse.concierge.extension.permission_1.0.0.201407201043.jar org.eclipse.concierge.service.xmlparser_1.0.0.201407191653.jar org.eclipse.equinox.console_1.1.0.v20140131-1639.jar org.eclipse.equinox.supplement_1.5.100.v20140428-1446.jar shell-1.0.0.jar ; do
    curl -o runtime/server/concierge/$f https://raw.githubusercontent.com/JochenHiller/concierge-tests/master/patches/openhab2/patches/runtime/server/concierge/$f
  done
fi

# set path to eclipse folder. If local folder, use '.'; otherwise, use /path/to/eclipse/
eclipsehome="runtime/server";

# set ports for HTTP(S) server
HTTP_PORT=8080
HTTPS_PORT=8443

# get path to equinox jar inside $eclipsehome folder
cp=$(find $eclipsehome -name "org.eclipse.concierge-5.0.0*.jar" | sort | tail -1);
# echo $cp

# debug options
# debug_opts="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8001,server=y,suspend=n"

# program args
prog_args="-Dorg.eclipse.concierge.init.xargs=./runtime/server/concierge/openhab2.xargs -Dlogback.configurationFile=./runtime/etc/logback_debug.xml -DmdnsName=openhab -Dopenhab.logdir=./userdata/logs -Dsmarthome.servicecfg=./runtime/etc/services.cfg -Dsmarthome.servicepid=org.openhab -Dorg.quartz.properties=./runtime/etc/quartz.properties"

echo Launching the openHAB runtime in debug mode...
# java $debug_opts $prog_args -Dosgi.clean=true -Declipse.ignoreApp=true -Dosgi.noShutdown=true -Djetty.home.bundle=org.eclipse.jetty.osgi.boot -Djetty.port=$HTTP_PORT -Djetty.port.ssl=$HTTPS_PORT -Dfelix.fileinstall.dir=addons -Djava.library.path=lib -Dequinox.ds.block_timeout=240000 -Dequinox.scr.waitTimeOnBlock=60000 -Dfelix.fileinstall.active.level=4 -Djava.awt.headless=true -jar $cp $* -console 

java $debug_opts $prog_args -Dosgi.clean=true -Dosgi.noShutdown=true -Djetty.home.bundle=org.eclipse.jetty.osgi.boot -Djetty.port=$HTTP_PORT -Djetty.port.ssl=$HTTPS_PORT -Dfelix.fileinstall.dir=addons -Djava.library.path=lib -Dequinox.ds.block_timeout=240000 -Dequinox.scr.waitTimeOnBlock=60000 -Dfelix.fileinstall.active.level=4 -Djava.awt.headless=true -jar $cp $* -console 2>&1 | tee userdata/logs/console.log
