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
package org.jclouds.rackspace.cloudloadbalancers.features;

import org.jclouds.rackspace.cloudloadbalancers.domain.SessionPersistence;


/**
 * Session persistence is a feature of the load balancing service that forces multiple requests from clients to be 
 * directed to the same node. This is common with many web applications that do not inherently share application 
 * state between back-end servers. Two session persistence modes are available, HTTP Cookie and Source IP.
 *  
 * @see SessionPersistenceAsyncApi
 * @author Everett Toews
 */
public interface SessionPersistenceApi {
   /**
    * Get the current session persistence.
    * 
    * @see SessionPersistence
    */
   SessionPersistence get();
   
   /**
    * Create session persistence.
    * 
    * @see SessionPersistence
    */
   void create(SessionPersistence sessionPersistence);
   
   /**
    * Delete session persistence.
    * 
    * @see SessionPersistence
    */
   void delete();
}