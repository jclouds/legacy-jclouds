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
   export INSTANCE_NAME="bootstrap"
export INSTANCE_HOME="/tmp/bootstrap"
export LOG_DIR="$INSTANCE_HOME"
   return $?
}
function bootstrap {
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
   bootstrap || exit 1
   mkdir -p $INSTANCE_HOME
   
   # create runscript header
   cat > $INSTANCE_HOME/bootstrap.sh <<-'END_OF_JCLOUDS_SCRIPT'
	#!/bin/bash
	set +u
	shopt -s xpg_echo
	shopt -s expand_aliases
	
	PROMPT_COMMAND='echo -ne \"\033]0;bootstrap\007\"'
	export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin

	export INSTANCE_NAME='bootstrap'
END_OF_JCLOUDS_SCRIPT
   cat >> $INSTANCE_HOME/bootstrap.sh <<-END_OF_JCLOUDS_SCRIPT
	export INSTANCE_NAME='$INSTANCE_NAME'
	export INSTANCE_HOME='$INSTANCE_HOME'
	export LOG_DIR='$LOG_DIR'
END_OF_JCLOUDS_SCRIPT
   cat >> $INSTANCE_HOME/bootstrap.sh <<-'END_OF_JCLOUDS_SCRIPT'
	function abort {
   echo "aborting: $@" 1>&2
   exit 1
}
alias apt-get-update="apt-get update -qq"
alias apt-get-install="apt-get install -f -y -qq --force-yes"
alias yum-install="yum --quiet --nogpgcheck -y install"

function ensure_cmd_or_install_package_apt(){
  local cmd=$1
  shift
  local pkg=$*
  
  hash $cmd 2>/dev/null || ( apt-get-update && apt-get-install $pkg )
}

function ensure_cmd_or_install_package_yum(){
  local cmd=$1
  shift
  local pkg=$*
  hash $cmd 2>/dev/null || yum-install $pkg
}

function ensure_netutils_apt() {
  ensure_cmd_or_install_package_apt nslookup dnsutils
  ensure_cmd_or_install_package_apt curl curl
}

function ensure_netutils_yum() {
  ensure_cmd_or_install_package_yum nslookup bind-utils
  ensure_cmd_or_install_package_yum curl curl
}

# most network services require that the hostname is in
# the /etc/hosts file, or they won't operate
function ensure_hostname_in_hosts() {
  [ -n "$SSH_CONNECTION" ] && {
    local ipaddr=`echo $SSH_CONNECTION | awk '{print $3}'`
  } || {
    local ipaddr=`hostname -i`
  }
  # NOTE: we blindly trust existing hostname settings in /etc/hosts
  egrep -q `hostname` /etc/hosts || echo "$ipaddr `hostname`" >> /etc/hosts
}

# download locations for many services are at public dns
function ensure_can_resolve_public_dns() {
  nslookup yahoo.com | grep yahoo.com > /dev/null || echo nameserver 208.67.222.222 >> /etc/resolv.conf
}

function setupPublicCurl() {
  ensure_hostname_in_hosts
  if which dpkg &> /dev/null; then
    ensure_netutils_apt
  elif which rpm &> /dev/null; then
    ensure_netutils_yum
  else
    abort "we only support apt-get and yum right now... please contribute!"
    return 1
  fi
  ensure_can_resolve_public_dns
  return 0  
}
# adds JAVA_HOME and into PATH in global and user-specific profiles
function setupJavaHomeInProfile() {
  PROFILES=(/etc/bashrc $HOME/.bashrc /etc/skel/.bashrc)
  test -n "$SUDO_USER" &&
    PROFILES=(${PROFILES[*]} `getent passwd $SUDO_USER| cut -f6 -d:`/.bashrc)
  for PROFILE in ${PROFILES[*]}; do
    cat >> $PROFILE <<-'END_OF_JCLOUDS_FILE'
	export JAVA_HOME=/usr/local/jdk
	export PATH=$JAVA_HOME/bin:$PATH
END_OF_JCLOUDS_FILE
  done
}

# resets JAVA_HOME to what an openjdk installer created
function findOpenJDK() {
  local oldJavaHome=$JAVA_HOME
  unset JAVA_HOME
  for CANDIDATE in $oldJavaHome `ls -d /usr/lib/jvm/java-1.6.0-openjdk-* /usr/lib/jvm/java-6-openjdk-* /usr/lib/jvm/java-6-openjdk 2>&-`; do
    if [ -n "$CANDIDATE" -a -x "$CANDIDATE/bin/java" ]; then
      export JAVA_HOME=$CANDIDATE
      break
    fi
  done
}

# assures JDK installed and JAVA_HOME to a link at /usr/local/jdk
function installOpenJDK() {
  if [ "$JAVA_HOME" == "/usr/local/jdk" ]; then
    echo skipping as JAVA_HOME is already set to /usr/local/jdk
    return 0
  fi
  if [ -n "$JAVA_HOME" -a -x "$JAVA_HOME/bin/java" ]; then
    echo reusing JAVA_HOME $JAVA_HOME
  else
    if which dpkg &> /dev/null; then
      apt-get-update && apt-get-install openjdk-6-jdk
    elif which rpm &> /dev/null; then
      yum-install java-1.6.0-openjdk-devel
    else
      abort "we only support apt-get and yum right now... please contribute"
    fi
    findOpenJDK
    if [ -n "$JAVA_HOME" ]; then
      echo installed JAVA_HOME $JAVA_HOME
    else
      abort "JDK installation failed"
    fi
  fi
  rm -rf /usr/local/jdk
  ln -Fs $JAVA_HOME /usr/local/jdk
  /usr/local/jdk/bin/java -version || abort "cannot run java"
  setupJavaHomeInProfile
}

END_OF_JCLOUDS_SCRIPT
   
   # add desired commands from the user
   cat >> $INSTANCE_HOME/bootstrap.sh <<-'END_OF_JCLOUDS_SCRIPT'
	cd $INSTANCE_HOME
	rm -f $INSTANCE_HOME/rc
	trap 'echo $?>$INSTANCE_HOME/rc' 0 1 2 3 15
	cat > /etc/sudoers <<-'END_OF_JCLOUDS_FILE'
		root ALL = (ALL) ALL
		%wheel ALL = (ALL) NOPASSWD:ALL
	END_OF_JCLOUDS_FILE
	chmod 0440 /etc/sudoers
	mkdir -p /home/users
	groupadd -f wheel
	useradd -c 'defaultAdminUsername' -s /bin/bash -g wheel -m  -d /home/users/defaultAdminUsername -p 'crypt(randompassword)' defaultAdminUsername
	mkdir -p /home/users/defaultAdminUsername/.ssh
	cat >> /home/users/defaultAdminUsername/.ssh/authorized_keys <<-'END_OF_JCLOUDS_FILE'
		publicKey
	END_OF_JCLOUDS_FILE
	chmod 600 /home/users/defaultAdminUsername/.ssh/authorized_keys
	chown -R defaultAdminUsername /home/users/defaultAdminUsername
	exec 3<> /etc/ssh/sshd_config && awk -v TEXT="PasswordAuthentication no
	PermitRootLogin no
	" 'BEGIN {print TEXT}{print}' /etc/ssh/sshd_config >&3
	hash service 2>&- && service ssh reload 2>&- || /etc/init.d/ssh* reload
	awk -v user=^${SUDO_USER:=${USER}}: -v password='crypt(randompassword)' 'BEGIN { FS=OFS=":" } $0 ~ user { $2 = password } 1' /etc/shadow >/etc/shadow.${SUDO_USER:=${USER}}
	test -f /etc/shadow.${SUDO_USER:=${USER}} && mv /etc/shadow.${SUDO_USER:=${USER}} /etc/shadow
	setupPublicCurl || return 1
	installOpenJDK || return 1
	
END_OF_JCLOUDS_SCRIPT
   
   # add runscript footer
   cat >> $INSTANCE_HOME/bootstrap.sh <<-'END_OF_JCLOUDS_SCRIPT'
	exit $?
	
END_OF_JCLOUDS_SCRIPT
   
   chmod u+x $INSTANCE_HOME/bootstrap.sh
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
