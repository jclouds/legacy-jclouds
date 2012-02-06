function installModuleAssistantIfNeeded {
   unset OSNAME;
   local OSNAME=`lsb_release -d -s | cut -d ' ' -f 1`; shift
   if [ $OSNAME = 'Ubuntu' ]
   then
      echo "OS is Ubuntu"
      apt-get -f -y -qq --force-yes install build-essential module-assistant && m-a prepare -i
   fi
   return 0
}