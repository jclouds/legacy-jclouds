/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.aws.ec2.compute.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static org.jclouds.compute.predicates.ImagePredicates.any;
import static org.jclouds.compute.predicates.ImagePredicates.idIn;
import static org.jclouds.compute.predicates.ImagePredicates.is64Bit;

import java.util.Arrays;

import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.RootDeviceType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.HardwareImpl;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.domain.Location;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/UserGuide/index.html?instance-types.html"
 *      />
 */
public class EC2Hardware extends HardwareImpl {
   /** The serialVersionUID */
   private static final long serialVersionUID = 8605688733788974797L;
   private final String instanceType;

   /**
    * evaluates true if the Image has the following rootDeviceType
    * 
    * @param type
    *           rootDeviceType of the image
    * @return predicate
    */
   public static Predicate<Image> hasRootDeviceType(final RootDeviceType type) {
      checkNotNull(type, "type must be defined");
      return new Predicate<Image>() {
         @Override
         public boolean apply(Image image) {
            return type.toString().equals(image.getUserMetadata().get("rootDeviceType"));
         }

         @Override
         public String toString() {
            return "hasRootDeviceType(" + type + ")";
         }
      };
   }

   EC2Hardware(String instanceType, Iterable<? extends Processor> processors, Integer ram,
            Iterable<? extends Volume> volumes, RootDeviceType rootDeviceType) {
      this(null, instanceType, processors, ram, volumes, hasRootDeviceType(rootDeviceType));
   }

   EC2Hardware(Location location, String instanceType, Iterable<? extends Processor> processors, Integer ram,
            Iterable<? extends Volume> volumes, Predicate<Image> supportsImage) {
      super(instanceType, instanceType, instanceType, location, null, ImmutableMap.<String, String> of(), processors,
               ram, volumes, supportsImage);
      this.instanceType = instanceType;
   }

   EC2Hardware(String instanceType, Iterable<? extends Processor> processors, Integer ram,
            Iterable<? extends Volume> volumes, boolean is64Bit) {
      this(null, instanceType, processors, ram, volumes, is64Bit ? is64Bit() : not(is64Bit()));
   }

   public EC2Hardware(Location location, String instanceType, Iterable<? extends Processor> processors, Integer ram,
            Iterable<? extends Volume> volumes, String[] ids) {
      this(location, instanceType, processors, ram, volumes, (ids.length == 0 ? is64Bit() : idIn(Arrays.asList(ids))));
   }

   /**
    * Returns the EC2 InstanceType associated with this size.
    */
   public String getInstanceType() {
      return instanceType;
   }

   /**
    * @see InstanceType#M1_SMALL
    */
   public static final EC2Hardware M1_SMALL = new EC2Hardware(InstanceType.M1_SMALL, ImmutableList.of(new Processor(
            1.0, 1.0)), 1740, ImmutableList.of(new VolumeImpl(10.0f, "/dev/sda1", true, false), new VolumeImpl(150.0f,
            "/dev/sda2", false, false)), false);
   /**
    * In Nova, m1.small can run 64bit images.
    * 
    * @see InstanceType#M1_SMALL
    */
   public static final EC2Hardware M1_SMALL_NOVA = new EC2Hardware(null, InstanceType.M1_SMALL, ImmutableList
            .of(new Processor(1.0, 1.0)), 1740, ImmutableList.of(new VolumeImpl(10.0f, "/dev/sda1", true, false),
            new VolumeImpl(150.0f, "/dev/sda2", false, false)), any());

   /**
    * @see InstanceType#T1_MICRO
    */
   public static final EC2Hardware T1_MICRO = new EC2Hardware(InstanceType.T1_MICRO, ImmutableList.of(new Processor(
            1.0, 1.0)), 630, ImmutableList.<Volume> of(), RootDeviceType.EBS);
   /**
    * @see InstanceType#M1_LARGE
    */
   public static final EC2Hardware M1_LARGE = new EC2Hardware(InstanceType.M1_LARGE, ImmutableList.of(new Processor(
            2.0, 2.0)), 7680, ImmutableList.of(new VolumeImpl(10.0f, "/dev/sda1", true, false), new VolumeImpl(420.0f,
            "/dev/sdb", false, false), new VolumeImpl(420.0f, "/dev/sdc", false, false)), true);

   /**
    * @see InstanceType#M1_XLARGE
    */
   public static final EC2Hardware M1_XLARGE = new EC2Hardware(InstanceType.M1_XLARGE, ImmutableList.of(new Processor(
            4.0, 2.0)), 15360, ImmutableList.of(new VolumeImpl(10.0f, "/dev/sda1", true, false), new VolumeImpl(420.0f,
            "/dev/sdb", false, false), new VolumeImpl(420.0f, "/dev/sdc", false, false), new VolumeImpl(420.0f,
            "/dev/sdd", false, false), new VolumeImpl(420.0f, "/dev/sde", false, false)), true);
   /**
    * @see InstanceType#M2_XLARGE
    */
   public static final EC2Hardware M2_XLARGE = new EC2Hardware(InstanceType.M2_XLARGE, ImmutableList.of(new Processor(
            2.0, 3.25)), 17510, ImmutableList.of(new VolumeImpl(420.0f, "/dev/sda1", true, false)), true);
   /**
    * @see InstanceType#M2_2XLARGE
    */
   public static final EC2Hardware M2_2XLARGE = new EC2Hardware(InstanceType.M2_2XLARGE, ImmutableList
            .of(new Processor(4.0, 3.25)), 35020, ImmutableList.of(new VolumeImpl(10.0f, "/dev/sda1", true, false),
            new VolumeImpl(840.0f, "/dev/sdb", false, false)), true);
   /**
    * @see InstanceType#M2_4XLARGE
    */
   public static final EC2Hardware M2_4XLARGE = new EC2Hardware(InstanceType.M2_4XLARGE, ImmutableList
            .of(new Processor(8.0, 3.25)), 70041, ImmutableList.of(new VolumeImpl(10.0f, "/dev/sda1", true, false),
            new VolumeImpl(840.0f, "/dev/sdb", false, false), new VolumeImpl(840.0f, "/dev/sdc", false, false)), true);
   /**
    * @see InstanceType#C1_MEDIUM
    */
   public static final EC2Hardware C1_MEDIUM = new EC2Hardware(InstanceType.C1_MEDIUM, ImmutableList.of(new Processor(
            2.0, 2.5)), 1740, ImmutableList.of(new VolumeImpl(10.0f, "/dev/sda1", true, false), new VolumeImpl(340.0f,
            "/dev/sda2", false, false)), false);

   /**
    * @see InstanceType#C1_XLARGE
    */
   public static final EC2Hardware C1_XLARGE = new EC2Hardware(InstanceType.C1_XLARGE, ImmutableList.of(new Processor(
            8.0, 2.5)), 7168, ImmutableList.of(new VolumeImpl(10.0f, "/dev/sda1", true, false), new VolumeImpl(420.0f,
            "/dev/sdb", false, false), new VolumeImpl(420.0f, "/dev/sdc", false, false), new VolumeImpl(420.0f,
            "/dev/sdd", false, false), new VolumeImpl(420.0f, "/dev/sde", false, false)), true);

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      EC2Hardware other = (EC2Hardware) obj;
      if (instanceType == null) {
         if (other.instanceType != null)
            return false;
      } else if (!instanceType.equals(other.instanceType))
         return false;
      return true;
   }

}
