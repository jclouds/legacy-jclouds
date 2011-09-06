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
package org.jclouds.vcloud.options;

import static com.google.common.base.Preconditions.checkState;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class CloneVAppOptions extends CloneOptions {

   private boolean deploy;
   private boolean powerOn;

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
    * {@inheritDoc}
    */
   @Override
   public CloneVAppOptions description(String description) {
      return CloneVAppOptions.class.cast(super.description(description));
   }

   public boolean isDeploy() {
      return deploy;
   }

   public boolean isPowerOn() {
      return powerOn;
   }

   public static class Builder {

      /**
       * @see CloneVAppOptions#deploy()
       */
      public static CloneVAppOptions deploy() {
         return new CloneVAppOptions().deploy();
      }

      /**
       * @see CloneVAppOptions#powerOn()
       */
      public static CloneVAppOptions powerOn() {
         return new CloneVAppOptions().powerOn();
      }

      /**
       * {@inheritDoc}
       */
      public static CloneVAppOptions description(String description) {
         return new CloneVAppOptions().description(description);
      }
   }

}
