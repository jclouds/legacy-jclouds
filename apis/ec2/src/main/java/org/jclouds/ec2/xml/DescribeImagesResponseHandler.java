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
package org.jclouds.ec2.xml;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.ec2.domain.Image;
import org.jclouds.location.Region;

import com.google.common.base.Supplier;

/**
 * Parses the following XML document:
 * <p/>
 * DescribeImagesResponse xmlns="http://ec2.amazonaws.com/doc/2010-06-15/"
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImages.html" />
 * @author Adrian Cole
 * @author Andrew Kennedy
 */
public class DescribeImagesResponseHandler extends BaseDescribeImagesResponseHandler<Image> {

   @Inject
   public DescribeImagesResponseHandler(@Region Supplier<String> defaultRegion) {
      super(defaultRegion);
   }

   @Override
   public Set<Image> getResult() {
      return contents;
   }

   @Override
   protected Image newImage(String region) {
      Image image = null;
      try {
         image = new Image(region, architecture, name, description, imageId, imageLocation, imageOwnerId,
               imageState, rawState, imageType, isPublic, productCodes, kernelId, platform,
               ramdiskId, rootDeviceType, rootDeviceName, ebsBlockDevices, virtualizationType, hypervisor);
      } catch (NullPointerException e) {
         logger.warn(e, "Malformed image id: %s", imageId);
      }
      return image;
   }
}
