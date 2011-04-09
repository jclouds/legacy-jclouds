====

    Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>

    ====================================================================
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    ====================================================================
====

 this is a simple ant script that executes a java command on a remote machine via ssh
   1. find or download a copy of jclouds-antcontrib-1.0-SNAPSHOT-jar-with-dependencies.jar
      - ex. ~/.m2/repository/org/jclouds/jclouds-antcontrib/1.0-SNAPSHOT/jclouds-antcontrib-1.0-SNAPSHOT-jar-with-dependencies.jar
      - ex. curl http://jclouds.rimuhosting.com/maven2/snapshots/org/jclouds/jclouds-antcontrib/1.0-SNAPSHOT/jclouds-antcontrib-1.0-20091215.023231-1-jar-with-dependencies.jar >jclouds-antcontrib-all.jar
   2. invoke ant, adding the library above, and passing the properties 'host' 'username' 'keyfile' which corresponds to your remote credentials
      - ex. ant -lib ~/.m2/repository/org/jclouds/jclouds-antcontrib/1.0-SNAPSHOT/jclouds-antcontrib-1.0-SNAPSHOT-jar-with-dependencies.jar -Dhost=67.202.42.237 -Dusername=root -Dkeyfile=$HOME/.ssh/id_dsa
      - ex. ant -lib ~/.m2/repository/org/jclouds/jclouds-antcontrib/1.0-SNAPSHOT/jclouds-antcontrib-1.0-SNAPSHOT-jar-with-dependencies.jar -Dhost=localhost -Dusername=$USER -Dkeyfile=$HOME/.ssh/id_dsa
