alias apt-get-update="apt-get update -qq"
alias apt-get-install="apt-get install -f -y -qq --force-yes"
alias yum-install="yum --quiet --nogpgcheck -y install"

function ensure_cmd_or_install_package_apt(){
  local cmd=$1
  shift
  local pkg=$*
  
  hash $cmd 2>/dev/null || ( apt-get-update && apt-get-install $pkg )
}

function ensure_cmd_or_install_package_yum(){
  local cmd=$1
  shift
  local pkg=$*
  hash $cmd 2>/dev/null || yum-install $pkg
}

function ensure_netutils_apt() {
  ensure_cmd_or_install_package_apt nslookup dnsutils
  ensure_cmd_or_install_package_apt curl curl
}

function ensure_netutils_yum() {
  ensure_cmd_or_install_package_yum nslookup bind-utils
  ensure_cmd_or_install_package_yum curl curl
}

# most network services require that the hostname is in
# the /etc/hosts file, or they won't operate
function ensure_hostname_in_hosts() {
  # NOTE: 
  # 1. We blindly trust existing hostname settings in /etc/hosts
  # 2. We assume hostname supports the -i option to return the default NICs IP address
  egrep -q `hostname` /etc/hosts || echo "`hostname -i` `hostname`" >> /etc/hosts
}

# download locations for many services are at public dns
function ensure_can_resolve_public_dns() {
  nslookup yahoo.com | grep yahoo.com > /dev/null || echo nameserver 208.67.222.222 >> /etc/resolv.conf
}

function setupPublicCurl() {
  ensure_hostname_in_hosts
  if which dpkg &> /dev/null; then
    ensure_netutils_apt
  elif which rpm &> /dev/null; then
    ensure_netutils_yum
  else
    abort "we only support apt-get and yum right now... please contribute!"
    return 1
  fi
  ensure_can_resolve_public_dns
  return 0  
}
