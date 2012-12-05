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

import com.google.common.collect.Sets;
import org.jclouds.cloudstack.ec2.CloudStackEC2ApiMetadata;
import org.jclouds.cloudstack.ec2.CloudStackEC2Client;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.services.KeyPairClient;
import org.jclouds.ec2.services.KeyPairClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.SortedSet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudStackEC2KeyPairClientLiveTest")
public class CloudStackEC2KeyPairClientLiveTest extends KeyPairClientLiveTest {

    private CloudStackEC2Client cloudstackEc2Client;
    private KeyPairClient cloudstackClient;
    private String regionId = "AmazonEC2";


    public CloudStackEC2KeyPairClientLiveTest() {
      provider = "cloudstack-ec2";
   }

    @Override
    @BeforeClass(groups = {"integration", "live"})
    public void setupContext() {
        initializeContext();
        cloudstackEc2Client = view.unwrap(CloudStackEC2ApiMetadata.CONTEXT_TOKEN).getApi();
        cloudstackClient = cloudstackEc2Client.getKeyPairServices();
    }

    @Test
    void testDescribeKeyPairs() {
        SortedSet<KeyPair> allResults = Sets.newTreeSet(cloudstackClient.describeKeyPairsInRegion(regionId));
        assertNotNull(allResults);
        if (allResults.size() >= 1) {
            KeyPair pair = allResults.last();
            SortedSet<KeyPair> result = Sets.newTreeSet(cloudstackClient.describeKeyPairsInRegion(regionId, pair.getKeyName()));
            assertNotNull(result);
            KeyPair compare = result.last();
            assertEquals(compare, pair);
        }
    }

    public static final String PREFIX = System.getProperty("user.name") + "-cloudstack-ec2";

    @Test
    void testCreateKeyPair() {
        String keyName = PREFIX + "1";
        cleanUpKeyPair(keyName);

        KeyPair result = cloudstackClient.createKeyPairInRegion(null, keyName);
        assertNotNull(result);
        assertNotNull(result.getKeyMaterial());
        assertNotNull(result.getSha1OfPrivateKey());
        assertEquals(result.getKeyName(), keyName);

        Set<KeyPair> twoResults = Sets.newLinkedHashSet(cloudstackClient.describeKeyPairsInRegion(null, keyName));
        assertNotNull(twoResults);
        assertEquals(twoResults.size(), 1);
        KeyPair listPair = twoResults.iterator().next();
        assertEquals(listPair.getKeyName(), result.getKeyName());
        assertEquals(listPair.getSha1OfPrivateKey(), result.getSha1OfPrivateKey());
        cleanUpKeyPair(keyName);
    }

    private void cleanUpKeyPair(String keyName) {
        try {
            cloudstackClient.deleteKeyPairInRegion(null, keyName);
        } catch (Exception e) {
        }
    }
}
