function installModuleAssistantIfNeeded {
   unset OSNAME;
   local OSNAME=`lsb_release -d -s | cut -d ' ' -f 1`; shift
   if [ $OSNAME = 'Ubuntu' ]
   then
      echo "OS is Ubuntu"
      apt-get -f -y -qq --force-yes install build-essential module-assistant;
      m-a prepare -i
      rm /etc/udev/rules.d/70-persistent-net.rules;
      mkdir /etc/udev/rules.d/70-persistent-net.rules;
      rm -rf /dev/.udev/;
      rm /lib/udev/rules.d/75-persistent-net-generator.rules
      rm -f /etc/passwd.lock /etc/group.lock /etc/gshadow.lock
   fi
}