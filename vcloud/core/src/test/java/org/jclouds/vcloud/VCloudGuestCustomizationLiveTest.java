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

package org.jclouds.vcloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.get;
import static org.testng.Assert.assertEquals;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshClient.Factory;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.vcloud.compute.options.VCloudTemplateOptions;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "vcloud.VCloudGuestCustomizationLiveTest")
public class VCloudGuestCustomizationLiveTest {

   protected String identity;
   protected String provider;
   protected String credential;
   protected ComputeServiceContext context;
   protected ComputeService client;
   protected RetryablePredicate<IPSocket> socketTester;
   protected Factory sshFactory;

   protected void setupCredentials() {
      provider = "vcloud";
      identity = checkNotNull(System.getProperty("vcloud.identity"), "vcloud.identity");
      credential = checkNotNull(System.getProperty("vcloud.credential"), "vcloud.credential");
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties props = new Properties();
      props.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      props.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      context = new ComputeServiceContextFactory().createContext(provider, identity, credential, ImmutableSet
               .<Module> of(new Log4JLoggingModule()), props);

      client = context.getComputeService();

      Injector injector = createSshClientInjector();
      sshFactory = injector.getInstance(SshClient.Factory.class);
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<IPSocket>(socketOpen, 60, 1, TimeUnit.SECONDS);
      injector.injectMembers(socketOpen); // add logger
   }

   protected Injector createSshClientInjector() {
      return Guice.createInjector(getSshModule());
   }

   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   @Test
   public void testExtendedOptionsWithCustomizationScript() throws Exception {

      String tag = "customize";

      TemplateOptions options = client.templateOptions();

      options.as(VCloudTemplateOptions.class).customizationScript("cat > /root/foo.txt<<EOF\nI love candy\nEOF\n");

      String nodeId = null;
      try {

         Set<? extends NodeMetadata> nodes = client.runNodesWithTag(tag, 1, options);

         NodeMetadata node = Iterables.get(nodes, 0);
         nodeId = node.getId();
         IPSocket socket = new IPSocket(get(node.getPublicAddresses(), 0), 22);
         socketTester.apply(socket);

         SshClient ssh = sshFactory.create(socket, node.getCredentials().identity, node.getCredentials().credential);
         try {
            ssh.connect();

            System.out
                     .println(ssh
                              .exec("vmtoolsd --cmd=\"info-get guestinfo.ovfenv\" |grep vCloud_CustomizationInfo|sed 's/.*value=\"\\(.*\\)\".*/\\1/g'|base64 -d"));

            ExecResponse hello = ssh.exec("cat /root/foo.txt");
            assertEquals(hello.getOutput().trim(), "I love candy");

         } finally {
            if (ssh != null)
               ssh.disconnect();
         }

      } finally {
         if (nodeId != null)
            client.destroyNode(nodeId);
      }
   }

}