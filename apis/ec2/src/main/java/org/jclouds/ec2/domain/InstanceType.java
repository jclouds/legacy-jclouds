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

import org.jclouds.ec2.EC2AsyncClient;

/**
 * 
 * The type of the instance. Description accurate as of 8-15-2009 release.
 * 
 * @author Adrian Cole
 * @see EC2AsyncClient#describeInstances
 * @see EC2AsyncClient#runInstances
 * @see EC2AsyncClient#terminateInstances
 * 
 */
public class InstanceType {
   /**
    * Micro Instance
    * <ul>
    * <li>613 MB of memory</li>
    * <li>up to 2 ECUs (for short periodic bursts)</li>
    * <li>No instance storage (EBS storage only)</li>
    * <li>32-bit or 64-bit platform</li>
    * </ul>
    */
   public static final String T1_MICRO = "t1.micro";
   /**
    * Small Instance
    * <ul>
    * <li>1.7 GB memory</li>
    * <li>1 EC2 Compute Unit (1 virtual core with 1 EC2 Compute Unit)</li>
    * <li>160 GB instance storage (150 GB plus 10 GB root partition)</li>
    * <li>32-bit or 64-bit platform</li>
    * <li>I/O Performance: Moderate</li>
    * </ul>
    */
   public static final String M1_SMALL = "m1.small";
   /**
    * Medium Instance
    * <ul>
    * <li>3.75 GB memory</li>
    * <li>2 EC2 Compute Unit (1 virtual core with 2 EC2 Compute Unit)</li>
    * <li>410 GB instance storage</li>
    * <li>32-bit or 64-bit platform</li>
    * <li>I/O Performance: Moderate</li>
    * </ul>
    */
   public static final String M1_MEDIUM = "m1.medium";
   /**
    * Large Instance
    * <ul>
    * <li>7.5 GB memory</li>
    * <li>4 EC2 Compute Units (2 virtual cores with 2 EC2 Compute Units each)</li>
    * <li>850 GB instance storage (2x420 GB plus 10 GB root partition)</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M1_LARGE = "m1.large";
   /**
    * Extra Large Instance
    * <ul>
    * <li>15 GB memory</li>
    * <li>8 EC2 Compute Units (4 virtual cores with 2 EC2 Compute Units each)</li>
    * <li>1690 GB instance storage (4x420 GB plus 10 GB root partition)</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M1_XLARGE = "m1.xlarge";
   /**
    * High-Memory Extra Large Instance
    * <ul>
    * <li>17.1 GB of memory</li>
    * <li>6.5 EC2 Compute Units (2 virtual cores with 3.25 EC2 Compute Units
    * each)</li>
    * <li>420 GB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: Moderate</li>
    * </ul>
    */
   public static final String M2_XLARGE = "m2.xlarge";
   /**
    * High-Memory Double Extra Large Instance
    * <ul>
    * <li>34.2 GB of memory</li>
    * <li>13 EC2 Compute Units (4 virtual cores with 3.25 EC2 Compute Units
    * each)</li>
    * <li>850 GB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M2_2XLARGE = "m2.2xlarge";
   /**
    * High-Memory Quadruple Extra Large Instance
    * <ul>
    * <li>68.4 GB of memory</li>
    * <li>26 EC2 Compute Units (8 virtual cores with 3.25 EC2 Compute Units
    * each)</li>
    * <li>1690 GB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String M2_4XLARGE = "m2.4xlarge";
   /**
    * High-CPU Medium Instance
    * <ul>
    * <li>1.7 GB of memory</li>
    * <li>5 EC2 Compute Units (2 virtual cores with 2.5 EC2 Compute Units each)</li>
    * <li>350 GB of instance storage</li>
    * <li>32-bit platform</li>
    * <li>I/O Performance: Moderate</li>
    * </ul>
    */
   public static final String C1_MEDIUM = "c1.medium";
   /**
    * High-CPU Extra Large Instance
    * <ul>
    * <li>7 GB of memory</li>
    * <li>20 EC2 Compute Units (8 virtual cores with 2.5 EC2 Compute Units each)
    * </li>
    * <li>1690 GB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String C1_XLARGE = "c1.xlarge";
   
   /**
    * Cluster Compute Instance
    * <ul>
    * <li>22 GB of memory</li>
    * <li>33.5 EC2 Compute Units (2 x Intel Xeon X5570, quad-core "Nehalem"
    * architecture)</li>
    * <li>1690 GB of 64-bit storage (2 x 840 GB, plus 10 GB root partition)</li>
    * <li>10 Gbps Ethernet</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String CG1_4XLARGE = "cg1.4xlarge";
   
   /**
    * Cluster Compute Instance
    * <ul>
    * <li>23 GB of memory</li>
    * <li>33.5 EC2 Compute Units (2 x Intel Xeon X5570, quad-core "Nehalem"
    * architecture)</li>
    * <li>1690 GB of 64-bit storage (2 x 840 GB, plus 10 GB root partition)</li>
    * <li>10 Gbps Ethernet</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String CC1_4XLARGE = "cc1.4xlarge";
   
   /**
    * Cluster Compute Eight Extra Large specifications
    * <ul>
    * <li>60.5 GB of memory</li>
    * <li>88 EC2 Compute Units (Eight-core 2 x Intel Xeon)</li>
    * <li>3370 GB of 64-bit storage (4 x 840 GB, plus 10 GB root partition)</li>
    * <li>10 Gbps Ethernet</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   public static final String CC2_8XLARGE = "cc2.8xlarge";

}
