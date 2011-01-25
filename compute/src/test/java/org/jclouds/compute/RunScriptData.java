/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import org.jclouds.scriptbuilder.domain.AuthorizeRSAPublicKey;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;

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

   public static Statement createScriptInstallAndStartJBoss(String publicKey, OperatingSystem os) {
      Map<String, String> envVariables = ImmutableMap.of("jbossHome", jbossHome);
      Statement toReturn = new InitBuilder(
               "jboss",
               jbossHome,
               jbossHome,
               envVariables,
               ImmutableList.<Statement> of(new AuthorizeRSAPublicKey(publicKey),//
                        installJavaAndCurl(os),//
                        authorizePortInIpTables(8080),//
                        extractTargzIntoDirectory(URI.create(System.getProperty("test.jboss-url",
                                 "http://d19xvfg065k8li.cloudfront.net/jboss-6.0.0.Final.tar.gz")), "/usr/local"),//
                        exec("{md} " + jbossHome), exec("mv /usr/local/jboss-*/* " + jbossHome),//
                        exec("chmod -R oug+r+w " + jbossHome)),//
               ImmutableList
                        .<Statement> of(interpret("java -Xms128m -Xmx512m -XX:MaxPermSize=256m -Dorg.jboss.resolver.warning=true -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Djava.endorsed.dirs=lib/endorsed -classpath bin/run.jar org.jboss.Main -c jbossweb-standalone -b 0.0.0.0")));
      return toReturn;
   }

   public static String aptInstall = "apt-get install -f -y -qq --force-yes";

   public static String installAfterUpdatingIfNotPresent(String cmd) {
      String aptInstallCmd = aptInstall + " " + cmd;
      return String.format("which %s || (%s || (apt-get update && %s))", cmd, aptInstallCmd, aptInstallCmd);
   }

   public static final Statement APT_RUN_SCRIPT = newStatementList(//
            exec(installAfterUpdatingIfNotPresent("curl")),//
            exec("(which java && java -fullversion 2>&1|egrep -q 1.6 ) ||"),//
            execHttpResponse(URI.create("http://whirr.s3.amazonaws.com/0.2.0-incubating-SNAPSHOT/sun/java/install")),//
            exec(new StringBuilder()//
                     .append("echo nameserver 208.67.222.222 >> /etc/resolv.conf\n")//
                     // jeos hasn't enough room!
                     .append("rm -rf /var/cache/apt /usr/lib/vmware-tools\n")//
                     .append("echo \"export PATH=\\\"\\$JAVA_HOME/bin/:\\$PATH\\\"\" >> /root/.bashrc")//
                     .toString()));

   public static final Statement YUM_RUN_SCRIPT = newStatementList(
            exec("which curl ||yum --nogpgcheck -y install curl"),//
            exec("(which java && java -fullversion 2>&1|egrep -q 1.6 ) ||"),//
            execHttpResponse(URI.create("http://whirr.s3.amazonaws.com/0.2.0-incubating-SNAPSHOT/sun/java/install")),//
            exec(new StringBuilder()//
                     .append("echo nameserver 208.67.222.222 >> /etc/resolv.conf\n") //
                     .append("echo \"export PATH=\\\"\\$JAVA_HOME/bin/:\\$PATH\\\"\" >> /root/.bashrc")//
                     .toString()));

   public static final Statement ZYPPER_RUN_SCRIPT = exec(new StringBuilder()//
            .append("echo nameserver 208.67.222.222 >> /etc/resolv.conf\n")//
            .append("which curl || zypper install curl\n")//
            .append("(which java && java -fullversion 2>&1|egrep -q 1.6 ) || zypper install java-1.6.0-openjdk\n")//
            .toString());
}
