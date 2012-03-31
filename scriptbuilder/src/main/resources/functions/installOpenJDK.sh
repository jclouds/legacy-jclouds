function setupJavaHomeInProfile() {
  test -n \"$SUDO_USER\" && cat >> `getent passwd $SUDO_USER| cut -f6 -d:`/.bashrc <<-'END_OF_JCLOUDS_FILE'
	export JAVA_HOME=/usr/local/jdk
	export PATH=$JAVA_HOME/bin:$PATH
END_OF_JCLOUDS_FILE
  cat >> /etc/bashrc <<-'END_OF_JCLOUDS_FILE'
	export JAVA_HOME=/usr/local/jdk
	export PATH=$JAVA_HOME/bin:$PATH
END_OF_JCLOUDS_FILE
  cat >> $HOME/.bashrc <<-'END_OF_JCLOUDS_FILE'
	export JAVA_HOME=/usr/local/jdk
	export PATH=$JAVA_HOME/bin:$PATH
END_OF_JCLOUDS_FILE
  cat >> /etc/skel/.bashrc <<-'END_OF_JCLOUDS_FILE'
	export JAVA_HOME=/usr/local/jdk
	export PATH=$JAVA_HOME/bin:$PATH
END_OF_JCLOUDS_FILE
}

function installOpenJDK() {
  if hash apt-get 2>/dev/null; then
    export pkg=openjdk-7-jdk
    apt-get-install $pkg || ( apt-get-upgrade && apt-get-install $pkg )
    export JAVA_HOME=`ls -d /usr/lib/jvm/java-7-openjdk-*|grep -v common`
  elif hash yum 2>/dev/null; then
    #TODO: find a jdk7 yum repo
    export pkg=java-1.6.0-openjdk-devel
    yum --nogpgcheck -y install $pkg
    export JAVA_HOME=`ls -d /usr/lib/jvm/java-1.6.0-openjdk-*`
  else
    abort "we only support apt-get and yum right now... please contribute!"
    return 1
  fi
  ln -Fs $JAVA_HOME /usr/local/jdk 
  /usr/local/jdk/bin/java -version || abort "cannot run java"
  setupJavaHomeInProfile
}