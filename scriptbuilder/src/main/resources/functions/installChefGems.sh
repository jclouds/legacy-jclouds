function installChefGems() {
  if ! hash chef-client 2>/dev/null; then
    if which rpm &> /dev/null; then
      #Install gems provided by libruby-extras deb package (based on https://launchpad.net/ubuntu/precise/+package/libruby-extras)
      /usr/bin/gem install cmdparse daemons log4r mmap ncurses --no-rdoc --no-ri --verbose
    fi
    /usr/bin/gem install ohai chef --no-rdoc --no-ri --verbose
  fi
}
