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
   export INSTANCE_NAME="mkebsboot"
export INSTANCE_HOME="/mnt/tmp"
export LOG_DIR="/tmp/logs"
   return 0
}
function mkebsboot {
   export IMAGE_DIR="/mnt/tmp"
export EBS_DEVICE="/dev/sdh"
export EBS_MOUNT_POINT="/mnt/ebs"
   return 0
}
function findPid {
   unset FOUND_PID;
   [ $# -eq 1 ] || {
      abort "findPid requires a parameter of pattern to match"
      return 1
   }
   local PATTERN="$1"; shift
   local _FOUND=`ps auxwww|grep "$PATTERN"|grep -v " $0"|grep -v grep|awk '{print $2}'`
   [ -n "$_FOUND" ] && {
      export FOUND_PID=$_FOUND
      return 0
   } || {
      return 1
   }
}
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
   [ -n "$FOUND_PID" ] && {
      echo $INSTANCE_NAME already running pid [$FOUND_PID]
   } || {
      nohup $SCRIPT >$LOG_DIR/stdout.log 2>$LOG_DIR/stderr.log &
      findPid $INSTANCE_NAME
      [ -n "$FOUND_PID" ] || abort "$INSTANCE_NAME did not start"
   }
   return 0
}
export PATH=/usr/ucb/bin:/bin:/usr/bin:/usr/sbin
case $1 in
init)
   default || exit 1
   mkebsboot || exit 1
   mkdir -p $INSTANCE_HOME
   rm $INSTANCE_HOME/mkebsboot.sh 2>&-
   echo '#!/bin/bash'>>$INSTANCE_HOME/mkebsboot.sh
   echo 'set +u'>>$INSTANCE_HOME/mkebsboot.sh
   echo 'shopt -s xpg_echo'>>$INSTANCE_HOME/mkebsboot.sh
   echo 'shopt -s expand_aliases'>>$INSTANCE_HOME/mkebsboot.sh
   echo "PROMPT_COMMAND='echo -ne \"\033]0;mkebsboot\007\"'">>$INSTANCE_HOME/mkebsboot.sh
   echo "export INSTANCE_NAME='mkebsboot'">>$INSTANCE_HOME/mkebsboot.sh
   echo "export IMAGE_DIR='$IMAGE_DIR'">>$INSTANCE_HOME/mkebsboot.sh
   echo "export EBS_DEVICE='$EBS_DEVICE'">>$INSTANCE_HOME/mkebsboot.sh
   echo "export EBS_MOUNT_POINT='$EBS_MOUNT_POINT'">>$INSTANCE_HOME/mkebsboot.sh
   echo 'cd $INSTANCE_HOME'>>$INSTANCE_HOME/mkebsboot.sh
   echo 'echo creating a filesystem and mounting the ebs volume'>>$INSTANCE_HOME/mkebsboot.sh
   echo 'mkdir -p $EBS_MOUNT_POINT'>>$INSTANCE_HOME/mkebsboot.sh
   echo 'mkfs.ext3 $EBS_DEVICE'>>$INSTANCE_HOME/mkebsboot.sh
   echo 'mount $EBS_DEVICE $EBS_MOUNT_POINT'>>$INSTANCE_HOME/mkebsboot.sh
   echo 'echo making a local working copy of the boot disk'>>$INSTANCE_HOME/mkebsboot.sh
   echo 'rsync --stats -av --exclude /root/.bash_history --exclude /home/*/.bash_history --exclude /etc/ssh/ssh_host_* --exclude /etc/ssh/moduli --exclude /etc/udev/rules.d/*persistent-net.rules --exclude /var/lib/ec2/* --exclude=/mnt/* --exclude=/proc/* --exclude=/tmp/* / $IMAGE_DIR'>>$INSTANCE_HOME/mkebsboot.sh
   echo 'echo preparing the local working copy'>>$INSTANCE_HOME/mkebsboot.sh
   echo 'touch $IMAGE_DIR/etc/init.d/ec2-init-user-data'>>$INSTANCE_HOME/mkebsboot.sh
   echo 'echo copying the local working copy to the ebs mount'>>$INSTANCE_HOME/mkebsboot.sh
   echo 'tar -cSf - -C ./ . | tar xvf - -C $EBS_MOUNT_POINT'>>$INSTANCE_HOME/mkebsboot.sh
   echo 'unmount $EBS_MOUNT_POINT'>>$INSTANCE_HOME/mkebsboot.sh
   echo 'echo ----COMPLETE----'>>$INSTANCE_HOME/mkebsboot.sh
   echo 'exit 0'>>$INSTANCE_HOME/mkebsboot.sh
   chmod u+x $INSTANCE_HOME/mkebsboot.sh
   ;;
status)
   default || exit 1
   findPid $INSTANCE_NAME || exit 1
   echo [$FOUND_PID]
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
   tail $LOG_DIR/stdout.log;;
tailerr)
   default || exit 1
   tail $LOG_DIR/stderr.log;;
run)
   default || exit 1
   $INSTANCE_HOME/$INSTANCE_NAME.sh;;
esac
exit 0
