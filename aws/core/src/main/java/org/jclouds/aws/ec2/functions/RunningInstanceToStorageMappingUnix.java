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
package org.jclouds.aws.ec2.functions;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.RunningInstance;

import java.util.Map;

import static org.jclouds.compute.reference.ComputeServiceConstants.LOCAL_PARTITION_GB_PATTERN;

/**
 * Map the instance to storage information known about it. This information is statically set as
 * described by Amazon.
 * 
 * Note that having the partitions available doesn't mean they're formatted/mounted by default. The
 * links below describe what partitions are formatted initially. To format/mount an available
 * device, refer to <a href="http://meinit.nl/howto-use-amazon-elastic-compute-cloud-ec2">this
 * article</a>.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/2010-06-15/UserGuide/index.html?instance-storage-concepts.html"
 *      />
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/UserGuide/index.html?instance-types.html"
 *      />
 * 
 * @author Oleksiy Yarmula
 */
public class RunningInstanceToStorageMappingUnix implements Function<RunningInstance, Map<String, String>> {

   public final static String ROOT_PARTITION_NAME_UNIX = "/dev/sda1";

   @Override
   public Map<String, String> apply(RunningInstance instance) {
      final String instanceType = instance.getInstanceType();

      Map<String, String> mapping = Maps.newHashMap();

      // root partition
      mapping.put(String.format(LOCAL_PARTITION_GB_PATTERN, ROOT_PARTITION_NAME_UNIX),
               getRootPartitionSizeForInstanceType(instanceType) + "");

      // primary partition (always formatted/mounted)
      mapping.put(String.format(LOCAL_PARTITION_GB_PATTERN, getPrimaryPartitionDeviceName(instanceType)),
               getPrimaryPartitionSizeForInstanceType(instanceType) + "");

      // additional partitions if any
      for (Map.Entry<String, Integer> entry : getAdditionalPartitionsMapping(instanceType).entrySet()) {
         mapping.put(String.format(LOCAL_PARTITION_GB_PATTERN, entry.getKey()), entry.getValue() + "");
      }

      return mapping;
   }

   /**
    * Retrieve the root partition size. Note, this is usually a rather small partition. Refer to
    * {@link #getPrimaryPartitionSizeForInstanceType} to determine the size of the primary (usually,
    * biggest) partition. In some cases, for large instances there are several partitions of the
    * size of primary partition.
    * 
    * @param instanceType
    *           for which the root partition size is to be determined
    * @return size in GB
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/2010-06-15/UserGuide/index.html?instance-storage-concepts.html"
    *      />
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/UserGuide/index.html?instance-types.html"
    *      />
    */
   public int getRootPartitionSizeForInstanceType(String instanceType) {
      /*
       * per documentation at
       * http://docs.amazonwebservices.com/AWSEC2/latest/UserGuide/index.html?instance-types.html M2
       * XLARGE doesn't have the root partition TODO verify
       */
      if (InstanceType.M2_XLARGE.equals(instanceType))
         return 0;

      // other types have 10 GB root partition
      return 10;
   }

   public static String getPrimaryPartitionDeviceName(String instanceType) {
      if (InstanceType.M1_SMALL.equals(instanceType) || InstanceType.C1_MEDIUM.equals(instanceType))
         return "/dev/sda2";
      return "/dev/sdb";
   }

   /**
    * Retrieve the primary partition size.
    * 
    * This is usually the biggest partition. In some cases, for large instances there are several
    * partitions of the size of primary partition.
    * 
    * @param instanceType
    *           for which the primary partition size is to be determined
    * @return size in GB
    */
   public static int getPrimaryPartitionSizeForInstanceType(String instanceType) {
      if (InstanceType.M1_SMALL.equals(instanceType)) {
         return 150;
      } else if (InstanceType.C1_MEDIUM.equals(instanceType)) {
         return 340;
      } else if (InstanceType.M1_LARGE.equals(instanceType)) {
         return 420;
      } else if (InstanceType.M1_XLARGE.equals(instanceType)) {
         return 420;
      } else if (InstanceType.C1_XLARGE.equals(instanceType)) {
         return 420;
      } else if (InstanceType.M2_XLARGE.equals(instanceType)) {
         return 420;
      } else if (InstanceType.M2_2XLARGE.equals(instanceType)) {
         return 840;
      } else if (InstanceType.M2_4XLARGE.equals(instanceType)) {
         return 840;
      } else if (InstanceType.CC1_4XLARGE.equals(instanceType))
         return 840;
      return 150;// TODO make this more graceful
   }

   /**
    * Retrieve additional devices mapping (non-root and non-primary) for the instance type.
    * 
    * @param instanceType
    * @return map with device name(s) and size(s) or empty map if the instance doesn't have any
    *         additional
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/UserGuide/index.html?concepts-amis-and-instances.html#instance-types"
    *      />
    */
   public static Map<String, Integer> getAdditionalPartitionsMapping(String instanceType) {
      Map<String, Integer> mapping = Maps.newHashMap();

      int size = 0;
      if (InstanceType.M1_LARGE.equals(instanceType) || InstanceType.M1_XLARGE.equals(instanceType)
               || InstanceType.C1_XLARGE.equals(instanceType)) {
         size = 420;
      } else if (InstanceType.M2_4XLARGE.equals(instanceType) || instanceType.startsWith("cc")) {
         size = 840;
      }

      // m1.large, m1.xlarge, and c1.xlarge
      if (InstanceType.M1_LARGE.equals(instanceType) || InstanceType.M1_XLARGE.equals(instanceType)
               || InstanceType.C1_XLARGE.equals(instanceType) || InstanceType.M2_4XLARGE.equals(instanceType)
               || instanceType.startsWith("cc")) {

         mapping.put("/dev/sdc", size);
      }

      if (InstanceType.M1_XLARGE.equals(instanceType) || InstanceType.C1_XLARGE.equals(instanceType)) {
         mapping.put("/dev/sdd", size);
      }

      if (InstanceType.M1_XLARGE.equals(instanceType) || InstanceType.C1_XLARGE.equals(instanceType)) {
         mapping.put("/dev/sde", size);
      }

      return mapping;
   }
}
