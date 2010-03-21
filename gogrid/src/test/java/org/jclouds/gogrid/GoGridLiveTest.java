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
package org.jclouds.gogrid;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jclouds.domain.Credentials;
import org.jclouds.gogrid.domain.*;
import org.jclouds.gogrid.options.AddLoadBalancerOptions;
import org.jclouds.gogrid.options.GetImageListOptions;
import org.jclouds.gogrid.options.GetIpListOptions;
import org.jclouds.gogrid.predicates.LoadBalancerLatestJobCompleted;
import org.jclouds.gogrid.predicates.ServerLatestJobCompleted;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.JschSshClient;
import org.testng.SkipException;
import org.testng.TestException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.*;
import static java.lang.String.format;

/**
 * End to end live test for GoGrid
 *
 * @author Oleksiy Yarmula
 */
@Test(groups = "live", testName = "gogrid.GoGridLiveTest")

public class GoGridLiveTest {

    private GoGridClient client;

    private RetryablePredicate<Server> serverLatestJobCompleted;
    private RetryablePredicate<LoadBalancer> loadBalancerLatestJobCompleted;
    /**
     * Keeps track of the servers, created during the tests,
     * to remove them after all tests complete
     */
    private List<String> serversToDeleteAfterTheTests = new ArrayList<String>();
    private List<String> loadBalancersToDeleteAfterTest = new ArrayList<String>();

    @BeforeGroups(groups = { "live" })
    public void setupClient() {
        String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
        String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

        client = GoGridContextFactory.createContext(user, password, new Log4JLoggingModule())
                .getApi();

        serverLatestJobCompleted = new RetryablePredicate<Server>(
                new ServerLatestJobCompleted(client.getJobServices()),
                800, 20, TimeUnit.SECONDS);
        loadBalancerLatestJobCompleted = new RetryablePredicate<LoadBalancer>(
                new LoadBalancerLatestJobCompleted(client.getJobServices()),
                800, 20, TimeUnit.SECONDS);
    }

    /**
     * Tests server start, reboot and deletion.
     * Also verifies IP services and job services.
     */
    @Test(enabled=true)
    public void testServerLifecycle() {
        int serverCountBeforeTest = client.getServerServices().getServerList().size();

        final String nameOfServer = "Server" + String.valueOf(new Date().getTime()).substring(6);
        serversToDeleteAfterTheTests.add(nameOfServer);

        Set<Ip> availableIps = client.getIpServices().getUnassignedPublicIpList();
        Ip availableIp = Iterables.getLast(availableIps);

        String ram = Iterables.get(client.getServerServices().getRamSizes(), 0).getName();

        Server createdServer = client.getServerServices().addServer(nameOfServer,
                "GSI-f8979644-e646-4711-ad58-d98a5fa3612c",
                ram,
                availableIp.getIp());
        assertNotNull(createdServer);
        assert serverLatestJobCompleted.apply(createdServer);

        //get server by name
        Set<Server> response = client.getServerServices().getServersByName(nameOfServer);
        assert (response.size() == 1);

        //restart the server
        client.getServerServices().power(nameOfServer, PowerCommand.RESTART);

        Set<Job> jobs = client.getJobServices().getJobsForObjectName(nameOfServer);
        assert("RestartVirtualServer".equals(Iterables.getLast(jobs).getCommand().getName()));

        assert serverLatestJobCompleted.apply(createdServer);

        int serverCountAfterAddingOneServer = client.getServerServices().getServerList().size();
        assert serverCountAfterAddingOneServer == serverCountBeforeTest + 1 :
                "There should be +1 increase in the number of servers since the test started";

        //delete the server
        client.getServerServices().deleteByName(nameOfServer);

        jobs = client.getJobServices().getJobsForObjectName(nameOfServer);
        assert("DeleteVirtualServer".equals(Iterables.getLast(jobs).getCommand().getName()));

        assert serverLatestJobCompleted.apply(createdServer);

        int serverCountAfterDeletingTheServer = client.getServerServices().getServerList().size();
        assert serverCountAfterDeletingTheServer == serverCountBeforeTest :
                "There should be the same # of servers as since the test started";

        //make sure that IP is put back to "unassigned"
        assert client.getIpServices().getUnassignedIpList().contains(availableIp);
    }

    /**
     * Starts a servers, verifies that jobs are created correctly and
     * an be retrieved from the job services
     */
    @Test(dependsOnMethods = "testServerLifecycle", enabled=true)
    public void testJobs() {
        final String nameOfServer = "Server" + String.valueOf(new Date().getTime()).substring(6);
        serversToDeleteAfterTheTests.add(nameOfServer);

        Set<Ip> availableIps = client.getIpServices().getUnassignedPublicIpList();

        String ram = Iterables.get(client.getServerServices().getRamSizes(), 0).getName();

        Server createdServer = client.getServerServices().addServer(nameOfServer,
                "GSI-f8979644-e646-4711-ad58-d98a5fa3612c",
                ram,
                Iterables.getLast(availableIps).getIp());

        assert serverLatestJobCompleted.apply(createdServer);

        //restart the server
        client.getServerServices().power(nameOfServer, PowerCommand.RESTART);

        Set<Job> jobs = client.getJobServices().getJobsForObjectName(nameOfServer);

        Job latestJob = Iterables.getLast(jobs);
        Long latestJobId = latestJob.getId();

        Job latestJobFetched = Iterables.getOnlyElement(client.getJobServices().getJobsById(latestJobId));

        assert latestJob.equals(latestJobFetched) : "Job and its reprentation found by ID don't match";

        List<Long> idsOfAllJobs = new ArrayList<Long>();
        for(Job job : jobs) {
            idsOfAllJobs.add(job.getId());
        }

        Set<Job> jobsFetched = client.getJobServices().getJobsById(idsOfAllJobs.toArray(new Long[jobs.size()]));
        assert jobsFetched.size() == jobs.size() : format("Number of jobs fetched by ids doesn't match the number of jobs " +
                "requested. Requested/expected: %d. Found: %d.",
                jobs.size(), jobsFetched.size());

        //delete the server
        client.getServerServices().deleteByName(nameOfServer);
    }


    /**
     * Tests common load balancer operations.
     * Also verifies IP services and job services.
     */
    @Test(enabled=true)
    public void testLoadBalancerLifecycle() {
        int lbCountBeforeTest = client.getLoadBalancerServices().getLoadBalancerList().size();

        final String nameOfLoadBalancer = "LoadBalancer" + String.valueOf(new Date().getTime()).substring(6);
        loadBalancersToDeleteAfterTest.add(nameOfLoadBalancer);

        Set<Ip> availableIps = client.getIpServices().getUnassignedPublicIpList();

        if(availableIps.size() < 4) throw new SkipException("Not enough available IPs (4 needed) to run the test");
        Iterator<Ip> ipIterator = availableIps.iterator();
        Ip vip = ipIterator.next();
        Ip realIp1 = ipIterator.next();
        Ip realIp2 = ipIterator.next();
        Ip realIp3 = ipIterator.next();

        AddLoadBalancerOptions options = new AddLoadBalancerOptions.Builder().
                create(LoadBalancerType.LEAST_CONNECTED, LoadBalancerPersistenceType.SOURCE_ADDRESS);
        LoadBalancer createdLoadBalancer = client.getLoadBalancerServices().
                addLoadBalancer(nameOfLoadBalancer, new IpPortPair(vip, 80),
                        Arrays.asList(new IpPortPair(realIp1, 80),
                                new IpPortPair(realIp2, 80)), options);
        assertNotNull(createdLoadBalancer);
        assert loadBalancerLatestJobCompleted.apply(createdLoadBalancer);

        //get load balancer by name
        Set<LoadBalancer> response = client.getLoadBalancerServices().getLoadBalancersByName(nameOfLoadBalancer);
        assert (response.size() == 1);
        createdLoadBalancer = Iterables.getOnlyElement(response);
        assertNotNull(createdLoadBalancer.getRealIpList());
        assertEquals(createdLoadBalancer.getRealIpList().size(), 2);
        assertNotNull(createdLoadBalancer.getVirtualIp());
        assertEquals(createdLoadBalancer.getVirtualIp().getIp().getIp(), vip.getIp());

        LoadBalancer editedLoadBalancer = client.getLoadBalancerServices().
                editLoadBalancer(nameOfLoadBalancer, Arrays.asList(new IpPortPair(realIp3, 8181)));
        assert loadBalancerLatestJobCompleted.apply(editedLoadBalancer);
        assertNotNull(editedLoadBalancer.getRealIpList());
        assertEquals(editedLoadBalancer.getRealIpList().size(), 1);
        assertEquals(Iterables.getOnlyElement(editedLoadBalancer.getRealIpList()).getIp().getIp(), realIp3.getIp());

        int lbCountAfterAddingOneServer = client.getLoadBalancerServices().getLoadBalancerList().size();
        assert lbCountAfterAddingOneServer == lbCountBeforeTest + 1 :
                "There should be +1 increase in the number of load balancers since the test started";

        //delete the load balancer
        client.getLoadBalancerServices().deleteByName(nameOfLoadBalancer);

        Set<Job> jobs = client.getJobServices().getJobsForObjectName(nameOfLoadBalancer);
        assert("DeleteLoadBalancer".equals(Iterables.getLast(jobs).getCommand().getName()));

        assert loadBalancerLatestJobCompleted.apply(createdLoadBalancer);

        int lbCountAfterDeletingTheServer = client.getLoadBalancerServices().getLoadBalancerList().size();
        assert lbCountAfterDeletingTheServer == lbCountBeforeTest :
                "There should be the same # of load balancers as since the test started";
    }

    /**
     * Tests common server image operations.
     */
    @Test(enabled=true)
    public void testImageLifecycle() {
        GetImageListOptions options = new GetImageListOptions.Builder().publicDatabaseServers();
        Set<ServerImage> images = client.getImageServices().getImageList(options);

        Predicate<ServerImage> isDatabaseServer = new Predicate<ServerImage>() {
            @Override
            public boolean apply(@Nullable ServerImage serverImage) {
                return checkNotNull(serverImage).getType() == ServerImageType.DATABASE_SERVER;
            }
        };

        assert Iterables.all(images, isDatabaseServer) : "All of the images should've been of database type";

        ServerImage image = Iterables.getLast(images);
        ServerImage imageFromServer = Iterables.getOnlyElement(
                client.getImageServices().getImagesByName(image.getName()));
        assertEquals(image, imageFromServer);

        try {
            client.getImageServices().editImageDescription(image.getName(), "newDescription");
            throw new TestException("An exception hasn't been thrown where expected; expected GoGridResponseException");
        } catch(GoGridResponseException e) {
            //expected situation - check and proceed
            assertTrue(e.getMessage().contains("GoGridIllegalArgumentException"));
        }

    }

    @Test(enabled=true)
    public void testShellAccess() throws IOException {
        final String nameOfServer = "Server" + String.valueOf(new Date().getTime()).substring(6);
        serversToDeleteAfterTheTests.add(nameOfServer);

        Set<Ip> availableIps = client.getIpServices().getUnassignedIpList();
        Ip availableIp = Iterables.getLast(availableIps);

        Server createdServer = client.getServerServices().addServer(nameOfServer,
                "GSI-f8979644-e646-4711-ad58-d98a5fa3612c",
                "1",
                availableIp.getIp());
        assertNotNull(createdServer);
        assert serverLatestJobCompleted.apply(createdServer);

        //get server by name
        Set<Server> response = client.getServerServices().getServersByName(nameOfServer);
        assert (response.size() == 1);
        createdServer = Iterables.getOnlyElement(response);

        Map<String, Credentials> credsMap = client.getServerServices().getServerCredentialsList();
        Credentials instanceCredentials = credsMap.get(createdServer.getName());
        assertNotNull(instanceCredentials);

        InetSocketAddress socket = new InetSocketAddress(InetAddress.getByName(createdServer.getIp().getIp()), 22);

        Predicate<InetSocketAddress> socketOpen =
                new RetryablePredicate<InetSocketAddress>(new SocketOpen(), 180, 5, TimeUnit.SECONDS);

        socketOpen.apply(socket);

        SshClient sshClient =
                new JschSshClient(socket, 60000,
                        instanceCredentials.account, instanceCredentials.key);
        sshClient.connect();
        String output = sshClient.exec("df").getOutput();
        assertTrue(output.contains("Filesystem"),
                "The output should've contained filesystem information, but it didn't. Output: " + output);
        sshClient.disconnect();

        //delete the server
        client.getServerServices().deleteByName(nameOfServer);
    }

    /**
     * In case anything went wrong during the tests, removes the objects
     * created in the tests.
     */
    @AfterTest
    public void cleanup() {
        for(String serverName : serversToDeleteAfterTheTests) {
            try {
                client.getServerServices().deleteByName(serverName);
            } catch(Exception e) {
                // it's already been deleted - proceed
            }
        }
        for(String loadBalancerName : loadBalancersToDeleteAfterTest) {
            try {
                client.getLoadBalancerServices().deleteByName(loadBalancerName);
            } catch(Exception e) {
                // it's already been deleted - proceed
            }
        }

    }

}
