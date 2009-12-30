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

import org.jclouds.aws.ec2.EC2AsyncClient;

/**
 * 
 * The current state of the instance..
 * 
 * @author Adrian Cole
 * @see EC2AsyncClient#describeInstances
 * @see EC2AsyncClient#runInstances
 * @see EC2AsyncClient#terminateInstances
 * 
 */
public enum InstanceState {

   /**
    * the instance is in the process of being launched
    */
   PENDING,

   /**
    * the instance launched (although the boot process might not be completed)
    */
   RUNNING,

   /**
    * the instance started shutting down
    */
   SHUTTING_DOWN,
   /**
    * the instance terminated
    */
   TERMINATED,
   /**
    * the instance is stopping
    */
   STOPPING,
   /**
    * the instance is stopped
    */
   STOPPED;

   public String value() {
      return name().toLowerCase().replaceAll("_", "-");
   }

   @Override
   public String toString() {
      return value();
   }

   public static InstanceState fromValue(String state) {
      return valueOf(checkNotNull(state, "state").replaceAll("-", "_").toUpperCase());
   }

   public static InstanceState fromValue(int v) {
      switch (v) {
         case 0:
            return PENDING;
         case 16:
            return RUNNING;
         case 32:
            return SHUTTING_DOWN;
         case 48:
            return TERMINATED;
         case 64:
            return STOPPING;
         case 80:
            return STOPPED;
         default:
            throw new IllegalArgumentException("invalid state:" + v);
      }
   }
}