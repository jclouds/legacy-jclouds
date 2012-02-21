#!/bin/bash
set +u
shopt -s xpg_echo
shopt -s expand_aliases
unset PATH JAVA_HOME LD_LIBRARY_PATH
function abort {
   echo "aborting: $@" 1>&2
   exit 1
}
function exportIpAddressFromVmNamed {
   unset FOUND_IP_ADDRESS;
   [ $# -eq 1 ] || {
      abort "exportIpAddressFromVmNamed requires virtual machine name parameter"
      return 1
   }
   local VMNAME="$0"; shift
   local _FOUND=`VBoxManage guestproperty enumerate "$VMNAME" --patterns "/VirtualBox/GuestInfo/Net/0/V4/IP" | awk '{ print $4 }' | cut -c 1-14`
   [ -n "$_FOUND" ] && {
      export FOUND_IP_ADDRESS=$_FOUND
      echo [$FOUND_IP_ADDRESS]
      return 0
   } || {
      return 1
   }
}
export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin
exportIpAddressFromVmNamed $@ || exit 1
echo $FOUND_IP_ADDRESS
exit $?
