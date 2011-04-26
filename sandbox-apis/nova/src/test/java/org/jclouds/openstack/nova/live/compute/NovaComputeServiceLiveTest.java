/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.openstack.nova.live.compute;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Module;
import com.jcraft.jsch.JSchException;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.RunScriptOnNodesException;
import org.jclouds.compute.domain.*;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newTreeSet;
import static org.jclouds.compute.ComputeTestUtils.buildScript;
import static org.jclouds.compute.options.TemplateOptions.Builder.overrideCredentialsWith;
import static org.jclouds.compute.predicates.NodePredicates.*;
import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;
import static org.testng.Assert.*;

/**
 * Generally disabled, as it incurs higher fees.
 *
 * @author Adrian Cole
 */
@Test(groups = "novalive", enabled = true, sequential = true)
public class NovaComputeServiceLiveTest extends ComputeBase {

   private static String group = "compute service test group";


   protected void checkNodes(Iterable<? extends NodeMetadata> nodes, String tag) throws IOException {
      for (NodeMetadata node : nodes) {
         assertNotNull(node.getProviderId());
         assertNotNull(node.getGroup());
         assertEquals(node.getGroup(), group);
         //assertEquals(node.getState(), NodeState.RUNNING);
         Credentials fromStore = context.getCredentialStore().get("node#" + node.getId());
         assertEquals(fromStore, node.getCredentials());
         assert node.getPublicAddresses().size() >= 1 || node.getPrivateAddresses().size() >= 1 : "no ips in" + node;
//         assertNotNull(node.getCredentials());
//         if (node.getCredentials().identity != null) {
//            assertNotNull(node.getCredentials().identity);
//            assertNotNull(node.getCredentials().credential);
//            doCheckJavaIsInstalledViaSsh(node);
//         }
         assertEquals(node.getLocation().getScope(), LocationScope.HOST);
      }
   }


   @BeforeTest
   @Override
   public void before() throws IOException, ExecutionException, TimeoutException, InterruptedException {
      super.before();
      computeService.destroyNodesMatching(inGroup(group));
   }

   @Test(enabled = true, expectedExceptions = AuthorizationException.class, timeOut = 60000)
   public void testCorrectAuthException() throws Exception {
      Properties properties = new Properties();
      properties.putAll(overrides);
      properties.remove(provider + ".identity");
      ComputeServiceContext context = null;
      try {
         context = new ComputeServiceContextFactory().createContext(provider, "MOMMA", "MIA", ImmutableSet
               .<Module>of(new SLF4JLoggingModule()), properties);
         context.getComputeService().listNodes();
      } finally {
         if (context != null)
            context.close();
      }
   }

   @Test(timeOut = 60000)
   public void testImagesCache() throws Exception {
      computeService.listImages();
      long time = System.currentTimeMillis();
      computeService.listImages();
      long duration = System.currentTimeMillis() - time;
      assert duration < 1000 : String.format("%dms to get images", duration);
   }

   @Test(enabled = true, expectedExceptions = NoSuchElementException.class, timeOut = 60000)
   public void testCorrectExceptionRunningNodesNotFound() throws Exception {
      computeService.runScriptOnNodesMatching(runningInGroup("zebras-are-awesome"), buildScript(new OperatingSystem.Builder()
            .family(OsFamily.UBUNTU).description("ffoo").build()));
   }

   @Test(expectedExceptions = JSchException.class, expectedExceptionsMessageRegExp = "Auth fail", timeOut = 60000)
   void testScriptExecutionWithWrongCredentials() throws Throwable, RunScriptOnNodesException, URISyntaxException, InterruptedException {
      NodeMetadata node = getDefaultNodeImmediately(group);
      String address = awaitForPublicAddressAssigned(node.getId());
      awaitForSshPort(address, new Credentials("root", keyPair.get("private")));
      OperatingSystem os = node.getOperatingSystem();
      try {
         Map<? extends NodeMetadata, ExecResponse> responses = runJavaInstallationScriptWithCreds(group, os, new Credentials(
               "root", "romeo"));
      } catch (RunScriptOnNodesException e) {
         throw e.getNodeErrors().values().iterator().next().getCause();
      }
   }

   @Test(timeOut = 60000)
   public void testScriptExecutionAfterBootWithBasicTemplate() throws InterruptedException, RunNodesException, RunScriptOnNodesException, URISyntaxException, IOException {

      NodeMetadata node = getDefaultNodeImmediately(group);
      String address = awaitForPublicAddressAssigned(node.getId());
      awaitForSshPort(address, new Credentials("root", keyPair.get("private")));
      for (Map.Entry<? extends NodeMetadata, ExecResponse> response : computeService.runScriptOnNodesMatching(
            runningInGroup(group), Statements.exec("echo hello"),
            overrideCredentialsWith(new Credentials("root", keyPair.get("private"))).wrapInInitScript(false).runAsRoot(false)).entrySet())
         assert response.getValue().getOutput().trim().equals("hello") : response.getKey() + ": "
               + response.getValue();

      //TODO runJavaInstallationScriptWithCreds(group, os, new Credentials("root", keyPair.get("private")));
      //TODO no response? if os is null (ZYPPER)

      checkNodes(Sets.<NodeMetadata>newHashSet(node), group);

      Credentials good = node.getCredentials();
      //TODO check good is being private key .overrideCredentialsWith
      //TODO test for .blockOnPort
   }

   @Test(timeOut = 60000)
   public void testTemplateMatch() throws Exception {
      Template template = buildTemplate(getDefaultTemplateBuilder());
      Template toMatch = computeService.templateBuilder().imageId(template.getImage().getId()).build();
      assertEquals(toMatch.getImage(), template.getImage());
   }

//   protected void checkHttpGet(NodeMetadata node) {
//      ComputeTestUtils.checkHttpGet(context.utils().http(), node, 8080);
//   }

   @Test(timeOut = 60000)
   public void testCreateTwoNodesWithRunScript() throws Exception {
      computeService.destroyNodesMatching(inGroup(group));
      Template template = getDefaultTemplateBuilder().options(TemplateOptions.Builder.blockUntilRunning(true)).build();
      SortedSet<NodeMetadata> nodes = newTreeSet(computeService.createNodesInGroup(group, 2, template));

      assertEquals(nodes.size(), 2);
      checkNodes(nodes, group);
      NodeMetadata node1 = nodes.first();
      NodeMetadata node2 = nodes.last();
      // credentials aren't always the same
      // assertEquals(node1.getCredentials(), node2.getCredentials());

      assertLocationSameOrChild(node1.getLocation(), template.getLocation());
      assertLocationSameOrChild(node2.getLocation(), template.getLocation());
      assertEquals(node1.getImageId(), template.getImage().getId());
      assertEquals(node2.getImageId(), template.getImage().getId());
//      checkOsMatchesTemplate(node1);
//      checkOsMatchesTemplate(node2);
      //TODO add with script;
   }

//   protected void checkOsMatchesTemplate(NodeMetadata node) {
//      if (node.getOperatingSystem() != null)
//         assert node.getOperatingSystem().getFamily().equals(getDefaultTemplateBuilder().build().getImage().getOperatingSystem().getFamily()) : String
//               .format("expecting family %s but got %s", getDefaultTemplateBuilder().build().getImage().getOperatingSystem().getFamily(), node
//                     .getOperatingSystem());
//   }


   @Test(timeOut = 60000)
   public void testCreateAnotherNodeWithNewContextToEnsureSharedMemIsntRequired() throws Exception {
      getDefaultNodeImmediately(group);
      initializeContextAndComputeService(overrides);

      NodeMetadata node = createDefaultNode(TemplateOptions.Builder.blockUntilRunning(true), group);
      checkNodes(Sets.<NodeMetadata>newHashSet(node), group);
      assertLocationSameOrChild(node.getLocation(), getDefaultTemplateBuilder().build().getLocation());
//      checkOsMatchesTemplate(node);
   }

   @Test(timeOut = 60000)
   public void testCredentialsCache() throws Exception {
      LinkedList<NodeMetadata> nodes = new LinkedList<NodeMetadata>();
      nodes.add(getDefaultNodeImmediately(group));
      initializeContextAndComputeService(overrides);
      nodes.add(createDefaultNode(group));
      initializeContextAndComputeService(overrides);
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

   @Test(timeOut = 60000)
   public void testGetNodeMetadata() throws Exception {
      Set<NodeMetadata> nodes = Sets.newHashSet(getDefaultNodeImmediately(group));
      awaitForPublicAddressAssigned(nodes.iterator().next().getId());
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
         NodeMetadata nodeMetadata = computeService.getNodeMetadata(node.getId());
         assertEquals(parseGroupFromName(nodeMetadata.getName()), group);
         assertEquals(nodeMetadata.getProviderId(), node.getProviderId());
         assertEquals(nodeMetadata.getGroup(), node.getGroup());
         assertLocationSameOrChild(nodeMetadata.getLocation(), getDefaultTemplateBuilder().build().getLocation());
         assertEquals(nodeMetadata.getImageId(), getDefaultTemplateBuilder().build().getImage().getId());
//         checkOsMatchesTemplate(metadata);
         assertEquals(nodeMetadata.getState(), NodeState.RUNNING);
         // due to DHCP the addresses can actually change in-between runs.
         assertTrue(nodeMetadata.getPrivateAddresses().size() > 0);
         assertTrue(nodeMetadata.getPublicAddresses().size() > 0);
      }
      assertNodeZero(metadataMap.values(), nodes);
   }


   protected void assertNodeZero(Collection<? extends NodeMetadata> metadataSet, Set<NodeMetadata> nodes) {
      assert metadataSet.size() == 0 : String.format("nodes left in set: [%s] which didn't match set: [%s]",
            metadataSet, nodes);
   }


   @Test(timeOut = 60000)
   public void testListNodes() throws Exception {
      for (ComputeMetadata node : computeService.listNodes()) {
         assert node.getProviderId() != null;
         assert node.getLocation() != null;
         assertEquals(node.getType(), ComputeType.NODE);
      }
   }

   @Test(timeOut = 60000)
   public void testGetNodesWithDetails() throws Exception {
      for (NodeMetadata node : computeService.listNodesDetailsMatching(all())) {
         assert node.getProviderId() != null : node;
         assert node.getLocation() != null : node;
         assertEquals(node.getType(), ComputeType.NODE);
         assert node.getProviderId() != null : node;
         // nullable
         // assert nodeMetadata.getImage() != null : node;
         // user specified name is not always supported
         // assert nodeMetadata.getName().parseGroupFromName() != null : nodeMetadata;

         if (node.getState() == NodeState.RUNNING) {
            assert node.getPublicAddresses() != null : node;
            assert node.getPublicAddresses().size() > 0 || node.getPrivateAddresses().size() > 0 : node;
            assertNotNull(node.getPrivateAddresses());
         }
      }
   }

   @Test(timeOut = 60000)
   public void testDestroyNodes() {
      int toDestroy = getFreshNodes(group).size();
      Set<? extends NodeMetadata> destroyed = computeService.destroyNodesMatching(inGroup(group));
      assertEquals(toDestroy, destroyed.size());
      for (NodeMetadata node : filter(computeService.listNodesDetailsMatching(all()), inGroup(group))) {
         assert node.getState() == NodeState.TERMINATED : node;
         assertEquals(context.getCredentialStore().get("node#" + node.getId()), null);
      }
   }


   @Test(timeOut = 60000)
   public void testCreateAndRunService() throws Exception {
      NodeMetadata node = getDefaultNodeImmediately(group);
      //TODO .inboundPorts
      //checkHttpGet(node);
   }

   @Test(timeOut = 60000)
   public void testTemplateOptions() throws Exception {
      TemplateOptions options = new TemplateOptions().withMetadata();
      Template t = getDefaultTemplateBuilder().smallest().options(options).build();
      assert t.getOptions().isIncludeMetadata() : "The metadata option should be 'true' " + "for the created template";
   }

   public void testListImages() throws Exception {
      for (Image image : computeService.listImages()) {
         assert image.getProviderId() != null : image;
         // image.getLocationId() can be null, if it is a location-free image
         assertEquals(image.getType(), ComputeType.IMAGE);
      }
   }

   @Test(timeOut = 60000)
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
      //TODO no inbound ports
      //TemplateOptions options = computeService.templateOptions().blockUntilRunning(false).inboundPorts();
      long time = System.currentTimeMillis();
      NodeMetadata node = getOnlyElement(computeService.createNodesInGroup(group, 1, getDefaultTemplateBuilder().build()));
      assert node.getState() != NodeState.RUNNING;
      long duration = System.currentTimeMillis() - time;
      assert duration < 30 * 1000 : "duration longer than 30 seconds!:  " + duration / 1000;
   }

   private void assertProvider(Location provider) {
      assertEquals(provider.getScope(), LocationScope.PROVIDER);
      assertEquals(provider.getParent(), null);
   }

   @Test(timeOut = 60000)
   public void testListHardwareProfiles() throws Exception {
      for (Hardware hardware : computeService.listHardwareProfiles()) {
         assert hardware.getProviderId() != null;
         assert getCores(hardware) > 0;
         assert hardware.getVolumes().size() >= 0;
         assert hardware.getRam() > 0;
         assertEquals(hardware.getType(), ComputeType.HARDWARE);
      }
   }


   @Test(timeOut = 60000)
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

      assert getCores(smallest) <= getCores(fastest);
      assert getCores(biggest) <= getCores(fastest);

      assert biggest.getRam() >= fastest.getRam();
      assert biggest.getRam() >= smallest.getRam();

      assert getCores(fastest) >= getCores(biggest);
      assert getCores(fastest) >= getCores(smallest);
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
      computeService.destroyNodesMatching(inGroup(group));
      context.close();
   }
}
