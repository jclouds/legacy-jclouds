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
package org.jclouds.openstack.glance.v1_0.domain;

/**
 * Backing store types for glance images
 * 
 * @author Adam Lowe
 * @see <a href= "http://glance.openstack.org/architecture.html#what-is-a-store" />
 */
public enum StoreType {
   /**
    * Filesystem store
    */
   FILE,
   /**
    * S3 store
    */
   S3,
   /**
    * OpenStack swift store
    */
   SWIFT,
   /**
    * RADOS (Reliable Autonomic Distributed Object Store) Block Device store
    */
   RBD,
   /**
    * HTTP (read-only) store
    */
   HTTP;

   public String value() {
      return name().toLowerCase().replace("_", "+");
   }

   @Override
   public String toString() {
      return value();
   }
}
