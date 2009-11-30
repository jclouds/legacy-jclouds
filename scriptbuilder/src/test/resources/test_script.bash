#!/bin/bash
set +u
shopt -s xpg_echo
shopt -s expand_aliases
unset PATH JAVA_HOME LD_LIBRARY_PATH
function abort {
   echo "aborting: $@" 1>&2
   set -u
}
function default {
   export JAVA_HOME="/apps/jdk1.6"
   return 0
}
export PATH=/usr/ucb/bin:/bin:/usr/bin:/usr/sbin
case $1 in
start)
   echo started
   ;;
stop)
   echo stopped
   ;;
esac
set -u
return 0
