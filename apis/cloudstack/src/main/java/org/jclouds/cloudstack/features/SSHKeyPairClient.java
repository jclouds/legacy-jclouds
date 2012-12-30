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

package org.jclouds.cloudstack.features;

import java.util.Set;
import org.jclouds.cloudstack.domain.SshKeyPair;
import org.jclouds.cloudstack.options.ListSSHKeyPairsOptions;

/**
 * Provides synchronous access to CloudStack SSHKeyPair features.
 *
 * @author Vijay Kiran
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.8/TOC_User.html"
 *      />
 */
public interface SSHKeyPairClient {
   /**
    * Returns a list of {@link SshKeyPair}s registered by current user.
    *
    * @param options if present, how to constrain the list
    * @return Set of {@link SshKeyPair}s matching the current constrains or
    *         empty set if no SshKeyPairs found.
    */
   Set<SshKeyPair> listSSHKeyPairs(ListSSHKeyPairsOptions... options);


   /**
    * Registers a {@link SshKeyPair} with the given name and  public kay material.
    *
    * @param name      of the keypair
    * @param publicKey Public key material of the keypair
    * @return Created SshKeyPair.
    */
   SshKeyPair registerSSHKeyPair(String name, String publicKey);

   /**
    * Creates a {@link SshKeyPair} with specified name.
    *
    * @param name of the SshKeyPair.
    * @return Created SshKeyPair.
    */
   SshKeyPair createSSHKeyPair(String name);

   /**
    * Retrieves the {@link SSHKeyPairClient} with given name.
    *
    * @param name name of the key pair
    * @return SSH Key pair or null if not found.
    */
   SshKeyPair getSSHKeyPair(String name);

   /**
    * Deletes the {@link SSHKeyPairClient} with given name.
    *
    * @param name name of the key pair
    * @return
    */
   void deleteSSHKeyPair(String name);

}
