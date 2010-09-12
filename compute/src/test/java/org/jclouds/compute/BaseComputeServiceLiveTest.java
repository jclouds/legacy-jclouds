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

package org.jclouds.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Throwables.getRootCause;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.google.common.collect.Sets.newTreeSet;
import static org.jclouds.compute.options.RunScriptOptions.Builder.overrideCredentialsWith;
import static org.jclouds.compute.predicates.NodePredicates.TERMINATED;
import static org.jclouds.compute.predicates.NodePredicates.all;
import static org.jclouds.compute.predicates.NodePredicates.runningWithTag;
import static org.jclouds.compute.predicates.NodePredicates.withTag;
import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.jclouds.io.Payloads.newStringPayload;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.Constants;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.OperatingSystemPredicates;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.util.Utils;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" }, sequential = true, testName = "compute.ComputeServiceLiveTest")
public abstract class BaseComputeServiceLiveTest {
   public static final String APT_RUN_SCRIPT = new StringBuilder()//
            .append("echo nameserver 208.67.222.222 >> /etc/resolv.conf\n")//
            .append("cp /etc/apt/sources.list /etc/apt/sources.list.old\n")//
            .append(
                     "sed 's~us.archive.ubuntu.com~mirror.anl.gov/pub~g' /etc/apt/sources.list.old >/etc/apt/sources.list\n")//
            .append("apt-get update\n")//
            .append("apt-get install -f -y --force-yes openjdk-6-jdk\n")//
            .toString();

   public static final String YUM_RUN_SCRIPT = new StringBuilder()
            .append("echo nameserver 208.67.222.222 >> /etc/resolv.conf\n")
            //
            .append("echo \"[jdkrepo]\" >> /etc/yum.repos.d/CentOS-Base.repo\n")
            //
            .append("echo \"name=jdkrepository\" >> /etc/yum.repos.d/CentOS-Base.repo\n")
            //
            .append(
                     "echo \"baseurl=http://ec2-us-east-mirror.rightscale.com/epel/5/i386/\" >> /etc/yum.repos.d/CentOS-Base.repo\n")//
            .append("echo \"enabled=1\" >> /etc/yum.repos.d/CentOS-Base.repo\n")//
            .append("yum --nogpgcheck -y install java-1.6.0-openjdk\n")//
            .append("echo \"export PATH=\\\"/usr/lib/jvm/jre-1.6.0-openjdk/bin/:\\$PATH\\\"\" >> /root/.bashrc\n")//
            .toString();

   public static final String ZYPPER_RUN_SCRIPT = new StringBuilder()//
            .append("echo nameserver 208.67.222.222 >> /etc/resolv.conf\n")//
            .append("sudo zypper install java-1.6.0-openjdk-devl\n")//
            .toString();

   abstract public void setServiceDefaults();

   protected String provider;
   protected String tag;

   protected RetryablePredicate<IPSocket> socketTester;
   protected SortedSet<NodeMetadata> nodes;
   protected ComputeServiceContext context;
   protected ComputeService client;
   protected String identity;
   protected String credential;
   protected Template template;
   protected Map<String, String> keyPair;

   @BeforeGroups(groups = { "integration", "live" })
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      setServiceDefaults();
      if (tag == null)
         tag = checkNotNull(provider, "provider");
      setupCredentials();
      setupKeyPairForTest();
      initializeContextAndClient();
      buildSocketTester();
   }

   protected void buildSocketTester() {
      SocketOpen socketOpen = Guice.createInjector(getSshModule()).getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<IPSocket>(socketOpen, 60, 1, TimeUnit.SECONDS);
   }

   protected void setupKeyPairForTest() throws FileNotFoundException, IOException {
      keyPair = setupKeyPair();
   }

   public static Map<String, String> setupKeyPair() throws FileNotFoundException, IOException {
      String secretKeyFile;
      try {
         secretKeyFile = checkNotNull(System.getProperty("jclouds.test.ssh.keyfile"), "jclouds.test.ssh.keyfile");
      } catch (NullPointerException e) {
         secretKeyFile = System.getProperty("user.home") + "/.ssh/id_rsa";
      }
      checkSecretKeyFile(secretKeyFile);
      String secret = Files.toString(new File(secretKeyFile), Charsets.UTF_8);
      assert secret.startsWith("-----BEGIN RSA PRIVATE KEY-----") : "invalid key:\n" + secret;
      return ImmutableMap.<String, String> of("private", secret, "public", Files.toString(new File(secretKeyFile
               + ".pub"), Charsets.UTF_8));
   }

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      credential = checkNotNull(System.getProperty("jclouds.test.credential"), "jclouds.test.credential");
   }

   private void initializeContextAndClient() throws IOException {
      if (context != null)
         context.close();
      Properties props = new Properties();
      props.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      props.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      context = new ComputeServiceContextFactory().createContext(provider, identity, credential, ImmutableSet.of(
               new Log4JLoggingModule(), getSshModule()), props);
      client = context.getComputeService();
   }

   private static void checkSecretKeyFile(String secretKeyFile) throws FileNotFoundException {
      Utils.checkNotEmpty(secretKeyFile, "System property: [jclouds.test.ssh.keyfile] set to an empty string");
      if (!new File(secretKeyFile).exists()) {
         throw new FileNotFoundException("secretKeyFile not found at: " + secretKeyFile);
      }
   }

   abstract protected Module getSshModule();

   // wait up to 5 seconds for an auth exception
   @Test(enabled = true, expectedExceptions = AuthorizationException.class)
   public void testCorrectAuthException() throws Exception {
      ComputeServiceContext context = null;
      try {
         context = new ComputeServiceContextFactory().createContext(provider, "MOMMA", "MIA", ImmutableSet
                  .<Module> of(new Log4JLoggingModule()));
         context.getComputeService().listNodes();
      } finally {
         if (context != null)
            context.close();
      }

   }

   @Test(enabled = true, dependsOnMethods = "testCorrectAuthException")
   public void testImagesCache() throws Exception {
      client.listImages();
      long time = System.currentTimeMillis();
      client.listImages();
      long duration = System.currentTimeMillis() - time;
      assert duration < 1000 : String.format("%dms to get images", duration);
   }

   // since surefire and eclipse don't otherwise guarantee the order, we are
   // starting this one alphabetically before create2nodes..
   @Test(enabled = true, dependsOnMethods = { "testCompareSizes" })
   public void testAScriptExecutionAfterBootWithBasicTemplate() throws Exception {
      String tag = this.tag + "run";
      try {
         client.destroyNodesMatching(withTag(tag));
      } catch (Exception e) {

      }

      TemplateOptions options = client.templateOptions().blockOnPort(22, 120);
      try {
         Set<? extends NodeMetadata> nodes = client.runNodesWithTag(tag, 1, options);
         Credentials good = nodes.iterator().next().getCredentials();
         assert good.identity != null : nodes;
         assert good.credential != null : nodes;

         OperatingSystem os = get(nodes, 0).getOperatingSystem();
         try {
            Map<? extends NodeMetadata, ExecResponse> responses = runScriptWithCreds(tag, os, new Credentials(
                     good.identity, "romeo"));
            assert false : "shouldn't pass with a bad password\n" + responses;
         } catch (RunScriptOnNodesException e) {
            assert getRootCause(e).getMessage().contains("Auth fail") : e;
         }

         runScriptWithCreds(tag, os, good);

         checkNodes(nodes, tag);

      } finally {
         client.destroyNodesMatching(withTag(tag));
      }
   }

   @Test(enabled = true, dependsOnMethods = { "testImagesCache" })
   public void testTemplateMatch() throws Exception {
      template = buildTemplate(client.templateBuilder());
      Template toMatch = client.templateBuilder().imageId(template.getImage().getId()).build();
      assertEquals(toMatch.getImage(), template.getImage());
   }

   @Test(enabled = true, dependsOnMethods = "testCompareSizes")
   public void testCreateTwoNodesWithRunScript() throws Exception {
      try {
         client.destroyNodesMatching(withTag(tag));
      } catch (NoSuchElementException e) {

      }
      refreshTemplate();
      try {
         nodes = newTreeSet(client.runNodesWithTag(tag, 2, template));
      } catch (RunNodesException e) {
         nodes = newTreeSet(concat(e.getSuccessfulNodes(), e.getNodeErrors().keySet()));
         throw e;
      }
      assertEquals(nodes.size(), 2);
      checkNodes(nodes, tag);
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
      template = buildTemplate(client.templateBuilder());

      template.getOptions().installPrivateKey(newStringPayload(keyPair.get("private"))).authorizePublicKey(
               newStringPayload(keyPair.get("public"))).runScript(
               newStringPayload(buildScript(template.getImage().getOperatingSystem())));
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
      refreshTemplate();
      TreeSet<NodeMetadata> nodes = newTreeSet(client.runNodesWithTag(tag, 1, template));
      checkNodes(nodes, tag);
      NodeMetadata node = nodes.first();
      this.nodes.add(node);
      assertEquals(nodes.size(), 1);
      assertLocationSameOrChild(node.getLocation(), template.getLocation());
      checkOsMatchesTemplate(node);
   }

   protected Map<? extends NodeMetadata, ExecResponse> runScriptWithCreds(final String tag, OperatingSystem os,
            Credentials creds) throws RunScriptOnNodesException {
      try {
         return client.runScriptOnNodesMatching(runningWithTag(tag), newStringPayload(buildScript(os)),
                  overrideCredentialsWith(creds));
      } catch (SshException e) {
         throw e;
      }
   }

   protected void checkNodes(Iterable<? extends NodeMetadata> nodes, String tag) throws IOException {
      for (NodeMetadata node : nodes) {
         assertNotNull(node.getProviderId());
         assertNotNull(node.getTag());
         assertEquals(node.getTag(), tag);
         assertEquals(node.getState(), NodeState.RUNNING);
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

   public static String buildScript(OperatingSystem os) {
      if (OperatingSystemPredicates.supportsApt().apply(os))
         return APT_RUN_SCRIPT;
      else if (OperatingSystemPredicates.supportsYum().apply(os))
         return YUM_RUN_SCRIPT;
      else if (OperatingSystemPredicates.supportsZypper().apply(os))
         return ZYPPER_RUN_SCRIPT;
      else
         throw new IllegalArgumentException("don't know how to handle" + os.toString());
   }

   @Test(enabled = true, dependsOnMethods = "testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired")
   public void testGet() throws Exception {
      Set<? extends NodeMetadata> nodes = client.listNodesDetailsMatching(all());
      Set<? extends NodeMetadata> metadataSet = newLinkedHashSet(filter(nodes, and(withTag(tag), not(TERMINATED))));
      for (NodeMetadata node : nodes) {
         metadataSet.remove(node);
         NodeMetadata metadata = client.getNodeMetadata(node.getId());
         assertEquals(metadata.getProviderId(), node.getProviderId());
         assertEquals(metadata.getTag(), node.getTag());
         assertLocationSameOrChild(metadata.getLocation(), template.getLocation());
         checkImageIdMatchesTemplate(metadata);
         checkOsMatchesTemplate(metadata);
         assertEquals(metadata.getState(), NodeState.RUNNING);
         assertEquals(metadata.getPrivateAddresses(), node.getPrivateAddresses());
         assertEquals(metadata.getPublicAddresses(), node.getPublicAddresses());
      }
      assertNodeZero(metadataSet);
   }

   protected void assertNodeZero(Set<? extends NodeMetadata> metadataSet) {
      assert metadataSet.size() == 0 : String.format("nodes left in set: [%s] which didn't match set: [%s]",
               metadataSet, nodes);
   }

   @Test(enabled = true, dependsOnMethods = "testGet")
   public void testReboot() throws Exception {
      client.rebootNodesMatching(withTag(tag));// TODO test
      // validation
      testGet();
   }

   @Test(enabled = true/* , dependsOnMethods = "testCompareSizes" */)
   public void testTemplateOptions() throws Exception {
      TemplateOptions options = new TemplateOptions().withMetadata();
      Template t = client.templateBuilder().smallest().options(options).build();
      assert t.getOptions().isIncludeMetadata() : "The metadata option should be 'true' " + "for the created template";
   }

   public void testListNodes() throws Exception {
      for (ComputeMetadata node : client.listNodes()) {
         assert node.getProviderId() != null;
         assert node.getLocation() != null;
         assertEquals(node.getType(), ComputeType.NODE);
      }
   }

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

   @Test(enabled = true, dependsOnMethods = "testGet")
   public void testOptionToNotBlock() throws Exception {
      String tag = this.tag + "block";
      try {
         client.destroyNodesMatching(withTag(tag));
      } catch (Exception e) {

      }
      // no inbound ports
      TemplateOptions options = client.templateOptions().blockUntilRunning(false).inboundPorts();
      try {
         long time = System.currentTimeMillis();
         Set<? extends NodeMetadata> nodes = client.runNodesWithTag(tag, 1, options);
         NodeMetadata node = getOnlyElement(nodes);
         assert node.getState() != NodeState.RUNNING;
         long duration = System.currentTimeMillis() - time;
         assert duration < 30 * 1000 : "duration longer than 30 seconds!:  " + duration / 1000;
      } finally {
         client.destroyNodesMatching(withTag(tag));
      }
   }

   private void assertProvider(Location provider) {
      assertEquals(provider.getScope(), LocationScope.PROVIDER);
      assertEquals(provider.getParent(), null);
   }

   public void testListSizes() throws Exception {
      for (Hardware hardware : client.listHardwareProfiles()) {
         assert hardware.getProviderId() != null;
         assert getCores(hardware) > 0;
         assert hardware.getVolumes().size() >= 0;
         assert hardware.getRam() > 0;
         assertEquals(hardware.getType(), ComputeType.HARDWARE);
      }
   }

   @Test(enabled = true)
   public void testCompareSizes() throws Exception {
      Hardware defaultSize = client.templateBuilder().build().getHardware();

      Hardware smallest = client.templateBuilder().smallest().build().getHardware();
      Hardware fastest = client.templateBuilder().fastest().build().getHardware();
      Hardware biggest = client.templateBuilder().biggest().build().getHardware();

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
      IPSocket socket = new IPSocket(get(node.getPublicAddresses(), 0), 22);
      socketTester.apply(socket); // TODO add transitionTo option that accepts
      // a socket conection
      // state.
      SshClient ssh = (node.getCredentials().credential != null && !node.getCredentials().credential
               .startsWith("-----BEGIN RSA PRIVATE KEY-----")) ? context.utils().sshFactory().create(socket,
               node.getCredentials().identity, node.getCredentials().credential) : context.utils().sshFactory().create(
               socket,
               node.getCredentials().identity,
               node.getCredentials().credential != null ? node.getCredentials().credential.getBytes() : keyPair.get(
                        "private").getBytes());
      try {
         ssh.connect();
         ExecResponse hello = ssh.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
         ExecResponse exec = ssh.exec("java -version");
         assert exec.getError().indexOf("OpenJDK") != -1 : exec;
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   @AfterTest
   protected void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      if (nodes != null) {
         client.destroyNodesMatching(withTag(tag));
         for (NodeMetadata node : filter(client.listNodesDetailsMatching(all()), withTag(tag))) {
            assert node.getState() == NodeState.TERMINATED : node;
         }
      }
      context.close();
   }

}
