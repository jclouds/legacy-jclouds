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
package org.jclouds.vcloud.features;

import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.jclouds.crypto.CryptoStreams.base64;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.vcloud.VCloudApiMetadata;
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
import com.google.common.net.HostAndPort;

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

      String group = prefix + "cus";

      NodeMetadata node = null;
      try {

         TemplateOptions options = client.templateOptions();
         options.blockOnPort(22, 180);
         options.as(VCloudTemplateOptions.class).customizationScript(script);
         options.as(VCloudTemplateOptions.class).description(group);
         node = getOnlyElement(client.createNodesInGroup(group, 1, options));

         VApp vapp = client.getContext().unwrap(VCloudApiMetadata.CONTEXT_TOKEN).getApi().getVAppClient().getVApp(
                  node.getUri());
         assertEquals(vapp.getDescription(), group);

         Vm vm = Iterables.get(vapp.getChildren(), 0);
         String apiOutput = vm.getGuestCustomizationSection().getCustomizationScript();
         checkApiOutput(apiOutput);

         ExecResponse vmTools = client.runScriptOnNode(node.getId(), PARSE_VMTOOLSD,
               wrapInInitScript(false).runAsRoot(false));
         checkApiOutput(new String(base64(vmTools.getOutput().trim())));

         ExecResponse foo = client.runScriptOnNode(node.getId(), "cat /root/foo.txt", wrapInInitScript(false)
               .runAsRoot(false));
         checkCustomizationOccurred(foo);

      } finally {
         if (node != null)
            client.destroyNode(node.getId());
      }
   }

   protected void checkCustomizationOccurred(ExecResponse exec) {
      // note that vmwaretools throws in \r characters when executing scripts
      assert exec.getOutput().equals(iLoveAscii + "\r\n") : exec;
   }

   protected void checkApiOutput(String apiOutput) {
      checkApiOutput1_0_1(apiOutput);
   }

   // make sure the script has a lot of screwy characters, knowing our parser
   // throws-out \r
   protected String iLoveAscii = "I '\"love\"' {asc|!}*&";

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

   protected HostAndPort getSocket(NodeMetadata node) {
      return HostAndPort.fromParts(get(node.getPublicAddresses(), 0), 22);
   }

}
