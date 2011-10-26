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
package org.jclouds.providers;

import java.net.URI;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

/**
 * The ProviderMetadata interface allows jclouds to provide a plugin framework for gathering cloud
 * provider metadata.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
public interface ProviderMetadata {

   public static final String BLOBSTORE_TYPE = "blobstore";
   public static final String COMPUTE_TYPE = "compute";
   public static final String LOADBALANCER_TYPE = "loadbalancer";
   public static final String TABLE_TYPE = "table";
   public static final String QUEUE_TYPE = "queue";
   public static final String MONITOR_TYPE = "monitor";

   /**
    * 
    * @return the provider's unique identifier
    */
   public String getId();

   /**
    * 
    * @return the provider's type
    */
   public String getType();

   /**
    * 
    * @return the name (display name) of the provider
    */
   public String getName();

   /**
    * 
    * @return the name (display name) of an identity on this provider (ex. user, email, account,
    *         apikey)
    */
   public String getIdentityName();

   /**
    * 
    * @return the name (display name) of a credential on this provider, or null if there is none
    *         (ex. password, secret, rsaKey)
    */
   @Nullable
   public String getCredentialName();

   /**
    * 
    * @return the url for the provider's homepage
    */
   public URI getHomepage();

   /**
    * 
    * @return the url for the provider's console
    */
   public URI getConsole();

   /**
    * 
    * @return the url for the API documentation related to this service
    */
   public URI getApiDocumentation();

   /**
    * 
    * @return all known services linked to the same account on this provider
    */
   public Set<String> getLinkedServices();

   /**
    * 
    * @return all known region/location ISO 3166 codes
    */
   public Set<String> getIso3166Codes();
}