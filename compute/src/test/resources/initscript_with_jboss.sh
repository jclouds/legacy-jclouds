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
   export INSTANCE_NAME="jboss"
export INSTANCE_HOME="/usr/local/jboss"
export LOG_DIR="/usr/local/jboss"
   return 0
}
function jboss {
   export JBOSS_HOME="/usr/local/jboss"
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
      sleep 1
      findPid $INSTANCE_NAME
      [ -n "$FOUND_PID" ] || abort "$INSTANCE_NAME did not start"
   }
   return 0
}
export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin
case $1 in
init)
   default || exit 1
   jboss || exit 1
   mkdir -p ~/.ssh
   cat >> ~/.ssh/authorized_keys <<'END_OF_FILE'
ssh-rsa
END_OF_FILE
   chmod 600 ~/.ssh/authorized_keys
   which curl || (apt-get install -f -y -qq --force-yes curl || (apt-get update && apt-get install -f -y -qq --force-yes curl))
   (which java && java -fullversion 2>&1|egrep -q 1.6 ) ||
   curl -X GET -s --retry 20  http://whirr.s3.amazonaws.com/0.2.0-incubating-SNAPSHOT/sun/java/install |(bash)
   echo nameserver 208.67.222.222 >> /etc/resolv.conf
   rm -rf /var/cache/apt /usr/lib/vmware-tools
   echo "export PATH=\"\$JAVA_HOME/bin/:\$PATH\"" >> /root/.bashrc
   iptables -I INPUT 1 -p tcp --dport 8080 -j ACCEPT
   iptables -I RH-Firewall-1-INPUT 1 -p tcp --dport 8080 -j ACCEPT
   iptables-save
   curl -X GET -s --retry 20  http://d19xvfg065k8li.cloudfront.net/jboss-6.0.0.Final.tar.gz |(mkdir -p /usr/local &&cd /usr/local &&tar -xpzf -)
   mkdir -p /usr/local/jboss
   mv /usr/local/jboss-*/* /usr/local/jboss
   chmod -R oug+r+w /usr/local/jboss
   mkdir -p $INSTANCE_HOME
   
   # create runscript header
   cat > $INSTANCE_HOME/jboss.sh <<END_OF_SCRIPT
#!/bin/bash
set +u
shopt -s xpg_echo
shopt -s expand_aliases
PROMPT_COMMAND='echo -ne "\033]0;jboss\007"'
export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin
export INSTANCE_NAME='jboss'
export JBOSS_HOME='$JBOSS_HOME'
export INSTANCE_NAME='$INSTANCE_NAME'
export INSTANCE_HOME='$INSTANCE_HOME'
export LOG_DIR='$LOG_DIR'
END_OF_SCRIPT
   
   # add desired commands from the user
   cat >> $INSTANCE_HOME/jboss.sh <<'END_OF_SCRIPT'
cd $INSTANCE_HOME
java -Xms128m -Xmx512m -XX:MaxPermSize=256m -Dorg.jboss.resolver.warning=true -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Djava.endorsed.dirs=lib/endorsed -classpath bin/run.jar org.jboss.Main -c jbossweb-standalone -b 0.0.0.0
END_OF_SCRIPT
   
   # add runscript footer
   cat >> $INSTANCE_HOME/jboss.sh <<'END_OF_SCRIPT'
exit 0
END_OF_SCRIPT
   
   chmod u+x $INSTANCE_HOME/jboss.sh
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
