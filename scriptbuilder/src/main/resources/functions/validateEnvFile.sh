function validateEnvFile {
   [ $# -eq 1 ] || {
      abort "validateEnvFile requires a parameter of the file to source"
      return 1
   }
   local ENV_FILE="$1"; shift
   [ -f "$ENV_FILE" ] || {
      abort "env file '$ENV_FILE' does not exist"
      return 1
   }
   [ -r "$ENV_FILE" ] || {
      abort "env file '$ENV_FILE' is not readable"
      return 1
   }
   grep '\<exit\>' "$ENV_FILE" > /dev/null && {
      abort "please remove the 'exit' statement from env file '$ENV_FILE'"
      return 1
   }
   [ -x "$ENV_FILE" ] && {
      abort "please remove the execute permission from env file '$ENV_FILE'"
      return 1
   }
   return 0
}