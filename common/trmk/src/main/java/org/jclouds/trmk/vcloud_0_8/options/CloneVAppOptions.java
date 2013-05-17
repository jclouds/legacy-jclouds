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
package org.jclouds.trmk.vcloud_0_8.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class CloneVAppOptions {

   private boolean deploy;
   private boolean powerOn;
   private String description;

   /**
    * the clone should be deployed after it is created
    */
   public CloneVAppOptions deploy() {
      this.deploy = true;
      return this;
   }

   /**
    * the clone should be powered on after it is deployed
    */
   public CloneVAppOptions powerOn() {
      checkState(deploy, "must set deploy before setting powerOn");
      powerOn = true;
      return this;
   }

   /**
    * the clone should be powered on after it is deployed
    */
   public CloneVAppOptions withDescription(String description) {
      checkNotNull(description, "description");
      this.description = description;
      return this;
   }

   public boolean isDeploy() {
      return deploy;
   }

   public boolean isPowerOn() {
      return powerOn;
   }

   public String getDescription() {
      return description;
   }

   public static class Builder {

      /**
       * @see CloneVAppOptions#deploy()
       */
      public static CloneVAppOptions deploy() {
         CloneVAppOptions options = new CloneVAppOptions();
         return options.deploy();
      }

      /**
       * @see CloneVAppOptions#powerOn()
       */
      public static CloneVAppOptions powerOn() {
         CloneVAppOptions options = new CloneVAppOptions();
         return options.powerOn();
      }

      /**
       * @see CloneVAppOptions#withDescription(String)
       */
      public static CloneVAppOptions withDescription(String description) {
         CloneVAppOptions options = new CloneVAppOptions();
         return options.withDescription(description);
      }
   }

}
