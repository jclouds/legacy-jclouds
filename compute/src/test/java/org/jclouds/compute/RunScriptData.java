/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.execHttpResponse;
import static org.jclouds.compute.util.ComputeServiceUtils.extractTargzIntoDirectory;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.jclouds.scriptbuilder.domain.Statements.interpret;
import static org.jclouds.scriptbuilder.domain.Statements.newStatementList;

import java.net.URI;
import java.util.Map;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.predicates.OperatingSystemPredicates;
import org.jclouds.scriptbuilder.InitBuilder;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 */
public class RunScriptData {

   private static String jbossHome = "/usr/local/jboss";

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

   public static Statement authorizePortInIpTables(int port) {
      return Statements.newStatementList(// just in case iptables are being used, try to open 8080
               exec("iptables -I INPUT 1 -p tcp --dport " + port + " -j ACCEPT"),//
               // TODO gogrid rules only allow ports 22, 3389, 80 and 443.
               // the above rule will be ignored, so we have to apply this
               // directly
               exec("iptables -I RH-Firewall-1-INPUT 1 -p tcp --dport " + port + " -j ACCEPT"),//
               exec("iptables-save"));
   }

   public static Statement createScriptInstallAndStartJBoss(OperatingSystem os) {
      Map<String, String> envVariables = ImmutableMap.of("jbossHome", jbossHome);
      Statement toReturn = new InitBuilder(
               "jboss",
               jbossHome,
               jbossHome,
               envVariables,
               ImmutableList.<Statement> of(AdminAccess.standard(),//
                        installJavaAndCurl(os),//
                        authorizePortInIpTables(8080),
                        extractTargzIntoDirectory(URI.create(System.getProperty("test.jboss-url",
                                 "http://d37gkgjhl3prlk.cloudfront.net/jboss-7.0.0.CR1.tar.gz")), "/usr/local"),//
                        exec("{md} " + jbossHome), exec("mv /usr/local/jboss-*/* " + jbossHome),//
                        changeStandaloneConfigToListenOnAllIPAddresses(),
                        exec("chmod -R oug+r+w " + jbossHome)),//
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
                                 .append("-Djboss.home.dir=$JBOSS_HOME")
                                 .toString())));
      return toReturn;
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

   public static Statement installSunJDKFromWhirrIfNotPresent() {
      return newStatementList(exec("(which java && java -fullversion 2>&1|egrep -q 1.6 ) ||"),//
               execHttpResponse(URI.create("http://whirr.s3.amazonaws.com/0.3.0-incubating/sun/java/install")));
   }

   // TODO make this a cli option
   private static Statement changeStandaloneConfigToListenOnAllIPAddresses() {
      return exec("(cd $JBOSS_HOME/standalone/configuration && sed 's~inet-address value=.*/~any-address/~g' standalone.xml > standalone.xml.new && mv standalone.xml.new standalone.xml)");
   }

   public static String aptInstall = "apt-get install -f -y -qq --force-yes";


   public static final Statement APT_RUN_SCRIPT = newStatementList(//
            normalizeHostAndDNSConfig(),//
            exec("apt-get update -qq"),
            exec("which curl || " + aptInstall + " curl"),//
            exec(aptInstall + " openjdk-6-jdk"),//
            exec("rm -rf /var/cache/apt /usr/lib/vmware-tools"),//
            exec("echo \"export PATH=\\\"\\$JAVA_HOME/bin/:\\$PATH\\\"\" >> /root/.bashrc"));

   public static String yumInstall = "yum --nogpgcheck -y install";

   public static final Statement YUM_RUN_SCRIPT = newStatementList(//
            normalizeHostAndDNSConfig(),//
            exec("which curl || " + yumInstall + " curl"),//
            exec(yumInstall + " java-1.6.0-openjdk-devel"),//
            exec("echo \"export PATH=\\\"\\$JAVA_HOME/bin/:\\$PATH\\\"\" >> /root/.bashrc"));

   public static final Statement ZYPPER_RUN_SCRIPT = newStatementList(//
            normalizeHostAndDNSConfig(),//
            exec("which curl || zypper install curl"),//
            exec("zypper install java-1.6.0-openjdk"),//
            exec("echo \"export PATH=\\\"\\$JAVA_HOME/bin/:\\$PATH\\\"\" >> /root/.bashrc"));
}
