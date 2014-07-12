#!/bin/bash

BUNDLE=org.eclipse.equinox.supplement_1.5.100.v20140428-1446.jar
echo "Patching $BUNDLE ..."
echo "  Add classes from Equinox OSGi framework (o.e.osgi.framework.console, o.e.osgi.report)"
echo "  Extend MANIFEST about these packages"

OSGI_FW=org.eclipse.osgi_3.10.0.v20140606-1445.jar

mkdir tmp
cd tmp
mkdir $BUNDLE
cd $BUNDLE
jar xf ../../$BUNDLE

cd ..
mkdir $OSGI_FW
cd $OSGI_FW
jar xf ../../$OSGI_FW

cd ..
cd $BUNDLE
cp -R ../$OSGI_FW/org/eclipse/osgi/framework/console ./org/eclipse/osgi/framework/console 
cp -R ../$OSGI_FW/org/eclipse/osgi/report/ ./org/eclipse/osgi/report 
cat META-INF/MANIFEST.MF | sed -e 's/org.eclipse.osgi.util;version="1.1"/org.eclipse.osgi.util;version="1.1",\
 org.eclipse.osgi.framework.console;version="1.1",\
 org.eclipse.osgi.report.resolution;version="1.0"/g' >META-INF/MANIFEST.MF.PATCHED

mv META-INF/MANIFEST.MF.PATCHED META-INF/MANIFEST.MF

if [ ! -d ../../patched ]  ; then mkdir ../../patched ; fi
if [ -f ../../patched/$BUNDLE ]  ; then rm ../../patched/$BUNDLE ; fi
jar cfM ../../patched/$BUNDLE .
cd ../..

# cleanup
rm -rf tmp/$BUNDLE
rm -rf tmp/$OSGI_FW
rmdir tmp
