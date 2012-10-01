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
package org.jclouds.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-DescribeImagesResponseItemType.html" />
 * @author Adrian Cole
 * @author Andrew Kennedy
 */
public class Image implements Comparable<Image> {

   private final String region;
   @Nullable
   private final Architecture architecture;
   @Nullable
   private final String name;
   @Nullable
   private final String description;
   private final String imageId;
   private final String imageLocation;
   private final String imageOwnerId;
   @Nullable
   private final ImageState imageState;
   @Nullable
   private final String rawState;
   @Nullable
   private final ImageType imageType;
   private final boolean isPublic;
   @Nullable
   private final String kernelId;
   @Nullable
   private final String platform;
   private final Set<String> productCodes;
   @Nullable
   private final String ramdiskId;
   private final RootDeviceType rootDeviceType;
   @Nullable
   private final String rootDeviceName;
   private final Map<String, EbsBlockDevice> ebsBlockDevices;
   private final VirtualizationType virtualizationType;

   public VirtualizationType getVirtualizationType() {
      return virtualizationType;
   }

   private final Hypervisor hypervisor;

   public Hypervisor getHypervisor() {
      return hypervisor;
   }

   public Image(String region, @Nullable Architecture architecture, @Nullable String name, @Nullable String description,
            String imageId, String imageLocation, String imageOwnerId, @Nullable ImageState imageState, @Nullable String rawState,
            @Nullable ImageType imageType, boolean isPublic, Iterable<String> productCodes, @Nullable String kernelId,
            @Nullable String platform, @Nullable String ramdiskId, RootDeviceType rootDeviceType,
            @Nullable String rootDeviceName, Map<String, EbsBlockDevice> ebsBlockDevices,
            VirtualizationType virtualizationType, Hypervisor hypervisor) {
      this.region = checkNotNull(region, "region");
      this.architecture = architecture;
      this.imageId = checkNotNull(imageId, "imageId");
      this.name = name;
      this.description = description;
      this.rootDeviceName = rootDeviceName;
      this.imageLocation = checkNotNull(imageLocation, "imageLocation");
      this.imageOwnerId = imageOwnerId;
      this.imageState = imageState;
      this.rawState = rawState;
      this.imageType = imageType;
      this.isPublic = isPublic;
      this.kernelId = kernelId;
      this.platform = platform;
      this.productCodes = ImmutableSortedSet.copyOf(checkNotNull(productCodes, "productCodes"));
      this.ramdiskId = ramdiskId;
      this.rootDeviceType = checkNotNull(rootDeviceType, "rootDeviceType");
      this.ebsBlockDevices = ImmutableSortedMap.copyOf(checkNotNull(ebsBlockDevices, "ebsBlockDevices"));
      this.virtualizationType = checkNotNull(virtualizationType, "virtualizationType");
      this.hypervisor = checkNotNull(hypervisor, "hypervisor");
   }

   public static enum ImageState {

      /** The image is successfully registered and available for launching. */
      AVAILABLE,

      /** The image is deregistered and no longer available for launching. */
      DEREGISTERED,

      /** The image state was not recognized. */
      UNRECOGNIZED;

      public String value() {
         return name().toLowerCase();
      }

      public static ImageState fromValue(@Nullable String v) {
         try {
            return valueOf(v.toUpperCase());
         } catch (NullPointerException e) {
            return null;
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static enum Architecture {

      I386,
      X86_64,
      UNRECOGNIZED;

      public String value() {
         return name().toLowerCase();
      }

      public static Architecture fromValue(@Nullable String v) {
         try {
            return valueOf(v.toUpperCase());
         } catch (NullPointerException e) {
            return null;
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static enum ImageType {

      MACHINE,
      KERNEL, 
      RAMDISK,
      UNRECOGNIZED;

      public String value() {
         return name().toLowerCase();
      }

      public static ImageType fromValue(@Nullable String v) {
         try {
            return valueOf(v.toUpperCase());
         } catch (NullPointerException e) {
            return null;
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static class EbsBlockDevice {

      @Nullable
      private final String snapshotId;
      private final long volumeSize;
      private final boolean deleteOnTermination;

      public EbsBlockDevice(@Nullable String snapshotId, long volumeSize, boolean deleteOnTermination) {
         this.snapshotId = snapshotId;
         this.volumeSize = volumeSize;
         this.deleteOnTermination = deleteOnTermination;
      }

      public String getSnapshotId() {
         return snapshotId;
      }

      public long getVolumeSize() {
         return volumeSize;
      }

      public boolean isDeleteOnTermination() {
         return deleteOnTermination;
      }

      /** {@inheritDoc} */
      @Override
      public int hashCode() {
         return Objects.hashCode(deleteOnTermination, snapshotId, volumeSize);
      }

      /** {@inheritDoc} */
      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null) return false;
         if (getClass() != obj.getClass()) return false;
         EbsBlockDevice that = (EbsBlockDevice) obj;
         return Objects.equal(this.deleteOnTermination, that.deleteOnTermination)
               && Objects.equal(this.snapshotId, that.snapshotId)
               && Objects.equal(this.volumeSize, that.volumeSize);
      }

      /** {@inheritDoc} */
      @Override
      public String toString() {
         return Objects.toStringHelper(getClass())
               .add("deleteOnTermination", deleteOnTermination)
               .add("snapshotId", snapshotId)
               .add("volumeSize", volumeSize)
               .toString();
      }
   }

   /**
    * To be removed in jclouds 1.6 <h4>Warning</h4>
    * 
    * Especially on EC2 clones that may not support regions, this value is fragile. Consider
    * alternate means to determine context.
    */
   @Deprecated
   public String getRegion() {
      return region;
   }

   /**
    * The architecture of the image (i386 or x86_64).
    */
   public Architecture getArchitecture() {
      return architecture;
   }

   /**
    * The ID of the AMI.
    */
   public String getId() {
      return imageId;
   }

   /**
    * The location of the AMI.
    */
   public String getImageLocation() {
      return imageLocation;
   }

   /**
    * AWS Access Key ID of the image owner.
    */
   public String getImageOwnerId() {
      return imageOwnerId;
   }

   /**
    * Current state of the AMI.
    *
    * If the operation returns {@link ImageState#AVAILABLE available} the image is successfully registered and
    * available for launching. If the operation returns {@link ImageState#DEREGISTERED deregistered} then the
    * image is not registered and is no longer available for launching.
    */
   public ImageState getImageState() {
      return imageState;
   }

   /**
    * Raw form of {@link #getImageState()} as taken directly from the response XML document.
    */
   public String getRawState() {
      return rawState;
   }

   /**
    * The type of image ({@link ImageType#MACHINE machine}, {@link ImageType#KERNEL kernel}, or {@link ImageType#RAMDISK ramdisk}).
    */
   public ImageType getImageType() {
      return imageType;
   }

   /**
    * Lunch permissions for the AMI.
    * 
    * @return {@literal true} if this image has public launch permissions or {@literal false} if it only has
    * implicit and explicit launch permissions.
    */
   public boolean isPublic() {
      return isPublic;
   }

   /**
    * The kernel associated with the image, if any. Only applicable for machine images.
    */
   public String getKernelId() {
      return kernelId;
   }

   /**
    * The operating platform of the instance.
    */
   public String getPlatform() {
      return platform;
   }

   /**
    * Product codes for the AMI.
    */
   public Set<String> getProductCodes() {
      return productCodes;
   }

   /**
    * The RAM disk associated with the image, if any. Only applicable for machine images.
    */
   public String getRamdiskId() {
      return ramdiskId;
   }

   /**
    * The root device type used by the AMI.
    *
    * The AMI can use an {@link RootDeviceType#EBS} or {@link RootDeviceType#INSTANCE_STORE instance store} root device.
    */
   public RootDeviceType getRootDeviceType() {
      return rootDeviceType;
   }

   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public String getRootDeviceName() {
      return rootDeviceName;
   }

   public Map<String, EbsBlockDevice> getEbsBlockDevices() {
      return ebsBlockDevices;
   }

   /** {@inheritDoc} */
   @Override
   public int compareTo(Image o) {
      return (this == o) ? 0 : getId().compareTo(o.getId());
   }

   /** {@inheritDoc} */
   @Override
   public int hashCode() {
      return Objects.hashCode(architecture, description, ebsBlockDevices, imageId, imageLocation, imageOwnerId,
            imageType, isPublic, kernelId, name, platform, productCodes, ramdiskId, region, rootDeviceName,
            rootDeviceType, virtualizationType, hypervisor);
   }

   /** {@inheritDoc} */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      Image that = (Image) obj;
      return Objects.equal(this.architecture, that.architecture)
            && Objects.equal(this.description, that.description)
            && Objects.equal(this.ebsBlockDevices, that.ebsBlockDevices)
            && Objects.equal(this.imageId, that.imageId)
            && Objects.equal(this.imageLocation, that.imageLocation)
            && Objects.equal(this.imageOwnerId, that.imageOwnerId)
            && Objects.equal(this.imageType, that.imageType)
            && Objects.equal(this.isPublic, that.isPublic)
            && Objects.equal(this.kernelId, that.kernelId)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.platform, that.platform)
            && Objects.equal(this.productCodes, that.productCodes)
            && Objects.equal(this.ramdiskId, that.ramdiskId)
            && Objects.equal(this.region, that.region)
            && Objects.equal(this.rootDeviceName, that.rootDeviceName)
            && Objects.equal(this.rootDeviceType, that.rootDeviceType)
            && Objects.equal(this.virtualizationType, that.virtualizationType)
            && Objects.equal(this.hypervisor, that.hypervisor) ;
   }

   /** {@inheritDoc} */
   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(getClass())
            .add("architecture", architecture)
            .add("description", description)
            .add("ebsBlockDevices", ebsBlockDevices)
            .add("imageId", imageId)
            .add("imageLocation", imageLocation)
            .add("imageOwnerId", imageOwnerId)
            .add("imageType", imageType)
            .add("isPublic", isPublic)
            .add("kernelId", kernelId)
            .add("name", name)
            .add("platform", platform)
            .add("productCodes", productCodes)
            .add("ramdiskId", ramdiskId)
            .add("region", region)
            .add("rootDeviceName", rootDeviceName)
            .add("rootDeviceType", rootDeviceType)
            .add("virtualizationType", virtualizationType)
            .add("hypervisor", hypervisor);
   }
}
