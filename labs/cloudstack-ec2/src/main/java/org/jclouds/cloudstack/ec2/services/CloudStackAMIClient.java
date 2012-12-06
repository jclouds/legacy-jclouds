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

import org.jclouds.cloudstack.ec2.options.CloudStackEC2RegisterImageOptions;
import org.jclouds.concurrent.Timeout;
import org.jclouds.ec2.options.CreateImageOptions;
import org.jclouds.ec2.services.AMIClient;
import org.jclouds.javax.annotation.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * @author Adrian Cole
 */
@Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
public interface CloudStackAMIClient extends AMIClient {

   /**
    * {@inheritDoc}
    */
   @Override
   @Timeout(duration = 10, timeUnit = TimeUnit.MINUTES)
   String createImageInRegion(@Nullable String region, String name, String instanceId, CreateImageOptions... options);

   /**
    * This method is overloaded<br/>
    * Here CloudStackEC2RegisterImageOptions parameter is changed as architecture has different meaning in CloudStack
    *
    * @see CloudStackEC2RegisterImageOptions
    * @see AMIClient#registerImageFromManifestInRegion
    */
   String registerImageFromManifestInRegion(@Nullable String region, String name, String pathToManifest,
                                            CloudStackEC2RegisterImageOptions... options);
}