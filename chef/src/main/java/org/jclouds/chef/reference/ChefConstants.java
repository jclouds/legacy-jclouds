/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.chef.reference;

/**
 * Configuration properties and constants used in Chef connections.
 * 
 * @author Adrian Cole
 */
public interface ChefConstants {
   public static final String PROPERTY_CHEF_ENDPOINT = "jclouds.chef.endpoint";
   /**
    * There are generally 3 types of identities
    * <ul>
    * <li>validator - used to create clients within an organization; {@code orgname}-validator</li>
    * <li>client - scoped to an organization, used on nodes to run chef</li>
    * <li>user - used to run commands like knife and access cookbook sites</li>
    * </ul>
    * 
    */
   public static final String PROPERTY_CHEF_IDENTITY = "jclouds.chef.identity";
   /**
    * The PEM-encoded key
    */
   public static final String PROPERTY_CHEF_RSA_KEY = "jclouds.chef.rsa-key";

   /**
    * how often to refresh timestamps in seconds.
    */
   public static final String PROPERTY_CHEF_TIMESTAMP_INTERVAL = "jclouds.chef.timestamp-interval";
}
