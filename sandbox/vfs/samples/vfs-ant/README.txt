====

    Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>

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

 this is a simple ant script that copyies a remote zip file to a blobstore container
   1. export ANT_OPTS to include log4j.xml as the property 'log4j.configuration'
      - ex. export ANT_OPTS="-Dlog4j.configuration=file://`pwd`/log4j.xml"
   2. find or download a copy of jclouds-vfs-1.0-SNAPSHOT-jar-with-dependencies.jar
      - ex. ~/.m2/repository/org/jclouds/jclouds-vfs/1.0-SNAPSHOT/jclouds-vfs-1.0-SNAPSHOT-jar-with-dependencies.jar
      - ex. curl http://jclouds.rimuhosting.com/maven2/snapshots/org/jclouds/jclouds-vfs/1.0-SNAPSHOT/jclouds-vfs-1.0-20091215.023231-1-jar-with-dependencies.jar >jclouds-vfs-all.jar
   3. invoke ant, adding the library above, and passing the property 'jclouds.blobstore.url' which corresponds to the provider you wish
      - ex. ant -lib ~/.m2/repository/org/jclouds/jclouds-vfs/1.0-SNAPSHOT/jclouds-vfs-1.0-SNAPSHOT-jar-with-dependencies.jar -Djclouds.blobstore.url=blobstore://user:hex_key@cloudfiles
      - ex. ant -lib ~/.m2/repository/org/jclouds/jclouds-vfs/1.0-SNAPSHOT/jclouds-vfs-1.0-SNAPSHOT-jar-with-dependencies.jar -Djclouds.blobstore.url=blobstore://hex_uid:base64_key@atmos
