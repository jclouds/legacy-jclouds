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
package org.jclouds.vcloud.director.v1_5.internal;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.ENTITY_NON_NULL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.TASK_COMPLETE_TIMELY;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.compute.BaseVersionedServiceLiveTest;
import org.jclouds.date.DateService;
import org.jclouds.logging.Logger;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.vcloud.director.testng.FormatApiResultsListener;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorAsyncClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.InstantiateVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.InstantiationParams;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.Org;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status;
import org.jclouds.vcloud.director.v1_5.domain.Session;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppNetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.features.TaskClient;
import org.jclouds.vcloud.director.v1_5.features.VAppClient;
import org.jclouds.vcloud.director.v1_5.features.VAppTemplateClient;
import org.jclouds.vcloud.director.v1_5.features.VdcClient;
import org.jclouds.vcloud.director.v1_5.predicates.ReferenceTypePredicates;
import org.jclouds.vcloud.director.v1_5.predicates.TaskStatusEquals;
import org.jclouds.vcloud.director.v1_5.predicates.TaskSuccess;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Module;

/**
 * Tests behavior of {@link VCloudDirectorClient} and acts as parent for other client live tests.
 *
 * @author Adrian Cole
 * @author grkvlt@apache.org
 */
@Listeners(FormatApiResultsListener.class)
@Test(groups = "live")
public abstract class BaseVCloudDirectorClientLiveTest extends BaseVersionedServiceLiveTest {
   
   @Resource
   protected Logger logger = Logger.NULL;

   protected static final long TASK_TIMEOUT_SECONDS = 100L;
   protected static final long LONG_TASK_TIMEOUT_SECONDS = 300L;

   public static final String VAPP = "vApp";
   public static final String VAPP_TEMPLATE = "vAppTemplate";
   public static final String VDC = "vdc";
   
   public Predicate<Task> retryTaskSuccess;
   public Predicate<Task> retryTaskSuccessLong;

   protected RestContext<VCloudDirectorClient, VCloudDirectorAsyncClient> context;
   protected Session session;

   protected String catalogName;
   protected String networkName;
   protected String userName;

   protected URI vAppTemplateURI;
   protected URI mediaURI;
   protected URI networkURI;
   protected URI vdcURI;
   protected URI userURI;

   protected Random random = new Random();
   
   protected BaseVCloudDirectorClientLiveTest() {
      provider = "vcloud-director";
   }

   protected static DateService dateService;

   @BeforeGroups("live")
   protected static void setupDateService() {
      dateService = Guice.createInjector().getInstance(DateService.class);
      assertNotNull(dateService);
   }
   
   // NOTE Implement as required to populate xxxClient fields, or NOP
   protected abstract void setupRequiredClients() throws Exception;

   @Inject
   protected void initTaskSuccess(TaskSuccess taskSuccess) {
      retryTaskSuccess = new RetryablePredicate<Task>(taskSuccess, TASK_TIMEOUT_SECONDS * 1000L);
   }

   @Inject
   protected void initTaskSuccessLong(TaskSuccess taskSuccess) {
      retryTaskSuccessLong = new RetryablePredicate<Task>(taskSuccess, LONG_TASK_TIMEOUT_SECONDS * 1000L);
   }

   @BeforeClass(groups = { "live" })
   protected void setupContext() throws Exception {
      setupCredentials();
      Properties overrides = setupProperties();

      context = new RestContextFactory().createContext(provider, identity, credential, ImmutableSet.<Module> of(
               new Log4JLoggingModule(), new SshjSshClientModule()), overrides);
      session = context.getApi().getCurrentSession();
      context.utils().injector().injectMembers(this);
      initTestParametersFromPropertiesOrLazyDiscover();
      setupRequiredClients();
   }

   // TODO change properties to URI, not id
   @SuppressWarnings("unchecked")
   protected void initTestParametersFromPropertiesOrLazyDiscover() {
      catalogName = Strings.emptyToNull(System.getProperty("test." + provider + ".catalog-name"));
      networkName = Strings.emptyToNull(System.getProperty("test." + provider + ".network-name"));

      String vAppTemplateId = Strings.emptyToNull(System.getProperty("test." + provider + ".vapptemplate-id"));
      if (vAppTemplateId != null)
         vAppTemplateURI = URI.create(endpoint + "/vAppTemplate/" + vAppTemplateId);

      String vdcId = Strings.emptyToNull(System.getProperty("test." + provider + ".vdc-id"));
      if (vdcId != null)
         vdcURI = URI.create(endpoint + "/vdc/" + vdcId);

      String mediaId = Strings.emptyToNull(System.getProperty("test." + provider + ".media-id"));
      if (mediaId != null)
         mediaURI = URI.create(endpoint + "/media/" + mediaId);

      String networkId = Strings.emptyToNull(System.getProperty("test." + provider + ".network-id"));
      if (networkId != null)
         networkURI = URI.create(endpoint + "/network/" + networkId);

      String userId = Strings.emptyToNull(System.getProperty("test." + provider + ".user-id"));
      if (userId != null)
         userURI = URI.create(endpoint + "/admin/user/" + userId);

      if (Iterables.any(Lists.newArrayList(catalogName, vAppTemplateURI, networkURI, vdcURI), Predicates.isNull())) {
         Org thisOrg = context.getApi().getOrgClient().getOrg(
                  Iterables.find(context.getApi().getOrgClient().getOrgList().getOrgs(),
                           ReferenceTypePredicates.<Reference> nameEquals(session.getOrg())).getHref());

         if (vdcURI == null)
            vdcURI = Iterables.find(thisOrg.getLinks(),
                     ReferenceTypePredicates.<Link> typeEquals(VCloudDirectorMediaType.VDC)).getHref();

         if (networkURI == null)
            networkURI = Iterables.find(thisOrg.getLinks(),
                     ReferenceTypePredicates.<Link> typeEquals(VCloudDirectorMediaType.ORG_NETWORK)).getHref();

         if (catalogName == null)
            catalogName = Iterables.find(thisOrg.getLinks(),
                     ReferenceTypePredicates.<Link> typeEquals(VCloudDirectorMediaType.CATALOG)).getName();

         // TODO look for default networkName
      }
   }

   protected void tearDown() {
      if (context != null)
         context.close();
   }
   
   public URI toAdminUri(Reference ref) {
      return toAdminUri(ref.getHref());
   }
   
   public URI toAdminUri(URI uri) {
      return Reference.builder().href(uri).build().toAdminReference(endpoint).getHref();
   }
   
   protected void assertTaskSucceeds(Task task) {
      assertTrue(retryTaskSuccess.apply(task), String.format(TASK_COMPLETE_TIMELY, task));
   }
   
   protected void assertTaskSucceedsLong(Task task) {
      assertTrue(retryTaskSuccessLong.apply(task), String.format(TASK_COMPLETE_TIMELY, task));
   }

   protected void assertTaskStatusEventually(Task task, String expectedStatus, Collection<String> failingStatuses) {
      TaskClient taskClient = context.getApi().getTaskClient();
      TaskStatusEquals predicate = new TaskStatusEquals(taskClient, expectedStatus, failingStatuses);
      RetryablePredicate<Task> retryablePredicate = new RetryablePredicate<Task>(predicate, TASK_TIMEOUT_SECONDS * 1000L);
      assertTrue(retryablePredicate.apply(task), "Task must enter status "+expectedStatus);
   }
   
   protected void assertTaskDoneEventually(Task task) {
      TaskClient taskClient = context.getApi().getTaskClient();
      TaskStatusEquals predicate = new TaskStatusEquals(
               taskClient, 
               ImmutableSet.of(Task.Status.ABORTED, Task.Status.CANCELED, Task.Status.ERROR, Task.Status.SUCCESS), 
               Collections.<String>emptySet());
      RetryablePredicate<Task> retryablePredicate = new RetryablePredicate<Task>(predicate, LONG_TASK_TIMEOUT_SECONDS * 1000L);
      assertTrue(retryablePredicate.apply(task), "Task must be done");
   }

   /**
    * Instantiate a {@link VApp} in a {@link Vdc} using the {@link VAppTemplate} we have configured for the tests.
    * 
    * @return the VApp that is being instantiated
    */
   protected VApp instantiateVApp() {
      return instantiateVApp("test-vapp-"+random.nextInt(Integer.MAX_VALUE));
   }
   
   protected VApp instantiateVApp(String name) {
      InstantiateVAppTemplateParams instantiate = InstantiateVAppTemplateParams.builder()
            .name(name)
            .notDeploy()
            .notPowerOn()
            .description("Test VApp")
            .instantiationParams(instantiationParams())
            .source(Reference.builder().href(vAppTemplateURI).build())
            .build();

      VdcClient vdcClient = context.getApi().getVdcClient();
      VApp vAppInstantiated = vdcClient.instantiateVApp(vdcURI, instantiate);
      assertNotNull(vAppInstantiated, String.format(ENTITY_NON_NULL, VAPP));

      return vAppInstantiated;
   }

   /** Build an {@link InstantiationParams} object. */
   private InstantiationParams instantiationParams() {
      InstantiationParams instantiationParams = InstantiationParams.builder()
            .sections(ImmutableSet.of(networkConfigSection()))
            .build();

      return instantiationParams;
   }

   /** Build a {@link NetworkConfigSection} object. */
   private NetworkConfigSection networkConfigSection() {
      NetworkConfigSection networkConfigSection = NetworkConfigSection.builder()
            .info("Configuration parameters for logical networks")
            .networkConfigs(
                  ImmutableSet.of(
                        VAppNetworkConfiguration.builder()
                              .networkName("vAppNetwork")
                              .configuration(networkConfiguration())
                              .build()))
            .build();

      return networkConfigSection;
   }

   /** Build a {@link NetworkConfiguration} object. */
   private NetworkConfiguration networkConfiguration() {
      Vdc vdc = context.getApi().getVdcClient().getVdc(vdcURI);
      assertNotNull(vdc, String.format(ENTITY_NON_NULL, VDC));
      
      Set<Reference> networks = vdc.getAvailableNetworks().getNetworks();

      // Look up the network in the Vdc with the id configured for the tests
      Optional<Reference> parentNetwork = Iterables.tryFind(networks, new Predicate<Reference>() {
         @Override
         public boolean apply(Reference reference) {
            return reference.getHref().equals(networkURI);
         }
      });

      // Check we actually found a network reference
      if (!parentNetwork.isPresent()) {
         fail(String.format("Could not find network %s in vdc", networkURI.toASCIIString()));
      }

      // Build the configuration object
      NetworkConfiguration networkConfiguration = NetworkConfiguration.builder()
            .parentNetwork(parentNetwork.get())
            .fenceMode("bridged")
            .build();

      return networkConfiguration;
   }
   
   protected void cleanUpVAppTemplate(VAppTemplate vAppTemplate) {
      VAppTemplateClient vappTemplateClient = context.getApi().getVAppTemplateClient();
      
      Task task = vappTemplateClient.deleteVappTemplate(vAppTemplate.getHref());
      assertTaskSucceeds(task);
   }

   protected void cleanUpVApp(VApp vApp) throws Exception {
      cleanUpVApp(vApp.getHref());
   }
   
   // TODO code tidy for cleanUpVApp? Seems extremely verbose!
   protected void cleanUpVApp(URI vAppUri) throws Exception {
      VAppClient vappClient = context.getApi().getVAppClient();

      VApp vApp;
      try {
         vApp = vappClient.getVApp(vAppUri); // update
      } catch (VCloudDirectorException e) {
         // presumably vApp has already been deleted; ignore
         return;
      }
      
      // Wait for busy tasks to complete (don't care if it's failed or successful)
      // Otherwise, get error on delete "entity is busy completing an operation.
      if (vApp.getTasks() != null) {
         for (Task task : vApp.getTasks()) {
            assertTaskDoneEventually(task);
         }
      }
      
      // Shutdown and power off the VApp if necessary
      if (vApp.getStatus().equals(Status.POWERED_ON.getValue())) {
         try {
            Task shutdownTask = vappClient.shutdown(vAppUri);
            retryTaskSuccess.apply(shutdownTask);
         } catch (Exception e) {
            // keep going; cleanup as much as possible
            logger.warn(e, "Continuing cleanup after error shutting down VApp %s", vApp);
         }
      }

      // Undeploy the VApp if necessary
      if (vApp.isDeployed()) {
         try {
            UndeployVAppParams params = UndeployVAppParams.builder().build();
            Task undeployTask = vappClient.undeploy(vAppUri, params);
            retryTaskSuccess.apply(undeployTask);
         } catch (Exception e) {
            // keep going; cleanup as much as possible
            logger.warn(e, "Continuing cleanup after error undeploying VApp %s", vApp);
         }
      }
      
      try {
         Task task = vappClient.deleteVApp(vAppUri);
         assertTaskSucceeds(task);
      } catch (Exception e) {
         try {
            vApp = vappClient.getVApp(vAppUri); // refresh
         } catch (Exception e2) {
            // ignore
         }

         logger.warn(e, "Deleting vApp failed: vApp="+vApp);
         throw e;
      }
   }
}
