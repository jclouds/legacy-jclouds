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

package org.jclouds.tools.ebsresize;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.domain.*;
import org.jclouds.aws.ec2.domain.Volume;
import org.jclouds.aws.ec2.predicates.InstanceStateRunning;
import org.jclouds.aws.ec2.reference.EC2Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.*;
import org.jclouds.domain.Credentials;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.easymock.classextension.EasyMock.*;


/**
 * Tests the resizing of instance's root EBS device.
 *
 * This test creates an instance, then resizes its root volume
 * (which includes stopping it) and terminates it afterwards.
 *
 * @author Oleksiy Yarmula
 */
@Test(groups = {"live" }, enabled = false, testName = "ec2.InstanceVolumeManagerLiveTest")
public class InstanceVolumeManagerLiveTest {

    private String tag;
    private String secret;
    private InstanceVolumeManager manager;
    private ComputeService client;
    private Template template;
    private Predicate<RunningInstance> instanceRunning;
    private RunningInstance instanceCreated;

    @BeforeTest
    public void setupClient() throws IOException {

        //set up the constants needed to run
        String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
        String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

        String secretKeyFile = checkNotNull(System.getProperty("jclouds.test.ssh.keyfile"),
                "jclouds.test.ssh.keyfile");
        tag = "ec2";

        //ended setting up the constants

        secret = Files.toString(new File(secretKeyFile), Charsets.UTF_8);
        assert secret.startsWith("-----BEGIN RSA PRIVATE KEY-----") : "invalid key:\n" + secret;

        //create a new manager
        Properties testOnlyProperties = new Properties();
        testOnlyProperties.put(EC2Constants.PROPERTY_EC2_AMI_OWNERS, "819060954727");
        manager = new InstanceVolumeManager(user, password, testOnlyProperties);
        client = manager.getContext().getComputeService();

        TemplateBuilder templateBuilder = client.templateBuilder();
        template =
                templateBuilder.imageId("ami-2675974f").
                        build();
        template.getOptions().installPrivateKey(secret);

        instanceRunning =
                new RetryablePredicate<RunningInstance>(new InstanceStateRunning(manager.getApi().
                        getInstanceServices()),
                        600, 10, TimeUnit.SECONDS);
    }

    @Test
    void testResizeVolume() {
        SortedSet<NodeMetadata> nodes = Sets.newTreeSet(
                client.runNodesWithTag(tag, 1, template).values()
        );
        assert nodes.size() == 1 : "Expected to have 1 nodes created; found: " + nodes.size();

        NodeMetadata launchedNode = Iterables.getOnlyElement(nodes);

        AvailabilityZone availabilityZone = AvailabilityZone.fromValue(launchedNode.getLocationId());
        Region region = getRegionNameForAvailabilityZone(availabilityZone);

        waitForInstanceInRunningState(launchedNode.getId(), region);

        manager.resizeVolume(launchedNode.getId(), region, new Credentials("ubuntu", ""),
                secret, 6);

        instanceCreated =
                Iterables.getOnlyElement(
                        Iterables.getOnlyElement(
                                manager.getApi().
                                        getInstanceServices().
                                        describeInstancesInRegion(region, launchedNode.getId()
                                )
                        )
                );
        Volume volumeAttached = manager.getEbsApi().getRootVolumeForInstance(instanceCreated);
        assert volumeAttached.getSize() == 6;
    }

    @AfterTest
    public void close() {
        manager.getApi().getInstanceServices().terminateInstancesInRegion
                (instanceCreated.getRegion(), instanceCreated.getId());
        manager.closeContext();
    }

    /**
     * Returns region that has the given availability zone, or null,
     *  when nothing is found.
     * This operates under the assumption that names of availability zones are
     * unique, or else it returns the first matched region.
     *
     * @param zone
     *          zone that needs to be matched with region. This can not be null.
     * @return region
     *          that has the provided zone
     */
    private Region getRegionNameForAvailabilityZone(AvailabilityZone zone) {
        for (Region region : ImmutableSet.of(Region.DEFAULT, Region.EU_WEST_1, Region.US_EAST_1,
                Region.US_WEST_1)) {
            SortedSet<AvailabilityZoneInfo> allResults = Sets.newTreeSet(manager.getApi().
                    getAvailabilityZoneAndRegionServices()
                    .describeAvailabilityZonesInRegion(region));
            for(AvailabilityZoneInfo zoneInfo : allResults) {
                if(zone == zoneInfo.getZone()) return zoneInfo.getRegion();
            }
        }
        return null; /*by contract*/
    }

    /**
     * Blocks until the instance with given id and region is
     *      in 'running' state.
     *
     * @param id
     *          id of the instance being run
     * @param region
     *              region where the instance is launched
     */
    private void waitForInstanceInRunningState(String id, Region region) {
        RunningInstance instanceMock =
                createInstanceFromIdAndRegion(id, region);

        checkState(instanceRunning.apply(instanceMock),
                /*or throw*/ "Couldn't run the instance");
    }

    /**
     * Uses EasyMock to create a mock {@link RunningInstance}.
     *
     * @param id
     *          id of the instance
     * @param region
     *          region where it's launched
     * @return
     *        instance of mock {@link org.jclouds.aws.ec2.domain.RunningInstance}
     */
    private RunningInstance createInstanceFromIdAndRegion(String id, Region region) {
        RunningInstance instanceMock = createMock(RunningInstance.class);
        expect(instanceMock.getId()).andStubReturn(id);
        expect(instanceMock.getRegion()).andStubReturn(region);
        replay(instanceMock);
        return instanceMock;
    }

}
