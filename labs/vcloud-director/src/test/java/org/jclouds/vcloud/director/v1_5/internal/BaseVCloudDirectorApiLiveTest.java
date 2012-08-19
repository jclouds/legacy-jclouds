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

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.io.Closeables.closeQuietly;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.ENTITY_NON_NULL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.TASK_COMPLETE_TIMELY;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.URN_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CATALOG;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NETWORK;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ORG_NETWORK;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.USER;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.VAPP;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.VDC;
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
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminApi;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminAsyncApi;
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status;
import org.jclouds.vcloud.director.v1_5.domain.Role.DefaultRoles;
import org.jclouds.vcloud.director.v1_5.domain.RoleReferences;
import org.jclouds.vcloud.director.v1_5.domain.Session;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.domain.network.Network;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.network.VAppNetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.org.Org;
import org.jclouds.vcloud.director.v1_5.domain.params.InstantiateVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.params.InstantiationParams;
import org.jclouds.vcloud.director.v1_5.domain.params.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.features.TaskApi;
import org.jclouds.vcloud.director.v1_5.features.VAppApi;
import org.jclouds.vcloud.director.v1_5.features.VAppTemplateApi;
import org.jclouds.vcloud.director.v1_5.features.VdcApi;
import org.jclouds.vcloud.director.v1_5.predicates.EntityPredicates;
import org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates;
import org.jclouds.vcloud.director.v1_5.predicates.ReferencePredicates;
import org.jclouds.vcloud.director.v1_5.predicates.TaskStatusEquals;
import org.jclouds.vcloud.director.v1_5.predicates.TaskSuccess;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorAsyncApi;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;

/**
 * Tests behavior of {@link VCloudDirectorApi} and acts as parent for other api live tests.
 *
 * @author Adrian Cole
 * @author grkvlt@apache.org
 */
@Listeners(FormatApiResultsListener.class)
@Test(groups = "live")
public abstract class BaseVCloudDirectorApiLiveTest extends BaseContextLiveTest<VCloudDirectorContext> {

   @Resource
   protected Logger logger = Logger.NULL;

   protected static final long TASK_TIMEOUT_SECONDS = 100L;
   protected static final long LONG_TASK_TIMEOUT_SECONDS = 300L;

   public static final int REQUIRED_ADMIN_VM_QUOTA = 0;
   public static final int REQUIRED_USER_VM_QUOTA = 0;

   public Predicate<Task> retryTaskSuccess;
   public Predicate<Task> retryTaskSuccessLong;

   protected RestContext<VCloudDirectorAdminApi, VCloudDirectorAdminAsyncApi> adminContext;
   protected RestContext<VCloudDirectorApi, VCloudDirectorAsyncApi> context; // FIXME: rename to userContext?
   protected Session adminSession;
   protected Session session;

   protected String catalogUrn;
   private Catalog catalog;
   protected URI vAppTemplateURI;
   protected URI mediaURI;
   protected String networkUrn;
   private Network network;
   protected String vdcUrn;
   private Vdc vdc;
   protected String userUrn;
   private User user;

   protected final Set<String> vAppNames = Sets.newLinkedHashSet();
   protected static final Random random = new Random();

   protected BaseVCloudDirectorApiLiveTest() {
      provider = "vcloud-director";
   }

   protected DateService dateService;

   protected VCloudDirectorTestSession testSession;

   protected Org org;
   
   protected static String testStamp;

   @BeforeClass(alwaysRun = true)
   protected void setupDateService() {
      dateService = Guice.createInjector().getInstance(DateService.class);
      assertNotNull(dateService);
   }

   /** Implement as required to populate xxxApi fields, or NOP */
   protected abstract void setupRequiredApis();

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
      closeQuietly(testSession);
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
      setupRequiredApis();
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

   public static Reference getRoleReferenceFor(String name, RestContext<VCloudDirectorAdminApi, VCloudDirectorAdminAsyncApi> adminContext) {
      RoleReferences roles = adminContext.getApi().getQueryApi().roleReferencesQueryAll();
      // backend in a builder to strip out unwanted xml cruft that the api chokes on
      return Reference.builder().fromReference(find(roles.getReferences(), ReferencePredicates.nameEquals(name))).build();
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

   protected void initTestParametersFromPropertiesOrLazyDiscover() {
      catalogUrn = emptyToNull(System.getProperty("test." + provider + ".catalog-id"));

      String vAppTemplateId = emptyToNull(System.getProperty("test." + provider + ".vapptemplate-id"));
      if (vAppTemplateId != null)
         vAppTemplateURI = URI.create(endpoint + "/vAppTemplate/" + vAppTemplateId);

      vdcUrn = emptyToNull(System.getProperty("test." + provider + ".vdc-id"));

      String mediaId = emptyToNull(System.getProperty("test." + provider + ".media-id"));
      if (mediaId != null)
         mediaURI = URI.create(endpoint + "/media/" + mediaId);

      networkUrn = emptyToNull(System.getProperty("test." + provider + ".network-id"));

      userUrn = emptyToNull(System.getProperty("test." + provider + ".user-id"));

      org = context.getApi().getOrgApi().get(
               find(context.getApi().getOrgApi().list(),
                        ReferencePredicates.<Reference> nameEquals(session.get())).getHref());
      
      if (any(Lists.newArrayList(vAppTemplateURI, networkUrn, vdcUrn), Predicates.isNull())) {

         if (vdcUrn == null) {
            vdc = context.getApi().getVdcApi().get(find(org.getLinks(),
                     ReferencePredicates.<Link> typeEquals(VDC)).getHref());
            vdcUrn = vdc.getId();
         }

         if (networkUrn == null) {
            network = context.getApi().getNetworkApi().get(find(org.getLinks(),
                     ReferencePredicates.<Link> typeEquals(ORG_NETWORK)).getHref());
            networkUrn = network.getId();
         }

         if (catalogUrn == null) {
            catalog = context.getApi().getCatalogApi().get(find(org.getLinks(),
                     ReferencePredicates.<Link> typeEquals(CATALOG)).getHref());
            catalogUrn = catalog.getId();
         }
      }
   }
   
   protected Vdc lazyGetVdc() {
      if (vdc == null) {
         assertNotNull(vdcUrn, String.format(URN_REQ_LIVE, VDC));
         vdc = from(org.getLinks()).filter(LinkPredicates.typeEquals(VDC))
                  .transform(new Function<Link, Vdc>() {

                     @Override
                     public Vdc apply(Link in) {
                        return context.getApi().getVdcApi().get(in.getHref());
                     }
                  }).firstMatch(EntityPredicates.idEquals(vdcUrn)).get();
         assertNotNull(vdc, String.format(ENTITY_NON_NULL, VDC));
      }
      return vdc;
   }

   protected Network lazyGetNetwork() {
      if (network == null) {
         assertNotNull(networkUrn, String.format(URN_REQ_LIVE, NETWORK));
         network = from(org.getLinks()).filter(LinkPredicates.typeEquals(ORG_NETWORK))
                  .transform(new Function<Link, Network>() {

                     @Override
                     public Network apply(Link in) {
                        return context.getApi().getNetworkApi().get(in.getHref());
                     }
                  }).firstMatch(EntityPredicates.idEquals(networkUrn)).get();
         assertNotNull(network, String.format(ENTITY_NON_NULL, NETWORK));
      }
      return network;
   }
   
   protected Catalog lazyGetCatalog() {
      if (catalog == null) {
         assertNotNull(catalogUrn, String.format(URN_REQ_LIVE, CATALOG));
         catalog = from(org.getLinks()).filter(LinkPredicates.typeEquals(CATALOG))
                  .transform(new Function<Link, Catalog>() {

                     @Override
                     public Catalog apply(Link in) {
                        return context.getApi().getCatalogApi().get(in.getHref());
                     }
                  }).firstMatch(EntityPredicates.idEquals(catalogUrn)).get();
         assertNotNull(catalog, String.format(ENTITY_NON_NULL, CATALOG));
      }
      return catalog;
   }

   protected User lazyGetUser() {
      if (user == null) {
         assertNotNull(userUrn, String.format(URN_REQ_LIVE, USER));
         user = adminContext.getApi().getUserApi().get(userUrn);
         assertNotNull(user, String.format(ENTITY_NON_NULL, USER));
      }
      return user;
   }
   
   @Deprecated
   public URI toAdminUri(Reference ref) {
      return toAdminUri(ref.getHref());
   }

   @Deprecated
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
      TaskApi taskApi = context.getApi().getTaskApi();
      TaskStatusEquals predicate = new TaskStatusEquals(taskApi, running, immutableSet);
      RetryablePredicate<Task> retryablePredicate = new RetryablePredicate<Task>(predicate, TASK_TIMEOUT_SECONDS * 1000L);
      return retryablePredicate.apply(task);
   }

   protected void assertTaskStatusEventually(Task task, Task.Status running, ImmutableSet<Task.Status> immutableSet) {
      assertTrue(taskStatusEventually(task, running, immutableSet),
            String.format("Task '%s' must reach status %s", task.getOperationName(), running));
   }

   protected boolean taskDoneEventually(Task task) {
      TaskApi taskApi = context.getApi().getTaskApi();
      TaskStatusEquals predicate = new TaskStatusEquals(
               taskApi,
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

      VdcApi vdcApi = context.getApi().getVdcApi();
      VApp vAppInstantiated = vdcApi.instantiateVApp(vdcUrn, instantiate);
      assertNotNull(vAppInstantiated, String.format(ENTITY_NON_NULL, VAPP));

      Task instantiationTask = getFirst(vAppInstantiated.getTasks(), null);
      if (instantiationTask != null) assertTaskSucceedsLong(instantiationTask);

      // Save VApp name for cleanUp
      vAppNames.add(name);

      return vAppInstantiated;
   }

   /** Build an {@link InstantiationParams} object. */
   protected InstantiationParams instantiationParams() {
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
      Vdc vdc = context.getApi().getVdcApi().get(vdcUrn);
      assertNotNull(vdc, String.format(ENTITY_NON_NULL, VDC));

      Set<Reference> networks = vdc.getAvailableNetworks();

      // Look up the network in the Vdc with the id configured for the tests
      Optional<Reference> parentNetwork = tryFind(networks, new Predicate<Reference>() {
         @Override
         public boolean apply(Reference reference) {
            return reference.getHref().equals(network.getHref());
         }
      });

      // Check we actually found a network reference
      if (!parentNetwork.isPresent()) {
         fail(String.format("Could not find network %s in vdc", network.getHref().toASCIIString()));
      }

      // Build the configuration object
      NetworkConfiguration networkConfiguration = NetworkConfiguration.builder()
            .parentNetwork(parentNetwork.get())
            .fenceMode(Network.FenceMode.BRIDGED)
            .build();

      return networkConfiguration;
   }

   protected void cleanUpVAppTemplate(VAppTemplate vAppTemplate) {
      VAppTemplateApi vappTemplateApi = context.getApi().getVAppTemplateApi();
      try {
	      Task task = vappTemplateApi.removeVappTemplate(vAppTemplate.getHref());
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
      VAppApi vAppApi = context.getApi().getVAppApi();

      VApp vApp = vAppApi.getVApp(vAppURI); // Refresh
      if (vApp == null) {
         logger.info("Cannot find VApp at %s", vAppURI.getPath());
         return; // Presumably vApp has already been removed. Ignore.
      }
      logger.debug("Deleting VApp %s (%s)", vApp.getName(), vAppURI.getPath());

      // Wait for busy tasks to complete (don't care if it's failed or successful)
      // Otherwise, get error on remove "entity is busy completing an operation.
      if (vApp.getTasks() != null) {
         for (Task task : vApp.getTasks()) {
            if (!taskDoneEventually(task)) {
               logger.warn("Task '%s' did not complete", task.getOperationName());
            }
         }
      }

      // Shutdown and power off the VApp if necessary
      if (vApp.getStatus() == Status.POWERED_ON) {
         try {
            Task shutdownTask = vAppApi.shutdown(vAppURI);
            taskDoneEventually(shutdownTask);
         } catch (Exception e) {
            // keep going; cleanup as much as possible
            logger.warn(e, "Continuing cleanup after error shutting down VApp %s", vApp.getName());
         }
      }

      // Undeploy the VApp if necessary
      if (vApp.isDeployed()) {
         try {
            UndeployVAppParams params = UndeployVAppParams.builder()
                  .undeployPowerAction(UndeployVAppParams.PowerAction.SHUTDOWN)
                  .build();
            Task undeployTask = vAppApi.undeploy(vAppURI, params);
            taskDoneEventually(undeployTask);
         } catch (Exception e) {
            // keep going; cleanup as much as possible
            logger.warn(e, "Continuing cleanup after error undeploying VApp %s", vApp.getName());
         }
      }

      try {
         Task task = vAppApi.removeVApp(vAppURI);
         taskDoneEventually(task);
         vAppNames.remove(vApp.getName());
         logger.info("Deleted VApp %s", vApp.getName());
      } catch (Exception e) {
         vApp = vAppApi.getVApp(vAppURI); // Refresh
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
