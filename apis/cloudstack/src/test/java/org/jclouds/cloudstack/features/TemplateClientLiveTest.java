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
package org.jclouds.cloudstack.features;

import static org.jclouds.cloudstack.options.ListTemplatesOptions.Builder.zoneId;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.net.URLDecoder;
import java.util.Set;

import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.domain.ExtractMode;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.OSType;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.TemplateExtraction;
import org.jclouds.cloudstack.domain.TemplateMetadata;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.Volume;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.options.CreateTemplateOptions;
import org.jclouds.cloudstack.options.ListNetworksOptions;
import org.jclouds.cloudstack.options.ListVolumesOptions;
import org.jclouds.cloudstack.options.RegisterTemplateOptions;
import org.jclouds.logging.Logger;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code TemplateClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "TemplateClientLiveTest")
public class TemplateClientLiveTest extends BaseCloudStackClientLiveTest {

   private static final String IMPORT_VHD_URL = "http://www.frontiertown.co.uk/jclouds/empty.vhd";
   private VirtualMachine vmForCreation;
   private VirtualMachine vmForRegistration;
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
         assert template.getId() != null : template;
         assert template.getName() != null : template;
         assert template.getDisplayText() != null : template;
         assert template.getCreated() != null : template;
         assert template.getFormat() != null && template.getFormat() != Template.Format.UNRECOGNIZED : template;
         assert template.getOSType() != null : template;
         assert template.getOSTypeId() != null : template;
         assert template.getAccount() != null : template;
         assert template.getZone() != null : template;
         assert template.getZoneId() != null : template;
         assert (template.getStatus() == null ||
            template.getStatus() == Template.Status.DOWNLOADED) : template;
         assert template.getType() != null && template.getType() != Template.Type.UNRECOGNIZED : template;
         assert template.getHypervisor() != null : template;
         assert template.getDomain() != null : template;
         assert template.getDomainId() != null : template;
         assert template.getSize() > 0 : template;
      }
   }

   @Test(enabled = true)
   public void testCreateTemplate() throws Exception {
      Zone zone = Iterables.getFirst(client.getZoneClient().listZones(), null);
      assertNotNull(zone);
      Iterable<Network> networks = client.getNetworkClient().listNetworks(ListNetworksOptions.Builder.zoneId(zone.getId()).isDefault(true));
      networks = Iterables.filter(networks, new Predicate<Network>() {
         @Override
         public boolean apply(Network network) {
            return network != null && network.getState().equals("Implemented");
         }
      });
      assertTrue(Iterables.size(networks) >= 1);
      Network network = Iterables.get(networks, 0);
      assertNotNull(network);

      // Create a VM and stop it
      String defaultTemplate = template != null ? template.getImageId() : null;
      vmForCreation = VirtualMachineClientLiveTest.createVirtualMachineInNetwork(network, defaultTemplate, client, jobComplete, virtualMachineRunning);
      assertTrue(jobComplete.apply(client.getVirtualMachineClient().stopVirtualMachine(vmForCreation.getId())), vmForCreation.toString());

      // Work out the VM's volume
      Set<Volume> volumes = client.getVolumeClient().listVolumes(ListVolumesOptions.Builder.virtualMachineId(vmForCreation.getId()));
      assertEquals(volumes.size(), 1);
      Volume volume = Iterables.getOnlyElement(volumes);

      // Create a template
      CreateTemplateOptions options = CreateTemplateOptions.Builder.volumeId(volume.getId());
      AsyncCreateResponse response = client.getTemplateClient().createTemplate(TemplateMetadata.builder().name(prefix+"-createTemplate").osTypeId(vmForCreation.getGuestOSId()).displayText("jclouds live testCreateTemplate").build(), options);
      assertTrue(jobComplete.apply(response.getJobId()), vmForCreation.toString());
      createdTemplate = client.getTemplateClient().getTemplateInZone(response.getId(), vmForCreation.getZoneId());

      // Assertions
      assertNotNull(createdTemplate);
   }

   @Test(enabled = true, dependsOnMethods = "testRegisterTemplate")
   public void testExtractTemplate() throws Exception {
      // Initiate the extraction and wait for it to complete
      AsyncCreateResponse response = client.getTemplateClient().extractTemplate(registeredTemplate.getId(), ExtractMode.HTTP_DOWNLOAD, registeredTemplate.getZoneId());
      assertTrue(jobComplete.apply(response.getJobId()), registeredTemplate.toString());

      // Get the result
      AsyncJob<TemplateExtraction> asyncJob = client.getAsyncJobClient().getAsyncJob(response.getJobId());
      TemplateExtraction extract = asyncJob.getResult();
      assertNotNull(extract);

      // Check that the URL can be retrieved
      String extractUrl = extract.getUrl();
      assertNotNull(extractUrl);
      URI uri = new URI(URLDecoder.decode(extractUrl, "utf-8"));
      assertTrue(cloudStackContext.utils().http().exists(uri), "does not exist: " + uri);
   }

   @Test(enabled = true)
   public void testRegisterTemplate() throws Exception {
      Zone zone = Iterables.getFirst(client.getZoneClient().listZones(), null);
      assertNotNull(zone);
      Iterable<Network> networks = client.getNetworkClient().listNetworks(ListNetworksOptions.Builder.zoneId(zone.getId()).isDefault(true));
      networks = Iterables.filter(networks, new Predicate<Network>() {
         @Override
         public boolean apply(Network network) {
            return network != null && network.getName().equals("Virtual Network");
         }
      });
      assertEquals(Iterables.size(networks), 1);
      Network network = Iterables.getOnlyElement(networks, null);
      assertNotNull(network);
      Set<OSType> osTypes = client.getGuestOSClient().listOSTypes();
      OSType osType = Iterables.getFirst(osTypes, null);

      // Register a template
      RegisterTemplateOptions options = RegisterTemplateOptions.Builder.bits(32).isExtractable(true);
      TemplateMetadata templateMetadata = TemplateMetadata.builder().name(prefix+"-registerTemplate").osTypeId(osType.getId()).displayText("jclouds live testRegisterTemplate").build();
      Set<Template> templates = client.getTemplateClient().registerTemplate(templateMetadata, "VHD", "XenServer", IMPORT_VHD_URL, zone.getId(), options);
      registeredTemplate = Iterables.getOnlyElement(templates, null);
      assertNotNull(registeredTemplate);

      // Ensure it is available
      final String zoneId = zone.getId();
      Predicate<Template> templateReadyPredicate = new Predicate<Template>() {
         @Override
         public boolean apply(Template template) {
            if (template == null) return false;
            Template t2 = client.getTemplateClient().getTemplateInZone(template.getId(), zoneId);
            Logger.CONSOLE.info("%s", t2.getStatus());
            return t2.getStatus() == Template.Status.DOWNLOADED;
         }
      };
      assertTrue(retry(templateReadyPredicate, 60000).apply(registeredTemplate));

      // Create a VM that uses this template
      vmForRegistration = VirtualMachineClientLiveTest.createVirtualMachineInNetwork(network, registeredTemplate.getId(), client, jobComplete, virtualMachineRunning);
      assertNotNull(vmForRegistration);
   }


   @AfterGroups(groups = "live")
   @Override
   protected void tearDownContext() {
      if (vmForCreation != null) {
         assertTrue(jobComplete.apply(client.getVirtualMachineClient().stopVirtualMachine(vmForCreation.getId())), vmForCreation.toString());
         assertTrue(jobComplete.apply(client.getVirtualMachineClient().destroyVirtualMachine(vmForCreation.getId())), vmForCreation.toString());
         assertTrue(virtualMachineDestroyed.apply(vmForCreation));
      }
      if (vmForRegistration != null) {
         assertTrue(jobComplete.apply(client.getVirtualMachineClient().stopVirtualMachine(vmForRegistration.getId())), vmForRegistration.toString());
         assertTrue(jobComplete.apply(client.getVirtualMachineClient().destroyVirtualMachine(vmForRegistration.getId())), vmForRegistration.toString());
         assert virtualMachineDestroyed.apply(vmForRegistration);
      }
      if (createdTemplate != null) {
         AsyncCreateResponse deleteJob = client.getTemplateClient().deleteTemplate(createdTemplate.getId());
         assertTrue(jobComplete.apply(deleteJob.getJobId()));
      }
      if (registeredTemplate != null) {
         AsyncCreateResponse deleteJob = client.getTemplateClient().deleteTemplate(registeredTemplate.getId());
         assertTrue(jobComplete.apply(deleteJob.getJobId()));
      }
      super.tearDownContext();
   }

}
