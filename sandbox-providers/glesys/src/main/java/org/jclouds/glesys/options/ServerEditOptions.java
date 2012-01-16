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
public class ServerEditOptions extends BaseHttpRequestOptions {

   public static class Builder {
      /**
       * @see org.jclouds.glesys.options.ServerEditOptions#disksize
       */
      public static ServerEditOptions disksize(int disksize) {
         ServerEditOptions options = new ServerEditOptions();
         return options.disksize(disksize);
      }
      
      /**
       * @see org.jclouds.glesys.options.ServerEditOptions#memorysize
       */
      public static ServerEditOptions memorysize(int memorysize) {
         ServerEditOptions options = new ServerEditOptions();
         return options.memorysize(memorysize);
      }

      /**
       * @see org.jclouds.glesys.options.ServerEditOptions#cpucores
       */
      public static ServerEditOptions cpucores(int cpucores) {
         ServerEditOptions options = new ServerEditOptions();
         return options.cpucores(cpucores);
      }

      /**
       * @see org.jclouds.glesys.options.ServerEditOptions#cpucores
       */
      public static ServerEditOptions transfer(int transfer) {
         ServerEditOptions options = new ServerEditOptions();
         return options.transfer(transfer);
      }
      
      /**
       * @see org.jclouds.glesys.options.ServerEditOptions#hostname
       */
      public static ServerEditOptions hostname(String hostname) {
         ServerEditOptions options = new ServerEditOptions();
         return options.hostname(hostname);
      }

      /**
       * @see org.jclouds.glesys.options.ServerEditOptions#description
       */
      public static ServerEditOptions description(String description) {
         ServerEditOptions options = new ServerEditOptions();
         return options.description(description);
      }
   }

   /** Configure the size of the disk, in GB, of the server */
   public ServerEditOptions disksize(int disksize) {
      formParameters.put("disksize", Integer.toString(disksize));
      return this;
   }

   /** Configure the amount of RAM, in MB, allocated to the server */
   public ServerEditOptions memorysize(int memorysize) {
      formParameters.put("memorysize", Integer.toString(memorysize));
      return this;
   }

   /** Configure the number of CPU cores allocated to the server */
   public ServerEditOptions cpucores(int cpucores) {
      formParameters.put("cpucores", Integer.toString(cpucores));
      return this;
   }

   /** Configure the transfer setting for the server */
   public ServerEditOptions transfer(int transfer) {
      formParameters.put("cpucores", Integer.toString(transfer));
      return this;
   }

   /** Configure the host name of the server (must be unique within the GleSYS account) */
   public ServerEditOptions hostname(String hostname) {
      formParameters.put("hostname", hostname);
      return this;
   }

   /** Configure the description of the server */
   public ServerEditOptions description(String description) {
      formParameters.put("description", description);
      return this;
   }

}
