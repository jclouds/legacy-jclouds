rm /etc/sudoers
cat >> /etc/sudoers <<'END_OF_FILE'
root ALL = (ALL) ALL
%wheel ALL = (ALL) NOPASSWD:ALL
END_OF_FILE
chmod 0440 /etc/sudoers
mkdir -p /home/users
groupadd -f wheel
useradd -s /bin/bash -g wheel -m  -d /home/users/foo -p 'crypt(bar)' foo
mkdir -p /home/users/foo/.ssh
cat >> /home/users/foo/.ssh/authorized_keys <<'END_OF_FILE'
fooPublicKey
END_OF_FILE
chmod 600 /home/users/foo/.ssh/authorized_keys
chown -R foo /home/users/foo
exec 3<> /etc/ssh/sshd_config && awk -v TEXT="PasswordAuthentication no
PermitRootLogin no
" 'BEGIN {print TEXT}{print}' /etc/ssh/sshd_config >&3
/etc/init.d/sshd reload||/etc/init.d/ssh reload
awk -v user=^${SUDO_USER:=${USER}}: -v password='crypt(0)' 'BEGIN { FS=OFS=":" } $0 ~ user { $2 = password } 1' /etc/shadow >/etc/shadow.${SUDO_USER:=${USER}}
test -f /etc/shadow.${SUDO_USER:=${USER}} && mv /etc/shadow.${SUDO_USER:=${USER}} /etc/shadow
