#!/bin/bash
set +u
shopt -s xpg_echo
shopt -s expand_aliases
unset PATH JAVA_HOME LD_LIBRARY_PATH RUNTIME
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
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
