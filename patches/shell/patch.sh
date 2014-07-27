#!/bin/bash

BUNDLE=shell-1.0.0.jar
echo "Patching $BUNDLE ..."
echo "  will repack shell to have MANIFEST.MF at right place"

mkdir tmp
cd tmp
mkdir $BUNDLE
cd $BUNDLE
jar xf ../../$BUNDLE
if [ ! -d ../../patched ]  ; then mkdir ../../patched ; fi
if [ -f ../../patched/$BUNDLE ]  ; then rm ../../patched/$BUNDLE ; fi
# strange: jar cvfM does NOT add MANIFEST.MF as first file to Jar ???
# jar cvfM ../../patched/$BUNDLE .
# is does work if manifest will be specified with m option
jar cfm ../../patched/$BUNDLE META-INF/MANIFEST.MF .
cd ../..

# cleanup
rm -rf tmp/$BUNDLE
rmdir tmp
