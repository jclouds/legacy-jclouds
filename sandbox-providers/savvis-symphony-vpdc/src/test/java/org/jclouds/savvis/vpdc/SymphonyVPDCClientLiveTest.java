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

package org.jclouds.savvis.vpdc;

import static com.google.common.collect.Iterables.find;
import static org.jclouds.vcloud.options.CloneVAppOptions.Builder.deploy;
import static org.jclouds.vcloud.predicates.VCloudPredicates.resourceType;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.domain.Credentials;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshClient.Factory;
import org.jclouds.ssh.SshException;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.vcloud.VCloudExpressClientLiveTest;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VCloudExpressVApp;
import org.jclouds.vcloud.domain.VCloudExpressVAppTemplate;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.domain.ovf.ResourceType;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.predicates.TaskSuccess;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Module;

public class SymphonyVPDCClientLiveTest extends VCloudExpressClientLiveTest {
   public SymphonyVPDCClientLiveTest() {
      provider = "savvis-symphony-vpdc";
   }

   @BeforeGroups(groups = { "live" })
   @Override
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();

      // TODO remove these lines when this is registered under jclouds-core/rest.properties
      Properties restProperties = new Properties();
      restProperties.setProperty("savvis-symphony-vpdc.contextbuilder", SymphonyVPDCContextBuilder.class.getName());
      restProperties.setProperty("savvis-symphony-vpdc.propertiesbuilder",
            SymphonyVPDCPropertiesBuilder.class.getName());

      Injector injector = new RestContextFactory(restProperties).createContextBuilder(provider,
            ImmutableSet.<Module> of(new Log4JLoggingModule(), new JschSshClientModule()), overrides).buildInjector();

      connection = injector.getInstance(SymphonyVPDCClient.class);

      sshFactory = injector.getInstance(SshClient.Factory.class);
      socketTester = new RetryablePredicate<IPSocket>(injector.getInstance(SocketOpen.class), 130, 10, TimeUnit.SECONDS);// make
      // it
      // longer
      // then
      // default internet
      // service timeout
      successTester = new RetryablePredicate<URI>(injector.getInstance(TaskSuccess.class), 650, 10, TimeUnit.SECONDS);
   }

   protected String expectedOs = "Ubuntu Linux (64-bit)";
   protected String itemName = "Ubuntu JeOS 9.10 (64-bit)";

   protected Factory sshFactory;
   private VCloudExpressVApp vApp;
   private RetryablePredicate<IPSocket> socketTester;
   private RetryablePredicate<URI> successTester;
   private VCloudExpressVApp clone;
   private VDC vdc;
   public static final String PREFIX = System.getProperty("user.name") + "-savvis";

   @Test(enabled = true)
   public void testInstantiateAndPowerOn() throws InterruptedException, ExecutionException, TimeoutException,
         IOException {
      prepare();
      StringBuffer name = new StringBuffer();
      for (int i = 0; i < 15; i++)
         name.append("c");
      String serverName = name.toString();// "adriantest";

      long hardDisk = 4194304;

      // long hardDisk = 4194304 / 4 * 10;
      // String catalogOs = "CentOS 5.3 (64-bit)";
      // String expectedOs = "Red Hat Enterprise Linux 5 (64-bit)";

      // lookup the datacenter you are deploying into
      vdc = connection.findVDCInOrgNamed(null, null);

      ReferenceType vAppTemplateId = Iterables.find(vdc.getResourceEntities().values(), new Predicate<ReferenceType>() {

         @Override
         public boolean apply(ReferenceType arg0) {
            return arg0.getType().equals(VCloudMediaType.VAPPTEMPLATE_XML);
         }

      });

      VCloudExpressVAppTemplate vAppTemplate = connection.getVAppTemplate(vAppTemplateId.getHref());

      assert vAppTemplate != null;

      // create an options object to collect the configuration we want.
      InstantiateVAppTemplateOptions instantiateOptions = new InstantiateVAppTemplateOptions();

      // instantiate, noting vApp returned has minimal details
      vApp = connection.instantiateVAppTemplateInVDC(vdc.getHref(), vAppTemplate.getHref(), serverName,
            instantiateOptions);

      assertEquals(vApp.getStatus(), Status.RESOLVED);

      Task deployTask = connection.deployVApp(vApp.getHref());

      // check to see the result of calling deploy twice
      deployTask = connection.deployVApp(vApp.getHref());
      assertEquals(deployTask.getHref(), deployTask.getHref());

      vApp = connection.getVApp(vApp.getHref());

      assertEquals(vApp.getStatus(), Status.RESOLVED);

      try {// per docs, this is not supported
         connection.cancelTask(deployTask.getHref());
      } catch (UnsupportedOperationException e) {
      }

      assert successTester.apply(deployTask.getHref());
      System.out.printf("%d: done deploying vApp%n", System.currentTimeMillis());

      vApp = connection.getVApp(vApp.getHref());

      ReferenceType vAppResource = connection.findVDCInOrgNamed(null, null).getResourceEntities().get(serverName);
      assertEquals(vAppResource.getHref(), vApp.getHref());

      int processorCount = 1;
      long memory = 512;
      verifyConfigurationOfVApp(vApp, serverName, expectedOs, processorCount, memory, hardDisk);
      assertEquals(vApp.getStatus(), Status.OFF);

      assert successTester.apply(connection.powerOnVApp(vApp.getHref()).getHref());
      System.out.printf("%d: done powering on vApp%n", System.currentTimeMillis());

      vApp = connection.getVApp(vApp.getHref());
      assertEquals(vApp.getStatus(), Status.ON);
   }

   protected void prepare() {

   }

   @Test(enabled = true, dependsOnMethods = "testInstantiateAndPowerOn")
   public void testCloneVApp() throws IOException {
      assert successTester.apply(connection.powerOffVApp(vApp.getHref()).getHref());
      System.out.printf("%d: done powering off vApp%n", System.currentTimeMillis());

      StringBuffer name = new StringBuffer();
      for (int i = 0; i < 15; i++)
         name.append("b");
      String newName = name.toString();

      CloneVAppOptions options = deploy().powerOn().withDescription("The description of " + newName);

      System.out.printf("%d: cloning vApp%n", System.currentTimeMillis());
      Task task = connection.cloneVAppInVDC(vdc.getHref(), vApp.getHref(), newName, options);

      // wait for the task to complete
      assert successTester.apply(task.getHref());
      System.out.printf("%d: done cloning vApp%n", System.currentTimeMillis());

      assert successTester.apply(connection.powerOnVApp(vApp.getHref()).getHref());
      System.out.printf("%d: done powering on vApp%n", System.currentTimeMillis());

      // refresh task to get the new vApp location
      task = connection.getTask(task.getHref());

      clone = connection.getVApp(task.getOwner().getHref());
      assertEquals(clone.getStatus(), Status.ON);

      assertEquals(clone.getName(), newName);
      assertEquals(clone.getNetworkToAddresses().values().size(), 1);
   }

   private void loopAndCheckPass(String publicIp) throws IOException {
      for (int i = 0; i < 5; i++) {// retry loop TODO replace with predicate.
         try {
            doCheckPass(publicIp);
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

   @Test(enabled = true, dependsOnMethods = "testCloneVApp")
   public void testLifeCycle() throws InterruptedException, ExecutionException, TimeoutException, IOException {

      try {// per docs, this is not supported
         connection.undeployVApp(vApp.getHref());
         assert false;
      } catch (UnsupportedOperationException e) {
      }

      try {// per docs, this is not supported
         connection.suspendVApp(vApp.getHref());
         assert false;
      } catch (UnsupportedOperationException e) {
      }

      assert successTester.apply(connection.resetVApp(vApp.getHref()).getHref());

      vApp = connection.getVApp(vApp.getHref());

      assertEquals(vApp.getStatus(), Status.ON);

      // TODO we need to determine whether shutdown is supported before invoking
      // it.
      // connection.shutdownVApp(vApp.getId());
      // vApp = connection.getVApp(vApp.getId());
      // assertEquals(vApp.getStatus(), VAppStatus.ON);

      assert successTester.apply(connection.powerOffVApp(vApp.getHref()).getHref());

      vApp = connection.getVApp(vApp.getHref());
      assertEquals(vApp.getStatus(), Status.OFF);
   }

   private void verifyConfigurationOfVApp(VCloudExpressVApp vApp, String serverName, String expectedOs,
         int processorCount, long memory, long hardDisk) {
      assertEquals(vApp.getName(), serverName);
      assertEquals(vApp.getOperatingSystemDescription(), expectedOs);
      assertEquals(find(vApp.getResourceAllocations(), resourceType(ResourceType.PROCESSOR)).getVirtualQuantity(),
            processorCount);
      assertEquals(
            find(vApp.getResourceAllocations(), resourceType(ResourceType.SCSI_CONTROLLER)).getVirtualQuantity(), 1);
      assertEquals(find(vApp.getResourceAllocations(), resourceType(ResourceType.MEMORY)).getVirtualQuantity(), memory);
      assertEquals(find(vApp.getResourceAllocations(), resourceType(ResourceType.DISK_DRIVE)).getVirtualQuantity(),
            hardDisk);
      assertEquals(vApp.getSize().longValue(),
            find(vApp.getResourceAllocations(), resourceType(ResourceType.DISK_DRIVE)).getVirtualQuantity());
   }

   private void doCheckPass(String address) throws IOException {
      IPSocket socket = new IPSocket(address, 22);

      System.out.printf("%d: %s awaiting ssh service to start%n", System.currentTimeMillis(), socket);
      assert socketTester.apply(socket);
      System.out.printf("%d: %s ssh service started%n", System.currentTimeMillis(), socket);

      SshClient connection = getConnectionFor(socket);
      try {
         connection.connect();
         System.out.printf("%d: %s ssh connection made%n", System.currentTimeMillis(), socket);
         System.out.println(connection.exec("df -h"));
      } finally {
         if (connection != null)
            connection.disconnect();
      }
   }

   protected SshClient getConnectionFor(IPSocket socket) {
      // TODO add in the correct login credentials for the vApp template
      return sshFactory.create(socket, new Credentials("root", "password"));
   }

   @AfterTest
   void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      if (vApp != null) {
         try {
            successTester.apply(connection.powerOffVApp(vApp.getHref()).getHref());
         } catch (Exception e) {

         }
         connection.deleteVApp(vApp.getHref());
      }
      if (clone != null) {
         try {
            successTester.apply(connection.powerOffVApp(clone.getHref()).getHref());
         } catch (Exception e) {

         }
         connection.deleteVApp(clone.getHref());
      }

   }
}