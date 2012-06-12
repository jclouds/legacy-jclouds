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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertNotNull;

import java.util.Map;
import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientLiveTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code KeyPairClient}
 * 
 * @author Michael Arnold
 */
@Test(groups = "live", testName = "KeyPairClientLiveTest")
public class KeyPairClientLiveTest extends BaseNovaClientLiveTest {

   public void testListKeyPairs() throws Exception {
      for (String zoneId : novaContext.getApi().getConfiguredZones()) {
         KeyPairClient client = novaContext.getApi().getKeyPairExtensionForZone(zoneId).get();
         Set<Map<String, KeyPair>> keyPairsList = client.listKeyPairs();
         assertNotNull(keyPairsList);
      }
   }

   public void testCreateAndDeleteKeyPair() throws Exception {
      final String KEYPAIR_NAME = "testkp";
      for (String zoneId : novaContext.getApi().getConfiguredZones()) {
         KeyPairClient client = novaContext.getApi().getKeyPairExtensionForZone(zoneId).get();
         KeyPair keyPair = null;
         try {
            keyPair = client.createKeyPair(KEYPAIR_NAME);
            assertNotNull(keyPair);
         } finally {
            if (keyPair != null) {
               client.deleteKeyPair(KEYPAIR_NAME);
            }
         }
      }
   }

   public void testCreateAndDeleteKeyPairWithPublicKey() throws Exception {
      final String KEYPAIR_NAME = "testkp";
      final String PUBLIC_KEY = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCrrBREFxz3002l1HuXz0+UOdJQ/mOYD5DiJwwB/TOybwIKQJPOxJWA9gBoo4k9dthTKBTaEYbzrll7iZcp59E80S6mNiAr3mUgi+x5Y8uyXeJ2Ws+h6peVyFVUu9epkwpcTd1GVfdcVWsTajwDz9+lxCDhl0RZKDFoT0scTxbj/w== nova@nv-aw2az2-api0002";

      for (String zoneId : novaContext.getApi().getConfiguredZones()) {
         KeyPairClient client = novaContext.getApi().getKeyPairExtensionForZone(zoneId).get();
         KeyPair keyPair = null;
         try {
            keyPair = client.createKeyPairWithPublicKey(KEYPAIR_NAME, PUBLIC_KEY);
            assertNotNull(keyPair);
         } finally {
            if (keyPair != null) {
               client.deleteKeyPair(KEYPAIR_NAME);
            }
         }
      }
   }
}
