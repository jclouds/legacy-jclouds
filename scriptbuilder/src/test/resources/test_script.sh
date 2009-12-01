#!/bin/bash
set +u
shopt -s xpg_echo
shopt -s expand_aliases
unset PATH JAVA_HOME LD_LIBRARY_PATH
function abort {
   echo "aborting: $@" 1>&2
   exit 1
}
function default {
   export RUNTIME="Moo"
   return 0
}
export PATH=/usr/ucb/bin:/bin:/usr/bin:/usr/sbin
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
   echo status ... the following should be empty, as we haven't sourced the variable"$RUNTIME"
   ;;
esac
exit 0
