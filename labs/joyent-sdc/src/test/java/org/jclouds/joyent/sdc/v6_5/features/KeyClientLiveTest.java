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
package org.jclouds.joyent.sdc.v6_5.features;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.crypto.SshKeys;
import org.jclouds.joyent.sdc.v6_5.domain.Key;
import org.jclouds.joyent.sdc.v6_5.internal.BaseSDCClientLiveTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "KeyClientLiveTest")
public class KeyClientLiveTest extends BaseSDCClientLiveTest {

   @Test
   public void testListAndGetKeys() throws Exception {
      KeyClient client = sdcContext.getApi().getKeyClient();
      Set<Key> response = client.list();
      assert null != response;
      for (Key key : response) {
         Key newDetails = client.get(key.getName());
         assertEquals(newDetails.getName(), key.getName());
         assertEquals(newDetails.get(), key.get());
         assertEquals(newDetails.getCreated(), key.getCreated());
      }

   }
   
   private String keyText;
   private String fingerprint;

   @BeforeTest
   public void initKeys() {
      keyText = SshKeys.generate().get("public");
      fingerprint = SshKeys.fingerprintPublicKey(keyText);
   }

   public void testCreateKey() {
      KeyClient client = sdcContext.getApi().getKeyClient();

      Key newKey = client.create(Key.builder().name(fingerprint).key(keyText).build());
      assertEquals(newKey.getName(), fingerprint);
      assertEquals(newKey.get(), keyText);

      newKey = client.get(fingerprint);
      assertEquals(newKey.getName(), fingerprint);
      assertEquals(newKey.get(), keyText);
   }

   @Test(dependsOnMethods = "testCreateKey", expectedExceptions = IllegalStateException.class)
   public void testDuplicateKey() {
      KeyClient client = sdcContext.getApi().getKeyClient();
      client.create(Key.builder().name(fingerprint).key(keyText).build());
   }

   @Test(dependsOnMethods = "testDuplicateKey")
   public void testDestroyKey() {
      final KeyClient client = sdcContext.getApi().getKeyClient();
      client.delete(fingerprint);
      // not that eventhough the key is destroyed it is visible via GET for at
      // least 45 seconds. This may be a cache issue on the server
   }

}
