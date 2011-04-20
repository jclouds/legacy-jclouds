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
package org.jclouds.openstack.nova.live;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Module;
import org.jclouds.Constants;
import org.jclouds.compute.*;
import org.jclouds.compute.domain.*;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.openstack.nova.NovaAsyncClient;
import org.jclouds.openstack.nova.NovaClient;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.ssh.jsch.JschSshClient;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Throwables.getRootCause;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newTreeSet;
import static org.jclouds.compute.ComputeTestUtils.buildScript;
import static org.jclouds.compute.options.TemplateOptions.Builder.blockOnComplete;
import static org.jclouds.compute.options.TemplateOptions.Builder.overrideCredentialsWith;
import static org.jclouds.compute.predicates.NodePredicates.*;
import static org.jclouds.compute.predicates.NodePredicates.all;
import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.jclouds.openstack.nova.live.PropertyHelper.overridePropertyFromSystemProperty;
import static org.jclouds.openstack.nova.live.PropertyHelper.setupKeyPair;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Generally disabled, as it incurs higher fees.
 *
 * @author Adrian Cole
 */
@Test(groups = "novalive", enabled = true, sequential = true)
public class NovaComputeServiceLiveTest {

   protected String group;

   protected RetryablePredicate<IPSocket> socketTester;
   protected SortedSet<NodeMetadata> nodes;
   protected ComputeServiceContext context;
   protected ComputeService computeService;

   protected Template template;
   protected Map<String, String> keyPair;

   protected String provider;
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   private Properties overrides;

   public NovaComputeServiceLiveTest() {
      provider = "nova";
   }


   protected void setupCredentials(Properties properties) {
      identity = checkNotNull(properties.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = checkNotNull(properties.getProperty("test." + provider + ".credential"), "test." + provider
            + ".credential");
      endpoint = properties.getProperty("test." + provider + ".endpoint");
      apiversion = properties.getProperty("test." + provider + ".apiversion");
   }

   protected void updateProperties(final Properties properties) {
      properties.setProperty(provider + ".identity", identity);
      properties.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         properties.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         properties.setProperty(provider + ".apiversion", apiversion);
   }


   protected Properties setupProperties() throws IOException {
      Properties overrides = new Properties();
      overrides.load(this.getClass().getResourceAsStream("/test.properties"));

      overridePropertyFromSystemProperty(overrides, "test." + provider + ".endpoint");
      overridePropertyFromSystemProperty(overrides, "test." + provider + ".apiversion");
      overridePropertyFromSystemProperty(overrides, "test." + provider + ".identity");
      overridePropertyFromSystemProperty(overrides, "test." + provider + ".credential");
      overridePropertyFromSystemProperty(overrides, "test.ssh.keyfile.public");
      overridePropertyFromSystemProperty(overrides, "test.ssh.keyfile.private");
      overridePropertyFromSystemProperty(overrides, "test.initializer");
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");

      return overrides;
   }

   protected Properties setupRestProperties() {
      return RestContextFactory.getPropertiesFromResource("/rest.properties");
   }

   private void initializeContextAndClient(Properties properties) throws IOException {
      if (context != null)
         context.close();
      context = new ComputeServiceContextFactory(setupRestProperties()).createContext(provider, ImmutableSet.of(
            new SLF4JLoggingModule(), getSshModule()), properties);
      computeService = context.getComputeService();
   }


   @BeforeGroups(groups = {"novalive"})
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      if (group == null)
         group = checkNotNull(provider, "provider");
      if (group.indexOf('-') == -1)
         group = group + "-";
      Properties properties = setupProperties();
      setupCredentials(properties);
      updateProperties(properties);
      overrides = properties;
      keyPair = setupKeyPair(properties);
      initializeContextAndClient(properties);
      buildSocketTester();
   }

   protected void buildSocketTester() {
      SocketOpen socketOpen = Guice.createInjector(getSshModule()).getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<IPSocket>(socketOpen, 60, 1, TimeUnit.SECONDS);
   }


   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   @Test
   public void testAssignability() throws Exception {
      @SuppressWarnings("unused")
      RestContext<NovaClient, NovaAsyncClient> tmContext = new ComputeServiceContextFactory()
            .createContext(provider, identity, credential, Collections.singleton(new JschSshClientModule()), overrides).getProviderSpecificContext();
   }


   protected void checkNodes(Iterable<? extends NodeMetadata> nodes, String tag) throws IOException {
      _checkNodes(nodes, tag);

      for (NodeMetadata node : nodes) {
         assertEquals(node.getLocation().getScope(), LocationScope.HOST);
      }
   }

   protected void _checkNodes(Iterable<? extends NodeMetadata> nodes, String group) throws IOException {
      for (NodeMetadata node : nodes) {
         assertNotNull(node.getProviderId());
         assertNotNull(node.getGroup());
         assertEquals(node.getGroup(), group);
         assertEquals(node.getState(), NodeState.RUNNING);
         Credentials fromStore = context.getCredentialStore().get("node#" + node.getId());
         assertEquals(fromStore, node.getCredentials());
         assert node.getPublicAddresses().size() >= 1 || node.getPrivateAddresses().size() >= 1 : "no ips in" + node;
//         assertNotNull(node.getCredentials());
//         if (node.getCredentials().identity != null) {
//            assertNotNull(node.getCredentials().identity);
//            assertNotNull(node.getCredentials().credential);
//            doCheckJavaIsInstalledViaSsh(node);
//         }
      }
   }


   @Test(enabled = true, expectedExceptions = AuthorizationException.class)
   public void testCorrectAuthException() throws Exception {
      ComputeServiceContext context = null;
      try {
         context = new ComputeServiceContextFactory(setupRestProperties()).createContext(provider, "MOMMA", "MIA", ImmutableSet
               .<Module>of(new SLF4JLoggingModule()), overrides);
         context.getComputeService().listNodes();
      } finally {
         if (context != null)
            context.close();
      }
   }

   @Test(enabled = true)
   public void testImagesCache() throws Exception {
      computeService.listImages();
      long time = System.currentTimeMillis();
      computeService.listImages();
      long duration = System.currentTimeMillis() - time;
      assert duration < 1000 : String.format("%dms to get images", duration);
   }

   @Test(enabled = true, expectedExceptions = NoSuchElementException.class)
   public void testCorrectExceptionRunningNodesNotFound() throws Exception {
      computeService.runScriptOnNodesMatching(runningInGroup("zebras-are-awesome"), buildScript(new OperatingSystem.Builder()
            .family(OsFamily.UBUNTU).description("ffoo").build()));
   }

   // since surefire and eclipse don't otherwise guarantee the order, we are
   // starting this one alphabetically before create2nodes..
   private String awaitForPublicAddressAssigned(String nodeId) throws InterruptedException {
      while (true) {
         Set<String> addresses = computeService.getNodeMetadata(nodeId).getPublicAddresses();
         System.out.println(addresses);
         if (addresses != null)
            if (!addresses.isEmpty()) return addresses.iterator().next();
         Thread.sleep(1000);
      }
   }

   private void awaitForSshPort(String address, Credentials credentials) throws URISyntaxException {
      IPSocket socket = new IPSocket(address, 22);

      JschSshClient ssh = new JschSshClient(
            new BackoffLimitedRetryHandler(), socket, 10000, credentials.identity, null, credentials.credential.getBytes());
      while (true) {
         try {
            System.out.println("ping: " + socket);
            ssh.connect();
            return;
         } catch (SshException ignore) {
         }
      }
   }


   @Test(enabled = true)
   public void testAScriptExecutionAfterBootWithBasicTemplate() throws Exception {
      String group = this.group + "r";

      computeService.destroyNodesMatching(inGroup(group));

      Template template = getDefaultTemplateBuilder().options(
            computeService.templateOptions()
                  .overrideCredentialsWith(new Credentials("root", keyPair.get("private")))
                  .blockUntilRunning(true))
            .build();

      try {
         Set<? extends NodeMetadata> nodes = computeService.createNodesInGroup(group, 1, template);

         System.out.println("==================================================");
         System.out.println("================ Created       ===================");

         String address = awaitForPublicAddressAssigned(get(nodes, nodes.size() - 1).getId());
         awaitForSshPort(address, new Credentials("root", keyPair.get("private")));

         OperatingSystem os = get(nodes, 0).getOperatingSystem();
         try {
            Map<? extends NodeMetadata, ExecResponse> responses = runJavaInstallationScriptWithCreds(group, os, new Credentials(
                  "root", "romeo"));
            assert false : "shouldn't pass with a bad password\n" + responses;
         } catch (RunScriptOnNodesException ignore) {
            if (!getRootCause(ignore).getMessage().contains("Auth fail")) throw ignore;
         }

         System.out.println("==================================================");
         System.out.println("================ Auth failed       ===================");

         for (Map.Entry<? extends NodeMetadata, ExecResponse> response : computeService.runScriptOnNodesMatching(
               runningInGroup(group), Statements.exec("echo hello"),
               overrideCredentialsWith(new Credentials("root", keyPair.get("private"))).wrapInInitScript(false).runAsRoot(false)).entrySet())
            assert response.getValue().getOutput().trim().equals("hello") : response.getKey() + ": "
                  + response.getValue();

         System.out.println("==================================================");
         System.out.println("================ Script       ===================");

         //TODO runJavaInstallationScriptWithCreds(group, os, new Credentials("root", keyPair.get("private")));
         //no response? if os is null (ZYPPER)

         checkNodes(nodes, group);

         Credentials good = nodes.iterator().next().getCredentials();
         //TODO check good is being private key .overrideCredentialsWith
         //TODO test for .blockOnPort

      } finally {
         computeService.destroyNodesMatching(inGroup(group));
      }
   }

   @Test(enabled = true, dependsOnMethods = {"testImagesCache"})
   public void testTemplateMatch() throws Exception {
      template = buildTemplate(computeService.templateBuilder());
      Template toMatch = computeService.templateBuilder().imageId(template.getImage().getId()).build();
      assertEquals(toMatch.getImage(), template.getImage());
   }

   protected void checkHttpGet(NodeMetadata node) {
      ComputeTestUtils.checkHttpGet(context.utils().http(), node, 8080);
   }

   @Test(enabled = true, dependsOnMethods = "testCompareSizes")
   public void testCreateTwoNodesWithRunScript() throws Exception {
      try {
         computeService.destroyNodesMatching(inGroup(group));
      } catch (NoSuchElementException e) {

      }
      refreshTemplate();
      try {
         nodes = newTreeSet(computeService.createNodesInGroup(group, 2, template));
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

   private void refreshTemplate() {
      template = buildTemplate(computeService.templateBuilder());

      template.getOptions().installPrivateKey(keyPair.get("private")).authorizePublicKey(keyPair.get("public"))
            .runScript(buildScript(template.getImage().getOperatingSystem()));
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
      initializeContextAndClient(overrides);
      refreshTemplate();
      TreeSet<NodeMetadata> nodes = newTreeSet(computeService.createNodesInGroup(group, 1, template));
      checkNodes(nodes, group);
      NodeMetadata node = nodes.first();
      this.nodes.add(node);
      assertEquals(nodes.size(), 1);
      assertLocationSameOrChild(node.getLocation(), template.getLocation());
      checkOsMatchesTemplate(node);
   }

   @Test(enabled = true, dependsOnMethods = "testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired")
   public void testCredentialsCache() throws Exception {
      initializeContextAndClient(overrides);
      for (NodeMetadata node : nodes)
         assert (context.getCredentialStore().get("node#" + node.getId()) != null) : "credentials for " + node.getId();
   }

   protected Map<? extends NodeMetadata, ExecResponse> runJavaInstallationScriptWithCreds(final String group, OperatingSystem os,
                                                                                          Credentials creds) throws RunScriptOnNodesException {
      return computeService.runScriptOnNodesMatching(runningInGroup(group), buildScript(os), overrideCredentialsWith(creds)
            .nameTask("runJavaInstallationScriptWithCreds"));

   }


   protected Template buildTemplate(TemplateBuilder templateBuilder) {
      return templateBuilder.build();
   }

   @Test(enabled = true, dependsOnMethods = "testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired")
   public void testGet() throws Exception {
      Map<String, ? extends NodeMetadata> metadataMap = newLinkedHashMap(uniqueIndex(filter(computeService
            .listNodesDetailsMatching(all()), and(inGroup(group), not(TERMINATED))),
            new Function<NodeMetadata, String>() {

               @Override
               public String apply(NodeMetadata from) {
                  return from.getId();
               }

            }));
      for (NodeMetadata node : nodes) {
         metadataMap.remove(node.getId());
         NodeMetadata metadata = computeService.getNodeMetadata(node.getId());
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
      assert metadataSet.size() == 0 : String.format("nodes left in set: [%s] which didn't match set: [%s]",
            metadataSet, nodes);
   }

   @Test(enabled = true, dependsOnMethods = "testGet")
   public void testReboot() throws Exception {
      computeService.rebootNodesMatching(inGroup(group));// TODO test
      // validation
      testGet();
   }

   @Test(enabled = true, dependsOnMethods = "testReboot")
   public void testSuspendResume() throws Exception {
      computeService.suspendNodesMatching(inGroup(group));

      Set<? extends NodeMetadata> stoppedNodes = refreshNodes();

      assert Iterables.all(stoppedNodes, new Predicate<NodeMetadata>() {

         @Override
         public boolean apply(NodeMetadata input) {
            boolean returnVal = input.getState() == NodeState.SUSPENDED;
            if (!returnVal)
               System.err.printf("warning: node %s in state %s%n", input.getId(), input.getState());
            return returnVal;
         }

      }) : stoppedNodes;

      computeService.resumeNodesMatching(inGroup(group));
      testGet();
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   public void testListNodes() throws Exception {
      for (ComputeMetadata node : computeService.listNodes()) {
         assert node.getProviderId() != null;
         assert node.getLocation() != null;
         assertEquals(node.getType(), ComputeType.NODE);
      }
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   public void testGetNodesWithDetails() throws Exception {
      for (NodeMetadata node : computeService.listNodesDetailsMatching(all())) {
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

   @Test(enabled = true, dependsOnMethods = {"testListNodes", "testGetNodesWithDetails"})
   public void testDestroyNodes() {
      int toDestroy = refreshNodes().size();
      Set<? extends NodeMetadata> destroyed = computeService.destroyNodesMatching(inGroup(group));
      assertEquals(toDestroy, destroyed.size());
      for (NodeMetadata node : filter(computeService.listNodesDetailsMatching(all()), inGroup(group))) {
         assert node.getState() == NodeState.TERMINATED : node;
         assertEquals(context.getCredentialStore().get("node#" + node.getId()), null);
      }
   }

   private Set<? extends NodeMetadata> refreshNodes() {
      return filter(computeService.listNodesDetailsMatching(all()), and(inGroup(group), not(TERMINATED)));
   }

   @Test(enabled = true)
   public void testCreateAndRunAService() throws Exception {

      String group = this.group + "s";
      try {
         computeService.destroyNodesMatching(inGroup(group));
      } catch (Exception e) {

      }

      template = getDefaultTemplateBuilder().options(blockOnComplete(false).inboundPorts(22, 8080))
            .build();

      // note this is a dependency on the template resolution
      template.getOptions().runScript(
            RunScriptData.createScriptInstallAndStartJBoss(keyPair.get("public"), template.getImage()
                  .getOperatingSystem()));
      try {
         NodeMetadata node = getOnlyElement(computeService.createNodesInGroup(group, 1, template));

         checkHttpGet(node);
      } finally {
         computeService.destroyNodesMatching(inGroup(group));
      }

   }

   @Test(enabled = true/* , dependsOnMethods = "testCompareSizes" */)
   public void testTemplateOptions() throws Exception {
      TemplateOptions options = new TemplateOptions().withMetadata();
      Template t = computeService.templateBuilder().smallest().options(options).build();
      assert t.getOptions().isIncludeMetadata() : "The metadata option should be 'true' " + "for the created template";
   }

   public void testListImages() throws Exception {
      for (Image image : computeService.listImages()) {
         assert image.getProviderId() != null : image;
         // image.getLocationId() can be null, if it is a location-free image
         assertEquals(image.getType(), ComputeType.IMAGE);
      }
   }

   @Test(groups = {"integration", "live"})
   public void testGetAssignableLocations() throws Exception {
      for (Location location : computeService.listAssignableLocations()) {
         System.err.printf("location %s%n", location);
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

   public void testOptionToNotBlock() throws Exception {
      String group = this.group + "block";
      try {
         computeService.destroyNodesMatching(inGroup(group));
      } catch (Exception e) {

      }
      // no inbound ports
      TemplateOptions options = computeService.templateOptions().blockUntilRunning(false).inboundPorts();
      try {
         long time = System.currentTimeMillis();
         Set<? extends NodeMetadata> nodes = computeService.createNodesInGroup(group, 1, options);
         NodeMetadata node = getOnlyElement(nodes);
         assert node.getState() != NodeState.RUNNING;
         long duration = System.currentTimeMillis() - time;
         assert duration < 30 * 1000 : "duration longer than 30 seconds!:  " + duration / 1000;
      } finally {
         computeService.destroyNodesMatching(inGroup(group));
      }
   }

   private void assertProvider(Location provider) {
      assertEquals(provider.getScope(), LocationScope.PROVIDER);
      assertEquals(provider.getParent(), null);
   }

   public void testListSizes() throws Exception {
      for (Hardware hardware : computeService.listHardwareProfiles()) {
         assert hardware.getProviderId() != null;
         assert getCores(hardware) > 0;
         assert hardware.getVolumes().size() >= 0;
         assert hardware.getRam() > 0;
         assertEquals(hardware.getType(), ComputeType.HARDWARE);
      }
   }

   private TemplateBuilder getDefaultTemplateBuilder() {
      return computeService.templateBuilder().imageId("95");
   }

   @Test(enabled = true)
   public void testCompareSizes() throws Exception {
      TemplateBuilder templateBuilder = getDefaultTemplateBuilder();

      Hardware defaultSize = templateBuilder.build().getHardware();

      Hardware smallest = templateBuilder.smallest().build().getHardware();
      Hardware fastest = templateBuilder.fastest().build().getHardware();
      Hardware biggest = templateBuilder.biggest().build().getHardware();

      System.out.printf("smallest %s%n", smallest);
      System.out.printf("fastest %s%n", fastest);
      System.out.printf("biggest %s%n", biggest);

      assertEquals(defaultSize, smallest);

      assert getCores(smallest) <= getCores(fastest) : String.format("%d ! <= %d", smallest, fastest);
      assert getCores(biggest) <= getCores(fastest) : String.format("%d ! <= %d", biggest, fastest);

      assert biggest.getRam() >= fastest.getRam() : String.format("%d ! >= %d", biggest, fastest);
      assert biggest.getRam() >= smallest.getRam() : String.format("%d ! >= %d", biggest, smallest);

      assert getCores(fastest) >= getCores(biggest) : String.format("%d ! >= %d", fastest, biggest);
      assert getCores(fastest) >= getCores(smallest) : String.format("%d ! >= %d", fastest, smallest);
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
