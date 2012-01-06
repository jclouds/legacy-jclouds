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
package org.jclouds.ec2.services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;
import java.util.SortedSet;

import org.jclouds.aws.domain.Region;
import org.jclouds.compute.BaseVersionedServiceLiveTest;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.PublicIpInstanceIdPair;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Module;

/**
 * Tests behavior of {@code ElasticIPAddressClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "ElasticIPAddressClientLiveTest")
public class ElasticIPAddressClientLiveTest extends BaseVersionedServiceLiveTest {
   public ElasticIPAddressClientLiveTest() {
      provider = "ec2";
   }

   private ElasticIPAddressClient client;
   private RestContext<EC2Client, EC2AsyncClient> context;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new ComputeServiceContextFactory().createContext(provider, ImmutableSet.<Module> of(new Log4JLoggingModule()),
               overrides).getProviderSpecificContext();
      client = context.getApi().getElasticIPAddressServices();
   }

   @Test
   void testDescribeAddresses() {
      for (String region : Lists.newArrayList(null, Region.EU_WEST_1, Region.US_EAST_1, Region.US_WEST_1,
               Region.AP_SOUTHEAST_1)) {
         SortedSet<PublicIpInstanceIdPair> allResults = Sets.newTreeSet(client.describeAddressesInRegion(region));
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            PublicIpInstanceIdPair pair = allResults.last();
            SortedSet<PublicIpInstanceIdPair> result = Sets.newTreeSet(client.describeAddressesInRegion(region, pair
                     .getPublicIp()));
            assertNotNull(result);
            PublicIpInstanceIdPair compare = result.last();
            assertEquals(compare, pair);
         }
      }
   }

   @AfterTest
   public void shutdown() {
      context.close();
   }
}
