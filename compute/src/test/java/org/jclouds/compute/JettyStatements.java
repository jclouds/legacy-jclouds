/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.compute;

import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.jclouds.scriptbuilder.domain.Statements.extractTargzAndFlattenIntoDirectory;
import static org.jclouds.scriptbuilder.domain.Statements.literal;

import java.net.URI;

import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.statements.java.InstallJDK;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;

/**
 * 
 * @author Adrian Cole
 */
public class JettyStatements {

   public static final URI JETTY_URL = URI.create(System.getProperty("test.jetty-url",
         "http://archive.eclipse.org/jetty/8.1.8.v20121106/dist/jetty-distribution-8.1.8.v20121106.tar.gz"));

   public static final String JETTY_HOME = "/usr/local/jetty";
   
   public static final int port = 8080;

   public static Statement version() {
      return exec(String.format("head -1 %s/VERSION.txt | cut -f1 -d ' '", JETTY_HOME));
   }

   public static Statement install() {
      return new StatementList(
            AdminAccess.builder().adminUsername("web").build(),
            InstallJDK.fromOpenJDK(),
            authorizePortInIpTables(),
            extractTargzAndFlattenIntoDirectory(JETTY_URL, JETTY_HOME),
            exec("chown -R web " + JETTY_HOME));
   }

   private static Statement authorizePortInIpTables() {
      return new StatementList(
            exec("iptables -I INPUT 1 -p tcp --dport " + port + " -j ACCEPT"),
            exec("iptables-save"));
   }
   
   public static Statement start() {
      return new StatementList(
            literal("cd " + JETTY_HOME),
            literal("nohup java -jar start.jar jetty.port=" + port + " > start.out 2> start.err < /dev/null &"),
            literal("test $? && sleep 1")); // in case it is slow starting the proc
   }
   
   public static Statement stop() {
      return new StatementList(
            literal("cd " + JETTY_HOME),
            literal("./bin/jetty.sh stop"));
   }
}
