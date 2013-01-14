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
package org.jclouds.compute.internal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newTreeSet;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.logging.Logger.getAnonymousLogger;
import static org.jclouds.compute.options.RunScriptOptions.Builder.nameTask;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.jclouds.compute.options.TemplateOptions.Builder.inboundPorts;
import static org.jclouds.compute.options.TemplateOptions.Builder.overrideLoginCredentials;
import static org.jclouds.compute.options.TemplateOptions.Builder.runAsRoot;
import static org.jclouds.compute.predicates.NodePredicates.TERMINATED;
import static org.jclouds.compute.predicates.NodePredicates.all;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.jclouds.compute.predicates.NodePredicates.runningInGroup;
import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeTestUtils;
import org.jclouds.compute.JettyStatements;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.RunScriptOnNodesException;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.util.OpenSocketFinder;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.statements.java.InstallJDK;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" }, singleThreaded = true)
public abstract class BaseComputeServiceLiveTest extends BaseComputeServiceContextLiveTest {

   protected String group;

   protected Predicate<HostAndPort> socketTester;
   protected OpenSocketFinder openSocketFinder;
   protected SortedSet<NodeMetadata> nodes;
   protected ComputeService client;

   protected Template template;
   protected Map<String, String> keyPair;

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      setServiceDefaults();
      if (group == null)
         group = checkNotNull(provider, "provider");
      // groups need to work with hyphens in them, so let's make sure there is
      // one!
      if (group.indexOf('-') == -1)
         group = group + "-";
      setupKeyPairForTest();
      super.setupContext();
      buildSocketTester();
   }

   public void setServiceDefaults() {

   }

   protected void setupKeyPairForTest() {
      keyPair = ComputeTestUtils.setupKeyPair();
   }


   protected void buildSocketTester() {
      SocketOpen socketOpen = view.utils().injector().getInstance(SocketOpen.class);
      socketTester = retry(socketOpen, 60, 1, SECONDS);
      // wait a maximum of 60 seconds for port 8080 to open.
      openSocketFinder = context.utils().injector().getInstance(OpenSocketFinder.class);
   }
   
   @Override
   protected void initializeContext() {
      super.initializeContext();
      client = view.getComputeService();
   }

   @Test(enabled = true, expectedExceptions = AuthorizationException.class)
   public void testCorrectAuthException() throws Exception {
      ComputeServiceContext context = null;
      try {
         Properties overrides = setupProperties();
         overrides.setProperty(provider + ".identity", "MOMMA");
         overrides.setProperty(provider + ".credential", "MIA");
         context = newBuilder()
               .modules(ImmutableSet.of(getLoggingModule(), credentialStoreModule))
               .overrides(overrides).build(ComputeServiceContext.class);
         context.getComputeService().listNodes();
      } catch (AuthorizationException e) {
         throw e;
      } catch (RuntimeException e) {
         e.printStackTrace();
         throw e;
      } finally {
         if (context != null)
            context.close();
      }
   }

   @Test(enabled = true)
   public void testImagesCache() throws Exception {
      client.listImages();
      long time = currentTimeMillis();
      client.listImages();
      long duration = currentTimeMillis() - time;
      assert duration < 1000 : format("%dms to get images", duration);
   }

   @Test(enabled = true, expectedExceptions = NoSuchElementException.class)
   public void testCorrectExceptionRunningNodesNotFound() throws Exception {
      client.runScriptOnNodesMatching(runningInGroup("zebras-are-awesome"), InstallJDK.fromOpenJDK());
   }

   public void testImageById() {
      Template defaultTemplate = view.getComputeService().templateBuilder().build();
      assertEquals(view.getComputeService().getImage(defaultTemplate.getImage().getId()), defaultTemplate.getImage());
   }
   
   // since surefire and eclipse don't otherwise guarantee the order, we are
   // starting this one alphabetically before create2nodes..
   @Test(enabled = true, dependsOnMethods = { "testCompareSizes" })
   public void testAScriptExecutionAfterBootWithBasicTemplate() throws Exception {
      String group = this.group + "r";
      try {
         client.destroyNodesMatching(inGroup(group));
      } catch (Exception e) {

      }

      TemplateOptions options = client.templateOptions().blockOnPort(22, 120);
      try {
         Set<? extends NodeMetadata> nodes = client.createNodesInGroup(group, 1, options);
         NodeMetadata node = get(nodes, 0);
         LoginCredentials good = node.getCredentials();
         assert good.identity != null : nodes;
         assert good.credential != null : nodes;

         for (Entry<? extends NodeMetadata, ExecResponse> response : client.runScriptOnNodesMatching(
               runningInGroup(group), "hostname",
               wrapInInitScript(false).runAsRoot(false).overrideLoginCredentials(good)).entrySet()) {
            checkResponseEqualsHostname(response.getValue(), response.getKey());
         }

         // test single-node execution
         ExecResponse response = client.runScriptOnNode(node.getId(), "hostname",
               wrapInInitScript(false).runAsRoot(false));
         checkResponseEqualsHostname(response, node);
         OperatingSystem os = node.getOperatingSystem();

         // test bad password
         tryBadPassword(group, good);

         runScriptWithCreds(group, os, good);

         checkNodes(nodes, group, "runScriptWithCreds");

         // test adding AdminAccess later changes the default boot user, in this
         // case to foo, with home dir /over/ridden/foo
         ListenableFuture<ExecResponse> future = client.submitScriptOnNode(node.getId(), AdminAccess.builder()
               .adminUsername("foo").adminHome("/over/ridden/foo").build(), nameTask("adminUpdate"));

         response = future.get(3, TimeUnit.MINUTES);

         assert response.getExitStatus() == 0 : node.getId() + ": " + response;

         node = client.getNodeMetadata(node.getId());
         // test that the node updated to the correct admin user!
         assertEquals(node.getCredentials().identity, "foo");
         assert node.getCredentials().credential != null : nodes;

         weCanCancelTasks(node);

         assert response.getExitStatus() == 0 : node.getId() + ": " + response;

         response = client.runScriptOnNode(node.getId(), "echo $USER", wrapInInitScript(false).runAsRoot(false));

         assert response.getOutput().trim().equals("foo") : node.getId() + ": " + response;

      } finally {
         client.destroyNodesMatching(inGroup(group));
      }
   }

   @Test(enabled = false)
   protected void tryBadPassword(String group, Credentials good) throws AssertionError {
      try {
         Map<? extends NodeMetadata, ExecResponse> responses = client.runScriptOnNodesMatching(
               runningInGroup(group),
               "echo I put a bad password",
               wrapInInitScript(false).runAsRoot(false).overrideLoginCredentials(
                     LoginCredentials.builder().user(good.identity).noPrivateKey().password("romeo").build()));
         assert responses.size() == 0 : "shouldn't pass with a bad password\n" + responses;
      } catch (AssertionError e) {
         throw e;
      } catch (RunScriptOnNodesException e) {
         assert Iterables.any(e.getNodeErrors().values(), Predicates.instanceOf(AuthorizationException.class)) : e
               + " not authexception!";
      }
   }

   @Test(enabled = false)
   public void weCanCancelTasks(NodeMetadata node) throws InterruptedException, ExecutionException {
      ListenableFuture<ExecResponse> future = client.submitScriptOnNode(node.getId(), "sleep 300",
            nameTask("sleeper").runAsRoot(false));
      ExecResponse response = null;
      try {
         response = future.get(1, TimeUnit.MILLISECONDS);
         fail(node.getId() + ": " + response);
      } catch (TimeoutException e) {
         assert !future.isDone();
         response = client.runScriptOnNode(node.getId(), "/tmp/init-sleeper status",
               wrapInInitScript(false).runAsRoot(false));
         assert !response.getOutput().trim().equals("") : node.getId() + ": " + response;
         future.cancel(true);
         response = client.runScriptOnNode(node.getId(), "/tmp/init-sleeper status",
               wrapInInitScript(false).runAsRoot(false));
         assert response.getOutput().trim().equals("") : node.getId() + ": " + response;
         try {
            future.get();
            fail(future.toString());
         } catch (CancellationException e1) {

         }
      }
   }

   protected void checkResponseEqualsHostname(ExecResponse execResponse, NodeMetadata node1) {
      assert execResponse.getOutput().trim().equals(node1.getHostname()) : node1 + ": " + execResponse;
   }

   @Test(enabled = true, dependsOnMethods = { "testImagesCache" })
   public void testTemplateMatch() throws Exception {
      template = buildTemplate(client.templateBuilder());
      Template toMatch = client.templateBuilder().imageId(template.getImage().getId()).build();
      assertEquals(toMatch.getImage(), template.getImage());
   }

   protected void checkHttpGet(NodeMetadata node) {
      ComputeTestUtils.checkHttpGet(view.utils().http(), node, 8080);
   }

   @Test(enabled = true, dependsOnMethods = "testConcurrentUseOfComputeServiceToCreateNodes")
   public void testCreateTwoNodesWithRunScript() throws Exception {
      try {
         client.destroyNodesMatching(inGroup(group));
      } catch (NoSuchElementException e) {

      }
      refreshTemplate();
      try {
         nodes = newTreeSet(client.createNodesInGroup(group, 2, template));
      } catch (RunNodesException e) {
         nodes = newTreeSet(concat(e.getSuccessfulNodes(), e.getNodeErrors().keySet()));
         throw e;
      }
      assertEquals(nodes.size(), 2);
      checkNodes(nodes, group, "bootstrap");
      NodeMetadata node1 = nodes.first();
      NodeMetadata node2 = nodes.last();
      // credentials aren't always the same
      // assertEquals(node1.getCredentials(), node2.getCredentials());

      assertLocationSameOrChild(checkNotNull(node1.getLocation(), "location of %s", node1), template.getLocation());
      assertLocationSameOrChild(checkNotNull(node2.getLocation(), "location of %s", node2), template.getLocation());
      checkImageIdMatchesTemplate(node1);
      checkImageIdMatchesTemplate(node2);
      checkOsMatchesTemplate(node1);
      checkOsMatchesTemplate(node2);
   }

   private Template refreshTemplate() {
      return template = addRunScriptToTemplate(buildTemplate(client.templateBuilder()));
   }

   protected static Template addRunScriptToTemplate(Template template) {
      template.getOptions().runScript(Statements.newStatementList(AdminAccess.standard(), InstallJDK.fromOpenJDK()));
      return template;
   }

   protected void checkImageIdMatchesTemplate(NodeMetadata node) {
      if (node.getImageId() != null)
         assertEquals(node.getImageId(), template.getImage().getId());
   }

   protected void checkOsMatchesTemplate(NodeMetadata node) {
      if (node.getOperatingSystem() != null)
         assert node.getOperatingSystem().getFamily().equals(template.getImage().getOperatingSystem().getFamily()) : String
               .format("expecting family %s but got %s", template.getImage().getOperatingSystem().getFamily(),
                     node.getOperatingSystem());
   }

   void assertLocationSameOrChild(Location test, Location expected) {
      if (!test.equals(expected)) {
         assertEquals(test.getParent().getId(), expected.getId());
      } else {
         assertEquals(test, expected);
      }
   }

   @Test(enabled = true, dependsOnMethods = "testCreateTwoNodesWithRunScript")
   public void testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired() throws Exception {
      initializeContext();

      Location existingLocation = Iterables.get(this.nodes, 0).getLocation();
      boolean existingLocationIsAssignable = Iterables.any(client.listAssignableLocations(),
            Predicates.equalTo(existingLocation));

      if (existingLocationIsAssignable) {
         getAnonymousLogger().info("creating another node based on existing nodes' location: " + existingLocation);
         template = addRunScriptToTemplate(client.templateBuilder().fromTemplate(template)
               .locationId(existingLocation.getId()).build());
      } else {
         refreshTemplate();
         getAnonymousLogger().info(
               format("%s is not assignable; using template's location %s as  ", existingLocation,
                     template.getLocation()));
      }

      Set<? extends NodeMetadata> nodes = client.createNodesInGroup(group, 1, template);
      assertEquals(nodes.size(), 1);
      checkNodes(nodes, group, "bootstrap");
      NodeMetadata node = Iterables.getOnlyElement(nodes);
      if (existingLocationIsAssignable)
         assertEquals(node.getLocation(), existingLocation);
      else
         this.assertLocationSameOrChild(checkNotNull(node.getLocation(), "location of %s", node), template.getLocation());
      checkOsMatchesTemplate(node);
      this.nodes.add(node);
   }

   @Test(enabled = true, dependsOnMethods = "testCompareSizes")
   public void testConcurrentUseOfComputeServiceToCreateNodes() throws Exception {
      final long timeoutMs = 20 * 60 * 1000;
      List<String> groups = Lists.newArrayList();
      List<ListenableFuture<NodeMetadata>> futures = Lists.newArrayList();
      ListeningExecutorService userExecutor = MoreExecutors.listeningDecorator(context.utils().userExecutor());

      try {
         for (int i = 0; i < 2; i++) {
            final int groupNum = i;
            final String group = "twin" + groupNum;
            groups.add(group);

            ListenableFuture<NodeMetadata> future = userExecutor.submit(new Callable<NodeMetadata>() {
               public NodeMetadata call() throws Exception {
                  NodeMetadata node = getOnlyElement(client.createNodesInGroup(group, 1, inboundPorts(22, 8080)
                           .blockOnPort(22, 300 + groupNum)));
                  getAnonymousLogger().info("Started node " + node.getId());
                  return node;
               }
            });
            futures.add(future);
         }

         ListenableFuture<List<NodeMetadata>> compoundFuture = Futures.allAsList(futures);
         compoundFuture.get(timeoutMs, TimeUnit.MILLISECONDS);

      } finally {
         for (String group : groups) {
            client.destroyNodesMatching(inGroup(group));
         }
      }
   }
   
   @Test(enabled = true, dependsOnMethods = "testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired")
   public void testCredentialsCache() throws Exception {
      initializeContext();
      for (NodeMetadata node : nodes)
         assert (view.utils().credentialStore().get("node#" + node.getId()) != null) : "credentials for " + node.getId();
   }

   protected Map<? extends NodeMetadata, ExecResponse> runScriptWithCreds(final String group, OperatingSystem os,
         LoginCredentials creds) throws RunScriptOnNodesException {
      return client.runScriptOnNodesMatching(runningInGroup(group), InstallJDK.fromOpenJDK(),
            overrideLoginCredentials(creds).nameTask("runScriptWithCreds"));
   }

   protected void checkNodes(Iterable<? extends NodeMetadata> nodes, String group, String taskName) throws IOException {
      for (NodeMetadata node : nodes) {
         assertNotNull(node.getProviderId());
         assertNotNull(node.getGroup());
         assertEquals(node.getGroup(), group);
         assertEquals(node.getStatus(), Status.RUNNING);
         Credentials fromStore = view.utils().credentialStore().get("node#" + node.getId());
         assertEquals(fromStore, node.getCredentials());
         assert node.getPublicAddresses().size() >= 1 || node.getPrivateAddresses().size() >= 1 : "no ips in" + node;
         assertNotNull(node.getCredentials());
         if (node.getCredentials().identity != null) {
            assertNotNull(node.getCredentials().identity);
            assertNotNull(node.getCredentials().credential);
            sshPing(node, taskName);
         }
      }
   }

   protected Template buildTemplate(TemplateBuilder templateBuilder) {
      return templateBuilder.build();
   }

   @Test(enabled = true, dependsOnMethods = "testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired")
   public void testGet() throws Exception {
      Map<String, ? extends NodeMetadata> metadataMap = newLinkedHashMap(uniqueIndex(
            filter(client.listNodesDetailsMatching(all()), and(inGroup(group), not(TERMINATED))),
            new Function<NodeMetadata, String>() {

               @Override
               public String apply(NodeMetadata from) {
                  return from.getId();
               }

            }));
      for (NodeMetadata node : nodes) {
         metadataMap.remove(node.getId());
         NodeMetadata metadata = client.getNodeMetadata(node.getId());
         assertEquals(metadata.getProviderId(), node.getProviderId());
         assertEquals(metadata.getGroup(), node.getGroup());
         assertLocationSameOrChild(checkNotNull(metadata.getLocation(), "location of %s", metadata), template.getLocation());
         checkImageIdMatchesTemplate(metadata);
         checkOsMatchesTemplate(metadata);
         assert (metadata.getStatus() == Status.RUNNING) : metadata;
         // due to DHCP the addresses can actually change in-between runs.
         assertEquals(metadata.getPrivateAddresses().size(), node.getPrivateAddresses().size(), format(
               "[%s] didn't match: [%s]", metadata.getPrivateAddresses(), node.getPrivateAddresses().size()));
         assertEquals(metadata.getPublicAddresses().size(), node.getPublicAddresses().size(), format(
               "[%s] didn't match: [%s]", metadata.getPublicAddresses(), node.getPublicAddresses().size()));
      }
      assertNodeZero(metadataMap.values());
   }

   protected void assertNodeZero(Collection<? extends NodeMetadata> metadataSet) {
      assert metadataSet.size() == 0 : format("nodes left in set: [%s] which didn't match set: [%s]", metadataSet,
            nodes);
   }

   @Test(enabled = true, dependsOnMethods = "testGet")
   public void testReboot() throws Exception {
      client.rebootNodesMatching(inGroup(group));// TODO test
      // validation
      testGet();
   }

   @Test(enabled = true, dependsOnMethods = "testReboot")
   public void testSuspendResume() throws Exception {
      client.suspendNodesMatching(inGroup(group));

      Set<? extends NodeMetadata> stoppedNodes = refreshNodes();

      assert Iterables.all(stoppedNodes, new Predicate<NodeMetadata>() {

         @Override
         public boolean apply(NodeMetadata input) {
            boolean returnVal = input.getStatus() == Status.SUSPENDED;
            if (!returnVal)
               getAnonymousLogger().warning(format("node %s in state %s%n", input.getId(), input.getStatus()));
            return returnVal;
         }

      }) : stoppedNodes;

      client.resumeNodesMatching(inGroup(group));
      testGet();
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   public void testListNodes() throws Exception {
      for (ComputeMetadata node : client.listNodes()) {
         assert node.getProviderId() != null : node;
         assert node.getLocation() != null : node;
         assertEquals(node.getType(), ComputeType.NODE);
      }
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   public void testGetNodesWithDetails() throws Exception {
      for (NodeMetadata node : client.listNodesDetailsMatching(all())) {
         assert node.getProviderId() != null : node;
         assert node.getLocation() != null : node;
         assertEquals(node.getType(), ComputeType.NODE);
         assert node instanceof NodeMetadata;
         NodeMetadata nodeMetadata = (NodeMetadata) node;
         assert nodeMetadata.getProviderId() != null : nodeMetadata;
         // nullable
         // assert nodeMetadata.getImage() != null : node;
         // user specified name is not always supported
         // assert nodeMetadata.getName() != null : nodeMetadata;
         if (nodeMetadata.getStatus() == Status.RUNNING) {
            assert nodeMetadata.getPublicAddresses() != null : nodeMetadata;
            assert nodeMetadata.getPublicAddresses().size() > 0 || nodeMetadata.getPrivateAddresses().size() > 0 : nodeMetadata;
            assertNotNull(nodeMetadata.getPrivateAddresses());
         }
      }
   }

   @Test(enabled = true, dependsOnMethods = { "testListNodes", "testGetNodesWithDetails" })
   public void testDestroyNodes() {
      int toDestroy = refreshNodes().size();
      Set<? extends NodeMetadata> destroyed = client.destroyNodesMatching(inGroup(group));
      assertEquals(toDestroy, destroyed.size());
      for (NodeMetadata node : filter(client.listNodesDetailsMatching(all()), inGroup(group))) {
         assert node.getStatus() == Status.TERMINATED : node;
         assert view.utils().credentialStore().get("node#" + node.getId()) == null : "credential should have been null for "
               + "node#" + node.getId();
      }
   }

   private Set<? extends NodeMetadata> refreshNodes() {
      return filter(client.listNodesDetailsMatching(all()), and(inGroup(group), not(TERMINATED)));
   }

   static class ServiceStats {
      long backgroundProcessMilliseconds;
      long socketOpenMilliseconds;

      @Override
      public String toString() {
         return format("[backgroundProcessMilliseconds=%s, socketOpenMilliseconds=%s]",
               backgroundProcessMilliseconds, socketOpenMilliseconds);
      }
   }

   protected ServiceStats trackAvailabilityOfProcessOnNode(Statement process, String processName, NodeMetadata node) {
      ServiceStats stats = new ServiceStats();
      Stopwatch watch = new Stopwatch().start();
      ExecResponse exec = client.runScriptOnNode(node.getId(), process, runAsRoot(false).wrapInInitScript(false));
      stats.backgroundProcessMilliseconds = watch.elapsedTime(TimeUnit.MILLISECONDS);
      watch.reset().start();
      
      HostAndPort socket = null;
      try {
         socket = openSocketFinder.findOpenSocketOnNode(node, 8080, 60, TimeUnit.SECONDS);
      } catch (NoSuchElementException e) {
         throw new NoSuchElementException(format("%s%n%s%s", e.getMessage(), exec.getOutput(), exec.getError()));
      }

      stats.socketOpenMilliseconds = watch.elapsedTime(TimeUnit.MILLISECONDS);

      getAnonymousLogger().info(format("<< %s on node(%s)[%s] %s", processName, node.getId(), socket, stats));
      return stats;
   }

   @Test(enabled = true)
   public void testCreateAndRunAService() throws Exception {

      String group = this.group + "s";
      try {
         client.destroyNodesMatching(inGroup(group));
      } catch (Exception e) {

      }

      try {
         createAndRunAServiceInGroup(group);
      } finally {
         client.destroyNodesMatching(inGroup(group));
      }

   }

   protected void createAndRunAServiceInGroup(String group) throws RunNodesException {
      // note that some cloud providers do not support mixed case tag names
      ImmutableMap<String, String> userMetadata = ImmutableMap.<String, String> of("test", group);
      
      ImmutableSet<String> tags = ImmutableSet. of(group);
      Stopwatch watch = new Stopwatch().start();
      NodeMetadata node = getOnlyElement(client.createNodesInGroup(group, 1,
            inboundPorts(22, 8080).blockOnPort(22, 300).userMetadata(userMetadata).tags(tags)));
      long createSeconds = watch.elapsedTime(TimeUnit.SECONDS);

      final String nodeId = node.getId();

      checkUserMetadataContains(node, userMetadata);
      checkTagsInNodeEquals(node, tags);

      getAnonymousLogger().info(
            format("<< available node(%s) os(%s) in %ss", node.getId(), node.getOperatingSystem(), createSeconds));

      watch.reset().start();

      client.runScriptOnNode(nodeId, JettyStatements.install(), nameTask("configure-jetty"));

      long configureSeconds = watch.elapsedTime(TimeUnit.SECONDS);

      getAnonymousLogger().info(
            format(
                  "<< configured node(%s) with %s and jetty %s in %ss",
                  nodeId,
                  exec(nodeId, "java -fullversion"),
                  exec(nodeId, JettyStatements.version()), configureSeconds));

      trackAvailabilityOfProcessOnNode(JettyStatements.start(), "start jetty", node);

      client.runScriptOnNode(nodeId, JettyStatements.stop(), runAsRoot(false).wrapInInitScript(false));

      trackAvailabilityOfProcessOnNode(JettyStatements.start(), "start jetty", node);
   }

   protected String exec(final String nodeId, String command) {
      return exec(nodeId, Statements.exec(command));
   }

   protected String exec(final String nodeId, Statement command) {
      return client.runScriptOnNode(nodeId, command, runAsRoot(false).wrapInInitScript(false)).getOutput().trim();
   }

   protected void checkUserMetadataContains(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      Map<String, String> missing = Maps.difference(node.getUserMetadata(), userMetadata).entriesOnlyOnRight();
      assert missing.isEmpty() : format("node userMetadata did not contain %s %s", missing, node);
   }

   protected void checkTagsInNodeEquals(NodeMetadata node, ImmutableSet<String> tags) {
      assert node.getTags().equals(tags) : format("node tags did not match %s %s", tags, node);
   }

   public void testListImages() throws Exception {
      for (Image image : client.listImages()) {
         assert image.getProviderId() != null : image;
         // image.getLocationId() can be null, if it is a location-free image
         assertEquals(image.getType(), ComputeType.IMAGE);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetAssignableLocations() throws Exception {
      for (Location location : client.listAssignableLocations()) {
         getAnonymousLogger().warning("location " + location);
         assert location.getId() != null : location;
         assert location != location.getParent() : location;
         assert location.getScope() != null : location;
         switch (location.getScope()) {
         case PROVIDER:
            assertProvider(location);
            break;
         case REGION:
            assertProvider(location.getParent());
            break;
         case ZONE:
            Location provider = location.getParent().getParent();
            // zone can be a direct descendant of provider
            if (provider == null)
               provider = location.getParent();
            assertProvider(provider);
            break;
         case HOST:
            Location provider2 = location.getParent().getParent().getParent();
            // zone can be a direct descendant of provider
            if (provider2 == null)
               provider2 = location.getParent().getParent();
            assertProvider(provider2);
            break;
         }
      }
   }

   protected int nonBlockDurationSeconds = 30;

   public void testOptionToNotBlock() throws Exception {
      String group = this.group + "block";
      try {
         client.destroyNodesMatching(inGroup(group));
      } catch (Exception e) {

      }
      // no inbound ports
      TemplateOptions options = client.templateOptions().blockUntilRunning(false).inboundPorts();
      try {
         long time = currentTimeMillis();
         Set<? extends NodeMetadata> nodes = client.createNodesInGroup(group, 1, options);
         NodeMetadata node = getOnlyElement(nodes);
         assert node.getStatus() != Status.RUNNING : node;
         long duration = (currentTimeMillis() - time) / 1000;
         assert duration < nonBlockDurationSeconds : format("duration(%d) longer than expected(%d) seconds! ",
               duration, nonBlockDurationSeconds);
      } finally {
         client.destroyNodesMatching(inGroup(group));
      }
   }

   private void assertProvider(Location provider) {
      assertEquals(provider.getScope(), LocationScope.PROVIDER);
      assertEquals(provider.getParent(), null);
   }

   public void testListSizes() throws Exception {
      for (Hardware hardware : client.listHardwareProfiles()) {
         assert hardware.getProviderId() != null : hardware;
         assert getCores(hardware) > 0 : hardware;
         assert hardware.getVolumes().size() >= 0 : hardware;
         assert hardware.getRam() > 0 : hardware;
         assertEquals(hardware.getType(), ComputeType.HARDWARE);
      }
   }

   @Test(enabled = true)
   public void testCompareSizes() throws Exception {
      Hardware defaultSize = client.templateBuilder().build().getHardware();

      Hardware smallest = client.templateBuilder().smallest().build().getHardware();
      Hardware fastest = client.templateBuilder().fastest().build().getHardware();
      Hardware biggest = client.templateBuilder().biggest().build().getHardware();

      getAnonymousLogger().info("smallest " + smallest);
      getAnonymousLogger().info("fastest " + fastest);
      getAnonymousLogger().info("biggest " + biggest);

      assertEquals(defaultSize, smallest);

      assert getCores(smallest) <= getCores(fastest) : format("%s ! <= %s", smallest, fastest);
      assert getCores(biggest) <= getCores(fastest) : format("%s ! <= %s", biggest, fastest);

      assert biggest.getRam() >= fastest.getRam() : format("%s ! >= %s", biggest, fastest);
      assert biggest.getRam() >= smallest.getRam() : format("%s ! >= %s", biggest, smallest);

      assert getCores(fastest) >= getCores(biggest) : format("%s ! >= %s", fastest, biggest);
      assert getCores(fastest) >= getCores(smallest) : format("%s ! >= %s", fastest, smallest);
   }

   private void sshPing(NodeMetadata node, String taskName) throws IOException {
      for (int i = 0; i < 5; i++) {// retry loop TODO replace with predicate.
         try {
            doCheckJavaIsInstalledViaSsh(node, taskName);
            return;
         } catch (SshException e) {
            try {
               Thread.sleep(10 * 1000);
            } catch (InterruptedException e1) {
            }
            continue;
         }
      }
   }

   protected void doCheckJavaIsInstalledViaSsh(NodeMetadata node, String taskName) throws IOException {
      SshClient ssh = view.utils().sshForNode().apply(node);
      try {
         ssh.connect();
         ExecResponse hello = ssh.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
         ExecResponse exec = ssh.exec("java -version");
         assert exec.getError().indexOf("OpenJDK") != -1 || exec.getOutput().indexOf("OpenJDK") != -1 : exec
               + "\n"
               + ssh.exec("cat /tmp/" + taskName + "/" + taskName + ".sh /tmp/" + taskName + "/stdout.log /tmp/"
                     + taskName + "/stderr.log");
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDownContext() {
      if (nodes != null) {
         testDestroyNodes();
      }
      super.tearDownContext();
   }

   @Override
   protected Module getSshModule() {
      throw new IllegalStateException("ssh is required for this test!");
   }
}
