function calculateMd5 {
   unset ISO_MD5;
   [ $# -eq 1 ] || {
      abort "calculateMd5 requires an iso file path parameter"
      return 1
   }
   if [ which -s md5 ] 
   then
       local ISO_PATH="$0"; shift
       local _MD5=`md5 "$ISO_PATH" | awk '{ print $4 }`
   elif [ which -s md5sum ] 
   then
       local _MD5=`md5sum "$ISO_PATH" | awk '{ print $1 }`
   fi
   [ -n "$_MD5" ] && {
   export ISO_MD5=$_MD5
   echo [$ISO_MD5]
    return 0
   } || {
    return 1
   }
}