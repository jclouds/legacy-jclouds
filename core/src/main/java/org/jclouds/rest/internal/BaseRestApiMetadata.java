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
package org.jclouds.rest.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.rest.RestApiMetadata;
import org.jclouds.rest.RestContext;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

/**
 * Useful in creating rest apis.
 * 
 * @author Adrian Cole
 */
@Beta
public class BaseRestApiMetadata extends BaseApiMetadata implements RestApiMetadata {

   protected final Class<?> api;
   protected final Class<?> asyncApi;

   @Override
   public Builder toBuilder() {
      return new Builder(getApi(), getAsyncApi()).fromApiMetadata(this);
   }

   public BaseRestApiMetadata(Class<?> api, Class<?> asyncApi) {
      super(new Builder(api, asyncApi));
      this.api = checkNotNull(api, "api");
      this.asyncApi = checkNotNull(asyncApi, "asyncApi");
   }

   protected BaseRestApiMetadata(Builder builder) {
      super(builder);
      this.api = checkNotNull(builder.api, "api");
      this.asyncApi = checkNotNull(builder.asyncApi, "asyncApi");
   }
   
   public static Properties defaultProperties() {
      Properties props = BaseApiMetadata.defaultProperties();
      return props;
   }
   
   public static <S, A> TypeToken<RestContext<S, A>> contextToken(TypeToken<S> apiToken, TypeToken<A> asyncApiToken) {
      return new TypeToken<RestContext<S, A>>() {
      }.where(new TypeParameter<S>() {
      }, apiToken).where(new TypeParameter<A>() {
      }, asyncApiToken);
   }
   
   public static class Builder extends BaseApiMetadata.Builder implements RestApiMetadata.Builder {
      protected Class<?> api;
      protected Class<?> asyncApi;

      
      public Builder(Class<?> api, Class<?> asyncApi) {
         checkNotNull(api, "api");
         checkNotNull(asyncApi, "asyncApi");
         javaApi(api, asyncApi)
         .name(String.format("%s->%s", api.getSimpleName(), asyncApi.getSimpleName()))
         .context(contextToken(TypeToken.of(api), TypeToken.of(asyncApi)))
         .defaultProperties(BaseRestApiMetadata.defaultProperties());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder javaApi(Class<?> api, Class<?> asyncApi) {
         this.api = checkNotNull(api, "api");
         this.asyncApi = checkNotNull(asyncApi, "asyncApi");
         return this;
      }


      @Override
      public ApiMetadata build() {
         return new BaseRestApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         if (in instanceof RestApiMetadata) {
            RestApiMetadata rest = RestApiMetadata.class.cast(in);
            javaApi(rest.getApi(), rest.getAsyncApi());
         }
         super.fromApiMetadata(in);
         return this;
      }

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<?> getApi() {
      return api;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<?> getAsyncApi() {
      return asyncApi;
   }

   @Override
   protected ToStringHelper string() {
      return super.string().add("api", getApi()).add("asyncApi", getAsyncApi());
   }
}
