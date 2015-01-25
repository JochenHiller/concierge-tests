#!/bin/sh

# pipe all output to console.log file
rm -f ./userdata/logs/console.log
(

cd `dirname $0`

# ENABLE that for development purposes, will speed up
if [ true == true ] ; then
  rm -rf ./runtime/server/concierge/
  cp -R ../../concierge-tests/patches/openhab2-minimal-runtime/patches/* .
  rm -rf ./userdata/storage
  rm -f ./userdata/logs/openhab.log
  rm -f ./userdata/logs/events.log
fi


# will download all needed files if not existing
if [ ! -d runtime/server/concierge ] ; then
  mkdir -p runtime/server/concierge
  for f in x ; do
    curl -o runtime/server/concierge/$f https://raw.githubusercontent.com/JochenHiller/concierge-tests/master/patches/openhab2-minimal/patches/runtime/server/concierge/$f
  done
fi

# set path to eclipse folder. If local folder, use '.'; otherwise, use /path/to/eclipse/
eclipsehome="runtime/server";

# set ports for HTTP(S) server
HTTP_PORT=8080
HTTPS_PORT=8443

# get path to equinox jar inside $eclipsehome folder
# cp=$(find $eclipsehome -name "org.eclipse.equinox.launcher_*.jar" | sort | tail -1);
cp=$(find $eclipsehome -name "org.eclipse.concierge-1.0.0*.jar" | sort | tail -1);
# echo $cp

# debug options
debug_opts="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8001,server=y,suspend=n"

# program args
prog_args="-Dlogback.configurationFile=./runtime/etc/logback_debug.xml -DmdnsName=openhab -Dopenhab.logdir=./userdata/logs -Dsmarthome.servicecfg=./runtime/etc/services.cfg -Dsmarthome.userdata=./userdata -Dsmarthome.servicepid=org.openhab -Dsmarthome.userdata=./userdata -Dorg.quartz.properties=./runtime/etc/quartz.properties -Djetty.etc.config.urls=etc/jetty.xml,etc/jetty-ssl.xml,etc/jetty-deployer.xml,etc/jetty-https.xml,etc/jetty-selector.xml"
prog_args="-Dorg.osgi.framework.storage=./userdata/storage $prog_args"
# echo $prog_args

echo Launching the openHAB runtime in debug mode...
# create logs dir for tee command
if [ ! -d userdata/logs ] ; then mkdir -p userdata/logs ; fi
# TODO remove eclipse.ignoreApp for Concierge
# dump complete output into log files
set -x
java $debug_opts $prog_args -Dosgi.clean=true -Declipse.ignoreApp=true -Dosgi.noShutdown=true -Djetty.home.bundle=org.openhab.io.jetty -Djetty.port=$HTTP_PORT -Djetty.port.ssl=$HTTPS_PORT -Dfelix.fileinstall.dir=addons -Djava.library.path=lib -Dequinox.ds.block_timeout=240000 -Dequinox.scr.waitTimeOnBlock=60000 -Dfelix.fileinstall.active.level=4 -Djava.awt.headless=true -jar $cp ./runtime/server/concierge/openhab2.xargs

) 2>&1 | tee ./userdata/logs/console.log
