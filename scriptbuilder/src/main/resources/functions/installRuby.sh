function installRuby() {
  if ! hash ruby 2>/dev/null; then
    if which dpkg &> /dev/null; then
      apt-get-update
      apt-get install -y ruby ruby1.8-dev build-essential
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
