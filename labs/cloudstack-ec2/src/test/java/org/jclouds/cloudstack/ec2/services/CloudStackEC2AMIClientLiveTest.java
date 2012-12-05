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
package org.jclouds.cloudstack.ec2.services;

import org.jclouds.cloudstack.ec2.CloudStackEC2ApiMetadata;
import org.jclouds.cloudstack.ec2.CloudStackEC2Client;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilderSpec;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.predicates.InstanceStateRunning;
import org.jclouds.ec2.services.AMIClientLiveTest;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.testng.Assert.*;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudStackEC2AMIClientLiveTest")
public class CloudStackEC2AMIClientLiveTest extends AMIClientLiveTest {



    private CloudStackEC2Client cloudstackEc2Client;
    private CloudStackAMIClient cloudstackClient;

    private String defaultZone = null;
    private RunningInstance instance;
    private String instanceId;

    private TemplateBuilderSpec cloudstackTemplate;
    private String createdImageId;

    public CloudStackEC2AMIClientLiveTest() {
        provider = "cloudstack-ec2";
    }

    @Override
    protected Properties setupProperties() {
        Properties overrides = super.setupProperties();
        String ebsSpec = checkNotNull(setIfTestSystemPropertyPresent(overrides, provider + ".ebs-template"), provider
                + ".ebs-template");
        cloudstackTemplate = TemplateBuilderSpec.parse(ebsSpec);
        return overrides;
    }

    @Override
    @BeforeClass(groups = {"integration", "live"})
    public void setupContext() {
        initializeContext();
        cloudstackEc2Client = view.unwrap(CloudStackEC2ApiMetadata.CONTEXT_TOKEN).getApi();
        cloudstackClient = cloudstackEc2Client.getAMIServices();
        runningTester = new RetryablePredicate<RunningInstance>(new InstanceStateRunning(cloudstackEc2Client), 600, 5,
                TimeUnit.SECONDS);

        if (cloudstackTemplate != null) {
            Template template = view.getComputeService().templateBuilder().from(cloudstackTemplate).build();
            regionId = template.getLocation().getId();
            imageId = template.getImage().getProviderId();
            for (Image image : cloudstackClient.describeImagesInRegion(regionId)) {
                if (ebsBackedImageName.equals(image.getName()))
                    cloudstackClient.deregisterImageInRegion(regionId, image.getId());
            }
        }
        if (imageId != null) {
            runInstance();
        }
    }

    private void runInstance() {
        Reservation<? extends RunningInstance> runningInstances = cloudstackEc2Client.getInstanceServices().runInstancesInRegion(
                regionId, defaultZone, imageId, 1, 1);
        instance = getOnlyElement(concat(runningInstances));
        instanceId = instance.getId();
        assertTrue(runningTester.apply(instance), instanceId + "didn't achieve the state running!");
        instance = (RunningInstance) (getOnlyElement(concat(cloudstackEc2Client.getInstanceServices().describeInstancesInRegion(regionId,
                instanceId))));
    }

    @Override
    @Test
    public void testDescribeImages() {
        Set<? extends Image> allResults = cloudstackClient.describeImagesInRegion(null);
        assertNotNull(allResults);
        assert allResults.size() >= 1 : allResults.size();
        Iterator<? extends Image> iterator = allResults.iterator();
        String id1 = iterator.next().getId();
        Set<? extends Image> twoResults = cloudstackClient.describeImagesInRegion(null, imageIds(id1));
        assertNotNull(twoResults);
        assertEquals(twoResults.size(), 1);
        iterator = twoResults.iterator();
        assertEquals(iterator.next().getId(), id1);
    }



    @Test
    public void testCreateImage(){
        cloudstackEc2Client.getInstanceServices().stopInstancesInRegion(regionId,false,instanceId);
        createdImageId = cloudstackClient.createImageInRegion(regionId,"jclouds-cloudstack-ec2",instanceId);
    }

    @Test(dependsOnMethods = "testCreateImage")
    void testDeregisterImageInRegion(){
        cloudstackClient.deregisterImageInRegion(regionId, createdImageId);
    }

    @Test
    void testGetLaunchPermissionForImageInRegion(){
        cloudstackClient.getLaunchPermissionForImageInRegion(regionId, imageId);
    }


    @Override
    @Test
    public void testCreateAndListEBSBackedImage() throws Exception {
        //just a place holder so that super class test doesn't run
    }

    @Override
    @Test(dependsOnMethods = "testCreateAndListEBSBackedImage")
    public void testGetLaunchPermissionForImage() {
       //just a place holder so that super class test doesn't run
    }

    @Override
    @AfterClass(groups = {"integration", "live"})
    protected void tearDownContext() {
        for (String imageId : imagesToDeregister)
            cloudstackClient.deregisterImageInRegion(regionId, imageId);

        for (String snapshotId : snapshotsToDelete)
            cloudstackEc2Client.getElasticBlockStoreServices().deleteSnapshotInRegion(regionId, snapshotId);

        if (instanceId != null) {
            cloudstackEc2Client.getInstanceServices().terminateInstancesInRegion(regionId, instanceId);
        }
        super.tearDownContext();
    }


}
