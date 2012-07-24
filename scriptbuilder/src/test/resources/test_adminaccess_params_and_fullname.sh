cat > /etc/sudoers <<-'END_OF_JCLOUDS_FILE'
	root ALL = (ALL) ALL
	%wheel ALL = (ALL) NOPASSWD:ALL
END_OF_JCLOUDS_FILE
chmod 0440 /etc/sudoers
mkdir -p /over/ridden
groupadd -f wheel
useradd -c 'JClouds Foo' -s /bin/bash -g wheel -m  -d /over/ridden/foo -p 'crypt(bar)' foo
mkdir -p /over/ridden/foo/.ssh
cat >> /over/ridden/foo/.ssh/authorized_keys <<-'END_OF_JCLOUDS_FILE'
	fooPublicKey
END_OF_JCLOUDS_FILE
chmod 600 /over/ridden/foo/.ssh/authorized_keys
chown -R foo /over/ridden/foo
exec 3<> /etc/ssh/sshd_config && awk -v TEXT="PasswordAuthentication no
PermitRootLogin no
" 'BEGIN {print TEXT}{print}' /etc/ssh/sshd_config >&3
hash service 2>&- && service ssh reload 2>&- || /etc/init.d/ssh* reload
awk -v user=^${SUDO_USER:=${USER}}: -v password='crypt(0)' 'BEGIN { FS=OFS=":" } $0 ~ user { $2 = password } 1' /etc/shadow >/etc/shadow.${SUDO_USER:=${USER}}
test -f /etc/shadow.${SUDO_USER:=${USER}} && mv /etc/shadow.${SUDO_USER:=${USER}} /etc/shadow
