/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.SortedSet;

import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.EC2ContextFactory;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.aws.ec2.domain.Volume;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code ElasticBlockStoreClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "ec2.ElasticBlockStoreClientLiveTest")
public class ElasticBlockStoreClientLiveTest {

   private ElasticBlockStoreClient client;
   private RestContext<EC2AsyncClient, EC2Client> context;
   private String volumeId;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      context = EC2ContextFactory.createContext(user, password, new Log4JLoggingModule());
      client = context.getApi().getElasticBlockStoreServices();
   }

   @Test
   void testDescribeVolumes() {
      for (Region region : ImmutableSet.of(Region.DEFAULT, Region.EU_WEST_1, Region.US_EAST_1,
               Region.US_WEST_1)) {
         SortedSet<Volume> allResults = Sets.newTreeSet(client.describeVolumesInRegion(region));
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            Volume volume = allResults.last();
            SortedSet<Volume> result = Sets.newTreeSet(client.describeVolumesInRegion(region,
                     volume.getId()));
            assertNotNull(result);
            Volume compare = result.last();
            assertEquals(compare, volume);
         }
      }
   }

   @Test
   void testCreateVolumeInAvailabilityZone() {
      Volume expected = client.createVolumeInAvailabilityZone(AvailabilityZone.US_EAST_1B, 1);
      assertNotNull(expected);
      System.out.println(expected);
      assertEquals(expected.getAvailabilityZone(), AvailabilityZone.US_EAST_1B);

      this.volumeId = expected.getId();

      SortedSet<Volume> result = Sets.newTreeSet(client.describeVolumesInRegion(Region.DEFAULT,
               expected.getId()));
      assertNotNull(result);
      assertEquals(result.size(), 1);
      Volume volume = result.iterator().next();
      assertEquals(volume.getId(), expected.getId());
   }

   @Test(dependsOnMethods = "testCreateVolumeInAvailabilityZone")
   void testDeleteInRegion() {
      client.deleteVolumeInRegion(Region.DEFAULT, volumeId);
      SortedSet<Volume> result = Sets.newTreeSet(client.describeVolumesInRegion(Region.DEFAULT,
               volumeId));
      assertEquals(result.size(), 1);
      Volume volume = result.iterator().next();
      assertEquals(volume.getStatus(), Volume.Status.DELETING);
   }

   @Test
   void testCreateVolumeFromSnapshotInAvailabilityZone() {
      // Volume result =
      // client.createVolumeFromSnapshotInAvailabilityZone(AvailabilityZone.US_EAST_1A, snapshotId);
      // assertNotNull(result);
      //      
      // SortedSet<Volume> result = Sets.newTreeSet(client.describeVolumesInRegion(
      // Region.DEFAULT, result.getId()));
      // assertNotNull(result);
      // assertEquals(result.size(), 1);
      // Volume volume = result.iterator().next();
      // assertEquals(volume, result);
   }

   @AfterTest
   public void shutdown() {
      context.close();
   }
}
