#!/bin/bash

DO_CLEAN=true

BUNDLE=org.eclipse.smarthome.core.transform_0.8.0.201501200907.jar
echo "Patching $BUNDLE ..."
echo "  Remove file OSGI-INF/transformationaction.xml"

if [ -d tmp ] ; then rm -rf tmp/* ; rmdir tmp ; fi
mkdir tmp
cd tmp
mkdir $BUNDLE
cd $BUNDLE
jar xf ../../$BUNDLE
if [ ! -d ../../patched ]  ; then mkdir ../../patched ; fi
if [ -f ../../patched/$BUNDLE ]  ; then rm ../../patched/$BUNDLE ; fi
rm OSGI-INF/transformationaction.xml
# remove line number 5
cat META-INF/MANIFEST.MF | sed -e 's/, OSGI-INF\/transformationac//g' | sed -e 's/ tion.xml//g' | sed '{5,1d;}' >META-INF/MANIFEST.MF.PATCHED
mv META-INF/MANIFEST.MF META-INF/MANIFEST.MF.ORI
mv META-INF/MANIFEST.MF.PATCHED META-INF/MANIFEST.MF

if [ ! -d ../../patched ]  ; then mkdir ../../patched ; fi
if [ -f ../../patched/$BUNDLE ]  ; then rm ../../patched/$BUNDLE ; fi
if [ "$DO_CLEAN" == "true" ] ; then rm META-INF/MANIFEST.MF.ORI ; fi
jar cfm ../../patched/$BUNDLE META-INF/MANIFEST.MF .
cd ../..

# cleanup
if [ "$DO_CLEAN" == "true" ] ; then
  rm -rf tmp/$BUNDLE
  rmdir tmp
fi
