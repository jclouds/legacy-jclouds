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
package org.jclouds.joyent.cloudapi.v6_5.config;

/**
 * Configuration properties and constants used in joyent JoyentCloud connections.
 * 
 * @author Adrian Cole
 */
public class JoyentCloudProperties {

   /**
    * Whenever a node is created, automatically generate keys for groups, as needed, also
    * delete the key(s) when the last node in the group is destroyed.
    */
   public static final String AUTOGENERATE_KEYS = "jclouds.joyent-cloudapi.autogenerate-keys";

}
