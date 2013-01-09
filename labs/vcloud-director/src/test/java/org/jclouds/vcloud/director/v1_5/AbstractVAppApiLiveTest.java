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
import java.util.Set;

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
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConnection;
import org.jclouds.vcloud.director.v1_5.domain.network.VAppNetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConnection.IpAddressAllocationMode;
import org.jclouds.vcloud.director.v1_5.domain.params.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.section.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.features.CatalogApi;
import org.jclouds.vcloud.director.v1_5.features.MetadataApi;
import org.jclouds.vcloud.director.v1_5.features.QueryApi;
import org.jclouds.vcloud.director.v1_5.features.VAppApi;
import org.jclouds.vcloud.director.v1_5.features.VAppTemplateApi;
import org.jclouds.vcloud.director.v1_5.features.VdcApi;
import org.jclouds.vcloud.director.v1_5.features.VmApi;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorApiLiveTest;
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
 * Shared code to test the behaviour of {@link VAppApi} and {@link VAppTemplateApi}.
 * 
 * @author grkvlt@apache.org
 */
public abstract class AbstractVAppApiLiveTest extends BaseVCloudDirectorApiLiveTest {

   public static final String VAPP = "VApp";
   public static final String VAPP_TEMPLATE = "VAppTemplate";
   public static final String VDC = "Vdc";
   public static final String VM = "Vm";

   /*
    * Convenience reference to API apis.
    */

   protected CatalogApi catalogApi;
   protected QueryApi queryApi;
   protected VAppApi vAppApi;
   protected VAppTemplateApi vAppTemplateApi;
   protected VdcApi vdcApi;
   protected VmApi vmApi;
   protected MetadataApi metadataApi;

   /*
    * Objects shared between tests.
    */

   protected Vdc vdc;
   protected Vm vm;
   protected VApp vApp;
   protected VAppTemplate vAppTemplate;
   protected String vmUrn;
   protected String vAppUrn;

   /**
    * Retrieves the required apis from the REST API context
    * 
    * @see BaseVCloudDirectorApiLiveTest#setupRequiredApis()
    */
   @Override
   protected void setupRequiredApis() {
      assertNotNull(context.getApi());

      catalogApi = context.getApi().getCatalogApi();
      queryApi = context.getApi().getQueryApi();
      vAppApi = context.getApi().getVAppApi();
      vAppTemplateApi = context.getApi().getVAppTemplateApi();
      vdcApi = context.getApi().getVdcApi();
      vmApi = context.getApi().getVmApi();
   }
   
   /**
    * Sets up the environment. Retrieves the test {@link Vdc} and {@link VAppTemplate} from their
    * configured {@link URI}s. Instantiates a new test VApp.
    */
   @BeforeClass(alwaysRun = true, description = "Retrieves the required apis from the REST API context")
   protected void setupEnvironment() {
      // Get the configured Vdc for the tests
      vdc = lazyGetVdc();

      // Get the configured VAppTemplate for the tests
      vAppTemplate = vAppTemplateApi.get(vAppTemplateUrn);
      assertNotNull(vAppTemplate, String.format(ENTITY_NON_NULL, VAPP_TEMPLATE));

      // Instantiate a new VApp
      VApp vAppInstantiated = instantiateVApp();
      assertNotNull(vAppInstantiated, String.format(ENTITY_NON_NULL, VAPP));
      vAppUrn = vAppInstantiated.getId();

      // Wait for the task to complete
      Task instantiateTask = Iterables.getOnlyElement(vAppInstantiated.getTasks());
      assertTrue(retryTaskSuccessLong.apply(instantiateTask), String.format(TASK_COMPLETE_TIMELY, "instantiateTask"));

      // Get the instantiated VApp
      vApp = vAppApi.get(vAppUrn);

      // Get the Vm
      List<Vm> vms = vApp.getChildren().getVms();
      vm = Iterables.getOnlyElement(vms);
      vmUrn = vm.getId();
      assertFalse(vms.isEmpty(), "The VApp must have a Vm");
   }

   protected void getGuestCustomizationSection(final Function<String, GuestCustomizationSection> getGuestCustomizationSection) {
      // Get URI for child VM
      String vmUrn = Iterables.getOnlyElement(vApp.getChildren().getVms()).getId();

      // The method under test
      try {
         GuestCustomizationSection section = getGuestCustomizationSection.apply(vmUrn);

         // Check the retrieved object is well formed
         checkGuestCustomizationSection(section);
      } catch (Exception e) {
         Throwables.propagate(e);
      }
   }

   protected void getNetworkConnectionSection(final Function<String, NetworkConnectionSection> getNetworkConnectionSection) {
      // Get URI for child VM
      String vmUrn = Iterables.getOnlyElement(vApp.getChildren().getVms()).getId();

      // The method under test
      try {
         NetworkConnectionSection section = getNetworkConnectionSection.apply(vmUrn);

         // Check the retrieved object is well formed
         checkNetworkConnectionSection(section);
      } catch (Exception e) {
         Throwables.propagate(e);
      }
   }

   @AfterClass(alwaysRun = true, description = "Cleans up the environment by deleting addd VApps")
   protected void cleanUpEnvironment() {
      vdc = vdcApi.get(vdcUrn); // Refresh

      // Find references in the Vdc with the VApp type and in the list of instantiated VApp names
      Iterable<Reference> vApps = Iterables.filter(vdc.getResourceEntities(),
            Predicates.and(ReferencePredicates.<Reference> typeEquals(VCloudDirectorMediaType.VAPP), ReferencePredicates.<Reference> nameIn(vAppNames)));

      // If we found any references, remove the VApp they point to
      if (!Iterables.isEmpty(vApps)) {
         for (Reference ref : vApps) {
            cleanUpVApp(context.getApi().getVAppApi().get(ref.getHref())); // NOTE may fail, but should continue deleting
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
   protected VApp powerOnVApp(String vAppUrn) {
      VApp test = vAppApi.get(vAppUrn);
      Status status = test.getStatus();
      if (status != Status.POWERED_ON) {
         Task powerOn = vAppApi.powerOn(vAppUrn);
         assertTaskSucceedsLong(powerOn);
      }
      test = vAppApi.get(vAppUrn);
      assertStatus(VAPP, test, Status.POWERED_ON);
      return test;
   }

   /**
    * Power on a {@link Vm}.
    */
   protected Vm powerOnVm(String vmUrn) {
      Vm test = vmApi.get(vmUrn);
      Status status = test.getStatus();
      if (status != Status.POWERED_ON) {
         Task powerOn = vmApi.powerOn(vmUrn);
         assertTaskSucceedsLong(powerOn);
      }
      test = vmApi.get(vmUrn);
      assertStatus(VM, test, Status.POWERED_ON);
      return test;
   }

   /**
    * Power off a {@link VApp}.
    */
   protected VApp powerOffVApp(String vAppUrn) {
      VApp test = vAppApi.get(vAppUrn);
      Status status = test.getStatus();
      if (status != Status.POWERED_OFF) {
         Task powerOff = vAppApi.powerOff(vAppUrn);
         assertTaskSucceedsLong(powerOff);
      }
      test = vAppApi.get(vAppUrn);
      assertStatus(VAPP, test, Status.POWERED_OFF);
      return test;
   }

   /**
    * Power off a {@link Vm}.
    */
   protected Vm powerOffVm(String vmUrn) {
      Vm test = vmApi.get(vmUrn);
      Status status = test.getStatus();
      if (status != Status.POWERED_OFF || test.isDeployed()) {
         UndeployVAppParams undeployParams = UndeployVAppParams.builder().build();
         Task shutdownVapp = vmApi.undeploy(vmUrn, undeployParams);
         assertTaskSucceedsLong(shutdownVapp);
      }
      test = vmApi.get(vmUrn);
      assertStatus(VM, test, Status.POWERED_OFF);
      return test;
   }

   /**
    * Suspend a {@link VApp}.
    */
   protected VApp suspendVApp(String vAppUrn) {
      VApp test = vAppApi.get(vAppUrn);
      Status status = test.getStatus();
      if (status != Status.SUSPENDED) {
         Task suspend = vAppApi.suspend(vAppUrn);
         assertTaskSucceedsLong(suspend);
      }
      test = vAppApi.get(vAppUrn);
      assertStatus(VAPP, test, Status.SUSPENDED);
      return test;
   }

   /**
    * Suspend a {@link Vm}.
    */
   protected Vm suspendVm(String vmUrn) {
      Vm test = vmApi.get(vmUrn);
      Status status = test.getStatus();
      if (status != Status.SUSPENDED) {
         Task suspend = vmApi.suspend(vmUrn);
         assertTaskSucceedsLong(suspend);
      }
      test = vmApi.get(vmUrn);
      assertStatus(VM, test, Status.SUSPENDED);
      return test;
   }

   /**
    * Check the {@link VApp}s current status.
    */
   protected void assertVAppStatus(final String vAppUrn, final Status status) {
      VApp testVApp = vAppApi.get(vAppUrn);
      assertStatus(VAPP, testVApp, status);
   }

   /**
    * Check the {@link Vm}s current status.
    */
   protected void assertVmStatus(String vmUrn, final Status status) {
      Vm testVm = vmApi.get(vmUrn);
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
   
   protected VAppNetworkConfiguration getVAppNetworkConfig(VApp vApp) {
      Set<VAppNetworkConfiguration> vAppNetworkConfigs = vAppApi.getNetworkConfigSection(vApp.getId()).getNetworkConfigs();
      return Iterables.tryFind(vAppNetworkConfigs, Predicates.notNull()).orNull();
   }
   
   protected boolean vAppHasNetworkConfigured(VApp vApp) {
      return getVAppNetworkConfig(vApp) != null;
   }

   protected boolean vmHasNetworkConnectionConfigured(Vm vm) {
      return listNetworkConnections(vm).size() > 0;
   }
   
   protected Set<NetworkConnection> listNetworkConnections(Vm vm) {
      return vmApi.getNetworkConnectionSection(vm.getId()).getNetworkConnections();
   }
   
   protected Set<VAppNetworkConfiguration> listVappNetworkConfigurations(VApp vApp) {
      Set<VAppNetworkConfiguration> vAppNetworkConfigs = vAppApi.getNetworkConfigSection(vApp.getId()).getNetworkConfigs();
      return vAppNetworkConfigs;
   }
   
   protected void attachVmToVAppNetwork(Vm vm, String vAppNetworkName) {
      Set<NetworkConnection> networkConnections = vmApi.getNetworkConnectionSection(vm.getId())
               .getNetworkConnections();

      NetworkConnectionSection section = NetworkConnectionSection.builder()
               .info("info")
               .primaryNetworkConnectionIndex(0)
               .build();
      
      for (NetworkConnection networkConnection : networkConnections) {
         NetworkConnection newNetworkConnection = networkConnection.toBuilder()
                  .network(vAppNetworkName)
                  .isConnected(true)
                  .networkConnectionIndex(0)
                  .ipAddressAllocationMode(IpAddressAllocationMode.POOL)
                  .build();
         
         section = section.toBuilder().networkConnection(newNetworkConnection).build();
      }
      Task configureNetwork = vmApi.editNetworkConnectionSection(vm.getId(), section);
      assertTaskSucceedsLong(configureNetwork);
   } 
}
