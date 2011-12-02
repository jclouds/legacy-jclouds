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
package org.jclouds.cloudstack.features;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jclouds.cloudstack.domain.*;
import org.jclouds.cloudstack.options.*;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Random;
import java.util.Set;

import static org.jclouds.cloudstack.options.ListTemplatesOptions.Builder.zoneId;
import static org.testng.Assert.*;

/**
 * Tests behavior of {@code TemplateClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "TemplateClientLiveTest")
public class TemplateClientLiveTest extends BaseCloudStackClientLiveTest {

   private static final String IMPORT_VHD_URL = "http://www.frontiertown.co.uk/jclouds/empty.vhd";
   private VirtualMachine vm;
   private Template createdTemplate;
   private Template registeredTemplate;

   @Test
   public void testListTemplates() throws Exception {
      Set<Template> response = client.getTemplateClient().listTemplates();
      assert null != response;
      long templateCount = response.size();
      assertTrue(templateCount >= 0);
      for (Template template : response) {
         Template newDetails = Iterables.getOnlyElement(client.getTemplateClient().listTemplates(
               zoneId(template.getZoneId()).id(template.getId())));
         Logger.CONSOLE.info("Checking template: " + template);

         assertEquals(template, newDetails);
         assertEquals(template, client.getTemplateClient().getTemplateInZone(template.getId(), template.getZoneId()));
         assert template.getId() > 0 : template;
         assert template.getName() != null : template;
         assert template.getDisplayText() != null : template;
         assert template.getCreated() != null : template;
         assert template.getFormat() != null && template.getFormat() != Template.Format.UNRECOGNIZED : template;
         assert template.getOSType() != null : template;
         assert template.getOSTypeId() > 0 : template;
         assert template.getAccount() != null : template;
         assert template.getZone() != null : template;
         assert template.getZoneId() > 0 : template;
         assert (template.getStatus() == null ||
            template.getStatus().equals("Download Complete")) : template;
         assert template.getType() != null && template.getType() != Template.Type.UNRECOGNIZED : template;
         assert template.getHypervisor() != null : template;
         assert template.getDomain() != null : template;
         assert template.getDomainId() > 0 : template;
      }
   }

   @Test(enabled = true)
   public void testCreateTemplate() throws Exception {
      Zone zone = Iterables.getFirst(client.getZoneClient().listZones(), null);
      assertNotNull(zone);
      Iterable<Network> networks = client.getNetworkClient().listNetworks(ListNetworksOptions.Builder.zoneId(zone.getId()).isDefault(true));
      networks = Iterables.filter(networks, new Predicate<Network>() {
         @Override
         public boolean apply(@Nullable Network network) {
            return network != null && network.getState().equals("Implemented");
         }
      });
      assertEquals(Iterables.size(networks), 1);
      Network network = Iterables.getOnlyElement(networks, null);
      assertNotNull(network);

      // Create a VM and stop it
      Long templateId = (imageId != null && !"".equals(imageId)) ? new Long(imageId) : null;
      vm = VirtualMachineClientLiveTest.createVirtualMachineInNetwork(network, templateId, client, jobComplete, virtualMachineRunning);
      assert jobComplete.apply(client.getVirtualMachineClient().stopVirtualMachine(vm.getId())) : vm;

      // Work out the VM's volume
      Set<Volume> volumes = client.getVolumeClient().listVolumes(ListVolumesOptions.Builder.virtualMachineId(vm.getId()));
      assertEquals(volumes.size(), 1);
      Volume volume = Iterables.getOnlyElement(volumes);

      // Create a template
      String tmplName = "jclouds-" + Integer.toHexString(new Random().nextInt());
      CreateTemplateOptions options = CreateTemplateOptions.Builder.volumeId(volume.getId());
      AsyncCreateResponse response = client.getTemplateClient().createTemplate(TemplateMetadata.builder().name(tmplName).osTypeId(vm.getGuestOSId()).displayText("jclouds live testCreateTemplate").build(), options);
      assert jobComplete.apply(response.getJobId()) : vm;
      createdTemplate = client.getTemplateClient().getTemplateInZone(response.getId(), vm.getZoneId());

      // Assertions
      assertNotNull(createdTemplate);
   }

   @Test(enabled = true, dependsOnMethods = "testRegisterTemplate")
   public void testExtractTemplate() throws Exception {
      // Initiate the extraction and wait for it to complete
      AsyncCreateResponse response = client.getTemplateClient().extractTemplate(registeredTemplate.getId(), ExtractMode.HTTP_DOWNLOAD, registeredTemplate.getZoneId());
      assert jobComplete.apply(response.getJobId()) : registeredTemplate;

      // Get the result
      AsyncJob<TemplateExtraction> asyncJob = client.getAsyncJobClient().getAsyncJob(response.getJobId());
      TemplateExtraction extract = asyncJob.getResult();
      assertNotNull(extract);

      // Check that the URL can be retrieved
      String extractUrl = extract.getUrl();
      assertNotNull(extractUrl);
      URI uri = new URI(URLDecoder.decode(extractUrl, "utf-8"));
      assertTrue(context.utils().http().exists(uri), "does not exist: " + uri);
   }

   @Test(enabled = true)
   public void testRegisterTemplate() throws Exception {
      Zone zone = Iterables.getFirst(client.getZoneClient().listZones(), null);
      assertNotNull(zone);
      Iterable<Network> networks = client.getNetworkClient().listNetworks(ListNetworksOptions.Builder.zoneId(zone.getId()).isDefault(true));
      networks = Iterables.filter(networks, new Predicate<Network>() {
         @Override
         public boolean apply(@Nullable Network network) {
            return network != null && network.getState().equals("Implemented");
         }
      });
      assertEquals(Iterables.size(networks), 1);
      Network network = Iterables.getOnlyElement(networks, null);
      assertNotNull(network);
      Set<OSType> osTypes = client.getGuestOSClient().listOSTypes();
      OSType osType = Iterables.getFirst(osTypes, null);

      // Register a template
      String tmplName = "jclouds-" + Integer.toHexString(new Random().nextInt());
      RegisterTemplateOptions options = RegisterTemplateOptions.Builder.bits(32).isExtractable(true);
      TemplateMetadata templateMetadata = TemplateMetadata.builder().name(tmplName).osTypeId(osType.getId()).displayText("jclouds live testRegisterTemplate").build();
      Set<Template> templates = client.getTemplateClient().registerTemplate(templateMetadata, "VHD", "XenServer", IMPORT_VHD_URL, zone.getId(), options);
      registeredTemplate = Iterables.getOnlyElement(templates, null);
      assertNotNull(registeredTemplate);

      // Ensure it is available
      final long zoneId = zone.getId();
      Predicate<Template> templateReadyPredicate = new Predicate<Template>() {
         @Override
         public boolean apply(@Nullable Template template) {
            if (template == null) return false;
            Template t2 = client.getTemplateClient().getTemplateInZone(template.getId(), zoneId);
            Logger.CONSOLE.info("%s", t2.getStatus());
            return "Download Complete".equals(t2.getStatus());
         }
      };
      assertTrue(new RetryablePredicate<Template>(templateReadyPredicate, 60000).apply(registeredTemplate));

      // Create a VM that uses this template
      vm = VirtualMachineClientLiveTest.createVirtualMachineInNetwork(network, registeredTemplate.getId(), client, jobComplete, virtualMachineRunning);
      assertNotNull(vm);
   }


   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (vm != null) {
         assert jobComplete.apply(client.getVirtualMachineClient().stopVirtualMachine(vm.getId())) : vm;
         assert jobComplete.apply(client.getVirtualMachineClient().destroyVirtualMachine(vm.getId())) : vm;
         assert virtualMachineDestroyed.apply(vm);
      }
      if (createdTemplate != null) {
         client.getTemplateClient().deleteTemplate(createdTemplate.getId());
      }
      if (registeredTemplate != null) {
         client.getTemplateClient().deleteTemplate(registeredTemplate.getId());
      }
      super.tearDown();
   }

}
