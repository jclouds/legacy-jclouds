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
package org.jclouds.glesys.options;

/**
 * @author Adam Lowe
 */
public class CloneServerOptions extends EditServerOptions {
   public static class Builder {
      /**
       * @see org.jclouds.glesys.options.CloneServerOptions#disksize
       */
      public static CloneServerOptions disksize(int disksize) {
         return CloneServerOptions.class.cast(new CloneServerOptions().disksize(disksize));
      }

      /**
       * @see org.jclouds.glesys.options.CloneServerOptions#memorysize
       */
      public static CloneServerOptions memorysize(int memorysize) {
         return CloneServerOptions.class.cast(new CloneServerOptions().memorysize(memorysize));
      }

      /**
       * @see org.jclouds.glesys.options.CloneServerOptions#cpucores
       */
      public static CloneServerOptions cpucores(int cpucores) {
         return CloneServerOptions.class.cast(new CloneServerOptions().cpucores(cpucores));
      }

      /**
       * @see org.jclouds.glesys.options.CloneServerOptions#cpucores
       */
      public static CloneServerOptions transfer(int transfer) {
         return CloneServerOptions.class.cast(new CloneServerOptions().transfer(transfer));
      }

      /**
       * @see org.jclouds.glesys.options.EditServerOptions#description
       */
      public static CloneServerOptions description(String description) {
         return CloneServerOptions.class.cast(new CloneServerOptions().description(description));
      }

      /**
       * @see org.jclouds.glesys.options.CloneServerOptions#dataCenter
       */
      public static CloneServerOptions dataCenter(String dataCenter) {
         return new CloneServerOptions().dataCenter(dataCenter);
      }
   }

   /**
    * Configure which datacenter to create the clone in
    */
   public CloneServerOptions dataCenter(String dataCenter) {
      formParameters.put("datacenter", dataCenter);
      return this;
   }
}
