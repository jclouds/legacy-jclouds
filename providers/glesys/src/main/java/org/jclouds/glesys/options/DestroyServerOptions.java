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
public class DestroyServerOptions extends BaseHttpRequestOptions {
   public static class Builder {
      /**
       * Discard the server's ip on destroy
       */
      public static DestroyServerOptions keepIp() {
         return new DestroyServerOptions().keepIp(true);
      }

      /**
       * Discard the server's ip on destroy
       */
      public static DestroyServerOptions discardIp() {
         return new DestroyServerOptions().keepIp(false);
      }

   }

   /**
    * Determines whether to keep the server's ip attached to your account when destroying a server
    *
    * @param keepIp if true, keep the ip address
    */
   public DestroyServerOptions keepIp(boolean keepIp) {
      formParameters.put("keepip", Boolean.toString(keepIp));
      return this;
   }
}
