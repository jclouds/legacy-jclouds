setupPublicCurl || return 1
(mkdir -p /tmp/ && cd /tmp/ && [ ! -f VBoxGuestAdditions_4.1.6.iso ] && curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -C - -X GET  http://download.virtualbox.org/virtualbox/4.1.6/VBoxGuestAdditions_4.1.6.iso >VBoxGuestAdditions_4.1.6.iso)
mount -o loop /tmp/VBoxGuestAdditions_4.1.6.iso /mnt
installModuleAssistantIfNeeded || return 1
/mnt/VBoxLinuxAdditions.run
umount /mnt
