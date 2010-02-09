echo nameserver 208.67.222.222 >> /etc/resolv.conf
apt-get update -qq
apt-get upgrade -y -qq
apt-get install -y -qq wget
apt-get install -y -qq openjdk-6-jdk
wget -q http://mirrors.axint.net/apache/tomcat/tomcat-6/v6.0.24/bin/apache-tomcat-6.0.24.tar.gz
tar xzf apache-tomcat-6.0.24.tar.gz
mkdir -p /tmp/cargo/containers
chmod 1777 /tmp/cargo
mv apache-tomcat-6.0.24 /tmp/cargo/containers/tomcat6x
