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
package org.jclouds.vcloud.features;

import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.crypto.CryptoStreams.base64;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.compute.options.VCloudTemplateOptions;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.internal.BaseVCloudClientLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * This tests that we can use guest customization as an alternative to bootstrapping with ssh. There
 * are a few advantages to this, including the fact that it can work inside google appengine where
 * network sockets (ssh:22) are prohibited.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true, testName = "VmClientLiveTest")
public class VmClientLiveTest extends BaseVCloudClientLiveTest {

   @Test
   public void testGetThumbnailOfVm() throws Exception {
      Org org = getVCloudApi().getOrgClient().findOrgNamed(null);
      for (ReferenceType vdc : org.getVDCs().values()) {
         VDC response = getVCloudApi().getVDCClient().getVDC(vdc.getHref());
         for (ReferenceType item : response.getResourceEntities().values()) {
            if (item.getType().equals(VCloudMediaType.VAPP_XML)) {
               try {
                  VApp app = getVCloudApi().getVAppClient().getVApp(item.getHref());
                  assertNotNull(app);
                  for (Vm vm : app.getChildren()) {
                     assert getVCloudApi().getVmClient().getScreenThumbnailForVm(vm.getHref()) != null;
                  }
               } catch (RuntimeException e) {

               }
            }
         }
      }
   }

   @Test
   public void testGetVm() throws Exception {
      Org org = getVCloudApi().getOrgClient().findOrgNamed(null);
      for (ReferenceType vdc : org.getVDCs().values()) {
         VDC response = getVCloudApi().getVDCClient().getVDC(vdc.getHref());
         for (ReferenceType item : response.getResourceEntities().values()) {
            if (item.getType().equals(VCloudMediaType.VAPP_XML)) {
               try {
                  VApp app = getVCloudApi().getVAppClient().getVApp(item.getHref());
                  assertNotNull(app);
                  for (Vm vm : app.getChildren()) {
                     assertEquals(getVCloudApi().getVmClient().getVm(vm.getHref()).getHref(), vm.getHref());
                  }
               } catch (RuntimeException e) {

               }
            }
         }
      }
   }

   @Test
   public void testExtendedOptionsWithCustomizationScript() throws Exception {
      String PARSE_VMTOOLSD = "vmtoolsd --cmd=\"info-get guestinfo.ovfenv\" |grep vCloud_CustomizationInfo|sed 's/.*value=\"\\(.*\\)\".*/\\1/g'";

      String group = prefix + "customize";

      NodeMetadata node = null;
      try {

         TemplateOptions options = client.templateOptions();
         options.as(VCloudTemplateOptions.class).customizationScript(script);
         options.as(VCloudTemplateOptions.class).description(group);
         node = getOnlyElement(client.createNodesInGroup(group, 1, options));

         VApp vapp = ((VCloudClient) client.getContext().getProviderSpecificContext().getApi()).getVAppClient()
                  .getVApp(node.getUri());
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
      } finally {
         if (node != null)
            client.destroyNode(node.getId());
      }
   }

   protected void checkApiOutput(String apiOutput) {
      checkApiOutput1_0_1(apiOutput);
   }

   // make sure the script has a lot of screwy characters, knowing our parser
   // throws-out \r
   String iLoveAscii = "I '\"love\"' {asc|!}*&";

   String script = "cat > /root/foo.txt<<EOF\n" + iLoveAscii + "\nEOF\n";

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
      assertEquals(decodedVMwareToolsOutput, script);
      // note that vmwaretools throws in \r characters when executing scripts
      assertEquals(fooTxtContentsMadeByVMwareTools, iLoveAscii + "\r\n");
   }

   protected IPSocket getSocket(NodeMetadata node) {
      return new IPSocket(get(node.getPublicAddresses(), 0), 22);
   }

}