#!/bin/bash

BUNDLE=org.eclipse.equinox.console_1.1.0.v20140131-1639.jar
echo "Patching $BUNDLE ..."
echo "  change org.osgi.framework.namespace version to 1.0.0 in MANIFEST"

mkdir tmp
cd tmp
mkdir $BUNDLE
cd $BUNDLE
jar xf ../../$BUNDLE
cat META-INF/MANIFEST.MF | sed -e 's/"1.1.0",org.osgi.framework.wiring/"1.0.0",org.osgi.framework.wiring/g' >META-INF/MANIFEST.MF.PATCHED
rm META-INF/MANIFEST.MF
mv META-INF/MANIFEST.MF.PATCHED META-INF/MANIFEST.MF
if [ ! -d ../../patched ]  ; then mkdir ../../patched ; fi
if [ -f ../../patched/$BUNDLE ]  ; then rm ../../patched/$BUNDLE ; fi
jar cfm ../../patched/$BUNDLE META-INF/MANIFEST.MF .
cd ../..

# cleanup
rm -rf tmp/$BUNDLE
rmdir tmp
