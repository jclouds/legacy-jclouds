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

package org.jclouds.aws.simpledb;

import static org.jclouds.aws.simpledb.SimpleDBPropertiesBuilder.DEFAULT_REGIONS;

import org.jclouds.simpledb.SimpleDBClientLiveTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code SimpleDBClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "AWSSimpleDBClientLiveTest")
public class AWSSimpleDBClientLiveTest extends SimpleDBClientLiveTest {
   public AWSSimpleDBClientLiveTest(){
      provider = "aws-simpledb";
   }
   @Test
   void testListDomainsInRegion() throws InterruptedException {
      for (String region : DEFAULT_REGIONS) {
         listDomainInRegion(region);
      }
   }

   @Test
   void testCreateDomainInRegions() throws InterruptedException {
      String domainName = PREFIX + "1";

      for (String region : DEFAULT_REGIONS) {
         domainName = createDomainInRegion(region, domainName);
      }
   }

}
