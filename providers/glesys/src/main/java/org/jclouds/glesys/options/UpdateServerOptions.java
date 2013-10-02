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

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * 
 * @author Adam Lowe
 */
public class UpdateServerOptions extends BaseHttpRequestOptions {

   public static class Builder {
      /**
       * @see org.jclouds.glesys.options.UpdateServerOptions#diskSizeGB
       */
      public static UpdateServerOptions disksizeGB(int disksizeGB) {
         return new UpdateServerOptions().diskSizeGB(disksizeGB);
      }
      
      /**
       * @see org.jclouds.glesys.options.UpdateServerOptions#memorySizeMB
       */
      public static UpdateServerOptions memorysizeMB(int memorysizeMB) {
         return new UpdateServerOptions().memorySizeMB(memorysizeMB);
      }

      /**
       * @see org.jclouds.glesys.options.UpdateServerOptions#cpuCores
       */
      public static UpdateServerOptions cpucores(int cpucores) {
         UpdateServerOptions options = new UpdateServerOptions();
         return options.cpuCores(cpucores);
      }

      /**
       * @see org.jclouds.glesys.options.UpdateServerOptions#transferGB
       */
      public static UpdateServerOptions transferGB(int transferGB) {
         return new UpdateServerOptions().transferGB(transferGB);
      }
      
      /**
       * @see org.jclouds.glesys.options.UpdateServerOptions#hostname
       */
      public static UpdateServerOptions hostname(String hostname) {
         UpdateServerOptions options = new UpdateServerOptions();
         return options.hostname(hostname);
      }

      /**
       * @see org.jclouds.glesys.options.UpdateServerOptions#description
       */
      public static UpdateServerOptions description(String description) {
         UpdateServerOptions options = new UpdateServerOptions();
         return options.description(description);
      }
   }

   /** Configure the size of the disk, in GB, of the server */
   public UpdateServerOptions diskSizeGB(int diskSizeGB) {
      formParameters.put("disksize", Integer.toString(diskSizeGB));
      return this;
   }

   /** Configure the amount of RAM, in MB, allocated to the server */
   public UpdateServerOptions memorySizeMB(int memorySizeMB) {
      formParameters.put("memorysize", Integer.toString(memorySizeMB));
      return this;
   }

   /** Configure the number of CPU cores allocated to the server */
   public UpdateServerOptions cpuCores(int cpucores) {
      formParameters.put("cpucores", Integer.toString(cpucores));
      return this;
   }

   /** Configure the transfer setting for the server */
   public UpdateServerOptions transferGB(int transferGB) {
      formParameters.put("transfer", Integer.toString(transferGB));
      return this;
   }

   /** Configure the host name of the server (must be unique within the GleSYS account) */
   public UpdateServerOptions hostname(String hostname) {
      formParameters.put("hostname", hostname);
      return this;
   }

   /** Configure the description of the server */
   public UpdateServerOptions description(String description) {
      formParameters.put("description", description);
      return this;
   }

}
