#!/bin/bash
set +u
shopt -s xpg_echo
shopt -s expand_aliases
unset PATH JAVA_HOME LD_LIBRARY_PATH
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
   export INSTANCE_NAME="mkebsboot"
export INSTANCE_HOME="/tmp"
export LOG_DIR="/tmp/logs"
   return $?
}
function mkebsboot {
   export IMAGE_DIR="/mnt/tmp"
export EBS_DEVICE="/dev/sdh"
export EBS_MOUNT_POINT="/mnt/ebs"
   return $?
}
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
function findPid {
   unset FOUND_PID;
   [ $# -eq 1 ] || {
      abort "findPid requires a parameter of pattern to match"
      return 1
   }
   local PATTERN="$1"; shift
   local _FOUND=`ps auxwww|grep "$PATTERN"|grep -v " $0"|grep -v grep|grep -v $$|awk '{print $2}'`
   [ -n "$_FOUND" ] && {
      export FOUND_PID=$_FOUND
      return 0
   } || {
      return 1
   }
}
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
function forget {
   unset FOUND_PID;
   [ $# -eq 3 ] || {
      abort "forget requires parameters INSTANCE_NAME SCRIPT LOG_DIR"
      return 1
   }
   local INSTANCE_NAME="$1"; shift
   local SCRIPT="$1"; shift
   local LOG_DIR="$1"; shift
   mkdir -p $LOG_DIR
   findPid $INSTANCE_NAME
   [ -n "$FOUND_PID" -a -f $LOG_DIR/stdout.log ] && {
      echo $INSTANCE_NAME already running pid $FOUND_PID
      return 1;
   } || {
      nohup $SCRIPT >$LOG_DIR/stdout.log 2>$LOG_DIR/stderr.log &
      RETURN=$?
      # this is generally followed by findPid, so we shouldn't exit 
      # immediately as the proc may not have registered in ps, yet
      test $RETURN && sleep 1
      return $RETURN;
   }
}
export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin
case $1 in
init)
   default || exit 1
   mkebsboot || exit 1
   mkdir -p $INSTANCE_HOME
   
   # create runscript header
   cat > $INSTANCE_HOME/mkebsboot.sh <<-'END_OF_JCLOUDS_SCRIPT'
	#!/bin/bash
	set +u
	shopt -s xpg_echo
	shopt -s expand_aliases
	
	PROMPT_COMMAND='echo -ne \"\033]0;mkebsboot\007\"'
	export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin

	export INSTANCE_NAME='mkebsboot'
END_OF_JCLOUDS_SCRIPT
   cat >> $INSTANCE_HOME/mkebsboot.sh <<-END_OF_JCLOUDS_SCRIPT
	export IMAGE_DIR='$IMAGE_DIR'
	export EBS_DEVICE='$EBS_DEVICE'
	export EBS_MOUNT_POINT='$EBS_MOUNT_POINT'
	export INSTANCE_NAME='$INSTANCE_NAME'
	export INSTANCE_HOME='$INSTANCE_HOME'
	export LOG_DIR='$LOG_DIR'
END_OF_JCLOUDS_SCRIPT
   
   # add desired commands from the user
   cat >> $INSTANCE_HOME/mkebsboot.sh <<-'END_OF_JCLOUDS_SCRIPT'
	cd $INSTANCE_HOME
	echo creating a filesystem and mounting the ebs volume
	mkdir -p $IMAGE_DIR $EBS_MOUNT_POINT
	rm -rf $IMAGE_DIR/*
	yes| mkfs -t ext3 $EBS_DEVICE 2>&-
	mount $EBS_DEVICE $EBS_MOUNT_POINT
	echo making a local working copy of the boot disk
	rsync -ax --exclude /ubuntu/.bash_history --exclude /home/*/.bash_history --exclude /etc/ssh/ssh_host_* --exclude /etc/ssh/moduli --exclude /etc/udev/rules.d/*persistent-net.rules --exclude /var/lib/ec2/* --exclude=/mnt/* --exclude=/proc/* --exclude=/tmp/* --exclude=/dev/log / $IMAGE_DIR
	echo preparing the local working copy
	touch $IMAGE_DIR/etc/init.d/ec2-init-user-data
	echo copying the local working copy to the ebs mount
	cd $IMAGE_DIR
	tar -cSf - * | tar xf - -C $EBS_MOUNT_POINT
	echo size of ebs
	du -sk $EBS_MOUNT_POINT
	echo size of source
	du -sk $IMAGE_DIR
	rm -rf $IMAGE_DIR/*
	umount $EBS_MOUNT_POINT
	echo ----COMPLETE----
END_OF_JCLOUDS_SCRIPT
   
   # add runscript footer
   cat >> $INSTANCE_HOME/mkebsboot.sh <<-'END_OF_JCLOUDS_SCRIPT'
	exit $?
	
END_OF_JCLOUDS_SCRIPT
   
   chmod u+x $INSTANCE_HOME/mkebsboot.sh
   ;;
status)
   default || exit 1
   findPid $INSTANCE_NAME || exit 1
   echo $FOUND_PID
   ;;
stop)
   default || exit 1
   findPid $INSTANCE_NAME || exit 1
   [ -n "$FOUND_PID" ]  && {
      echo stopping $FOUND_PID
      kill -9 $FOUND_PID
   }
   ;;
start)
   default || exit 1
   forget $INSTANCE_NAME $INSTANCE_HOME/$INSTANCE_NAME.sh $LOG_DIR || exit 1
   ;;
tail)
   default || exit 1
   tail $LOG_DIR/stdout.log
   ;;
tailerr)
   default || exit 1
   tail $LOG_DIR/stderr.log
   ;;
run)
   default || exit 1
   $INSTANCE_HOME/$INSTANCE_NAME.sh
   ;;
esac
exit $?
