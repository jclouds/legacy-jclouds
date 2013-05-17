/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudwatch.domain;

/**
 * Constants interface for the AWS AutoScaling dimensions and metric names as of 2012-04-24.
 *
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/DeveloperGuide/CW_Support_For_AWS.html#ec2-metricscollected" />
 *
 * @author Jeremy Whitlock
 */
public interface EC2Constants {

   public static class Dimension {

      public static final String AUTO_SCALING_GROUP_NAME = "AutoScalingGroupName";
      public static final String IMAGE_ID = "ImageId";
      public static final String INSTANCE_ID = "InstanceId";
      public static final String INSTANCE_TYPE = "InstanceType";

   }

   public static class MetricName {

      public static final String CPU_UTILIZATION = "CPUUtilization";
      public static final String DISK_READ_BYTES = "DiskReadBytes";
      public static final String DISK_READ_OPS = "DiskReadOps";
      public static final String DISK_WRITE_BYTES = "DiskWriteBytes";
      public static final String DISK_WRITE_OPS = "DiskWriteOps";
      public static final String NETWORK_IN = "NetworkIn";
      public static final String NETWORK_OUT = "NetworkOut";

   }

}
