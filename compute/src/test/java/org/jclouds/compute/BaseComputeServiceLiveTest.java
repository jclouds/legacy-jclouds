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
package org.jclouds.compute;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Throwables.getRootCause;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newTreeSet;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.logging.Logger.getAnonymousLogger;
import static org.jclouds.compute.ComputeTestUtils.buildScript;
import static org.jclouds.compute.RunScriptData.installAdminUserJBossAndOpenPorts;
import static org.jclouds.compute.RunScriptData.startJBoss;
import static org.jclouds.compute.options.RunScriptOptions.Builder.nameTask;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.jclouds.compute.options.TemplateOptions.Builder.inboundPorts;
import static org.jclouds.compute.options.TemplateOptions.Builder.overrideCredentialsWith;
import static org.jclouds.compute.options.TemplateOptions.Builder.runAsRoot;
import static org.jclouds.compute.predicates.NodePredicates.TERMINATED;
import static org.jclouds.compute.predicates.NodePredicates.all;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.jclouds.compute.predicates.NodePredicates.runningInGroup;
import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jclouds.Constants;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.util.Strings2;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" }, singleThreaded = true)
public abstract class BaseComputeServiceLiveTest {

   protected String group;

   protected Predicate<IPSocket> socketTester;
   protected Predicate<IPSocket> preciseSocketTester;
   protected SortedSet<NodeMetadata> nodes;
   protected ComputeServiceContext context;
   protected ComputeService client;

   protected Template template;
   protected Map<String, String> keyPair;

   protected String provider;
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;


   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      if (credential != null)
         overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "integration", "live" })
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      setServiceDefaults();
      if (group == null)
         group = checkNotNull(provider, "provider");
      if (group.indexOf('-') == -1)
         group = group + "-";
      setupCredentials();
      setupKeyPairForTest();
      initializeContextAndClient();
      buildSocketTester();
   }

   public void setServiceDefaults() {

   }

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected void setupKeyPairForTest() throws FileNotFoundException, IOException {
      keyPair = ComputeTestUtils.setupKeyPair();
   }

   private void initializeContextAndClient() throws IOException {
      if (context != null)
         context.close();
      Properties props = setupProperties();
      context = new ComputeServiceContextFactory(setupRestProperties()).createContext(provider, ImmutableSet.of(
               new Log4JLoggingModule(), getSshModule()), props);
      client = context.getComputeService();
   }

   protected Properties setupRestProperties() {
      return RestContextFactory.getPropertiesFromResource("/rest.properties");
   }

   protected void buildSocketTester() {
      SocketOpen socketOpen = Guice.createInjector(getSshModule()).getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<IPSocket>(socketOpen, 60, 1, TimeUnit.SECONDS);
      // wait a maximum of 60 seconds for port 8080 to open.
      long maxWait = TimeUnit.SECONDS.toMillis(60);
      long interval = 50;
      // get more precise than default socket tester
      preciseSocketTester = new RetryablePredicate<IPSocket>(socketOpen, maxWait, interval, interval,
               TimeUnit.MILLISECONDS);
   }

   abstract protected Module getSshModule();

   // wait up to 5 seconds for an auth exception
   @Test(enabled = true, expectedExceptions = AuthorizationException.class)
   public void testCorrectAuthException() throws Exception {
      ComputeServiceContext context = null;
      try {
         context = new ComputeServiceContextFactory(setupRestProperties()).createContext(provider, "MOMMA", "MIA",
                  ImmutableSet.<Module> of(new Log4JLoggingModule()));
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
      client.runScriptOnNodesMatching(runningInGroup("zebras-are-awesome"), buildScript(new OperatingSystem.Builder()
               .family(OsFamily.UBUNTU).description("ffoo").build()));
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
         Credentials good = node.getCredentials();
         assert good.identity != null : nodes;
         assert good.credential != null : nodes;

         for (Entry<? extends NodeMetadata, ExecResponse> response : client.runScriptOnNodesMatching(
                  runningInGroup(group), Statements.exec("hostname"),
                  overrideCredentialsWith(good).wrapInInitScript(false).runAsRoot(false)).entrySet()){
            checkResponseEqualsHostname(response.getValue(), response.getKey());
         }
         
         // test single-node execution
         ExecResponse response = client.runScriptOnNode(node.getId(), "hostname", wrapInInitScript(false)
                  .runAsRoot(false));
         checkResponseEqualsHostname(response, node);
         OperatingSystem os = node.getOperatingSystem();
         
         // test bad password
         try {
            Map<? extends NodeMetadata, ExecResponse> responses = runScriptWithCreds(group, os, new Credentials(
                     good.identity, "romeo"));
            assert responses.size() == 0 : "shouldn't pass with a bad password\n" + responses;
         } catch (RunScriptOnNodesException e) {
            assert getRootCause(e).getMessage().contains("Auth fail") : e;
         }

         runScriptWithCreds(group, os, good);

         checkNodes(nodes, group);

         // test adding AdminAccess later changes the default boot user, in this case to foo
         response = client.runScriptOnNode(node.getId(), AdminAccess.builder().adminUsername("foo").build(), nameTask("adminUpdate"));
         
         response = client.runScriptOnNode(node.getId(), "echo $USER", wrapInInitScript(false)
                  .runAsRoot(false));
         
         assert response.getOutput().trim().equals("foo") : node.getId() + ": " + response;
         
      } finally {
         client.destroyNodesMatching(inGroup(group));
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
      ComputeTestUtils.checkHttpGet(context.utils().http(), node, 8080);
   }

   @Test(enabled = true, dependsOnMethods = "testCompareSizes")
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
      checkNodes(nodes, group);
      NodeMetadata node1 = nodes.first();
      NodeMetadata node2 = nodes.last();
      // credentials aren't always the same
      // assertEquals(node1.getCredentials(), node2.getCredentials());

      assertLocationSameOrChild(node1.getLocation(), template.getLocation());
      assertLocationSameOrChild(node2.getLocation(), template.getLocation());
      checkImageIdMatchesTemplate(node1);
      checkImageIdMatchesTemplate(node2);
      checkOsMatchesTemplate(node1);
      checkOsMatchesTemplate(node2);
   }

   private Template refreshTemplate() {
      return template = addRunScriptToTemplate(buildTemplate(client.templateBuilder()));
   }

   protected static Template addRunScriptToTemplate(Template template) {
      template.getOptions().runScript(
               Statements.newStatementList(AdminAccess.standard(),
                        buildScript(template.getImage().getOperatingSystem())));
      return template;
   }

   protected void checkImageIdMatchesTemplate(NodeMetadata node) {
      if (node.getImageId() != null)
         assertEquals(node.getImageId(), template.getImage().getId());
   }

   protected void checkOsMatchesTemplate(NodeMetadata node) {
      if (node.getOperatingSystem() != null)
         assert node.getOperatingSystem().getFamily().equals(template.getImage().getOperatingSystem().getFamily()) : String
                  .format("expecting family %s but got %s", template.getImage().getOperatingSystem().getFamily(), node
                           .getOperatingSystem());
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
      initializeContextAndClient();

      Location existingLocation = Iterables.get(this.nodes, 0).getLocation();
      boolean existingLocationIsAssignable = Iterables.any(client.listAssignableLocations(), Predicates
               .equalTo(existingLocation));

      if (existingLocationIsAssignable) {
         getAnonymousLogger().info("creating another node based on existing nodes' location: " + existingLocation);
         template = addRunScriptToTemplate(client.templateBuilder().fromTemplate(template).locationId(
                  existingLocation.getId()).build());
      } else {
         refreshTemplate();
         getAnonymousLogger().info(
                  format("%s is not assignable; using template's location %s as  ", existingLocation, template
                           .getLocation()));
      }

      Set<? extends NodeMetadata> nodes = client.createNodesInGroup(group, 1, template);
      assertEquals(nodes.size(), 1);
      checkNodes(nodes, group);
      NodeMetadata node = Iterables.getOnlyElement(nodes);
      if (existingLocationIsAssignable)
         assertEquals(node.getLocation(), existingLocation);
      else
         this.assertLocationSameOrChild(node.getLocation(), template.getLocation());
      checkOsMatchesTemplate(node);
      this.nodes.add(node);
   }

   @Test(enabled = true, dependsOnMethods = "testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired")
   public void testCredentialsCache() throws Exception {
      initializeContextAndClient();
      for (NodeMetadata node : nodes)
         assert (context.getCredentialStore().get("node#" + node.getId()) != null) : "credentials for " + node.getId();
   }

   protected Map<? extends NodeMetadata, ExecResponse> runScriptWithCreds(final String group, OperatingSystem os,
            Credentials creds) throws RunScriptOnNodesException {
      try {
         return client.runScriptOnNodesMatching(runningInGroup(group), buildScript(os), overrideCredentialsWith(creds)
                  .nameTask("runScriptWithCreds"));
      } catch (SshException e) {
         throw e;
      }
   }

   protected void checkNodes(Iterable<? extends NodeMetadata> nodes, String group) throws IOException {
      for (NodeMetadata node : nodes) {
         assertNotNull(node.getProviderId());
         assertNotNull(node.getGroup());
         assertEquals(node.getGroup(), group);
         assertEquals(node.getState(), NodeState.RUNNING);
         Credentials fromStore = context.getCredentialStore().get("node#" + node.getId());
         assertEquals(fromStore, node.getCredentials());
         assert node.getPublicAddresses().size() >= 1 || node.getPrivateAddresses().size() >= 1 : "no ips in" + node;
         assertNotNull(node.getCredentials());
         if (node.getCredentials().identity != null) {
            assertNotNull(node.getCredentials().identity);
            assertNotNull(node.getCredentials().credential);
            sshPing(node);
         }
      }
   }

   protected Template buildTemplate(TemplateBuilder templateBuilder) {
      return templateBuilder.build();
   }

   @Test(enabled = true, dependsOnMethods = "testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired")
   public void testGet() throws Exception {
      Map<String, ? extends NodeMetadata> metadataMap = newLinkedHashMap(uniqueIndex(filter(client
               .listNodesDetailsMatching(all()), and(inGroup(group), not(TERMINATED))),
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
         assertLocationSameOrChild(metadata.getLocation(), template.getLocation());
         checkImageIdMatchesTemplate(metadata);
         checkOsMatchesTemplate(metadata);
         assert (metadata.getState() == NodeState.RUNNING) : metadata;
         // due to DHCP the addresses can actually change in-between runs.
         assertEquals(metadata.getPrivateAddresses().size(), node.getPrivateAddresses().size());
         assertEquals(metadata.getPublicAddresses().size(), node.getPublicAddresses().size());
      }
      assertNodeZero(metadataMap.values());
   }

   protected void assertNodeZero(Collection<? extends NodeMetadata> metadataSet) {
      assert metadataSet.size() == 0 : format("nodes left in set: [%s] which didn't match set: [%s]",
               metadataSet, nodes);
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
            boolean returnVal = input.getState() == NodeState.SUSPENDED;
            if (!returnVal)
               getAnonymousLogger().warning(
                        format("node %s in state %s%n", input.getId(), input.getState()));
            return returnVal;
         }

      }) : stoppedNodes;

      client.resumeNodesMatching(inGroup(group));
      testGet();
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   public void testListNodes() throws Exception {
      for (ComputeMetadata node : client.listNodes()) {
         assert node.getProviderId() != null;
         assert node.getLocation() != null;
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
         if (nodeMetadata.getState() == NodeState.RUNNING) {
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
         assert node.getState() == NodeState.TERMINATED : node;
         assertEquals(context.getCredentialStore().get("node#" + node.getId()), null);
      }
   }

   private Set<? extends NodeMetadata> refreshNodes() {
      return filter(client.listNodesDetailsMatching(all()), and(inGroup(group), not(TERMINATED)));
   }

   static class ServiceStats {
      long backgroundProcessSeconds;
      long socketOpenMilliseconds;
      long reportedStartupTimeMilliseconds;

      @Override
      public String toString() {
         return String.format(
                  "[backgroundProcessSeconds=%s, socketOpenMilliseconds=%s, reportedStartupTimeMilliseconds=%s]",
                  backgroundProcessSeconds, socketOpenMilliseconds, reportedStartupTimeMilliseconds);
      }
   }

   protected ServiceStats trackAvailabilityOfProcessOnNode(Supplier<ExecResponse> bgProcess, String processName,
            NodeMetadata node, Pattern parseReported) {
      ServiceStats stats = new ServiceStats();
      long startSeconds = currentTimeMillis();

      ExecResponse exec = bgProcess.get();
      stats.backgroundProcessSeconds = (currentTimeMillis() - startSeconds) / 1000;

      IPSocket socket = new IPSocket(Iterables.get(node.getPublicAddresses(), 0), 8080);
      assert preciseSocketTester.apply(socket) : String.format("failed to open socket %s on node %s", socket, node);
      stats.socketOpenMilliseconds = currentTimeMillis() - startSeconds;

      exec = client.runScriptOnNode(node.getId(), "./" + processName + " tail", runAsRoot(false)
               .wrapInInitScript(false));

      Matcher matcher = parseReported.matcher(exec.getOutput());
      if (matcher.find())
         stats.reportedStartupTimeMilliseconds = new Long(matcher.group(1));

      getAnonymousLogger().info(format("<< %s on node(%s) %s", bgProcess, node.getId(), stats));
      return stats;
   }

   // started in 6462ms -
   public static final Pattern JBOSS_PATTERN = Pattern.compile("started in ([0-9]+)ms -");

   protected ServiceStats trackAvailabilityOfJBossProcessOnNode(Supplier<ExecResponse> startProcess, NodeMetadata node) {
      return trackAvailabilityOfProcessOnNode(startProcess, "jboss", node, JBOSS_PATTERN);
   }

   @Test(enabled = true)
   public void testCreateAndRunAService() throws Exception {

      String group = this.group + "s";
      final String configuration = Strings2.toStringAndClose(RunScriptData.class
               .getResourceAsStream("/standalone-basic.xml"));
      try {
         client.destroyNodesMatching(inGroup(group));
      } catch (Exception e) {

      }

      try {
         long startSeconds = currentTimeMillis();
         NodeMetadata node = getOnlyElement(client.createNodesInGroup(group, 1, inboundPorts(22, 8080).blockOnPort(22,
                  300)));
         final String nodeId = node.getId();
         long createSeconds = (currentTimeMillis() - startSeconds) / 1000;

         getAnonymousLogger().info(
                  format("<< available node(%s) os(%s) in %ss", node.getId(), node.getOperatingSystem(), createSeconds));

         startSeconds = currentTimeMillis();

         // note this is a dependency on the template resolution so we have the right process per
         // operating system.  moreover, we wish this to run as root, so that it can change ip
         // tables rules and setup our admin user
         client.runScriptOnNode(nodeId, installAdminUserJBossAndOpenPorts(node.getOperatingSystem()),
                  nameTask("configure-jboss"));

         long configureSeconds = (currentTimeMillis() - startSeconds) / 1000;

         getAnonymousLogger().info(format("<< configured node(%s) in %ss", nodeId, configureSeconds));

         trackAvailabilityOfJBossProcessOnNode(new Supplier<ExecResponse>() {

            @Override
            public ExecResponse get() {
               return client.runScriptOnNode(nodeId, startJBoss(configuration), runAsRoot(false).blockOnComplete(false)
                        .nameTask("jboss"));
            }

            @Override
            public String toString() {
               return "initial start of jboss";
            }

         }, node);

         client.runScriptOnNode(nodeId, "./jboss stop", runAsRoot(false).wrapInInitScript(false));

         trackAvailabilityOfJBossProcessOnNode(new Supplier<ExecResponse>() {

            @Override
            public ExecResponse get() {
               return client.runScriptOnNode(nodeId, "./jboss start", runAsRoot(false).wrapInInitScript(false));
            }

            @Override
            public String toString() {
               return "warm start of jboss";
            }

         }, node);

      } finally {
         client.destroyNodesMatching(inGroup(group));
      }

   }

   @Test(enabled = true/* , dependsOnMethods = "testCompareSizes" */)
   public void testTemplateOptions() throws Exception {
      TemplateOptions options = new TemplateOptions().withMetadata();
      Template t = client.templateBuilder().smallest().options(options).build();
      assert t.getOptions().isIncludeMetadata() : "The metadata option should be 'true' " + "for the created template";
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
         assert node.getState() != NodeState.RUNNING;
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

   private void sshPing(NodeMetadata node) throws IOException {
      for (int i = 0; i < 5; i++) {// retry loop TODO replace with predicate.
         try {
            doCheckJavaIsInstalledViaSsh(node);
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

   protected void doCheckJavaIsInstalledViaSsh(NodeMetadata node) throws IOException {
      SshClient ssh = context.utils().sshForNode().apply(node);
      try {
         ssh.connect();
         ExecResponse hello = ssh.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
         ExecResponse exec = ssh.exec("java -version");
         assert exec.getError().indexOf("1.6") != -1 || exec.getOutput().indexOf("1.6") != -1 : exec + "\n"
                  + ssh.exec("cat /tmp/bootstrap/stdout.log /tmp/bootstrap/stderr.log");
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   @AfterTest
   protected void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      if (nodes != null) {
         testDestroyNodes();
      }
      context.close();
   }

}
