#!/bin/bash
set +u
shopt -s xpg_echo
shopt -s expand_aliases
unset PATH JAVA_HOME LD_LIBRARY_PATH
function abort {
   echo "aborting: $@" 1>&2
   exit 1
}
function installGuestAdditions {
   unset OSNAME;
   [ $# -eq 1 ] || {
      abort "installGuestAdditions requires virtual machine name parameter"
      return 1
   }
   local OSNAME=`lsb_release -d -s | cut -d ' ' -f 1`; shift
   if [ $OSNAME = 'Ubuntu' ]
   then
      echo "OS Name is Ubuntu"
       `apt-get install build-essential module-assistant && m-a prepare -i`
   fi
   return 0
}
export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin
(mkdir -p /tmp && cd /tmp && [ ! -f VBoxGuestAdditions_4.1.6.iso ] && curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -C - -X GET  http://download.virtualbox.org/virtualbox/4.1.6/VBoxGuestAdditions_4.1.6.iso >VBoxGuestAdditions_4.1.6.iso)
mount -o loop /tmp/VBoxGuestAdditions_4.1.6.iso /mnt
installGuestAdditions || exit 1
sh /mnt/VBoxLinuxAdditions.run
umount /mnt
exit 0
