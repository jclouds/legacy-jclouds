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
package org.jclouds.ec2.compute.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static org.jclouds.compute.predicates.ImagePredicates.any;
import static org.jclouds.compute.predicates.ImagePredicates.idIn;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Location;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.VirtualizationType;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/UserGuide/index.html?instance-types.html"
 *      />
 */
public class EC2HardwareBuilder extends HardwareBuilder {
   private Predicate<Image> rootDeviceType = any();
   private Predicate<Image> virtualizationType = Predicates.or(new IsWindows(), new RequiresVirtualizationType(
         VirtualizationType.PARAVIRTUAL));
   private Predicate<Image> imageIds = any();
   private Predicate<Image> is64Bit = any();

   public EC2HardwareBuilder() {
      this.supportsImage = null;
   }

   /**
    * evaluates true if the Image has the following rootDeviceType
    * 
    * @param type
    *           rootDeviceType of the image
    * @return predicate
    */
   public static class RequiresRootDeviceType implements Predicate<Image> {
      final RootDeviceType type;

      public RequiresRootDeviceType(final RootDeviceType type) {
         this.type = checkNotNull(type, "type must be defined");
      }

      @Override
      public boolean apply(Image image) {
         return image.getUserMetadata().containsKey("rootDeviceType")
               && type == RootDeviceType.fromValue(image.getUserMetadata().get("rootDeviceType"));
      }

      @Override
      public String toString() {
         return "requiresRootDeviceType(" + type + ")";
      }

   }

   public static class IsWindows implements Predicate<Image> {

      @Override
      public boolean apply(Image image) {
         return image.getOperatingSystem() != null && OsFamily.WINDOWS == image.getOperatingSystem().getFamily();
      }

      @Override
      public String toString() {
         return "isWindows()";
      }

   }

   /**
    * evaluates true if the Image requires the following virtualizationType
    * 
    * @param type
    *           virtualizationType of the image
    * @return predicate
    */
   public static class RequiresVirtualizationType implements Predicate<Image> {
      final VirtualizationType type;

      public RequiresVirtualizationType(final VirtualizationType type) {
         this.type = checkNotNull(type, "type must be defined");
      }

      @Override
      public boolean apply(Image image) {
         return image.getOperatingSystem() != null && image.getOperatingSystem().getArch() != null
               && type == VirtualizationType.fromValue(image.getOperatingSystem().getArch());
      }

      @Override
      public String toString() {
         return "requiresVirtualizationType(" + type + ")";
      }

   }

   public EC2HardwareBuilder(String instanceType) {
      ids(instanceType);
   }

   public EC2HardwareBuilder virtualizationType(VirtualizationType virtualizationType) {
      this.virtualizationType = new RequiresVirtualizationType(virtualizationType);
      return this;
   }

   public EC2HardwareBuilder rootDeviceType(RootDeviceType rootDeviceType) {
      this.rootDeviceType = new RequiresRootDeviceType(rootDeviceType);
      return this;
   }

   public EC2HardwareBuilder supportsImageIds(Iterable<String> ids) {
      this.imageIds = idIn(ids);
      return this;
   }

   public EC2HardwareBuilder ids(String id) {
      return EC2HardwareBuilder.class.cast(super.ids(id));
   }

   public EC2HardwareBuilder ram(int ram) {
      return EC2HardwareBuilder.class.cast(super.ram(ram));
   }

   public EC2HardwareBuilder processors(List<Processor> processors) {
      return EC2HardwareBuilder.class.cast(super.processors(processors));
   }

   public EC2HardwareBuilder volumes(List<Volume> volumes) {
      return EC2HardwareBuilder.class.cast(super.volumes(volumes));
   }

   public EC2HardwareBuilder supportsImage(Predicate<Image> supportsImage) {
      return EC2HardwareBuilder.class.cast(super.supportsImage(supportsImage));
   }

   public EC2HardwareBuilder is64Bit(boolean is64Bit) {
      this.is64Bit = is64Bit ? ImagePredicates.is64Bit() : not(ImagePredicates.is64Bit());
      return this;
   }

   public EC2HardwareBuilder id(String id) {
      return EC2HardwareBuilder.class.cast(super.id(id));
   }

   @Override
   public EC2HardwareBuilder providerId(String providerId) {
      return EC2HardwareBuilder.class.cast(super.providerId(providerId));
   }

   @Override
   public EC2HardwareBuilder name(String name) {
      return EC2HardwareBuilder.class.cast(super.name(name));
   }

   @Override
   public EC2HardwareBuilder location(Location location) {
      return EC2HardwareBuilder.class.cast(super.location(location));
   }

   @Override
   public EC2HardwareBuilder uri(URI uri) {
      return EC2HardwareBuilder.class.cast(super.uri(uri));
   }

   @Override
   public EC2HardwareBuilder userMetadata(Map<String, String> userMetadata) {
      return EC2HardwareBuilder.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * @see InstanceType#M1_SMALL
    */
   public static EC2HardwareBuilder m1_small() {
      return new EC2HardwareBuilder(InstanceType.M1_SMALL)
            .ram(1740)
            .processors(ImmutableList.of(new Processor(1.0, 1.0)))
            .volumes(
                  ImmutableList.<Volume> of(new VolumeImpl(10.0f, "/dev/sda1", true, false), new VolumeImpl(150.0f,
                        "/dev/sda2", false, false)));
   }

   /**
    * @see InstanceType#M1_MEDIUM
    */
   public static EC2HardwareBuilder m1_medium() {
      return new EC2HardwareBuilder(InstanceType.M1_MEDIUM)
            .ram(3750)
            .processors(ImmutableList.of(new Processor(1.0, 2.0)))
            .volumes(
                     ImmutableList.<Volume> of(new VolumeImpl(10.0f, "/dev/sda1", true, false), new VolumeImpl(420.0f,
                           "/dev/sdb", false, false), new VolumeImpl(420.0f, "/dev/sdc", false, false)));
   }


   /**
    * @see InstanceType#T1_MICRO
    */
   public static EC2HardwareBuilder t1_micro() {
      return new EC2HardwareBuilder(InstanceType.T1_MICRO).ram(630)
            .processors(ImmutableList.of(new Processor(1.0, 1.0))).rootDeviceType(RootDeviceType.EBS);
   }

   /**
    * @see InstanceType#M1_LARGE
    */
   public static EC2HardwareBuilder m1_large() {
      return new EC2HardwareBuilder(InstanceType.M1_LARGE)
            .ram(7680)
            .processors(ImmutableList.of(new Processor(2.0, 2.0)))
            .volumes(
                  ImmutableList.<Volume> of(new VolumeImpl(10.0f, "/dev/sda1", true, false), new VolumeImpl(420.0f,
                        "/dev/sdb", false, false), new VolumeImpl(420.0f, "/dev/sdc", false, false))).is64Bit(true);
   }

   /**
    * @see InstanceType#M1_XLARGE
    */
   public static EC2HardwareBuilder m1_xlarge() {
      return new EC2HardwareBuilder(InstanceType.M1_XLARGE)
            .ram(15360)
            .processors(ImmutableList.of(new Processor(4.0, 2.0)))
            .volumes(
                  ImmutableList.<Volume> of(new VolumeImpl(10.0f, "/dev/sda1", true, false), new VolumeImpl(420.0f,
                        "/dev/sdb", false, false), new VolumeImpl(420.0f, "/dev/sdc", false, false), new VolumeImpl(
                        420.0f, "/dev/sdd", false, false), new VolumeImpl(420.0f, "/dev/sde", false, false)))
            .is64Bit(true);
   }

   /**
    * @see InstanceType#M2_XLARGE
    */
   public static EC2HardwareBuilder m2_xlarge() {
      return new EC2HardwareBuilder(InstanceType.M2_XLARGE).ram(17510)
            .processors(ImmutableList.of(new Processor(2.0, 3.25)))
            .volumes(ImmutableList.<Volume> of(new VolumeImpl(420.0f, "/dev/sda1", true, false))).is64Bit(true);
   }

   /**
    * @see InstanceType#M2_2XLARGE
    */
   public static EC2HardwareBuilder m2_2xlarge() {
      return new EC2HardwareBuilder(InstanceType.M2_2XLARGE)
            .ram(35020)
            .processors(ImmutableList.of(new Processor(4.0, 3.25)))
            .volumes(
                  ImmutableList.<Volume> of(new VolumeImpl(10.0f, "/dev/sda1", true, false), new VolumeImpl(840.0f,
                        "/dev/sdb", false, false))).is64Bit(true);
   }

   /**
    * @see InstanceType#M2_4XLARGE
    */
   public static EC2HardwareBuilder m2_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.M2_4XLARGE)
            .ram(70041)
            .processors(ImmutableList.of(new Processor(8.0, 3.25)))
            .volumes(
                  ImmutableList.<Volume> of(new VolumeImpl(10.0f, "/dev/sda1", true, false), new VolumeImpl(840.0f,
                        "/dev/sdb", false, false), new VolumeImpl(840.0f, "/dev/sdc", false, false))).is64Bit(true);
   }

   /**
    * @see InstanceType#C1_MEDIUM
    */
   public static EC2HardwareBuilder c1_medium() {
      return new EC2HardwareBuilder(InstanceType.C1_MEDIUM)
            .ram(1740)
            .processors(ImmutableList.of(new Processor(2.0, 2.5)))
            .volumes(
                  ImmutableList.<Volume> of(new VolumeImpl(10.0f, "/dev/sda1", true, false), new VolumeImpl(340.0f,
                        "/dev/sda2", false, false)));
   }

   /**
    * @see InstanceType#C1_XLARGE
    */
   public static EC2HardwareBuilder c1_xlarge() {
      return new EC2HardwareBuilder(InstanceType.C1_XLARGE)
            .ram(7168)
            .processors(ImmutableList.of(new Processor(8.0, 2.5)))
            .volumes(
                  ImmutableList.<Volume> of(new VolumeImpl(10.0f, "/dev/sda1", true, false), new VolumeImpl(420.0f,
                        "/dev/sdb", false, false), new VolumeImpl(420.0f, "/dev/sdc", false, false), new VolumeImpl(
                        420.0f, "/dev/sdd", false, false), new VolumeImpl(420.0f, "/dev/sde", false, false)))
            .is64Bit(true);
   }

   public static EC2HardwareBuilder cg1_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.CG1_4XLARGE)
            .ram(22 * 1024)
            .processors(ImmutableList.of(new Processor(4.0, 4.0), new Processor(4.0, 4.0)))
            .volumes(
                  ImmutableList.<Volume> of(new VolumeImpl(10.0f, "/dev/sda1", true, false), new VolumeImpl(840.0f,
                        "/dev/sdb", false, false), new VolumeImpl(840.0f, "/dev/sdc", false, false)))
            .virtualizationType(VirtualizationType.HVM);
   }

   public static EC2HardwareBuilder cc1_4xlarge() {
      return new EC2HardwareBuilder(InstanceType.CC1_4XLARGE)
            .ram(23 * 1024)
            .processors(ImmutableList.of(new Processor(4.0, 4.0), new Processor(4.0, 4.0)))
            .volumes(
                  ImmutableList.<Volume> of(new VolumeImpl(10.0f, "/dev/sda1", true, false), new VolumeImpl(840.0f,
                        "/dev/sdb", false, false), new VolumeImpl(840.0f, "/dev/sdc", false, false)))
            .virtualizationType(VirtualizationType.HVM);
   }

   public static EC2HardwareBuilder cc2_8xlarge() {
      return new EC2HardwareBuilder(InstanceType.CC2_8XLARGE)
            .ram(60 * 1024 + 512)
            .processors(ImmutableList.of(new Processor(8.0, 5.5), new Processor(8.0, 5.5)))
            .volumes(
                  ImmutableList.<Volume> of(new VolumeImpl(10.0f, "/dev/sda1", true, false), new VolumeImpl(840.0f,
                        "/dev/sdb", false, false), new VolumeImpl(840.0f, "/dev/sdc", false, false), new VolumeImpl(
                        840.0f, "/dev/sdb", false, false), new VolumeImpl(840.0f, "/dev/sdc", false, false)))
            .virtualizationType(VirtualizationType.HVM);
   }

   @SuppressWarnings("unchecked")
   @Override
   public Hardware build() {
      boolean reset = false;
      if (this.supportsImage == null)
         reset = true;
      try {
         supportsImage = Predicates.<Image> and(rootDeviceType, virtualizationType, imageIds, is64Bit);
         return super.build();
      } finally {
         if (reset)
            this.supportsImage = null;
      }

   }

}
