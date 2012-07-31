function installGit() {
  if hash apt-get 2>/dev/null; then
    ensure_cmd_or_install_package_apt git git-core
  elif hash yum 2>/dev/null; then
    ensure_cmd_or_install_package_yum git git-core
  else
    abort "we only support apt-get and yum right now... please contribute!"
    return 1
  fi
  return 0  
}
