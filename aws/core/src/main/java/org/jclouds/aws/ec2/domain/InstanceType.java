package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.aws.ec2.EC2AsyncClient;

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
public enum InstanceType {
   /**
    * Small Instance
    * <ul>
    * <li>1.7 GB memory</li>
    * <li>1 EC2 Compute Unit (1 virtual core with 1 EC2 Compute Unit)</li>
    * <li>160 GB instance storage (150 GB plus 10 GB root partition)</li>
    * <li>32-bit platform</li>
    * <li>I/O Performance: Moderate</li>
    * </ul>
    */
   M1_SMALL,
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
   M1_LARGE,
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
   M1_XLARGE,
   /**
    * High-Memory Double Extra Large Instance
    * <ul>
    * <li>34.2 GB of memory</li>
    * <li>13 EC2 Compute Units (4 virtual cores with 3.25 EC2 Compute Units each)</li>
    * <li>850 GB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   M2_2XLARGE,
   /**
    * High-Memory Quadruple Extra Large Instance
    * <ul>
    * <li>68.4 GB of memory</li>
    * <li>26 EC2 Compute Units (8 virtual cores with 3.25 EC2 Compute Units each)</li>
    * <li>1690 GB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   M2_4XLARGE,
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
   C1_MEDIUM,
   /**
    * High-CPU Extra Large Instance
    * <ul>
    * <li>7 GB of memory</li>
    * <li>20 EC2 Compute Units (8 virtual cores with 2.5 EC2 Compute Units each)</li>
    * <li>1690 GB of instance storage</li>
    * <li>64-bit platform</li>
    * <li>I/O Performance: High</li>
    * </ul>
    */
   C1_XLARGE;
   public String value() {
      return name().toLowerCase().replaceAll("_", ".");
   }

   @Override
   public String toString() {
      return value();
   }

   public static InstanceType fromValue(String type) {
      return valueOf(checkNotNull(type, "type").replaceAll("\\.", "_").toUpperCase());
   }

}