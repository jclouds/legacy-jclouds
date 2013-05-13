#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# note this does not work until someone addresses user auth
# https://issues.jenkins-ci.org/browse/JENKINS-10556
# https://forums.oracle.com/forums/thread.jspa?messageID=10236239&tstart=0
function installJDKFromURL() {
  if hash curl 2>/dev/null; then
    if [ `uname -m` == 'x86_64' ]; then
      local url=${1:-http://download.oracle.com/otn-pub/java/jdk/7/jdk-7-linux-x64.tar.gz}
    else
      local url=${1:-http://download.oracle.com/otn-pub/java/jdk/7/jdk-7-linux-i586.tar.gz}
    fi
    curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -X GET $url |(mkdir -p /usr/local &&cd /usr/local &&tar -xpzf -)
    mv /usr/local/jdk* /usr/local/jdk/
    test -n \"$SUDO_USER\" && cat >> /home/$SUDO_USER/.bashrc <<-'END_OF_JCLOUDS_FILE'
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
    # TODO: eventhough we are setting the above, sometimes images (ex.
    # cloudservers ubuntu) kick out of .bashrc (ex. [ -z "$PS1" ] &&
    # return), for this reason, we should also explicitly link.
    # A better way would be to update using alternatives or the like
    ln -fs /usr/local/jdk/bin/java /usr/bin/java
    /usr/bin/java -version || abort "cannot run /usr/bin/java"
  else
    abort "curl not available.. cannot install openjdk"
  fi
  return 0
}