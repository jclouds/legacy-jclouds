function abort {
   echo "aborting: $@" 1>&2
   set -u
}
