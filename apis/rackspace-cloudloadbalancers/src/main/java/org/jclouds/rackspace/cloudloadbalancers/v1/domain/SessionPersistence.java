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
package org.jclouds.rackspace.cloudloadbalancers.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Session persistence is a feature of the load balancing service that forces multiple requests from clients to be
 * directed to the same node. This is common with many web applications that do not inherently share application
 * state between back-end servers.
 */
public enum SessionPersistence {
   /**
    * A session persistence mechanism that inserts an HTTP cookie and is used to determine the destination back-end
    * node. This is supported for HTTP load balancing only.
    */
   HTTP_COOKIE,
   /**
    * A session persistence mechanism that will keep track of the source IP address that is mapped and is able to
    * determine the destination back-end node. This is supported for HTTPS pass-through and non-HTTP load balancing
    * only.
    */
   SOURCE_IP,

   UNRECOGNIZED;

   public static SessionPersistence fromValue(String sessionPersistence) {
      try {
         return valueOf(checkNotNull(sessionPersistence, "sessionPersistence"));
      }
      catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}
