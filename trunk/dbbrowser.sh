#/bin/bash

# Sets up the environment and runs dbbrowser.
#
# NOTE that the the user must be in the dbbrowser directory for this script to
# work.  Otherwise, it will not be able to find the archives.
#
# Revision
# 07/27/2002 This script was created

# The classpathmunge function was inspired by RedHat's pathmunge function
# in /etc/profile.
classpathmunge () {
	if ! echo $CLASSPATH | /bin/egrep -q "(^|:)$1($|:)" ; then
	   if [ "$2" = "after" ] ; then
	      CLASSPATH=$CLASSPATH:$1
	   else
	      CLASSPATH=$1:$CLASSPATH
	   fi
	fi
}

# The directory into which DBBrowser is installed.
# Set this variable's value in order to be able to run the script from anywhere
bindir=""

# Make sure that we can find the java runtime.
javapath=$(which java 2>/dev/null)

if [ "$javapath" == "" ]
then
  echo "Unable to find the java runtime.  Make sure that the 'java' executable is in "
  echo "your PATH."
  exit 1
fi

# Build the CLASSPATH
# Look for jar files.
for f in $(find $bindir -name '*.jar' -print)
do
  classpathmunge $f
done
# Look for zip files.
for f in $(find $bindir -name '*.zip' -print)
do
  classpathmunge $f
done

# Set up the environment
export CLASSPATH

# Run dbbrowser with any arguments we were given
$javapath us.pcsw.dbbrowser.Main "$@"
