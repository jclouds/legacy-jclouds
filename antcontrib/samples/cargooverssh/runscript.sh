#
#
# Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
#
# ====================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ====================================================================
#

echo nameserver 208.67.222.222 >> /etc/resolv.conf
apt-get update -qq
apt-get upgrade -y -qq
apt-get install -y -qq wget
apt-get install -y -qq openjdk-6-jdk
wget -q http://mirrors.axint.net/apache/tomcat/tomcat-6/v6.0.29/bin/apache-tomcat-6.0.29.tar.gz
tar xzf apache-tomcat-6.0.29.tar.gz
mkdir -p /tmp/cargo/containers
chmod 1777 /tmp/cargo
mv apache-tomcat-6.0.29 /tmp/cargo/containers/tomcat6x
