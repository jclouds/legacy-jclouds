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
package org.jclouds.ec2.features;

import org.jclouds.ec2.domain.PasswordData;
import org.jclouds.rest.annotations.SinceApiVersion;

import com.google.common.annotations.Beta;

/**
 * Provides access to EC2 Windows Features via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference" >doc</a>
 * @see WindowsAsyncApi
 * @author Adrian Cole
 */
@Beta
@SinceApiVersion("2008-08-08")
public interface WindowsApi {

   /**
    * 
    * Retrieves the encrypted administrator password for the instances running Windows. <h4>Note</h4>
    * 
    * The Windows password is only generated the first time an AMI is launched. It is not generated
    * for rebundled AMIs or after the password is changed on an instance.
    * 
    * The password is encrypted using the key pair that you provided.
    * 
    * @param instanceId
    *           The ID of the instance to query
    * @return password data or null if not available
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-GetPasswordData.html"
    *      />
    */
   PasswordData getPasswordDataForInstance(String instanceId);
}
