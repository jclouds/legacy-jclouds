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
import static org.jclouds.scriptbuilder.domain.Statements.newStatementList;

import java.io.IOException;
import java.net.URI;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.predicates.OperatingSystemPredicates;
import org.jclouds.scriptbuilder.InitBuilder;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class RunScriptData {

   public static final URI JDK7_URL = URI.create(System.getProperty("test.jdk7-url",
         "http://download.oracle.com/otn-pub/java/jdk/7u1-b08/jdk-7u1-linux-x64.tar.gz"));
   public static final URI JBOSS7_URL = URI.create(System.getProperty("test.jboss7-url",//
         "http://download.jboss.org/jbossas/7.0/jboss-as-7.0.2.Final/jboss-as-web-7.0.2.Final.tar.gz"));

   public static String JBOSS_HOME = "/usr/local/jboss";

   public static Statement installJavaAndCurl(OperatingSystem os) {
      if (os == null || OperatingSystemPredicates.supportsApt().apply(os))
         return APT_RUN_SCRIPT;
      else if (OperatingSystemPredicates.supportsYum().apply(os))
         return YUM_RUN_SCRIPT;
      else if (OperatingSystemPredicates.supportsZypper().apply(os))
         return ZYPPER_RUN_SCRIPT;
      else
         throw new IllegalArgumentException("don't know how to handle" + os.toString());
   }

   public static Statement authorizePortsInIpTables(int... ports) {
      Builder<Statement> builder = ImmutableList.<Statement> builder();
      for (int port : ports)
         builder.add(exec("iptables -I INPUT 1 -p tcp --dport " + port + " -j ACCEPT"));
      builder.add(exec("iptables-save"));
      return new StatementList(builder.build());
   }

   public static StatementList installAdminUserJBossAndOpenPorts(OperatingSystem os) throws IOException {
      return new StatementList(//
                        AdminAccess.builder().adminUsername("web").build(),//
                        installJavaAndCurl(os),//
                        authorizePortsInIpTables(22, 8080),//
                        extractTargzIntoDirectory(JBOSS7_URL, "/usr/local"),//
                        exec("{md} " + JBOSS_HOME), exec("mv /usr/local/jboss-*/* " + JBOSS_HOME),//
                        changeStandaloneConfigToListenOnAllIPAddresses(),
                        exec("chmod -R oug+r+w " + JBOSS_HOME),
                        exec("chown -R web " + JBOSS_HOME));
   }
   
   // NOTE do not name this the same as your login user, or the init process may kill you!
   public static InitBuilder startJBoss(String configuration) {
      return new InitBuilder(
               "jboss",
               JBOSS_HOME,
               JBOSS_HOME,
               ImmutableMap.of("jbossHome", JBOSS_HOME),
               ImmutableList.<Statement>of(appendFile(JBOSS_HOME + "/standalone/configuration/standalone-custom.xml", Splitter.on('\n').split(configuration))),
               ImmutableList
                        .<Statement> of(interpret(new StringBuilder().append("java ").append(' ')
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
                                 .toString())));
   }
   
   public static Statement normalizeHostAndDNSConfig() {
      return newStatementList(//
               addHostnameToEtcHostsIfMissing(),//
               addDnsToResolverIfMissing());
   }

   public static Statement addHostnameToEtcHostsIfMissing() {
      return exec("grep `hostname` /etc/hosts >/dev/null || awk -v hostname=`hostname` 'END { print $1\" \"hostname }' /proc/net/arp >> /etc/hosts");
   }

   public static Statement addDnsToResolverIfMissing() {
      return exec("nslookup yahoo.com >/dev/null || echo nameserver 208.67.222.222 >> /etc/resolv.conf");
   }

   // TODO make this a cli option
   private static Statement changeStandaloneConfigToListenOnAllIPAddresses() {
      return exec(format(
                        "(cd %s/standalone/configuration && sed 's~inet-address value=.*/~any-address/~g' standalone.xml > standalone.xml.new && mv standalone.xml.new standalone.xml)",
                        JBOSS_HOME));
   }
   
   public static final ImmutableSet<String> exportJavaHomeAndAddToPath = ImmutableSet.of(
         "export JAVA_HOME=/usr/local/jdk", "export PATH=$JAVA_HOME/bin:$PATH");

   public static final Statement JDK7_INSTALL_TGZ = newStatementList(//
         exec("{md} /usr/local/jdk"), extractTargzIntoDirectory(JDK7_URL, "/usr/local"),//
         exec("mv /usr/local/jdk1.7*/* /usr/local/jdk/"),//
         exec("test -n \"$SUDO_USER\" && "), appendFile("/home/$SUDO_USER/.bashrc", exportJavaHomeAndAddToPath),//
         appendFile("/etc/bashrc", exportJavaHomeAndAddToPath),//
         appendFile("$HOME/.bashrc", exportJavaHomeAndAddToPath),//
         appendFile("/etc/skel/.bashrc", exportJavaHomeAndAddToPath),//
         // TODO:
         // eventhough we are setting the above, sometimes images (ex.
         // cloudservers ubuntu) kick out of .bashrc (ex. [ -z "$PS1" ] &&
         // return), for this reason, we should also explicitly link.
         // A better way would be to update using alternatives or the like
         exec("ln -fs /usr/local/jdk/bin/java /usr/bin/java"));
  
   public static String aptInstall = "apt-get install -f -y -qq --force-yes";

   public static final Statement APT_RUN_SCRIPT = newStatementList(//
         normalizeHostAndDNSConfig(),//
         exec("which curl >&- 2>&-|| " + aptInstall + " curl"),//
         exec("which nslookup >&- 2>&-|| " + aptInstall + " dnsutils"),//
         JDK7_INSTALL_TGZ);

   public static String yumInstall = "yum --nogpgcheck -y install";

   public static final Statement YUM_RUN_SCRIPT = newStatementList(//
         normalizeHostAndDNSConfig(),//
         exec("which curl >&- 2>&-|| " + yumInstall + " curl"),//
         exec("which nslookup >&- 2>&-|| " + yumInstall + " bind-utils"),//
         JDK7_INSTALL_TGZ);

   public static final Statement ZYPPER_RUN_SCRIPT = newStatementList(//
         normalizeHostAndDNSConfig(),//
         exec("which curl >&- 2>&-|| zypper install curl"),//
         JDK7_INSTALL_TGZ);
}
