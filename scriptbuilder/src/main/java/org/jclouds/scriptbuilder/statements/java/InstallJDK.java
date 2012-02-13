/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.scriptbuilder.statements.java;

import static org.jclouds.scriptbuilder.domain.Statements.appendFile;
import static org.jclouds.scriptbuilder.domain.Statements.call;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.jclouds.scriptbuilder.domain.Statements.extractTargzIntoDirectory;

import java.net.URI;

import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;

import com.google.common.collect.ImmutableSet;

/**
 * Installs a default JDK to a host
 * 
 * @author Adrian Cole
 */
public class InstallJDK {
   public static Statement fromURL() {
      return new FromURL();
   }

   public static Statement fromURL(URI url) {
      return new FromURL(url);
   }

   static class FromURL extends StatementList {

      public static final URI JDK7_URL = URI.create(System.getProperty("jdk7-url",
            "http://download.oracle.com/otn-pub/java/jdk/7/jdk-7-linux-x64.tar.gz"));

      public FromURL() {
         this(JDK7_URL);
      }

      public static final ImmutableSet<String> exportJavaHomeAndAddToPath = ImmutableSet.of(
            "export JAVA_HOME=/usr/local/jdk", "export PATH=$JAVA_HOME/bin:$PATH");

      public FromURL(URI jdk7Url) {
         super(call("setupPublicCurl"), //
               extractTargzIntoDirectory(jdk7Url, "/usr/local"),//
               exec("mv /usr/local/jdk* /usr/local/jdk/"),//
               exec("test -n \"$SUDO_USER\" && "), //
               appendFile("/home/$SUDO_USER/.bashrc", exportJavaHomeAndAddToPath),//
               appendFile("/etc/bashrc", exportJavaHomeAndAddToPath),//
               appendFile("$HOME/.bashrc", exportJavaHomeAndAddToPath),//
               appendFile("/etc/skel/.bashrc", exportJavaHomeAndAddToPath),//
               // TODO:
               // eventhough we are setting the above, sometimes images (ex.
               // cloudservers ubuntu) kick out of .bashrc (ex. [ -z "$PS1" ] &&
               // return), for this reason, we should also explicitly link.
               // A better way would be to update using alternatives or the like
               exec("ln -fs /usr/local/jdk/bin/java /usr/bin/java"));
      }
   }
}