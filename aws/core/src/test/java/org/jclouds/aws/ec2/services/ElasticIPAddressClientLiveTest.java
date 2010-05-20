/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.SortedSet;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.EC2ContextFactory;
import org.jclouds.aws.ec2.domain.PublicIpInstanceIdPair;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;
import com.google.inject.internal.Lists;

/**
 * Tests behavior of {@code ElasticIPAddressClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "ec2.ElasticIPAddressClientLiveTest")
public class ElasticIPAddressClientLiveTest {

   private ElasticIPAddressClient client;
   private RestContext<EC2AsyncClient, EC2Client> context;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      context = EC2ContextFactory.createContext(user, password, new Log4JLoggingModule())
               .getProviderSpecificContext();
      client = context.getApi().getElasticIPAddressServices();
   }

   @Test
   void testDescribeAddresses() {
      for (String region : Lists.newArrayList(null, Region.EU_WEST_1, Region.US_EAST_1,
               Region.US_WEST_1, Region.AP_SOUTHEAST_1)) {
         SortedSet<PublicIpInstanceIdPair> allResults = Sets.newTreeSet(client
                  .describeAddressesInRegion(region));
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            PublicIpInstanceIdPair pair = allResults.last();
            SortedSet<PublicIpInstanceIdPair> result = Sets.newTreeSet(client
                     .describeAddressesInRegion(region, pair.getPublicIp()));
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
