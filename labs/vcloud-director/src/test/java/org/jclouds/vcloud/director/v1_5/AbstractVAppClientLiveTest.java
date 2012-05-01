/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 *(Link.builder().regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.ENTITY_NON_NULL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.TASK_COMPLETE_TIMELY;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkGuestCustomizationSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkNetworkConnectionSection;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.List;

import org.jclouds.dmtf.cim.CimBoolean;
import org.jclouds.dmtf.cim.CimString;
import org.jclouds.dmtf.cim.CimUnsignedInt;
import org.jclouds.dmtf.cim.CimUnsignedLong;
import org.jclouds.vcloud.director.v1_5.domain.AbstractVAppType;
import org.jclouds.vcloud.director.v1_5.domain.RasdItemsList;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.RasdItem;
import org.jclouds.vcloud.director.v1_5.domain.section.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.features.CatalogClient;
import org.jclouds.vcloud.director.v1_5.features.MetadataClient;
import org.jclouds.vcloud.director.v1_5.features.QueryClient;
import org.jclouds.vcloud.director.v1_5.features.VAppClient;
import org.jclouds.vcloud.director.v1_5.features.VAppTemplateClient;
import org.jclouds.vcloud.director.v1_5.features.VdcClient;
import org.jclouds.vcloud.director.v1_5.features.VmClient;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.jclouds.vcloud.director.v1_5.predicates.ReferencePredicates;
import org.jclouds.xml.internal.JAXBParser;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

/**
 * Shared code to test the behaviour of {@link VAppClient} and {@link VAppTemplateClient}.
 * 
 * @author grkvlt@apache.org
 */
public abstract class AbstractVAppClientLiveTest extends BaseVCloudDirectorClientLiveTest {

   public static final String VAPP = "VApp";
   public static final String VAPP_TEMPLATE = "VAppTemplate";
   public static final String VDC = "Vdc";
   public static final String VM = "Vm";

   /*
    * Convenience reference to API clients.
    */

   protected CatalogClient catalogClient;
   protected QueryClient queryClient;
   protected VAppClient vAppClient;
   protected VAppTemplateClient vAppTemplateClient;
   protected VdcClient vdcClient;
   protected VmClient vmClient;
   protected MetadataClient.Writeable metadataClient;

   /*
    * Objects shared between tests.
    */

   protected Vdc vdc;
   protected Vm vm;
   protected VApp vApp;
   protected VAppTemplate vAppTemplate;
   protected URI vmURI;
   protected URI vAppURI;

   /**
    * Retrieves the required clients from the REST API context
    * 
    * @see BaseVCloudDirectorClientLiveTest#setupRequiredClients()
    */
   @Override
   @BeforeClass(alwaysRun = true, description = "Retrieves the required clients from the REST API context")
   protected void setupRequiredClients() {
      assertNotNull(context.getApi());

      catalogClient = context.getApi().getCatalogClient();
      queryClient = context.getApi().getQueryClient();
      vAppClient = context.getApi().getVAppClient();
      vAppTemplateClient = context.getApi().getVAppTemplateClient();
      vdcClient = context.getApi().getVdcClient();
      vmClient = context.getApi().getVmClient();

      setupEnvironment();
   }

   /**
    * Sets up the environment. Retrieves the test {@link Vdc} and {@link VAppTemplate} from their
    * configured {@link URI}s. Instantiates a new test VApp.
    */
   protected void setupEnvironment() {
      // Get the configured Vdc for the tests
      vdc = vdcClient.getVdc(vdcURI);
      assertNotNull(vdc, String.format(ENTITY_NON_NULL, VDC));

      // Get the configured VAppTemplate for the tests
      vAppTemplate = vAppTemplateClient.getVAppTemplate(vAppTemplateURI);
      assertNotNull(vAppTemplate, String.format(ENTITY_NON_NULL, VAPP_TEMPLATE));

      // Instantiate a new VApp
      VApp vAppInstantiated = instantiateVApp();
      assertNotNull(vAppInstantiated, String.format(ENTITY_NON_NULL, VAPP));
      vAppURI = vAppInstantiated.getHref();

      // Wait for the task to complete
      Task instantiateTask = Iterables.getOnlyElement(vAppInstantiated.getTasks());
      assertTrue(retryTaskSuccessLong.apply(instantiateTask), String.format(TASK_COMPLETE_TIMELY, "instantiateTask"));

      // Get the instantiated VApp
      vApp = vAppClient.getVApp(vAppURI);

      // Get the Vm
      List<Vm> vms = vApp.getChildren().getVms();
      vm = Iterables.getOnlyElement(vms);
      vmURI = vm.getHref();
      assertFalse(vms.isEmpty(), "The VApp must have a Vm");
   }

   protected void getGuestCustomizationSection(final Function<URI, GuestCustomizationSection> getGuestCustomizationSection) {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();

      // The method under test
      try {
         GuestCustomizationSection section = getGuestCustomizationSection.apply(vmURI);

         // Check the retrieved object is well formed
         checkGuestCustomizationSection(section);
      } catch (Exception e) {
         Throwables.propagate(e);
      }
   }

   protected void getNetworkConnectionSection(final Function<URI, NetworkConnectionSection> getNetworkConnectionSection) {
      // Get URI for child VM
      URI vmURI = Iterables.getOnlyElement(vApp.getChildren().getVms()).getHref();

      // The method under test
      try {
         NetworkConnectionSection section = getNetworkConnectionSection.apply(vmURI);

         // Check the retrieved object is well formed
         checkNetworkConnectionSection(section);
      } catch (Exception e) {
         Throwables.propagate(e);
      }
   }

   @AfterClass(alwaysRun = true, description = "Cleans up the environment by deleting created VApps")
   protected void cleanUpEnvironment() {
      vdc = vdcClient.getVdc(vdcURI); // Refresh

      // Find references in the Vdc with the VApp type and in the list of instantiated VApp names
      Iterable<Reference> vApps = Iterables.filter(vdc.getResourceEntities(),
            Predicates.and(ReferencePredicates.<Reference> typeEquals(VCloudDirectorMediaType.VAPP), ReferencePredicates.<Reference> nameIn(vAppNames)));

      // If we found any references, delete the VApp they point to
      if (!Iterables.isEmpty(vApps)) {
         for (Reference ref : vApps) {
            cleanUpVApp(ref.getHref()); // NOTE may fail, but should continue deleting
         }
      } else {
         logger.warn("No VApps in list found in Vdc %s (%s)", vdc.getName(), Iterables.toString(vAppNames));
      }
   }

   protected static CimBoolean cimBoolean(boolean val) {
      CimBoolean result = new CimBoolean();
      result.setValue(val);
      return result;
   }

   protected static CimUnsignedInt cimUnsignedInt(long val) {
      CimUnsignedInt result = new CimUnsignedInt();
      result.setValue(val);
      return result;
   }

   protected static CimUnsignedLong cimUnsignedLong(BigInteger val) {
      CimUnsignedLong result = new CimUnsignedLong();
      result.setValue(val);
      return result;
   }

   protected static CimString cimString(String value) {
      return new CimString(value);
   }

   protected void checkHasMatchingItem(final String context, final RasdItemsList items, final String instanceId, final String elementName) {
      Optional<RasdItem> found = Iterables.tryFind(items.getItems(), new Predicate<RasdItem>() {
         @Override
         public boolean apply(RasdItem item) {
            String itemInstanceId = item.getInstanceID();
            if (itemInstanceId.equals(instanceId)) {
               Assert.assertEquals(item.getElementName(), elementName,
                     String.format(OBJ_FIELD_EQ, VAPP, context + "/" + instanceId + "/elementName", elementName, item.getElementName()));
               return true;
            }
            return false;
         }
      });
      assertTrue(found.isPresent(), "no " + context + " item found with id " + instanceId + "; only found " + items);
   }

   /**
    * Power on a {@link VApp}.
    */
   protected VApp powerOnVApp(final URI testVAppURI) {
      VApp test = vAppClient.getVApp(testVAppURI);
      Status status = test.getStatus();
      if (status != Status.POWERED_ON) {
         Task powerOn = vAppClient.powerOn(vm.getHref());
         assertTaskSucceedsLong(powerOn);
      }
      test = vAppClient.getVApp(testVAppURI);
      assertStatus(VAPP, test, Status.POWERED_ON);
      return test;
   }

   /**
    * Power on a {@link Vm}.
    */
   protected Vm powerOnVm(final URI testVmURI) {
      Vm test = vmClient.getVm(testVmURI);
      Status status = test.getStatus();
      if (status != Status.POWERED_ON) {
         Task powerOn = vmClient.powerOn(vm.getHref());
         assertTaskSucceedsLong(powerOn);
      }
      test = vmClient.getVm(testVmURI);
      assertStatus(VM, test, Status.POWERED_ON);
      return test;
   }

   /**
    * Power off a {@link VApp}.
    */
   protected VApp powerOffVApp(final URI testVAppURI) {
      VApp test = vAppClient.getVApp(testVAppURI);
      Status status = test.getStatus();
      if (status != Status.POWERED_OFF) {
         Task powerOff = vAppClient.powerOff(vm.getHref());
         assertTaskSucceedsLong(powerOff);
      }
      test = vAppClient.getVApp(testVAppURI);
      assertStatus(VAPP, test, Status.POWERED_OFF);
      return test;
   }

   /**
    * Power off a {@link Vm}.
    */
   protected Vm powerOffVm(final URI testVmURI) {
      Vm test = vmClient.getVm(testVmURI);
      Status status = test.getStatus();
      if (status != Status.POWERED_OFF) {
         Task powerOff = vmClient.powerOff(vm.getHref());
         assertTaskSucceedsLong(powerOff);
      }
      test = vmClient.getVm(testVmURI);
      assertStatus(VM, test, Status.POWERED_OFF);
      return test;
   }

   /**
    * Suspend a {@link VApp}.
    */
   protected VApp suspendVApp(final URI testVAppURI) {
      VApp test = vAppClient.getVApp(testVAppURI);
      Status status = test.getStatus();
      if (status != Status.SUSPENDED) {
         Task suspend = vAppClient.suspend(vm.getHref());
         assertTaskSucceedsLong(suspend);
      }
      test = vAppClient.getVApp(testVAppURI);
      assertStatus(VAPP, test, Status.SUSPENDED);
      return test;
   }

   /**
    * Suspend a {@link Vm}.
    */
   protected Vm suspendVm(final URI testVmURI) {
      Vm test = vmClient.getVm(testVmURI);
      Status status = test.getStatus();
      if (status != Status.SUSPENDED) {
         Task suspend = vmClient.suspend(vm.getHref());
         assertTaskSucceedsLong(suspend);
      }
      test = vmClient.getVm(testVmURI);
      assertStatus(VM, test, Status.SUSPENDED);
      return test;
   }

   /**
    * Check the {@link VApp}s current status.
    */
   protected void assertVAppStatus(final URI testVAppURI, final Status status) {
      VApp testVApp = vAppClient.getVApp(testVAppURI);
      assertStatus(VAPP, testVApp, status);
   }

   /**
    * Check the {@link Vm}s current status.
    */
   protected void assertVmStatus(final URI testVmURI, final Status status) {
      Vm testVm = vmClient.getVm(testVmURI);
      assertStatus(VM, testVm, status);
   }

   /**
    * Check a {@link VApp} or {@link Vm}s status.
    */
   protected static void assertStatus(final String type, final AbstractVAppType testVApp, final Status status) {
      assertEquals(testVApp.getStatus(), status, String.format(OBJ_FIELD_EQ, type, "status", status.toString(), testVApp.getStatus().toString()));
   }

   /**
    * Marshals a JAXB annotated object into XML. The XML is output using
    * {@link org.jclouds.logging.Logger#debug(String)}
    */
   protected void debug(final Object object) {
      JAXBParser parser = new JAXBParser("true");
      try {
         String xml = parser.toXML(object);
         logger.debug(Strings.padStart(Strings.padEnd(" " + object.getClass().toString() + " ", 70, '-'), 80, '-'));
         logger.debug(xml);
         logger.debug(Strings.repeat("-", 80));
      } catch (IOException ioe) {
         Throwables.propagate(ioe);
      }
   }
}
