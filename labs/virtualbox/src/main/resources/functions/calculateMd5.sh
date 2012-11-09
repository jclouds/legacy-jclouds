function calculateMd5 {
   unset ISO_MD5;
   [ $# -eq 1 ] || {
      abort "calculateMd5 requires an iso file path parameter"
      return 1
   }

   if command -v md5 >/dev/null 2>&1
   then
       local ISO_PATH="$1"; shift
       local _MD5=`md5 "$ISO_PATH" | awk '{ print $4 }'`
   elif command -v md5sum > /dev/null 2>&1
   then
       local _MD5=`md5sum "$ISO_PATH" | awk '{ print $1 }'`
   fi
   [ -n "$_MD5" ] && {
   export ISO_MD5=$_MD5
   echo $ISO_MD5
    return 0
   } || {
    return 1
   }
}