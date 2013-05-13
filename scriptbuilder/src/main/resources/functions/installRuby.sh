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
function installRuby() {
  if ! hash ruby 2>/dev/null; then
    if which dpkg &> /dev/null; then
      apt-get-update
      apt-get install -y ruby ruby-dev build-essential
    elif which rpm &> /dev/null; then
      # Disable chef from the base repo (http://tickets.opscode.com/browse/CHEF-2906)
      sed -i "s/\[base\]/\0\n\exclude=ruby*/g" /etc/yum.repos.d/CentOS-Base.repo
      # Make sure to install an appropriate ruby version
      yum erase -y ruby ruby-libs
      rpm -Uvh http://rbel.co/rbel5
      yum install -y ruby ruby-devel make gcc gcc-c++ automake autoconf
    else
      abort "we only support apt-get and yum right now... please contribute"
    fi
  fi
}
