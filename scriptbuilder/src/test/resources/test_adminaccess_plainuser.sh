mkdir -p /home/users/defaultAdminUsername
useradd -s /bin/bash -d /home/users/defaultAdminUsername -p 'crypt(0)' defaultAdminUsername
mkdir -p /home/users/defaultAdminUsername/.ssh
cat >> /home/users/defaultAdminUsername/.ssh/authorized_keys <<'END_OF_FILE'
publicKey
END_OF_FILE
chmod 600 /home/users/defaultAdminUsername/.ssh/authorized_keys
mkdir -p /home/users/defaultAdminUsername/.ssh
rm /home/users/defaultAdminUsername/.ssh/id_rsa
cat >> /home/users/defaultAdminUsername/.ssh/id_rsa <<'END_OF_FILE'
privateKey
END_OF_FILE
chmod 600 /home/users/defaultAdminUsername/.ssh/id_rsa
chown -R defaultAdminUsername /home/users/defaultAdminUsername
