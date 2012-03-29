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
package org.jclouds.aws.ec2.services;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.services.KeyPairClient;
import org.jclouds.javax.annotation.Nullable;

/**
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
public interface AWSKeyPairClient extends KeyPairClient {

   /**
    * Imports the public key from an RSA key pair that you created with a third-party tool. Compare
    * this with CreateKeyPair, in which AWS creates the key pair and gives the keys to you (AWS
    * keeps a copy of the public key). With ImportKeyPair, you create the key pair and give AWS just
    * the public key. The private key is never transferred between you and AWS.
    * 
    * <p/>
    * You can easily create an RSA key pair on Windows and Linux using the ssh-keygen command line
    * tool (provided with the standard OpenSSH installation). Standard library support for RSA key
    * pair creation is also available in Java, Ruby, Python, and many other programming languages.
    * 
    * <p/>
    * <h4>Supported Formats</h4>
    * <ul>
    * <li>OpenSSH public key format (e.g., the format in ~/.ssh/authorized_keys)</li>
    * <li>Base64 encoded DER format</li>
    * <li>SSH public key file format as specified in RFC4716</li>
    * </ul>
    * DSA keys are not supported. Make sure your key generator is set up to create RSA keys.
    * <p/>
    * Supported lengths: 1024, 2048, and 4096.
    * <p/>
    * 
    * @param region
    *           region to import the key into
    * @param keyName
    *           A unique name for the key pair. Accepts alphanumeric characters, spaces, dashes, and
    *           underscores.
    * @param publicKeyMaterial
    *           The public key
    * @return imported key including fingerprint
    */
   KeyPair importKeyPairInRegion(@Nullable String region, String keyName, String publicKeyMaterial);

}
