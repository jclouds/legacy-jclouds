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

import com.google.common.collect.Iterables;
import org.jclouds.gogrid.domain.*;
import org.jclouds.gogrid.predicates.ServerLatestJobCompleted;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertNotNull;
import static java.lang.String.format;

/**
 * End to end live test for GoGrid
 *
 * @author Oleksiy Yarmula
 */
@Test(groups = "live", testName = "gogrid.GoGridLiveTest")

public class GoGridLiveTest {

    private GoGridClient client;

    private RetryablePredicate<Server> latestJobCompleted;
    /**
     * Keeps track of the servers, created during the tests,
     * to remove them after all tests complete
     */
    private List<String> serversToDeleteAfterTheTests = new ArrayList<String>();

    @BeforeGroups(groups = { "live" })
    public void setupClient() {
        String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
        String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

        client = GoGridContextFactory.createContext(user, password, new Log4JLoggingModule())
                .getApi();


        latestJobCompleted = new RetryablePredicate<Server>(new ServerLatestJobCompleted(client.getJobServices()),
                240, 15, TimeUnit.SECONDS);
    }

    /**
     * Tests server start, reboot and deletion.
     * Also verifies IP services and job services.
     */
    @Test(enabled=false)
    public void testServerLifecycle() {
        int serverCountBeforeTest = client.getServerServices().getServerList().size();

        final String nameOfServer = "ServerCreated" + String.valueOf(new Date().getTime()).substring(8);
        serversToDeleteAfterTheTests.add(nameOfServer);

        Set<Ip> availableIps = client.getIpServices().getUnassignedIpList();
        Ip availableIp = Iterables.getLast(availableIps);

        Server createdServer = client.getServerServices().addServer(nameOfServer,
                "GSI-f8979644-e646-4711-ad58-d98a5fa3612c",
                "1",
                availableIp.getIp());
        assertNotNull(createdServer);
        assert latestJobCompleted.apply(createdServer);

        //get server by name
        Set<Server> response = client.getServerServices().getServersByName(nameOfServer);
        assert (response.size() == 1);

        //restart the server
        client.getServerServices().power(nameOfServer, PowerCommand.RESTART);

        Set<Job> jobs = client.getJobServices().getJobsForObjectName(nameOfServer);
        assert("RestartVirtualServer".equals(Iterables.getLast(jobs).getCommand().getName()));

        assert latestJobCompleted.apply(createdServer);

        int serverCountAfterAddingOneServer = client.getServerServices().getServerList().size();
        assert serverCountAfterAddingOneServer == serverCountBeforeTest + 1 :
                "There should be +1 increase in the number of servers since the test started";

        //delete the server
        client.getServerServices().deleteByName(nameOfServer);

        jobs = client.getJobServices().getJobsForObjectName(nameOfServer);
        assert("DeleteVirtualServer".equals(Iterables.getLast(jobs).getCommand().getName()));

        assert latestJobCompleted.apply(createdServer);

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
    @Test(/*dependsOnMethods = "testServerLifecycle", */ enabled=false)
    public void testJobs() {
        final String nameOfServer = "ServerCreated" + String.valueOf(new Date().getTime()).substring(8);
        serversToDeleteAfterTheTests.add(nameOfServer);

        Set<Ip> availableIps = client.getIpServices().getUnassignedIpList();

        Server createdServer = client.getServerServices().addServer(nameOfServer,
                "GSI-f8979644-e646-4711-ad58-d98a5fa3612c",
                "1",
                Iterables.getLast(availableIps).getIp());

        assert latestJobCompleted.apply(createdServer);

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


    @Test(enabled=false)
    public void testLoadBalancers() {
        Set<LoadBalancer> balancers = client.getLoadBalancerServices().getLoadBalancerList();
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
    }

}
