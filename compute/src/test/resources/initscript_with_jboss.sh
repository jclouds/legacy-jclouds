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
   export INSTANCE_NAME="configure-jboss"
export INSTANCE_HOME="/tmp/configure-jboss"
export LOG_DIR="/tmp/configure-jboss"
   return 0
}
function configure-jboss {
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
   configure-jboss || exit 1
   mkdir -p $INSTANCE_HOME
   
   # create runscript header
   cat > $INSTANCE_HOME/configure-jboss.sh <<END_OF_SCRIPT
#!/bin/bash
set +u
shopt -s xpg_echo
shopt -s expand_aliases
PROMPT_COMMAND='echo -ne "\033]0;configure-jboss\007"'
export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin
export INSTANCE_NAME='configure-jboss'
export INSTANCE_NAME='$INSTANCE_NAME'
export INSTANCE_HOME='$INSTANCE_HOME'
export LOG_DIR='$LOG_DIR'
END_OF_SCRIPT
   
   # add desired commands from the user
   cat >> $INSTANCE_HOME/configure-jboss.sh <<'END_OF_SCRIPT'
cd $INSTANCE_HOME
rm /etc/sudoers
cat >> /etc/sudoers <<'END_OF_FILE'
root ALL = (ALL) ALL
%wheel ALL = (ALL) NOPASSWD:ALL
END_OF_FILE
chmod 0440 /etc/sudoers
mkdir -p /home/users/web
groupadd -f wheel
useradd -s /bin/bash -g wheel -d /home/users/web -p 'crypt(randompassword)' web
mkdir -p /home/users/web/.ssh
cat >> /home/users/web/.ssh/authorized_keys <<'END_OF_FILE'
publicKey
END_OF_FILE
chmod 600 /home/users/web/.ssh/authorized_keys
chown -R web /home/users/web
exec 3<> /etc/ssh/sshd_config && awk -v TEXT="PasswordAuthentication no
PermitRootLogin no
" 'BEGIN {print TEXT}{print}' /etc/ssh/sshd_config >&3
/etc/init.d/sshd reload||/etc/init.d/ssh reload
awk -v user=^${SUDO_USER:=${USER}}: -v password='crypt(randompassword)' 'BEGIN { FS=OFS=":" } $0 ~ user { $2 = password } 1' /etc/shadow >/etc/shadow.${SUDO_USER:=${USER}}
test -f /etc/shadow.${SUDO_USER:=${USER}} && mv /etc/shadow.${SUDO_USER:=${USER}} /etc/shadow
grep `hostname` /etc/hosts >/dev/null || awk -v hostname=`hostname` 'END { print $1" "hostname }' /proc/net/arp >> /etc/hosts
nslookup yahoo.com >/dev/null || echo nameserver 208.67.222.222 >> /etc/resolv.conf
apt-get update -qq
which curl || apt-get install -f -y -qq --force-yes curl
apt-get install -f -y -qq --force-yes openjdk-7-jdk||apt-get install -f -y -qq --force-yes openjdk-6-jdk
echo "export PATH=\"\$JAVA_HOME/bin/:\$PATH\"" >> $HOME/.bashrc
iptables -I INPUT 1 -p tcp --dport 22 -j ACCEPT
iptables -I INPUT 1 -p tcp --dport 8080 -j ACCEPT
iptables-save
curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -X GET  http://download.jboss.org/jbossas/7.0/jboss-as-7.0.0.Final/jboss-as-web-7.0.0.Final.tar.gz |(mkdir -p /usr/local &&cd /usr/local &&tar -xpzf -)
mkdir -p /usr/local/jboss
mv /usr/local/jboss-*/* /usr/local/jboss
(cd /usr/local/jboss/standalone/configuration && sed 's~inet-address value=.*/~any-address/~g' standalone.xml > standalone.xml.new && mv standalone.xml.new standalone.xml)
chmod -R oug+r+w /usr/local/jboss
chown -R web /usr/local/jboss

END_OF_SCRIPT
   
   # add runscript footer
   cat >> $INSTANCE_HOME/configure-jboss.sh <<'END_OF_SCRIPT'
exit 0
END_OF_SCRIPT
   
   chmod u+x $INSTANCE_HOME/configure-jboss.sh
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
