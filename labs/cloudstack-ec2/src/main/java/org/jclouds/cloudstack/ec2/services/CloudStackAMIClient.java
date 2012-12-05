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
 * 
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
     * Registers an AMI with Amazon EC2. Images must be registered before they can be launched. To
     * launch instances, use the {@link org.jclouds.cloudstack.ec2.services.CloudStackEC2InstanceClient#runInstancesInRegion} operation.
     * <p/>
     * Each AMI is associated with an unique ID which is provided by the Amazon EC2 service through
     * this operation. If needed, you can deregister an AMI at any time.
     * <p/>
     * <h3>Note</h3> Any modifications to an AMI backed by Amazon S3 invalidates this registration.
     * If you make changes to an image, deregister the previous image and register the new image.
     *
     * @param region
     *           AMIs are tied to the Region where its files are located within Amazon S3.
     * @param name
     *           The name of the AMI that was provided during image creation. 3-128 alphanumeric
     *           characters, parenthesis (()), commas (,), slashes (/), dashes (-), or underscores(_)
     * @param pathToManifest
     *           Full path to your AMI manifest in Amazon S3 storage.
     * @param options
     *           Options to specify metadata such as architecture or secondary volumes to be
     *           associated with this image.
     * @return imageId
     *
     * @see #describeImages
     * @see #deregisterImage
     * @see <a href=
     *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-RegisterImage.html"
     *      />
     */
    String registerImageFromManifestInRegion(@Nullable String region, String name, String pathToManifest,
                                             CloudStackEC2RegisterImageOptions... options);



}
