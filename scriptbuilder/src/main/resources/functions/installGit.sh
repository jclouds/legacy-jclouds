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
function installGit() {
  if which dpkg &> /dev/null; then
    ensure_cmd_or_install_package_apt git git-core
  elif which rpm &> /dev/null; then
    case $(uname -r) in
      *el5)
        wget http://download.fedoraproject.org/pub/epel/5/$(uname -i)/epel-release-5-4.noarch.rpm &&
        rpm -Uvh epel-release-5-4.noarch.rpm
        rm -f epel-release-5-4.noarch.rpm;;
      *el6)
        wget http://download.fedoraproject.org/pub/epel/6/$(uname -i)/epel-release-6-7.noarch.rpm &&
        rpm -Uvh epel-release-6-7.noarch.rpm 
        rm -f epel-release-6-7.noarch.rpm;; 
    esac
    ensure_cmd_or_install_package_yum git git-core
  else
    abort "we only support apt-get and yum right now... please contribute!"
    return 1
  fi
  return 0  
}
