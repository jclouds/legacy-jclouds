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
package org.jclouds.glesys.options;

/**
 * @author Adam Lowe
 */
public class CloneServerOptions extends UpdateServerOptions {
   public static class Builder {
      /**
       * @see org.jclouds.glesys.options.CloneServerOptions#diskSizeGB
       */
      public static CloneServerOptions diskSizeGB(int diskSizeGB) {
         return CloneServerOptions.class.cast(new CloneServerOptions().diskSizeGB(diskSizeGB));
      }

      /**
       * @see org.jclouds.glesys.options.CloneServerOptions#memorySizeMB
       */
      public static CloneServerOptions memorySizeMB(int memorySizeMB) {
         return CloneServerOptions.class.cast(new CloneServerOptions().memorySizeMB(memorySizeMB));
      }

      /**
       * @see org.jclouds.glesys.options.CloneServerOptions#cpuCores
       */
      public static CloneServerOptions cpucores(int cpucores) {
         return CloneServerOptions.class.cast(new CloneServerOptions().cpuCores(cpucores));
      }

      /**
       * @see org.jclouds.glesys.options.CloneServerOptions#transferGB
       */
      public static CloneServerOptions transferGB(int transferGB) {
         return CloneServerOptions.class.cast(new CloneServerOptions().transferGB(transferGB));
      }

      /**
       * @see org.jclouds.glesys.options.UpdateServerOptions#description
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
