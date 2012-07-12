rm /etc/sudoers
cat >> /etc/sudoers <<'END_OF_FILE'
root ALL = (ALL) ALL
%wheel ALL = (ALL) NOPASSWD:ALL
END_OF_FILE
chmod 0440 /etc/sudoers
mkdir -p /home/users
groupadd -f wheel
useradd -c defaultAdminUsername -s /bin/bash -g wheel -d /home/users/defaultAdminUsername -p 'crypt(0)' defaultAdminUsername
mkdir -p /home/users/defaultAdminUsername/.ssh
cat >> /home/users/defaultAdminUsername/.ssh/authorized_keys <<'END_OF_FILE'
publicKey
END_OF_FILE
chmod 600 /home/users/defaultAdminUsername/.ssh/authorized_keys
chown -R defaultAdminUsername /home/users/defaultAdminUsername
exec 3<> /etc/ssh/sshd_config && awk -v TEXT="PasswordAuthentication no
PermitRootLogin no
" 'BEGIN {print TEXT}{print}' /etc/ssh/sshd_config >&3
hash service 2>/dev/null && service ssh reload || /etc/init.d/ssh* reload
awk -v user=^${SUDO_USER:=${USER}}: -v password='crypt(1)' 'BEGIN { FS=OFS=":" } $0 ~ user { $2 = password } 1' /etc/shadow >/etc/shadow.${SUDO_USER:=${USER}}
test -f /etc/shadow.${SUDO_USER:=${USER}} && mv /etc/shadow.${SUDO_USER:=${USER}} /etc/shadow
