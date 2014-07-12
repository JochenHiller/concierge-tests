#!/bin/bash

BUNDLE=osgi.core-permission-5.0.0.jar
echo "Patching $BUNDLE ..."
echo "  Add missing classes from condpermadmin and permission package"

OSGI_CORE=osgi.core-5.0.0.jar

mkdir tmp
cd tmp
mkdir $BUNDLE
cd $BUNDLE
jar xf ../../$OSGI_CORE

rm LICENSE
rm about.html
rm -rf OSGI-OPT
rm -rf org/osgi/framework
rm -rf org/osgi/resource
rm -rf org/osgi/util
rm -rf org/osgi/service/packageadmin
rm -rf org/osgi/service/startlevel
rm -rf org/osgi/service/url

cp ../../MANIFEST.MF META-INF

if [ ! -d ../../patched ]  ; then mkdir ../../patched ; fi
if [ -f ../../patched/$BUNDLE ]  ; then rm ../../patched/$BUNDLE ; fi
jar cfM ../../patched/$BUNDLE .
cd ../..

# cleanup
rm -rf tmp/$BUNDLE
rmdir tmp
