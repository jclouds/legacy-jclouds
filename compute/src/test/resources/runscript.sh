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
   export INSTANCE_NAME="runScriptWithCreds"
export INSTANCE_HOME="/tmp/runScriptWithCreds"
export LOG_DIR="/tmp/runScriptWithCreds"
   return 0
}
function runScriptWithCreds {
      return 0
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
      echo $INSTANCE_NAME already running pid [$FOUND_PID]
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
   runScriptWithCreds || exit 1
   mkdir -p $INSTANCE_HOME
   
   # create runscript header
   cat > $INSTANCE_HOME/runScriptWithCreds.sh <<END_OF_SCRIPT
#!/bin/bash
set +u
shopt -s xpg_echo
shopt -s expand_aliases
PROMPT_COMMAND='echo -ne "\033]0;runScriptWithCreds\007"'
export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin
export INSTANCE_NAME='runScriptWithCreds'
export INSTANCE_NAME='$INSTANCE_NAME'
export INSTANCE_HOME='$INSTANCE_HOME'
export LOG_DIR='$LOG_DIR'
END_OF_SCRIPT
   
   # add desired commands from the user
   cat >> $INSTANCE_HOME/runScriptWithCreds.sh <<'END_OF_SCRIPT'
cd $INSTANCE_HOME
grep `hostname` /etc/hosts >/dev/null || awk -v hostname=`hostname` 'END { print $1" "hostname }' /proc/net/arp >> /etc/hosts
nslookup yahoo.com >/dev/null || echo nameserver 208.67.222.222 >> /etc/resolv.conf
which curl >&- 2>&-|| apt-get install -f -y -qq --force-yes curl
which nslookup >&- 2>&-|| apt-get install -f -y -qq --force-yes dnsutils
mkdir -p /usr/local/jdk
curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -X GET  http://download.oracle.com/otn-pub/java/jdk/7u1-b08/jdk-7u1-linux-x64.tar.gz |(mkdir -p /usr/local &&cd /usr/local &&tar -xpzf -)
mv /usr/local/jdk1.7*/* /usr/local/jdk/
test -n "$SUDO_USER" && 
cat >> /home/$SUDO_USER/.bashrc <<'END_OF_FILE'
export JAVA_HOME=/usr/local/jdk
export PATH=$JAVA_HOME/bin:$PATH
END_OF_FILE
cat >> /etc/bashrc <<'END_OF_FILE'
export JAVA_HOME=/usr/local/jdk
export PATH=$JAVA_HOME/bin:$PATH
END_OF_FILE
cat >> $HOME/.bashrc <<'END_OF_FILE'
export JAVA_HOME=/usr/local/jdk
export PATH=$JAVA_HOME/bin:$PATH
END_OF_FILE
cat >> /etc/skel/.bashrc <<'END_OF_FILE'
export JAVA_HOME=/usr/local/jdk
export PATH=$JAVA_HOME/bin:$PATH
END_OF_FILE
ln -fs /usr/local/jdk/bin/java /usr/bin/java

END_OF_SCRIPT
   
   # add runscript footer
   cat >> $INSTANCE_HOME/runScriptWithCreds.sh <<'END_OF_SCRIPT'
exit 0
END_OF_SCRIPT
   
   chmod u+x $INSTANCE_HOME/runScriptWithCreds.sh
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
exit 0
