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

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides access to EC2 via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 45, timeUnit = TimeUnit.SECONDS)
public interface KeyPairClient {

   /**
    * Creates a new 2048-bit RSA key pair with the specified name. The public key is stored by
    * Amazon EC2 and the private key is displayed on the console. The private key is returned as an
    * unencrypted PEM encoded PKCS#8 private key. If a key with the specified name already exists,
    * Amazon EC2 returns an error.
    * 
    * @param region
    *           Key pairs (to connect to instances) are Region-specific.
    * @param keyName
    *           A unique name for the key pair.
    * 
    * @see #runInstances
    * @see #describeKeyPairs
    * @see #deleteKeyPair
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateKeyPair.html"
    *      />
    */
   KeyPair createKeyPairInRegion(@Nullable String region, String keyName);

   /**
    * Returns information about key pairs available to you. If you specify key pairs, information
    * about those key pairs is returned. Otherwise, information for all registered key pairs is
    * returned.
    * 
    * @param region
    *           Key pairs (to connect to instances) are Region-specific.
    * @param keyPairNames
    *           Key pairs to describe.
    * 
    * @see #runInstances
    * @see #describeAvailabilityZones
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeKeyPairs.html"
    *      />
    */
   Set<KeyPair> describeKeyPairsInRegion(@Nullable String region, String... keyPairNames);

   /**
    * Deletes the specified key pair, by removing the public key from Amazon EC2. You must own the
    * key pair
    * 
    * @param region
    *           Key pairs (to connect to instances) are Region-specific.
    * @param keyName
    *           Name of the key pair to delete
    * 
    * @see #describeKeyPairs
    * @see #createKeyPair
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DeleteKeyPair.html"
    *      />
    */
   void deleteKeyPairInRegion(@Nullable String region, String keyName);

}
