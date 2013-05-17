/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.gogrid;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.IpPortPair;
import org.jclouds.gogrid.domain.Job;
import org.jclouds.gogrid.domain.LoadBalancer;
import org.jclouds.gogrid.domain.LoadBalancerPersistenceType;
import org.jclouds.gogrid.domain.LoadBalancerType;
import org.jclouds.gogrid.domain.PowerCommand;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.domain.ServerImage;
import org.jclouds.gogrid.domain.ServerImageType;
import org.jclouds.gogrid.options.AddLoadBalancerOptions;
import org.jclouds.gogrid.options.AddServerOptions;
import org.jclouds.gogrid.options.GetImageListOptions;
import org.jclouds.gogrid.predicates.LoadBalancerLatestJobCompleted;
import org.jclouds.gogrid.predicates.ServerLatestJobCompleted;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.predicates.SocketOpen;
import org.testng.SkipException;
import org.testng.TestException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.google.inject.Guice;

/**
 * End to end live test for GoGrid
 * <p/>
 * Takes too long to execute. Please split into multiple tests
 * 
 * @author Oleksiy Yarmula
 */
@Test(enabled = false, groups = "live", singleThreaded = true, testName = "GoGridLiveTestDisabled")
public class GoGridLiveTestDisabled extends BaseApiLiveTest<GoGridClient> {

   public GoGridLiveTestDisabled() {
      provider = "gogrid";
   }

   private Predicate<Server> serverLatestJobCompleted;
   private Predicate<LoadBalancer> loadBalancerLatestJobCompleted;
   /**
    * Keeps track of the servers, created during the tests, to remove them after all tests complete
    */
   private List<String> serversToDeleteAfterTheTests = Lists.newArrayList();
   private List<String> loadBalancersToDeleteAfterTest = Lists.newArrayList();


   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setup() {
      super.setup();
      serverLatestJobCompleted = retry(new ServerLatestJobCompleted(api.getJobServices()), 800, 20, SECONDS);
      loadBalancerLatestJobCompleted = retry(new LoadBalancerLatestJobCompleted(api.getJobServices()), 800, 20,
            SECONDS);
   }

   @Test(enabled = true)
   public void testDescriptionIs500Characters() {
      final String nameOfServer = "Description" + String.valueOf(new Date().getTime()).substring(6);
      serversToDeleteAfterTheTests.add(nameOfServer);

      Set<Ip> availableIps = api.getIpServices().getUnassignedPublicIpList();
      Ip availableIp = Iterables.getLast(availableIps);

      String ram = Iterables.get(api.getServerServices().getRamSizes(), 0).getName();
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < 500; i++)
         builder.append('a');

      String description = builder.toString();

      Server createdServer = api.getServerServices().addServer(nameOfServer,
               "GSI-f8979644-e646-4711-ad58-d98a5fa3612c", ram, availableIp.getIp(),
               new AddServerOptions().withDescription(description));
      assertNotNull(createdServer);
      assert serverLatestJobCompleted.apply(createdServer);

      assertEquals(Iterables.getLast(api.getServerServices().getServersByName(nameOfServer)).getDescription(),
               description);

   }

   /**
    * Tests server start, reboot and deletion. Also verifies IP services and job services.
    */
   @Test(enabled = true)
   public void testServerLifecycle() {
      int serverCountBeforeTest = api.getServerServices().getServerList().size();

      final String nameOfServer = "Server" + String.valueOf(new Date().getTime()).substring(6);
      serversToDeleteAfterTheTests.add(nameOfServer);

      Set<Ip> availableIps = api.getIpServices().getUnassignedPublicIpList();
      Ip availableIp = Iterables.getLast(availableIps);

      String ram = Iterables.get(api.getServerServices().getRamSizes(), 0).getName();

      Server createdServer = api.getServerServices().addServer(nameOfServer,
               "GSI-f8979644-e646-4711-ad58-d98a5fa3612c", ram, availableIp.getIp());
      assertNotNull(createdServer);
      assert serverLatestJobCompleted.apply(createdServer);

      // get server by name
      Set<Server> response = api.getServerServices().getServersByName(nameOfServer);
      assert (response.size() == 1);

      // restart the server
      api.getServerServices().power(nameOfServer, PowerCommand.RESTART);

      Set<Job> jobs = api.getJobServices().getJobsForObjectName(nameOfServer);
      assert ("RestartVirtualServer".equals(Iterables.getLast(jobs).getCommand().getName()));

      assert serverLatestJobCompleted.apply(createdServer);

      int serverCountAfterAddingOneServer = api.getServerServices().getServerList().size();
      assert serverCountAfterAddingOneServer == serverCountBeforeTest + 1 : "There should be +1 increase in the number of servers since the test started";

      // delete the server
      api.getServerServices().deleteByName(nameOfServer);

      jobs = api.getJobServices().getJobsForObjectName(nameOfServer);
      assert ("DeleteVirtualServer".equals(Iterables.getLast(jobs).getCommand().getName()));

      assert serverLatestJobCompleted.apply(createdServer);

      int serverCountAfterDeletingTheServer = api.getServerServices().getServerList().size();
      assert serverCountAfterDeletingTheServer == serverCountBeforeTest : "There should be the same # of servers as since the test started";

      // make sure that IP is put back to "unassigned"
      assert api.getIpServices().getUnassignedIpList().contains(availableIp);
   }

   /**
    * Starts a servers, verifies that jobs are created correctly and an be retrieved from the job
    * services
    */
   @Test(dependsOnMethods = "testServerLifecycle", enabled = true)
   public void testJobs() {
      final String nameOfServer = "Server" + String.valueOf(new Date().getTime()).substring(6);
      serversToDeleteAfterTheTests.add(nameOfServer);

      Set<Ip> availableIps = api.getIpServices().getUnassignedPublicIpList();

      String ram = Iterables.get(api.getServerServices().getRamSizes(), 0).getName();

      Server createdServer = api.getServerServices().addServer(nameOfServer,
               "GSI-f8979644-e646-4711-ad58-d98a5fa3612c", ram, Iterables.getLast(availableIps).getIp());

      assert serverLatestJobCompleted.apply(createdServer);

      // restart the server
      api.getServerServices().power(nameOfServer, PowerCommand.RESTART);

      Set<Job> jobs = api.getJobServices().getJobsForObjectName(nameOfServer);

      Job latestJob = Iterables.getLast(jobs);
      Long latestJobId = latestJob.getId();

      Job latestJobFetched = Iterables.getOnlyElement(api.getJobServices().getJobsById(latestJobId));

      assert latestJob.equals(latestJobFetched) : "Job and its representation found by ID don't match";

      long[] idsOfAllJobs = new long[jobs.size()];
      int i = 0;
      for (Job job : jobs) {
         idsOfAllJobs[i++] = job.getId();
      }

      Set<Job> jobsFetched = api.getJobServices().getJobsById(idsOfAllJobs);
      assert jobsFetched.size() == jobs.size() : format(
               "Number of jobs fetched by ids doesn't match the number of jobs "
                        + "requested. Requested/expected: %d. Found: %d.", jobs.size(), jobsFetched.size());

      // delete the server
      api.getServerServices().deleteByName(nameOfServer);
   }

   /**
    * Tests common load balancer operations. Also verifies IP services and job services.
    */
   @Test(enabled = true)
   public void testLoadBalancerLifecycle() {
      int lbCountBeforeTest = api.getLoadBalancerServices().getLoadBalancerList().size();

      final String nameOfLoadBalancer = "LoadBalancer" + String.valueOf(new Date().getTime()).substring(6);
      loadBalancersToDeleteAfterTest.add(nameOfLoadBalancer);

      Set<Ip> availableIps = api.getIpServices().getUnassignedPublicIpList();

      if (availableIps.size() < 4)
         throw new SkipException("Not enough available IPs (4 needed) to run the test");
      Iterator<Ip> ipIterator = availableIps.iterator();
      Ip vip = ipIterator.next();
      Ip realIp1 = ipIterator.next();
      Ip realIp2 = ipIterator.next();
      Ip realIp3 = ipIterator.next();

      AddLoadBalancerOptions options = new AddLoadBalancerOptions.Builder().create(LoadBalancerType.LEAST_CONNECTED,
               LoadBalancerPersistenceType.SOURCE_ADDRESS);
      LoadBalancer createdLoadBalancer = api.getLoadBalancerServices().addLoadBalancer(nameOfLoadBalancer,
               IpPortPair.builder().ip(vip).port(80).build(), Arrays.asList(IpPortPair.builder().ip(realIp1).port(80).build(),
               IpPortPair.builder().ip(realIp2).port(80).build()),
               options);
      assertNotNull(createdLoadBalancer);
      assert loadBalancerLatestJobCompleted.apply(createdLoadBalancer);

      // get load balancer by name
      Set<LoadBalancer> response = api.getLoadBalancerServices().getLoadBalancersByName(nameOfLoadBalancer);
      assert (response.size() == 1);
      createdLoadBalancer = Iterables.getOnlyElement(response);
      assertNotNull(createdLoadBalancer.getRealIpList());
      assertEquals(createdLoadBalancer.getRealIpList().size(), 2);
      assertNotNull(createdLoadBalancer.getVirtualIp());
      assertEquals(createdLoadBalancer.getVirtualIp().getIp().getIp(), vip.getIp());

      LoadBalancer editedLoadBalancer = api.getLoadBalancerServices().editLoadBalancerNamed(nameOfLoadBalancer,
               Arrays.asList(IpPortPair.builder().ip(realIp3).port(8181).build()));
      assert loadBalancerLatestJobCompleted.apply(editedLoadBalancer);
      assertNotNull(editedLoadBalancer.getRealIpList());
      assertEquals(editedLoadBalancer.getRealIpList().size(), 1);
      assertEquals(Iterables.getOnlyElement(editedLoadBalancer.getRealIpList()).getIp().getIp(), realIp3.getIp());

      int lbCountAfterAddingOneServer = api.getLoadBalancerServices().getLoadBalancerList().size();
      assert lbCountAfterAddingOneServer == lbCountBeforeTest + 1 : "There should be +1 increase in the number of load balancers since the test started";

      // delete the load balancer
      api.getLoadBalancerServices().deleteByName(nameOfLoadBalancer);

      Set<Job> jobs = api.getJobServices().getJobsForObjectName(nameOfLoadBalancer);
      assert ("DeleteLoadBalancer".equals(Iterables.getLast(jobs).getCommand().getName()));

      assert loadBalancerLatestJobCompleted.apply(createdLoadBalancer);

      int lbCountAfterDeletingTheServer = api.getLoadBalancerServices().getLoadBalancerList().size();
      assert lbCountAfterDeletingTheServer == lbCountBeforeTest : "There should be the same # of load balancers as since the test started";
   }

   /**
    * Tests common server image operations.
    */
   @Test(enabled = true)
   public void testImageLifecycle() {
      GetImageListOptions options = GetImageListOptions.Builder.publicDatabaseServers();
      Set<ServerImage> images = api.getImageServices().getImageList(options);

      Predicate<ServerImage> isDatabaseServer = new Predicate<ServerImage>() {
         @Override
         public boolean apply(@Nullable ServerImage serverImage) {
            return checkNotNull(serverImage).getType() == ServerImageType.DATABASE_SERVER;
         }
      };

      assert Iterables.all(images, isDatabaseServer) : "All of the images should've been of database type";

      ServerImage image = Iterables.getLast(images);
      ServerImage imageFromServer = Iterables
               .getOnlyElement(api.getImageServices().getImagesByName(image.getName()));
      assertEquals(image, imageFromServer);

      try {
         api.getImageServices().editImageDescription(image.getName(), "newDescription");
         throw new TestException("An exception hasn't been thrown where expected; expected GoGridResponseException");
      } catch (GoGridResponseException e) {
         // expected situation - check and proceed
         assertTrue(e.getMessage().contains("GoGridIllegalArgumentException"));
      }

   }

   @Test(enabled = true)
   public void testShellAccess() throws IOException {
      final String nameOfServer = "Server" + String.valueOf(new Date().getTime()).substring(6);
      serversToDeleteAfterTheTests.add(nameOfServer);

      Set<Ip> availableIps = api.getIpServices().getUnassignedIpList();
      Ip availableIp = Iterables.getLast(availableIps);

      Server createdServer = api.getServerServices().addServer(nameOfServer,
               "GSI-f8979644-e646-4711-ad58-d98a5fa3612c", "1", availableIp.getIp());
      assertNotNull(createdServer);
      assert serverLatestJobCompleted.apply(createdServer);

      // get server by name
      Set<Server> response = api.getServerServices().getServersByName(nameOfServer);
      assert (response.size() == 1);
      createdServer = Iterables.getOnlyElement(response);

      Map<String, Credentials> credsMap = api.getServerServices().getServerCredentialsList();
      LoginCredentials instanceCredentials = LoginCredentials.fromCredentials(credsMap.get(createdServer.getName()));
      assertNotNull(instanceCredentials);

      HostAndPort socket = HostAndPort.fromParts(createdServer.getIp().getIp(), 22);
      SocketOpen socketOpen = Guice.createInjector().getInstance(SocketOpen.class);
      Predicate<HostAndPort> socketTester = retry(socketOpen, 180, 5, SECONDS);
      socketTester.apply(socket);

      // check that the get credentials call is the same as this
      assertEquals(api.getServerServices().getServerCredentials(createdServer.getId()), instanceCredentials);

      try {
         assertEquals(api.getServerServices().getServerCredentials(Long.MAX_VALUE), null);
      } catch (AssertionError e) {
         e.printStackTrace();
      }

      // delete the server
      api.getServerServices().deleteByName(nameOfServer);
   }

   /**
    * In case anything went wrong during the tests, removes the objects created in the tests.
    */
   @AfterTest
   public void cleanup() {
      for (String serverName : serversToDeleteAfterTheTests) {
         try {
            api.getServerServices().deleteByName(serverName);
         } catch (Exception e) {
            // it's already been deleted - proceed
         }
      }
      for (String loadBalancerName : loadBalancersToDeleteAfterTest) {
         try {
            api.getLoadBalancerServices().deleteByName(loadBalancerName);
         } catch (Exception e) {
            // it's already been deleted - proceed
         }
      }
   }
}
