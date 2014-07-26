#!/bin/sh


# SPEED DEVELOPMENT
cp -R ../../concierge-tests/patches/openhab2/patches/* .
rm -rf storage



cd `dirname $0`

# set path to eclipse folder. If local folder, use '.'; otherwise, use /path/to/eclipse/
eclipsehome="runtime/server";

# set ports for HTTP(S) server
HTTP_PORT=8080
HTTPS_PORT=8443

# get path to equinox jar inside $eclipsehome folder
cp=$(find $eclipsehome -name "org.eclipse.concierge-5.0.0*.jar" | sort | tail -1);
# echo $cp

# debug options
# debug_opts="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8001,server=y,suspend=y"

# program args
prog_args="-Dorg.eclipse.concierge.init.xargs=./runtime/server/concierge/concierge.xargs -Dlogback.configurationFile=./runtime/etc/logback_debug.xml -DmdnsName=openhab -Dopenhab.logdir=./userdata/logs -Dsmarthome.servicecfg=./runtime/etc/services.cfg -Dsmarthome.servicepid=org.openhab -Dorg.quartz.properties=./runtime/etc/quartz.properties"

echo Launching the openHAB runtime in debug mode...
# java $debug_opts $prog_args -Dosgi.clean=true -Declipse.ignoreApp=true -Dosgi.noShutdown=true -Djetty.home.bundle=org.eclipse.jetty.osgi.boot -Djetty.port=$HTTP_PORT -Djetty.port.ssl=$HTTPS_PORT -Dfelix.fileinstall.dir=addons -Djava.library.path=lib -Dequinox.ds.block_timeout=240000 -Dequinox.scr.waitTimeOnBlock=60000 -Dfelix.fileinstall.active.level=4 -Djava.awt.headless=true -jar $cp $* -console 

java $debug_opts $prog_args -Dosgi.clean=true -Dosgi.noShutdown=true -Djetty.home.bundle=org.eclipse.jetty.osgi.boot -Djetty.port=$HTTP_PORT -Djetty.port.ssl=$HTTPS_PORT -Dfelix.fileinstall.dir=addons -Djava.library.path=lib -Dequinox.ds.block_timeout=240000 -Dequinox.scr.waitTimeOnBlock=60000 -Dfelix.fileinstall.active.level=4 -Djava.awt.headless=true -jar $cp $* -console
