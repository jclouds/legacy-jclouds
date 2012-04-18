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
    export JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-6-openjdk}
    test -d $JAVA_HOME || apt-get-install openjdk-6-jdk || ( apt-get-update && apt-get-install openjdk-6-jdk )
  elif hash yum 2>/dev/null; then
    export pkg=java-1.6.0-openjdk-devel
    yum --nogpgcheck -y install $pkg &&
    export JAVA_HOME=`ls -d /usr/lib/jvm/java-1.6.0-openjdk-*`
  else
    abort "we only support apt-get and yum right now... please contribute!"
    return 1
  fi
  test -n "$JAVA_HOME" || abort "JDK installation failed!"
  ln -Fs $JAVA_HOME /usr/local/jdk 
  /usr/local/jdk/bin/java -version || abort "cannot run java"
  setupJavaHomeInProfile
}