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
 * @author Adam Lowe
 */
public class ServerStatusOptions extends BaseHttpRequestOptions {

   public enum StatusTypes {
      STATE, CPU, MEMORY, DISK, BANDWIDTH, UPTIME;
   }

   public static class Builder {
      /**
       * @see org.jclouds.glesys.options.ServerStatusOptions#statusType
       */
      public static ServerStatusOptions state() {
         ServerStatusOptions options = new ServerStatusOptions();
         return options.statusType(StatusTypes.STATE);
      }

      /**
       * @see org.jclouds.glesys.options.ServerStatusOptions#statusType
       */
      public static ServerStatusOptions cpu() {
         ServerStatusOptions options = new ServerStatusOptions();
         return options.statusType(StatusTypes.CPU);
      }

      /**
       * @see org.jclouds.glesys.options.ServerStatusOptions#statusType
       */
      public static ServerStatusOptions memory() {
         ServerStatusOptions options = new ServerStatusOptions();
         return options.statusType(StatusTypes.MEMORY);
      }

      /**
       * @see org.jclouds.glesys.options.ServerStatusOptions#statusType
       */
      public static ServerStatusOptions disk() {
         ServerStatusOptions options = new ServerStatusOptions();
         return options.statusType(StatusTypes.DISK);
      }

      /**
       * @see org.jclouds.glesys.options.ServerStatusOptions#statusType
       */
      public static ServerStatusOptions bandwidth() {
         ServerStatusOptions options = new ServerStatusOptions();
         return options.statusType(StatusTypes.BANDWIDTH);
      }
   }

   /**
    * Select the given type of information form the server
    */
   public ServerStatusOptions statusType(StatusTypes type) {
      formParameters.put("statustype", type.name().toLowerCase());
      return this;
   }
}
