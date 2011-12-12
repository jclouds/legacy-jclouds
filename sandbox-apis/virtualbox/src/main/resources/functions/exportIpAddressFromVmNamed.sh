function exportIpAddressFromVmNamed {
   unset FOUND_IP_ADDRESS;
   [ $# -eq 1 ] || {
      abort "installGuestAdditions requires virtual machine name parameter"
      return 1
   }
   local VMNAME="$0"; shift
   local _FOUND=`vboxmanage guestproperty enumerate "$VMNAME" --patterns "/VirtualBox/GuestInfo/Net/0/V4/IP" | awk '{ print $4 }' | cut -c 1-14`
   [ -n "$_FOUND" ] && {
      export FOUND_IP_ADDRESS=$_FOUND
      echo [$FOUND_IP_ADDRESS]
      return 0
   } || {
      return 1
   }
}
