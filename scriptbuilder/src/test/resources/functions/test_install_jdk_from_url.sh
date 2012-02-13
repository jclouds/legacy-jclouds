setupPublicCurl || return 1
curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -X GET  http://download.oracle.com/otn-pub/java/jdk/7/jdk-7-linux-x64.tar.gz |(mkdir -p /usr/local &&cd /usr/local &&tar -xpzf -)
mv /usr/local/jdk* /usr/local/jdk/
test -n "$SUDO_USER" && 
cat >> /home/$SUDO_USER/.bashrc <<'END_OF_FILE'
export JAVA_HOME=/usr/local/jdk
export PATH=$JAVA_HOME/bin:$PATH
END_OF_FILE
cat >> /etc/bashrc <<'END_OF_FILE'
export JAVA_HOME=/usr/local/jdk
export PATH=$JAVA_HOME/bin:$PATH
END_OF_FILE
cat >> $HOME/.bashrc <<'END_OF_FILE'
export JAVA_HOME=/usr/local/jdk
export PATH=$JAVA_HOME/bin:$PATH
END_OF_FILE
cat >> /etc/skel/.bashrc <<'END_OF_FILE'
export JAVA_HOME=/usr/local/jdk
export PATH=$JAVA_HOME/bin:$PATH
END_OF_FILE
ln -fs /usr/local/jdk/bin/java /usr/bin/java
