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
public class ServerCloneOptions extends ServerEditOptions {
   public static class Builder {
      /**
       * @see org.jclouds.glesys.options.ServerCloneOptions#disksize
       */
      public static ServerCloneOptions disksize(int disksize) {
         ServerCloneOptions options = new ServerCloneOptions();
         return ServerCloneOptions.class.cast(options.disksize(disksize));
      }

      /**
       * @see org.jclouds.glesys.options.ServerCloneOptions#memorysize
       */
      public static ServerCloneOptions memorysize(int memorysize) {
         ServerCloneOptions options = new ServerCloneOptions();
         return ServerCloneOptions.class.cast(options.memorysize(memorysize));
      }

      /**
       * @see org.jclouds.glesys.options.ServerCloneOptions#cpucores
       */
      public static ServerCloneOptions cpucores(int cpucores) {
         ServerCloneOptions options = new ServerCloneOptions();
         return ServerCloneOptions.class.cast(options.cpucores(cpucores));
      }

      /**
       * @see org.jclouds.glesys.options.ServerCloneOptions#cpucores
       */
      public static ServerCloneOptions transfer(int transfer) {
         ServerCloneOptions options = new ServerCloneOptions();
         return ServerCloneOptions.class.cast(options.transfer(transfer));
      }

      /**
       * @see org.jclouds.glesys.options.ServerCloneOptions#hostname
       */
      public static ServerCloneOptions hostname(String hostname) {
         ServerCloneOptions options = new ServerCloneOptions();
         return ServerCloneOptions.class.cast(options.hostname(hostname));
      }

      /**
       * @see org.jclouds.glesys.options.ServerEditOptions#description
       */
      public static ServerCloneOptions description(String description) {
         ServerCloneOptions options = new ServerCloneOptions();
         return ServerCloneOptions.class.cast(options.description(description));
      }

      /**
       * @see org.jclouds.glesys.options.ServerCloneOptions#dataCenter
       */
      public static ServerCloneOptions dataCenter(String dataCenter) {
         ServerCloneOptions options = new ServerCloneOptions();
         return options.dataCenter(dataCenter);
      }
   }

   public ServerCloneOptions dataCenter(String dataCenter) {
      formParameters.put("datacenter", dataCenter);
      return this;
   }
}
