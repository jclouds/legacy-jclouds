/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.internal.Nullable;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-DescribeImagesResponseItemType.html"
 *      />
 * @author Adrian Cole
 */
public class Image implements Comparable<Image> {

   private final Architecture architecture;
   @Nullable
   private final String name;
   @Nullable
   private final String description;
   private final String imageId;
   private final String imageLocation;
   private final String imageOwnerId;
   private final ImageState imageState;
   private final ImageType imageType;
   private final boolean isPublic;
   @Nullable
   private final String kernelId;
   @Nullable
   private final String platform;
   private final Set<String> productCodes = Sets.newHashSet();
   @Nullable
   private final String ramdiskId;
   private final RootDeviceType rootDeviceType;
   @Nullable
   private final String rootDeviceName;
   private final Map<String, EbsBlockDevice> ebsBlockDevices = Maps.newHashMap();

   public Image(Architecture architecture, @Nullable String name, @Nullable String description,
            String imageId, String imageLocation, String imageOwnerId, ImageState imageState,
            ImageType imageType, boolean isPublic, Iterable<String> productCodes,
            @Nullable String kernelId, @Nullable String platform, @Nullable String ramdiskId,
            RootDeviceType rootDeviceType, String rootDeviceName,
            Map<String, EbsBlockDevice> ebsBlockDevices) {
      this.architecture = checkNotNull(architecture, "architecture");
      this.imageId = checkNotNull(imageId, "imageId");
      this.name = name;
      this.description = description;
      this.rootDeviceName = rootDeviceName;
      this.imageLocation = checkNotNull(imageLocation, "imageLocation");
      this.imageOwnerId = checkNotNull(imageOwnerId, "imageOwnerId");
      this.imageState = checkNotNull(imageState, "imageState");
      this.imageType = checkNotNull(imageType, "imageType");
      this.isPublic = isPublic;
      this.kernelId = kernelId;
      this.platform = platform;
      Iterables.addAll(this.productCodes, checkNotNull(productCodes, "productCodes"));
      this.ramdiskId = ramdiskId;
      this.rootDeviceType = checkNotNull(rootDeviceType, "rootDeviceType");
      this.ebsBlockDevices.putAll(checkNotNull(ebsBlockDevices, "ebsBlockDevices"));
   }

   /** The serialVersionUID */
   private static final long serialVersionUID = -6965068835316857535L;

   public static enum ImageState {
      /**
       * the image is successfully registered and available for launching
       */
      AVAILABLE,
      /**
       * the image is deregistered and no longer available for launching
       */
      DEREGISTERED;
      public String value() {
         return name().toLowerCase();
      }

      public static ImageState fromValue(String v) {
         return valueOf(v.toUpperCase());
      }
   }

   /**
    * The root device type used by the AMI. The AMI can use an Amazon EBS or instance store root
    * device.
    */
   public static enum RootDeviceType {

      INSTANCE_STORE,

      EBS;

      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
      }

      public static RootDeviceType fromValue(String v) {
         return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, v));
      }
   }

   public static enum Architecture {
      I386, X86_64;
      public String value() {
         return name().toLowerCase();
      }

      public static Architecture fromValue(String v) {
         return valueOf(v.toUpperCase());
      }
   }

   public static enum ImageType {

      MACHINE, KERNEL, RAMDISK;
      public String value() {
         return name().toLowerCase();
      }

      public static ImageType fromValue(String v) {
         return valueOf(v.toUpperCase());
      }

   }

   public static class EbsBlockDevice {
      @Nullable
      private final String snapshotId;
      private final long volumeSize;
      private final boolean deleteOnTermination;

      public EbsBlockDevice(@Nullable String snapshotId, long volumeSize,
               boolean deleteOnTermination) {
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

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + (deleteOnTermination ? 1231 : 1237);
         result = prime * result + ((snapshotId == null) ? 0 : snapshotId.hashCode());
         result = prime * result + (int) (volumeSize ^ (volumeSize >>> 32));
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         EbsBlockDevice other = (EbsBlockDevice) obj;
         if (deleteOnTermination != other.deleteOnTermination)
            return false;
         if (snapshotId == null) {
            if (other.snapshotId != null)
               return false;
         } else if (!snapshotId.equals(other.snapshotId))
            return false;
         if (volumeSize != other.volumeSize)
            return false;
         return true;
      }

      @Override
      public String toString() {
         return "EbsBlockDevice [deleteOnTermination=" + deleteOnTermination + ", snapshotId="
                  + snapshotId + ", volumeSize=" + volumeSize + "]";
      }

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
   public String getImageId() {
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
    * Current state of the AMI. If the operation returns available, the image is successfully
    * registered and avail able for launching. If the operation returns deregistered, the image is
    * deregistered and no longer available for launching.
    */
   public ImageState getImageState() {
      return imageState;
   }

   /**
    * The type of image (machine, kernel, or ramdisk).
    */
   public ImageType getImageType() {
      return imageType;
   }

   /**
    * Returns true if this image has public launch permissions. Returns false if it only has
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
    * Product codes of the AMI.
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
    * {@inheritDoc}
    */
   public int compareTo(Image o) {
      return (this == o) ? 0 : getImageId().compareTo(o.getImageId());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((architecture == null) ? 0 : architecture.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((ebsBlockDevices == null) ? 0 : ebsBlockDevices.hashCode());
      result = prime * result + ((imageId == null) ? 0 : imageId.hashCode());
      result = prime * result + ((imageLocation == null) ? 0 : imageLocation.hashCode());
      result = prime * result + ((imageOwnerId == null) ? 0 : imageOwnerId.hashCode());
      result = prime * result + ((imageState == null) ? 0 : imageState.hashCode());
      result = prime * result + ((imageType == null) ? 0 : imageType.hashCode());
      result = prime * result + (isPublic ? 1231 : 1237);
      result = prime * result + ((kernelId == null) ? 0 : kernelId.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((platform == null) ? 0 : platform.hashCode());
      result = prime * result + ((productCodes == null) ? 0 : productCodes.hashCode());
      result = prime * result + ((ramdiskId == null) ? 0 : ramdiskId.hashCode());
      result = prime * result + ((rootDeviceName == null) ? 0 : rootDeviceName.hashCode());
      result = prime * result + ((rootDeviceType == null) ? 0 : rootDeviceType.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Image other = (Image) obj;
      if (architecture == null) {
         if (other.architecture != null)
            return false;
      } else if (!architecture.equals(other.architecture))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (ebsBlockDevices == null) {
         if (other.ebsBlockDevices != null)
            return false;
      } else if (!ebsBlockDevices.equals(other.ebsBlockDevices))
         return false;
      if (imageId == null) {
         if (other.imageId != null)
            return false;
      } else if (!imageId.equals(other.imageId))
         return false;
      if (imageLocation == null) {
         if (other.imageLocation != null)
            return false;
      } else if (!imageLocation.equals(other.imageLocation))
         return false;
      if (imageOwnerId == null) {
         if (other.imageOwnerId != null)
            return false;
      } else if (!imageOwnerId.equals(other.imageOwnerId))
         return false;
      if (imageState == null) {
         if (other.imageState != null)
            return false;
      } else if (!imageState.equals(other.imageState))
         return false;
      if (imageType == null) {
         if (other.imageType != null)
            return false;
      } else if (!imageType.equals(other.imageType))
         return false;
      if (isPublic != other.isPublic)
         return false;
      if (kernelId == null) {
         if (other.kernelId != null)
            return false;
      } else if (!kernelId.equals(other.kernelId))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (platform == null) {
         if (other.platform != null)
            return false;
      } else if (!platform.equals(other.platform))
         return false;
      if (productCodes == null) {
         if (other.productCodes != null)
            return false;
      } else if (!productCodes.equals(other.productCodes))
         return false;
      if (ramdiskId == null) {
         if (other.ramdiskId != null)
            return false;
      } else if (!ramdiskId.equals(other.ramdiskId))
         return false;
      if (rootDeviceName == null) {
         if (other.rootDeviceName != null)
            return false;
      } else if (!rootDeviceName.equals(other.rootDeviceName))
         return false;
      if (rootDeviceType == null) {
         if (other.rootDeviceType != null)
            return false;
      } else if (!rootDeviceType.equals(other.rootDeviceType))
         return false;
      return true;
   }

   /**
    * 
    * @return The root device type used by the AMI. The AMI can use an Amazon EBS or instance store
    *         root device.
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

   @Override
   public String toString() {
      return "Image [architecture=" + architecture + ", description=" + description
               + ", ebsBlockDevices=" + ebsBlockDevices + ", imageId=" + imageId
               + ", imageLocation=" + imageLocation + ", imageOwnerId=" + imageOwnerId
               + ", imageState=" + imageState + ", imageType=" + imageType + ", isPublic="
               + isPublic + ", kernelId=" + kernelId + ", name=" + name + ", platform=" + platform
               + ", productCodes=" + productCodes + ", ramdiskId=" + ramdiskId
               + ", rootDeviceName=" + rootDeviceName + ", rootDeviceType=" + rootDeviceType + "]";
   }

   public Map<String, EbsBlockDevice> getEbsBlockDevices() {
      return ebsBlockDevices;
   }

}