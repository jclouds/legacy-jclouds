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
package org.jclouds.openstack.nova.v1_1.extensions;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.nova.v1_1.domain.KeyPair;

/**
 * Provides synchronous access to Security Groups.
 * <p/>
 * 
 * @see KeyPairAsyncClient
 * @author Jeremy Daggett
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface KeyPairClient {

   /**
    * List all Key Pairs.
    * 
    * @return all Key Pairs
    */
   Set<Map<String,KeyPair>> listKeyPairs();

   /**
    * Create a Key Pair.
    * 
    * @return a Key Pair
    */
   KeyPair createKeyPair(String name);

   /**
    * Create a Key Pair with a public key.
    * 
    * @return a Key Pair with a public key.
    */
   KeyPair createKeyPairWithPublicKey(String name, String publicKey);

   /**
    * Delete a Key Pairs.
    * 
    * @return
    */
   Boolean deleteKeyPair(String name);

}
