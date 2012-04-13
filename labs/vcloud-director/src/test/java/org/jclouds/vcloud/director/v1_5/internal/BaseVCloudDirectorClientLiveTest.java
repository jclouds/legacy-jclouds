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
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.date.DateService;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.vcloud.director.testng.FormatApiResultsListener;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorApiMetadata;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorContext;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminAsyncClient;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminClient;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.RoleReferences;
import org.jclouds.vcloud.director.v1_5.domain.Session;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status;
import org.jclouds.vcloud.director.v1_5.domain.Role.DefaultRoles;
import org.jclouds.vcloud.director.v1_5.domain.network.Network;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.network.VAppNetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.org.Org;
import org.jclouds.vcloud.director.v1_5.domain.params.InstantiateVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.params.InstantiationParams;
import org.jclouds.vcloud.director.v1_5.domain.params.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.features.TaskClient;
import org.jclouds.vcloud.director.v1_5.features.VAppClient;
import org.jclouds.vcloud.director.v1_5.features.VAppTemplateClient;
import org.jclouds.vcloud.director.v1_5.features.VdcClient;
import org.jclouds.vcloud.director.v1_5.predicates.ReferencePredicates;
import org.jclouds.vcloud.director.v1_5.predicates.TaskStatusEquals;
import org.jclouds.vcloud.director.v1_5.predicates.TaskSuccess;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorAsyncClient;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;
import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;

/**
 * Tests behavior of {@link VCloudDirectorClient} and acts as parent for other client live tests.
 *
 * @author Adrian Cole
 * @author grkvlt@apache.org
 */
@Listeners(FormatApiResultsListener.class)
@Test(groups = "live")
public abstract class BaseVCloudDirectorClientLiveTest extends BaseContextLiveTest<VCloudDirectorContext> {

   @Resource
   protected Logger logger = Logger.CONSOLE;

   protected static final long TASK_TIMEOUT_SECONDS = 100L;
   protected static final long LONG_TASK_TIMEOUT_SECONDS = 300L;

   public static final String VAPP = "vApp";
   public static final String VAPP_TEMPLATE = "vAppTemplate";
   public static final String VDC = "vdc";
   public static final int REQUIRED_ADMIN_VM_QUOTA = 0;
   public static final int REQUIRED_USER_VM_QUOTA = 0;

   public Predicate<Task> retryTaskSuccess;
   public Predicate<Task> retryTaskSuccessLong;

   protected RestContext<VCloudDirectorAdminClient, VCloudDirectorAdminAsyncClient> adminContext;
   protected RestContext<VCloudDirectorClient, VCloudDirectorAsyncClient> context; // FIXME: rename to userContext?
   protected Session adminSession;
   protected Session session;

   protected String catalogId;
   protected URI catalogURI;
   protected URI vAppTemplateURI;
   protected URI mediaURI;
   protected URI networkURI;
   protected URI vdcURI;
   protected URI userURI;

   protected final Set<String> vAppNames = Sets.newLinkedHashSet();
   protected static final Random random = new Random();

   protected BaseVCloudDirectorClientLiveTest() {
      provider = "vcloud-director";
   }

   protected DateService dateService;

   protected VCloudDirectorTestSession testSession;

   protected static String testStamp;

   @BeforeClass(alwaysRun = true)
   protected void setupDateService() {
      dateService = Guice.createInjector().getInstance(DateService.class);
      assertNotNull(dateService);
   }

   /** Implement as required to populate xxxClient fields, or NOP */
   protected abstract void setupRequiredClients();

   @Inject
   protected void initTaskSuccess(TaskSuccess taskSuccess) {
      retryTaskSuccess = new RetryablePredicate<Task>(taskSuccess, TASK_TIMEOUT_SECONDS * 1000L);
   }

   @Inject
   protected void initTaskSuccessLong(TaskSuccess taskSuccess) {
      retryTaskSuccessLong = new RetryablePredicate<Task>(taskSuccess, LONG_TASK_TIMEOUT_SECONDS * 1000L);
   }

   @AfterClass(alwaysRun = true)
   protected void tearDownTestSession() {
      Closeables.closeQuietly(testSession);
   }

   @Override
   protected void initializeContext() {
      Properties overrides = setupProperties();
      testSession = VCloudDirectorTestSession.builder()
            .provider(provider)
            .identity(identity)
            .credential(credential)
            .endpoint(endpoint)
            .overrides(overrides)
            .build();

      context = testSession.getUserContext();
      adminContext = testSession.getAdminContext();

      if (adminContext != null) {
         adminSession = adminContext.getApi().getCurrentSession();
         adminContext.utils().injector().injectMembers(this);
      }

      session = context.getApi().getCurrentSession();
      context.utils().injector().injectMembers(this);

      initTestParametersFromPropertiesOrLazyDiscover();
      setupRequiredClients();
   }

   public static String getTestDateTimeStamp() {
      if (testStamp == null) {
         testStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
      }

      return testStamp;
   }

   public Reference getRoleReferenceFor(String name) {
      return getRoleReferenceFor(name, adminContext);
   }

   public static Reference getRoleReferenceFor(String name, RestContext<VCloudDirectorAdminClient, VCloudDirectorAdminAsyncClient> adminContext) {
      RoleReferences roles = adminContext.getApi().getQueryClient().roleReferencesQueryAll();
      // wrapped in a builder to strip out unwanted xml cruft that the api chokes on
      return Reference.builder().fromReference(Iterables.find(roles.getReferences(), ReferencePredicates.nameEquals(name))).build();
   }

   public User randomTestUser(String prefix) {
      return randomTestUser(prefix, getRoleReferenceFor(DefaultRoles.USER.value()));
   }

   public User randomTestUser(String prefix, Reference role) {
      return User.builder()
         .name(name(prefix)+getTestDateTimeStamp())
         .fullName("testFullName")
         .emailAddress("test@test.com")
         .telephone("555-1234")
         .isEnabled(false)
         .im("testIM")
         .isAlertEnabled(false)
         .alertEmailPrefix("testPrefix")
         .alertEmail("testAlert@test.com")
         .isExternal(false)
         .isGroupRole(false)
         .role(role)
         .password("password")
         .build();
   }

   // TODO change properties to URI, not id
   protected void initTestParametersFromPropertiesOrLazyDiscover() {
      catalogId = Strings.emptyToNull(System.getProperty("test." + provider + ".catalog-id"));
      if (catalogId != null) {
         catalogURI = URI.create(endpoint + "/catalog/" + catalogId);
      }

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

      if (Iterables.any(Lists.newArrayList(vAppTemplateURI, networkURI, vdcURI), Predicates.isNull())) {
         Org thisOrg = context.getApi().getOrgClient().getOrg(
                  Iterables.find(context.getApi().getOrgClient().getOrgList().getOrgs(),
                           ReferencePredicates.<Reference> nameEquals(session.getOrg())).getHref());

         if (vdcURI == null)
            vdcURI = Iterables.find(thisOrg.getLinks(),
                     ReferencePredicates.<Link> typeEquals(VCloudDirectorMediaType.VDC)).getHref();

         if (networkURI == null)
            networkURI = Iterables.find(thisOrg.getLinks(),
                     ReferencePredicates.<Link> typeEquals(VCloudDirectorMediaType.ORG_NETWORK)).getHref();

         // FIXME the URI should be opaque
         if (Strings.isNullOrEmpty(catalogId)) {
            String uri = Iterables.find(thisOrg.getLinks(),
                     ReferencePredicates.<Link> typeEquals(VCloudDirectorMediaType.CATALOG)).getHref().toASCIIString();
            catalogId = Iterables.getLast(Splitter.on('/').split(uri));
         }
      }
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

   protected boolean taskStatusEventually(Task task, Task.Status running, ImmutableSet<Task.Status> immutableSet) {
      TaskClient taskClient = context.getApi().getTaskClient();
      TaskStatusEquals predicate = new TaskStatusEquals(taskClient, running, immutableSet);
      RetryablePredicate<Task> retryablePredicate = new RetryablePredicate<Task>(predicate, TASK_TIMEOUT_SECONDS * 1000L);
      return retryablePredicate.apply(task);
   }

   protected void assertTaskStatusEventually(Task task, Task.Status running, ImmutableSet<Task.Status> immutableSet) {
      assertTrue(taskStatusEventually(task, running, immutableSet),
            String.format("Task '%s' must reach status %s", task.getOperationName(), running));
   }

   protected boolean taskDoneEventually(Task task) {
      TaskClient taskClient = context.getApi().getTaskClient();
      TaskStatusEquals predicate = new TaskStatusEquals(
               taskClient,
               ImmutableSet.of(Task.Status.ABORTED, Task.Status.CANCELED, Task.Status.ERROR, Task.Status.SUCCESS),
               Collections.<Task.Status>emptySet());
      RetryablePredicate<Task> retryablePredicate = new RetryablePredicate<Task>(predicate, LONG_TASK_TIMEOUT_SECONDS * 1000L);
      return retryablePredicate.apply(task);
   }

   protected void assertTaskDoneEventually(Task task) {
      assertTrue(taskDoneEventually(task),
            String.format("Task '%s' must complete", task.getOperationName()));
   }

   /**
    * Instantiate a {@link VApp} in a {@link Vdc} using the {@link VAppTemplate} we have configured for the tests.
    *
    * @return the VApp that is being instantiated
    */
   protected VApp instantiateVApp() {
      return instantiateVApp(name("test-vapp-"));
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

      Task instantiationTask = Iterables.getFirst(vAppInstantiated.getTasks(), null);
      if (instantiationTask != null) assertTaskSucceedsLong(instantiationTask);

      // Save VApp name for cleanUp
      vAppNames.add(name);

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

      Set<Reference> networks = vdc.getAvailableNetworks();

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
            .fenceMode(Network.FenceMode.ISOLATED)
            .build();

      return networkConfiguration;
   }

   protected void cleanUpVAppTemplate(VAppTemplate vAppTemplate) {
      VAppTemplateClient vappTemplateClient = context.getApi().getVAppTemplateClient();
      try {
	      Task task = vappTemplateClient.deleteVappTemplate(vAppTemplate.getHref());
	      taskDoneEventually(task);
      } catch (Exception e) {
         logger.warn(e, "Error deleting template '%s'", vAppTemplate.getName());
      }
    }

   protected void cleanUpVApp(VApp vApp) {
      cleanUpVApp(vApp.getHref());
   }

   // TODO code tidy for cleanUpVApp? Seems extremely verbose!
   protected void cleanUpVApp(URI vAppURI) {
      VAppClient vAppClient = context.getApi().getVAppClient();

      VApp vApp;
      try {
         vApp = vAppClient.getVApp(vAppURI); // Refresh
         logger.debug("Deleting VApp %s (%s)", vApp.getName(), vAppURI.getPath());
      } catch (VCloudDirectorException e) {
         // Presumably vApp has already been deleted. Ignore.
         logger.info("Cannot find VApp at %s", vAppURI.getPath());
         return;
      }

      // Wait for busy tasks to complete (don't care if it's failed or successful)
      // Otherwise, get error on delete "entity is busy completing an operation.
      if (vApp.getTasks() != null) {
         for (Task task : vApp.getTasks()) {
            if (!taskDoneEventually(task)) {
               logger.warn("Task '%s' did not complete", task.getOperationName());
            }
         }
      }

      // Shutdown and power off the VApp if necessary
      if (vApp.getStatus().equals(Status.POWERED_ON.getValue())) {
         try {
            Task shutdownTask = vAppClient.shutdown(vAppURI);
            taskDoneEventually(shutdownTask);
         } catch (Exception e) {
            // keep going; cleanup as much as possible
            logger.warn(e, "Continuing cleanup after error shutting down VApp %s", vApp.getName());
         }
      }

      // Undeploy the VApp if necessary
      if (vApp.isDeployed()) {
         try {
            UndeployVAppParams params = UndeployVAppParams.builder().build();
            Task undeployTask = vAppClient.undeploy(vAppURI, params);
            taskDoneEventually(undeployTask);
         } catch (Exception e) {
            // keep going; cleanup as much as possible
            logger.warn(e, "Continuing cleanup after error undeploying VApp %s", vApp.getName());
         }
      }

      try {
         Task task = vAppClient.deleteVApp(vAppURI);
         taskDoneEventually(task);
         vAppNames.remove(vApp.getName());
         logger.info("Deleted VApp %s", vApp.getName());
      } catch (Exception e) {
         try {
            vApp = vAppClient.getVApp(vAppURI); // Refresh
         } catch (Exception e2) {
            // Ignore
         }

         logger.warn(e, "Deleting VApp %s failed (%s)", vApp.getName(), vAppURI.getPath());
      }
   }

   public static String name(String prefix) {
      return prefix + Integer.toString(random.nextInt(Integer.MAX_VALUE));
   }

   @Override
   protected TypeToken<VCloudDirectorContext> contextType() {
      return VCloudDirectorApiMetadata.CONTEXT_TOKEN;
   }

}
