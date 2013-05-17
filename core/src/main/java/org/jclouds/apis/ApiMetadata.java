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
package org.jclouds.apis;

import java.net.URI;
import java.util.Properties;
import java.util.Set;

import org.jclouds.Context;
import org.jclouds.View;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * The ApiMetadata interface allows jclouds to provide a plugin framework for
 * gathering cloud api metadata.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>, Adrian Cole
 * @since 1.5
 */
@Beta
public interface ApiMetadata {

   public static interface Builder<B extends Builder<B>>{
      /**
       * @see ApiMetadata#getId()
       */
      B id(String id);

      /**
       * @see ApiMetadata#getName()
       */
      B name(String name);

      /**
       * @see ApiMetadata#getContext()
       */
      B context(TypeToken<? extends Context> context);

      /**
       * @see ApiMetadata#getViews()
       */
      B view(Class<? extends View> view);
      
      /**
       * @see ApiMetadata#getViews()
       */
      B view(TypeToken<? extends View> view);

      /**
       * @see ApiMetadata#getViews()
       */
      B views(Set<TypeToken<? extends View>> views);

      /**
       * @see ApiMetadata#getEndpointName()
       */
      B endpointName(String endpointName);

      /**
       * @see ApiMetadata#getIdentityName()
       */
      B identityName(String identityName);

      /**
       * @see ApiMetadata#getCredentialName()
       */
      B credentialName(@Nullable String credentialName);

      /**
       * @see ApiMetadata#getVersion()
       */
      B version(String version);

      /**
       * @see ApiMetadata#getBuildVersion()
       */
      B buildVersion(@Nullable String buildVersion);

      /**
       * @see ApiMetadata#getDefaultEndpoint()
       */
      B defaultEndpoint(@Nullable String defaultEndpoint);

      /**
       * @see ApiMetadata#getDefaultIdentity()
       */
      B defaultIdentity(@Nullable String defaultIdentity);

      /**
       * @see ApiMetadata#getDefaultCredential()
       */
      B defaultCredential(@Nullable String defaultCredential);

      /**
       * @see ApiMetadata#getDefaultProperties()
       */
      B defaultProperties(Properties defaultProperties);
      
      /**
       * @see ApiMetadata#getDefaultModules()
       */
      B defaultModule(Class<? extends Module> defaultModule);

      /**
       * @see ApiMetadata#getDefaultModules()
       */
      B defaultModules(Set<Class<? extends Module>> defaultModules);
      
      /**
       * @see ApiMetadata#getDocumentation()
       */
      B documentation(URI documentation);

      ApiMetadata build();

      B fromApiMetadata(ApiMetadata from);
      
   }

   /**
    * @see Builder
    */
   Builder<?> toBuilder();

   /**
    * 
    * @return the api's unique identifier (ex. vcloud, virtualbox)
    */
   String getId();

   /**
    * 
    * @return the name (display name) of the api (ex. EC2 Base API)
    */
   String getName();

   /**
    * 
    * The {@code endpointName} helps the user supply the correct data when
    * prompted.
    * <p/>
    * For example, on OpenStack APIs, this could be: {@code Keystone url} <br/>
    * For file-based apis, this could be: {@code Path of byon.yaml}
    * <p/>
    * Default: {@code "https endpoint"}
    * <p/>
    * 
    * @return the name (display name) of an endpoint to this api (ex. Keystone
    *         url, vCloud Director URL).
    */
   String getEndpointName();
   
   /**
    * 
    * @return the name (display name) of an identity on this api (ex. user,
    *         email, account, apikey, tenantId:username)
    */
   String getIdentityName();

   /**
    * Note: if the api doesn't need a credential, this will return absent.
    * 
    * @return the name (display name) of a credential on this api, if it is
    *         required (ex. password, secret, rsaKey)
    */
   Optional<String> getCredentialName();

   /**
    * Explicitly identifies the version of an api.
    */
   String getVersion();

   /**
    * Explicitly identifies the build that the server jclouds connects to is
    * running.
    * 
    * For example, for virtualbox, the api version may be {@code 4.1.8} while
    * the build version is {@code 4.1.8r75467}. Or a vcloud endpoint may be api
    * version {@code 1.0} while the build is {@code 1.5.0.0.124312}
    */
   Optional<String> getBuildVersion();

   /**
    * Explicitly identifies the most top-level endpoint to a service provider.
    * This helps differentiate two providers of the same api, or a different
    * environments providing the same api.
    * 
    * <h3>note</h3>
    * 
    * The type of endpoint is {@code String} as we permit endpoints that require
    * variable expansion.
    * 
    * ex.
    * 
    * <pre>
    * https://${jclouds.identity}.blob.core.windows.net
    * </pre>
    * 
    * @return the api's default endpoint, if known.
    */
   Optional<String> getDefaultEndpoint();

   /**
    * Explicitly identifies the login identity into a provider
    * 
    * @return the login identity into a provider, if known.
    */
   Optional<String> getDefaultIdentity();

   /**
    * Explicitly sets the secret, which when combined with the identity, will
    * create an authenticated subject or session
    * 
    * @return the api's default credential, if known.
    * @see #getDefaultIdentity
    * @see #getCredentialName
    */
   Optional<String> getDefaultCredential();

   /**
    * Configuration Properties used when creating connections to this api
    * 
    * @return properties used to create connections to this api
    */
   Properties getDefaultProperties();

   /**
    * Modules that configure dependency injection for this context
    * 
    * @return modules that configure dependency injection for this context
    */
   Set<Class<? extends Module>> getDefaultModules();
   
   /**
    * 
    * @return the url for the API documentation related to this service
    */
   URI getDocumentation();

   /**
    * @return the primary context of this api, for example {@code RestContext<EC2Client, EC2AsyncClient>}
    */
   TypeToken<? extends Context> getContext();
   
   /**
    * @return types of contexts this can be transformed into, for example {@code BlobStoreContext}
    */
   Set<TypeToken<? extends View>> getViews();

}
