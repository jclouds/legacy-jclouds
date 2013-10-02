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
 * Constants interface for the AWS provided namespaces as of 2012-04-24.
 *
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/DeveloperGuide/CW_Support_For_AWS.html#aws-namespaces" />
 *
 * @author Jeremy Whitlock
 */
public interface Namespaces {

   public static final String AUTO_SCALING = "AWS/AutoScaling";
   public static final String DYNAMODB = "AWS/DynamoDB";
   public static final String EBS = "AWS/EBS";
   public static final String EC2 = "AWS/EC2";
   public static final String ELB = "AWS/ELB";
   public static final String EMR = "AWS/EMR";
   public static final String RDS = "AWS/RDS";
   public static final String SNS = "AWS/SNS";
   public static final String SQS = "AWS/SQS";
   public static final String STORAGE_GATEWAY = "AWS/StorageGateway";

}
