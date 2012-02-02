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