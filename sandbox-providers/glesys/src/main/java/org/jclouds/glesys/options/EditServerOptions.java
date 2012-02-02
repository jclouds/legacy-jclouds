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

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * 
 * @author Adam Lowe
 */
public class EditServerOptions extends BaseHttpRequestOptions {

   public static class Builder {
      /**
       * @see org.jclouds.glesys.options.EditServerOptions#diskSizeGB
       */
      public static EditServerOptions disksizeGB(int disksizeGB) {
         return new EditServerOptions().diskSizeGB(disksizeGB);
      }
      
      /**
       * @see org.jclouds.glesys.options.EditServerOptions#memorySizeMB
       */
      public static EditServerOptions memorysizeMB(int memorysizeMB) {
         return new EditServerOptions().memorySizeMB(memorysizeMB);
      }

      /**
       * @see org.jclouds.glesys.options.EditServerOptions#cpuCores
       */
      public static EditServerOptions cpucores(int cpucores) {
         EditServerOptions options = new EditServerOptions();
         return options.cpuCores(cpucores);
      }

      /**
       * @see org.jclouds.glesys.options.EditServerOptions#transferGB
       */
      public static EditServerOptions transferGB(int transferGB) {
         return new EditServerOptions().transferGB(transferGB);
      }
      
      /**
       * @see org.jclouds.glesys.options.EditServerOptions#hostname
       */
      public static EditServerOptions hostname(String hostname) {
         EditServerOptions options = new EditServerOptions();
         return options.hostname(hostname);
      }

      /**
       * @see org.jclouds.glesys.options.EditServerOptions#description
       */
      public static EditServerOptions description(String description) {
         EditServerOptions options = new EditServerOptions();
         return options.description(description);
      }
   }

   /** Configure the size of the disk, in GB, of the server */
   public EditServerOptions diskSizeGB(int diskSizeGB) {
      formParameters.put("disksize", Integer.toString(diskSizeGB));
      return this;
   }

   /** Configure the amount of RAM, in MB, allocated to the server */
   public EditServerOptions memorySizeMB(int memorySizeMB) {
      formParameters.put("memorysize", Integer.toString(memorySizeMB));
      return this;
   }

   /** Configure the number of CPU cores allocated to the server */
   public EditServerOptions cpuCores(int cpucores) {
      formParameters.put("cpucores", Integer.toString(cpucores));
      return this;
   }

   /** Configure the transfer setting for the server */
   public EditServerOptions transferGB(int transferGB) {
      formParameters.put("transfer", Integer.toString(transferGB));
      return this;
   }

   /** Configure the host name of the server (must be unique within the GleSYS account) */
   public EditServerOptions hostname(String hostname) {
      formParameters.put("hostname", hostname);
      return this;
   }

   /** Configure the description of the server */
   public EditServerOptions description(String description) {
      formParameters.put("description", description);
      return this;
   }

}
