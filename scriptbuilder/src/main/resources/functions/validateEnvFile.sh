#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
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