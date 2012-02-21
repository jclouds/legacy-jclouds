#!/bin/bash
set +u
shopt -s xpg_echo
shopt -s expand_aliases
unset PATH JAVA_HOME LD_LIBRARY_PATH RUNTIME
function abort {
   echo "aborting: $@" 1>&2
   exit 1
}
function default {
   export RUNTIME="Moo"
   return $?
}
export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin
case $1 in
start)
   default || exit 1
   echo start $RUNTIME
   ;;
stop)
   default || exit 1
   echo stop $RUNTIME
   ;;
status)
   cat >> /tmp/$USER/scripttest/temp.txt <<-'END_OF_JCLOUDS_FILE'
	hello world
END_OF_JCLOUDS_FILE
   echo "the following should be []: [$RUNTIME]"
   ;;
esac
exit $?
