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
package org.jclouds.vcloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.crypto.CryptoStreams.base64;
import static org.testng.Assert.assertEquals;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshClient.Factory;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.vcloud.compute.options.VCloudTemplateOptions;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.Vm;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Module;

/**
 * This tests that we can use guest customization as an alternative to
 * bootstrapping with ssh. There are a few advantages to this, including the
 * fact that it can work inside google appengine where network sockets (ssh:22)
 * are prohibited.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true)
public class VCloudGuestCustomizationLiveTest {

   public static final String PARSE_VMTOOLSD = "vmtoolsd --cmd=\"info-get guestinfo.ovfenv\" |grep vCloud_CustomizationInfo|sed 's/.*value=\"\\(.*\\)\".*/\\1/g'";

   protected ComputeServiceContext context;
   protected ComputeService client;
   protected RetryablePredicate<IPSocket> socketTester;
   protected Factory sshFactory;

   protected String provider = "vcloud";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   protected NodeMetadata node;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      if (credential != null)
         overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      client = new ComputeServiceContextFactory(setupRestProperties()).createContext(provider,
            ImmutableSet.<Module> of(new Log4JLoggingModule()), overrides).getComputeService();
      socketTester = new RetryablePredicate<IPSocket>(new InetSocketAddressConnect(), 300, 1, TimeUnit.SECONDS);
      sshFactory = Guice.createInjector(getSshModule()).getInstance(Factory.class);
   }

   protected Properties setupRestProperties() {
      return RestContextFactory.getPropertiesFromResource("/rest.properties");
   }

   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   // make sure the script has a lot of screwy characters, knowing our parser
   // throws-out \r
   private String iLoveAscii = "I '\"love\"' {asc|!}*&";

   String script = "cat > /root/foo.txt<<EOF\n" + iLoveAscii + "\nEOF\n";

   @Test
   public void testExtendedOptionsWithCustomizationScript() throws Exception {

      String group = "customize";

      TemplateOptions options = client.templateOptions();
      options.as(VCloudTemplateOptions.class).customizationScript(script);
      options.as(VCloudTemplateOptions.class).description(group);
      node = getOnlyElement(client.createNodesInGroup(group, 1, options));

      VApp vapp = ((VCloudClient) client.getContext().getProviderSpecificContext().getApi()).getVApp(node.getUri());
      assertEquals(vapp.getDescription(), group);

      Vm vm = Iterables.get(vapp.getChildren(), 0);
      String apiOutput = vm.getGuestCustomizationSection().getCustomizationScript();
      checkApiOutput(apiOutput);

      IPSocket socket = getSocket(node);

      System.out.printf("%s:%s@%s", node.getCredentials().identity, node.getCredentials().credential, socket);
      assert socketTester.apply(socket) : socket;

      SshClient ssh = sshFactory.create(socket, node.getCredentials());
      try {
         ssh.connect();
         ExecResponse vmTools = ssh.exec(PARSE_VMTOOLSD);
         System.out.println(vmTools);
         String fooTxt = ssh.exec("cat /root/foo.txt").getOutput();
         String decodedVmToolsOutput = new String(base64(vmTools.getOutput().trim()));
         checkVmOutput(fooTxt, decodedVmToolsOutput);
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   protected void checkApiOutput(String apiOutput) {
      checkApiOutput1_0_1(apiOutput);
   }

   protected void checkApiOutput1_0_1(String apiOutput) {
      // in 1.0.1, vcloud director seems to pass through characters via api
      // flawlessly
      assertEquals(apiOutput, script);
   }

   protected void checkApiOutput1_0_0(String apiOutput) {
      // in 1.0.0, vcloud director seems to remove all newlines
      assertEquals(apiOutput, script.replace("\n", ""));
   }

   protected void checkVmOutput(String fooTxtContentsMadeByVMwareTools, String decodedVMwareToolsOutput) {
      // note that vmwaretools throws in \r characters when executing scripts
      assertEquals(fooTxtContentsMadeByVMwareTools, iLoveAscii + "\r\n");
      assertEquals(decodedVMwareToolsOutput, script);
   }

   @AfterTest
   public void tearDown() {
      if (node != null)
         client.destroyNode(node.getId());
      if (context != null)
         context.close();
   }

   protected IPSocket getSocket(NodeMetadata node) {
      return new IPSocket(get(node.getPublicAddresses(), 0), 22);
   }

}