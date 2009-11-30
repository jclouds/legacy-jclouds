#!/bin/bash
unset PATH JAVA_HOME LD_LIBRARY_PATH
export PATH=/usr/ucb/bin:/bin:/usr/bin:/usr/sbin
export JAVA_HOME="/apps/jdk1.6"
case $1 in
start)
   echo started
   ;;
stop)
   echo stopped
   ;;
esac
