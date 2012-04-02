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
package org.jclouds.apis;


import java.net.URI;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;


/**
 * The ApiMetadata interface allows jclouds to provide a plugin framework for
 * gathering cloud api metadata.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>, Adrian Cole
 * @since 1.5
 */
@Beta
public interface ApiMetadata {

   public static interface Builder<B extends Builder<B>> {
      /**
       * @see ApiMetadata#getId()
       */
      B id(String id);

      /**
       * @see ApiMetadata#getName()
       */
      B name(String name);

      /**
       * @see ApiMetadata#getType()
       */
      B type(ApiType type);

      /**
       * @see ApiMetadata#getIdentityName()
       */
      B identityName(String identityName);

      /**
       * @see ApiMetadata#getCredentialName()
       */
      B credentialName(@Nullable String credentialName);

      /**
       * @see ApiMetadata#getDocumentation()
       */
      B documentation(URI documentation);

      ApiMetadata build();

      B fromApiMetadata(ApiMetadata in);
   }

   /**
    * @see Builder
    */
   Builder<?> toBuilder();

   /**
    * 
    * @return the api's unique identifier
    */
   public String getId();

   /**
    * 
    * @return the name (display name) of the api
    */
   public String getName();

   /**
    * 
    * @return the api's type
    */
   public ApiType getType();

   /**
    * 
    * @return the name (display name) of an identity on this api (ex. user,
    *         email, account, apikey)
    */
   public String getIdentityName();

   /**
    * 
    * @return the name (display name) of a credential on this api, or null if
    *         there is none (ex. password, secret, rsaKey)
    */
   @Nullable
   public String getCredentialName();

   /**
    * 
    * @return the url for the API documentation related to this service
    */
   public URI getDocumentation();

}