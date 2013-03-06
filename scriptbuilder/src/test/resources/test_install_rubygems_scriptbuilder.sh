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
   export INSTANCE_NAME="install_rubygems"
export INSTANCE_HOME="/tmp/$INSTANCE_NAME"
export LOG_DIR="$INSTANCE_HOME"
   return $?
}
function install_rubygems {
      return $?
}
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
   install_rubygems || exit 1
   mkdir -p $INSTANCE_HOME
   
   # create runscript header
   cat > $INSTANCE_HOME/install_rubygems.sh <<-'END_OF_JCLOUDS_SCRIPT'
	#!/bin/bash
	set +u
	shopt -s xpg_echo
	shopt -s expand_aliases
	
	PROMPT_COMMAND='echo -ne \"\033]0;install_rubygems\007\"'
	export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin

	export INSTANCE_NAME='install_rubygems'
END_OF_JCLOUDS_SCRIPT
   cat >> $INSTANCE_HOME/install_rubygems.sh <<-END_OF_JCLOUDS_SCRIPT
	export INSTANCE_NAME='$INSTANCE_NAME'
	export INSTANCE_HOME='$INSTANCE_HOME'
	export LOG_DIR='$LOG_DIR'
END_OF_JCLOUDS_SCRIPT
   
   # add desired commands from the user
   cat >> $INSTANCE_HOME/install_rubygems.sh <<-'END_OF_JCLOUDS_SCRIPT'
	cd $INSTANCE_HOME
	rm -f $INSTANCE_HOME/rc
	trap 'echo $?>$INSTANCE_HOME/rc' 0 1 2 3 15
	if ! hash gem 2>/dev/null; then
	(
	mkdir /tmp/$$
	curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -X GET  http://production.cf.rubygems.org/rubygems/rubygems-1.8.10.tgz |(mkdir -p /tmp/$$ &&cd /tmp/$$ &&tar -xpzf -)
	mkdir -p /tmp/rubygems
	mv /tmp/$$/*/* /tmp/rubygems
	rm -rf /tmp/$$
	cd /tmp/rubygems
	ruby setup.rb --no-format-executable
	rm -fr /tmp/rubygems
	)
	fi
	
END_OF_JCLOUDS_SCRIPT
   
   # add runscript footer
   cat >> $INSTANCE_HOME/install_rubygems.sh <<-'END_OF_JCLOUDS_SCRIPT'
	exit $?
	
END_OF_JCLOUDS_SCRIPT
   
   chmod u+x $INSTANCE_HOME/install_rubygems.sh
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
stdout)
   default || exit 1
   cat $LOG_DIR/stdout.log
   ;;
stderr)
   default || exit 1
   cat $LOG_DIR/stderr.log
   ;;
exitstatus)
   default || exit 1
   [ -f $LOG_DIR/rc ] && cat $LOG_DIR/rc;;
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
