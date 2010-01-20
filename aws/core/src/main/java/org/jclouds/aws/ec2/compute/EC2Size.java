/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.aws.ec2.compute;

import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.internal.SizeImpl;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class EC2Size extends SizeImpl {
   private final InstanceType instanceType;

   EC2Size(InstanceType instanceType, Integer cores, Integer ram, Integer disk,
            Iterable<Architecture> supportedArchitectures) {
      super(cores, ram, disk, supportedArchitectures);
      this.instanceType = instanceType;
   }

   /**
    * Returns the EC2 InstanceType associated with this size.
    */
   public InstanceType getInstanceType() {
      return instanceType;
   }

   /**
    * @see InstanceType#M1_SMALL
    */
   public static final EC2Size M1_SMALL = new EC2Size(InstanceType.M1_SMALL, 1, 1740, 160,
            ImmutableSet.of(Architecture.X86_32));
   /**
    * @see InstanceType#M1_LARGE
    */
   public static final EC2Size M1_LARGE = new EC2Size(InstanceType.M1_LARGE, 4, 7680, 850,
            ImmutableSet.of(Architecture.X86_64));
   /**
    * @see InstanceType#M1_XLARGE
    */
   public static final EC2Size M1_XLARGE = new EC2Size(InstanceType.M1_XLARGE, 8, 15360, 1690,
            ImmutableSet.of(Architecture.X86_64));
   /**
    * @see InstanceType#M2_2XLARGE
    */
   public static final EC2Size M2_2XLARGE = new EC2Size(InstanceType.M2_2XLARGE, 13, 35020, 850,
            ImmutableSet.of(Architecture.X86_64));
   /**
    * @see InstanceType#M2_4XLARGE
    */
   public static final EC2Size M2_4XLARGE = new EC2Size(InstanceType.M2_4XLARGE, 26, 70041, 1690,
            ImmutableSet.of(Architecture.X86_64));
   /**
    * @see InstanceType#C1_MEDIUM
    */
   public static final EC2Size C1_MEDIUM = new EC2Size(InstanceType.C1_MEDIUM, 5, 1740, 350,
            ImmutableSet.of(Architecture.X86_32));
   /**
    * @see InstanceType#C1_XLARGE
    */
   public static final EC2Size C1_XLARGE = new EC2Size(InstanceType.C1_XLARGE, 20, 7168, 1690,
            ImmutableSet.of(Architecture.X86_64));

}
