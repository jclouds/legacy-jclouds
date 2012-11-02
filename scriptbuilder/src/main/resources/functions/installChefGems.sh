function installChefGems() {
  if [ ! -f /usr/bin/chef-client ]; then
    if which dpkg &> /dev/null; then
      apt-get-update
      apt-get install -y ruby ruby1.8-dev build-essential wget libruby-extras libruby1.8-extras
    elif which rpm &> /dev/null; then
      # Disable chef from the base repo (http://tickets.opscode.com/browse/CHEF-2906)
      sed -i "s/\[base\]/\0\n\exclude=ruby*/g" /etc/yum.repos.d/CentOS-Base.repo
      # Make sure to install an appropriate ruby version
      yum erase -y ruby ruby-libs
      rpm -Uvh http://rbel.co/rbel5
      yum install -y ruby ruby-devel make gcc gcc-c++ kernel-devel automake autoconf wget
    else
      abort "we only support apt-get and yum right now... please contribute"
    fi
    (
    mkdir -p /tmp/bootchef
    cd /tmp/bootchef
    wget http://production.cf.rubygems.org/rubygems/rubygems-1.3.7.tgz
    tar zxf rubygems-1.3.7.tgz
    cd rubygems-1.3.7
    ruby setup.rb --no-format-executable
    rm -fr /tmp/bootchef
    )
    if which rpm &> /dev/null; then
      #Install gems provided by libruby-extras deb package (based on https://launchpad.net/ubuntu/precise/+package/libruby-extras)
      /usr/bin/gem install cmdparse daemons log4r mmap ncurses --no-rdoc --no-ri --verbose
    fi
    /usr/bin/gem install ohai chef --no-rdoc --no-ri --verbose
  fi
}
