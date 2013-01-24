function cleanupUdevIfNeeded {
   if [ -f '/etc/udev/rules.d/70-persistent-net.rules' ]
   then
      rm /etc/udev/rules.d/70-persistent-net.rules
   fi

}