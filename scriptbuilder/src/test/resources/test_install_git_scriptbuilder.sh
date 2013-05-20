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
   export INSTANCE_NAME="install_git"
export INSTANCE_HOME="/tmp/$INSTANCE_NAME"
export LOG_DIR="$INSTANCE_HOME"
   return $?
}
function install_git {
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
   install_git || exit 1
   mkdir -p $INSTANCE_HOME
   
   # create runscript header
   cat > $INSTANCE_HOME/install_git.sh <<-'END_OF_JCLOUDS_SCRIPT'
	#!/bin/bash
	set +u
	shopt -s xpg_echo
	shopt -s expand_aliases
	
	PROMPT_COMMAND='echo -ne \"\033]0;install_git\007\"'
	export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin

	export INSTANCE_NAME='install_git'
END_OF_JCLOUDS_SCRIPT
   cat >> $INSTANCE_HOME/install_git.sh <<-END_OF_JCLOUDS_SCRIPT
	export INSTANCE_NAME='$INSTANCE_NAME'
	export INSTANCE_HOME='$INSTANCE_HOME'
	export LOG_DIR='$LOG_DIR'
END_OF_JCLOUDS_SCRIPT
   cat >> $INSTANCE_HOME/install_git.sh <<-'END_OF_JCLOUDS_SCRIPT'
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
function installGit() {
  if which dpkg &> /dev/null; then
    ensure_cmd_or_install_package_apt git git-core
  elif which rpm &> /dev/null; then
    case $(uname -r) in
      *el5)
        wget http://download.fedoraproject.org/pub/epel/5/$(uname -i)/epel-release-5-4.noarch.rpm &&
        rpm -Uvh epel-release-5-4.noarch.rpm
        rm -f epel-release-5-4.noarch.rpm;;
      *el6)
        wget http://download.fedoraproject.org/pub/epel/6/$(uname -i)/epel-release-6-7.noarch.rpm &&
        rpm -Uvh epel-release-6-7.noarch.rpm 
        rm -f epel-release-6-7.noarch.rpm;; 
    esac
    ensure_cmd_or_install_package_yum git git-core
  else
    abort "we only support apt-get and yum right now... please contribute!"
    return 1
  fi
  return 0  
}

END_OF_JCLOUDS_SCRIPT
   
   # add desired commands from the user
   cat >> $INSTANCE_HOME/install_git.sh <<-'END_OF_JCLOUDS_SCRIPT'
	cd $INSTANCE_HOME
	rm -f $INSTANCE_HOME/rc
	trap 'echo $?>$INSTANCE_HOME/rc' 0 1 2 3 15
	setupPublicCurl || exit 1
	
	installGit || exit 1
	
END_OF_JCLOUDS_SCRIPT
   
   # add runscript footer
   cat >> $INSTANCE_HOME/install_git.sh <<-'END_OF_JCLOUDS_SCRIPT'
	exit $?
	
END_OF_JCLOUDS_SCRIPT
   
   chmod u+x $INSTANCE_HOME/install_git.sh
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
