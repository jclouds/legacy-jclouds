function sourceEnvFile {
   [ $# -eq 1 ] || {
      abort "sourceEnvFile requires a parameter of the file to source"
      return 1
   }
   local ENV_FILE="$1"; shift
   . "$ENV_FILE" || {
      abort "Please append 'return 0' to the end of '$ENV_FILE'"
      return 1
   }
   return 0
}