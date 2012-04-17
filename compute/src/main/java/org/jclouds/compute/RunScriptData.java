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
package org.jclouds.compute;

import static java.lang.String.format;
import static org.jclouds.compute.util.ComputeServiceUtils.extractTargzIntoDirectory;
import static org.jclouds.scriptbuilder.domain.Statements.appendFile;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.jclouds.scriptbuilder.domain.Statements.interpret;

import java.io.IOException;
import java.net.URI;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.scriptbuilder.InitScript;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.statements.java.InstallJDK;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList.Builder;

/**
 * 
 * @author Adrian Cole
 */
public class RunScriptData {

   public static final URI JBOSS7_URL = URI.create(System.getProperty("test.jboss7-url",//
         "http://download.jboss.org/jbossas/7.0/jboss-as-7.0.2.Final/jboss-as-web-7.0.2.Final.tar.gz"));

   public static String JBOSS_HOME = "/usr/local/jboss";

   public static Statement authorizePortsInIpTables(int... ports) {
      Builder<Statement> builder = ImmutableList.builder();
      for (int port : ports)
         builder.add(exec("iptables -I INPUT 1 -p tcp --dport " + port + " -j ACCEPT"));
      builder.add(exec("iptables-save"));
      return new StatementList(builder.build());
   }

   public static StatementList installAdminUserJBossAndOpenPorts(OperatingSystem os) throws IOException {
      return new StatementList(//
                        AdminAccess.builder().adminUsername("web").build(),//
                        InstallJDK.fromOpenJDK(),//
                        authorizePortsInIpTables(22, 8080),//
                        extractTargzIntoDirectory(JBOSS7_URL, "/usr/local"),//
                        exec("{md} " + JBOSS_HOME), exec("mv /usr/local/jboss-*/* " + JBOSS_HOME),//
                        changeStandaloneConfigToListenOnAllIPAddresses(),
                        exec("chmod -R oug+r+w " + JBOSS_HOME),
                        exec("chown -R web " + JBOSS_HOME));
   }
   
   // NOTE do not name this the same as your login user, or the init process may kill you!
   public static InitScript startJBoss(String configuration) {
      return InitScript.builder()
               .name("jboss")
               .home(JBOSS_HOME)
               .exportVariables(ImmutableMap.of("jbossHome", JBOSS_HOME))
               .init(appendFile(JBOSS_HOME + "/standalone/configuration/standalone-custom.xml", Splitter.on('\n').split(configuration)))
               .run(interpret(new StringBuilder().append("java ").append(' ')
                                 .append("-server -Xms128m -Xmx128m -XX:MaxPermSize=128m -Djava.net.preferIPv4Stack=true -XX:+UseFastAccessorMethods -XX:+TieredCompilation -Xverify:none -Dorg.jboss.resolver.warning=true -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000").append(' ')
                                 .append("-Djboss.modules.system.pkgs=org.jboss.byteman").append(' ')
                                 .append("-Dorg.jboss.boot.log.file=$JBOSS_HOME/standalone/log/boot.log").append(' ')
                                 .append("-Dlogging.configuration=file:$JBOSS_HOME/standalone/configuration/logging.properties").append(' ')
                                 .append("-jar $JBOSS_HOME/jboss-modules.jar").append(' ')
                                 .append("-mp $JBOSS_HOME/modules").append(' ')
                                 .append("-logmodule org.jboss.logmanager").append(' ')
                                 .append("-jaxpmodule javax.xml.jaxp-provider").append(' ')
                                 .append("org.jboss.as.standalone").append(' ')
                                 .append("-Djboss.home.dir=$JBOSS_HOME").append(' ')
                                 .append("--server-config=standalone-custom.xml")
                                 .toString())).build();
   }
   
   // TODO make this a cli option
   private static Statement changeStandaloneConfigToListenOnAllIPAddresses() {
      return exec(format(
                        "(cd %s/standalone/configuration && sed 's~inet-address value=.*/~any-address/~g' standalone.xml > standalone.xml.new && mv standalone.xml.new standalone.xml)",
                        JBOSS_HOME));
   }
}
