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
