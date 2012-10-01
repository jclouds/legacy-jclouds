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
package org.jclouds.aws.ec2.domain;

import java.util.Map;

import org.jclouds.ec2.domain.Hypervisor;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.VirtualizationType;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;

/**
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-DescribeImagesResponseItemType.html" />
 * @author Adrian Cole
 * @author Andrew Kennedy
 */
public class AWSImage extends Image {

   private final Map<String, String> tags;

   public AWSImage(String region, @Nullable Architecture architecture, @Nullable String name, String description,
            String imageId, String imageLocation, String imageOwnerId, @Nullable ImageState imageState, @Nullable String rawState,
            @Nullable ImageType imageType, boolean isPublic, Iterable<String> productCodes, @Nullable String kernelId,
            @Nullable String platform, @Nullable String ramdiskId, RootDeviceType rootDeviceType,
            @Nullable String rootDeviceName, Map<String, EbsBlockDevice> ebsBlockDevices,
            VirtualizationType virtualizationType, Hypervisor hypervisor, @Nullable Map<String, String> tags) {
      super(region, architecture, name, description, imageId, imageLocation, imageOwnerId, imageState, rawState, imageType, isPublic, productCodes, kernelId, platform, ramdiskId, rootDeviceType, rootDeviceName, ebsBlockDevices, virtualizationType, hypervisor);
      this.tags = tags == null ? ImmutableMap.<String, String>of() : ImmutableMap.copyOf(tags);
   }

   /**
    * The set of tags and their values for the AMI.
    */
   public Map<String, String> getTags() {
      return tags;
   }

   /** {@inheritDoc} */
   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), tags);
   }

   /** {@inheritDoc} */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      AWSImage that = (AWSImage) obj;
      return super.equals(that)
            && Objects.equal(this.tags, that.tags);
   }

   @Override
   protected ToStringHelper string() {
      return super.string()
            .add("tags", tags);
   }
}
