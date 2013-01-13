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
package org.jclouds.joyent.cloudapi.v6_5.features;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.joyent.cloudapi.v6_5.domain.Key;
import org.jclouds.joyent.cloudapi.v6_5.features.KeyApi;
import org.jclouds.joyent.cloudapi.v6_5.internal.BaseJoyentCloudApiLiveTest;
import org.jclouds.ssh.SshKeys;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "KeyApiLiveTest")
public class KeyApiLiveTest extends BaseJoyentCloudApiLiveTest {

   @Test
   public void testListAndGetKeys() throws Exception {
      KeyApi api = cloudApiContext.getApi().getKeyApi();
      Set<Key> response = api.list();
      assert null != response;
      for (Key key : response) {
         Key newDetails = api.get(key.getName());
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
      KeyApi api = cloudApiContext.getApi().getKeyApi();

      Key newKey = api.create(Key.builder().name(fingerprint).key(keyText).build());
      assertEquals(newKey.getName(), fingerprint);
      assertEquals(newKey.get(), keyText);

      newKey = api.get(fingerprint);
      assertEquals(newKey.getName(), fingerprint);
      assertEquals(newKey.get(), keyText);
   }

   @Test(dependsOnMethods = "testCreateKey", expectedExceptions = IllegalStateException.class)
   public void testDuplicateKey() {
      KeyApi api = cloudApiContext.getApi().getKeyApi();
      api.create(Key.builder().name(fingerprint).key(keyText).build());
   }

   @Test(dependsOnMethods = "testDuplicateKey")
   public void testDestroyKey() {
      final KeyApi api = cloudApiContext.getApi().getKeyApi();
      api.delete(fingerprint);
      // not that eventhough the key is destroyed it is visible via GET for at
      // least 45 seconds. This may be a cache issue on the server
   }

}
