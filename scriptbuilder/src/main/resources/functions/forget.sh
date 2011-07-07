function forget {
   unset FOUND_PID;
   [ $# -eq 3 ] || {
      abort "forget requires parameters INSTANCE_NAME SCRIPT LOG_DIR"
      return 1
   }
   local INSTANCE_NAME="$1"; shift
   local SCRIPT="$1"; shift
   local LOG_DIR="$1"; shift
   mkdir -p $LOG_DIR
   findPid $INSTANCE_NAME
   [ -n "$FOUND_PID" -a -f $LOG_DIR/stdout.log ] && {
      echo $INSTANCE_NAME already running pid [$FOUND_PID]
   } || {
      nohup $SCRIPT >$LOG_DIR/stdout.log 2>$LOG_DIR/stderr.log &
      sleep 1
      findPid $INSTANCE_NAME
      [ -n "$FOUND_PID" ] || abort "$INSTANCE_NAME did not start"
   }
   return 0
}
