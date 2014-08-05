#!/bin/bash

BUNDLE=com.sun.jersey_1.17.0.v20130314-2020.jar
echo "Patching $BUNDLE ..."
echo "  Add import package for Jackson"

mkdir tmp
cd tmp
mkdir $BUNDLE
cd $BUNDLE
jar xf ../../$BUNDLE
# copy prechanged MANIFEST file
cp ../../MANIFEST.MF META-INF/MANIFEST.MF
if [ ! -d ../../patched ]  ; then mkdir ../../patched ; fi
if [ -f ../../patched/$BUNDLE ]  ; then rm ../../patched/$BUNDLE ; fi
jar cfm ../../patched/$BUNDLE META-INF/MANIFEST.MF .
cd ../..

# cleanup
rm -rf tmp/$BUNDLE
rmdir tmp
