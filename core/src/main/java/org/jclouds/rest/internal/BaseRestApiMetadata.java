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
package org.jclouds.rest.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.rest.RestApiMetadata;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

/**
 * Useful in creating rest apis.
 * 
 * @author Adrian Cole
 * @deprecated please use {@link BaseHttpApiMetadata} as
 *             async interface will be removed in jclouds 1.7.
 */
@Beta
@Deprecated
public abstract class BaseRestApiMetadata extends BaseApiMetadata implements RestApiMetadata {

   protected final Class<?> api;
   protected final Class<?> asyncApi;

   protected BaseRestApiMetadata(Builder<?> builder) {
      super(builder);
      this.api = checkNotNull(builder.api, "api");
      this.asyncApi = checkNotNull(builder.asyncApi, "asyncApi");
   }
   
   public static Properties defaultProperties() {
      Properties props = BaseApiMetadata.defaultProperties();
      return props;
   }
   
   public static <S, A> TypeToken<org.jclouds.rest.RestContext<S, A>> contextToken(TypeToken<S> apiToken, TypeToken<A> asyncApiToken) {
      return new TypeToken<org.jclouds.rest.RestContext<S, A>>() {
         private static final long serialVersionUID = 1L;
      }.where(new TypeParameter<S>() {
      }, apiToken).where(new TypeParameter<A>() {
      }, asyncApiToken);
   }
   
   public abstract static class Builder<T extends Builder<T>> extends BaseApiMetadata.Builder<T> implements RestApiMetadata.Builder<T> {
      protected Class<?> api;
      protected Class<?> asyncApi;
      
      protected Builder(Class<?> api, Class<?> asyncApi) {
         checkNotNull(api, "api");
         checkNotNull(asyncApi, "asyncApi");
         javaApi(api, asyncApi)
         .name(String.format("%s->%s", api.getSimpleName(), asyncApi.getSimpleName()))
         .context(contextToken(typeToken(api), typeToken(asyncApi)))
         .defaultProperties(BaseRestApiMetadata.defaultProperties());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public T javaApi(Class<?> api, Class<?> asyncApi) {
         this.api = checkNotNull(api, "api");
         this.asyncApi = checkNotNull(asyncApi, "asyncApi");
         return self();
      }

      @Override
      public T fromApiMetadata(ApiMetadata in) {
         if (in instanceof RestApiMetadata) {
            RestApiMetadata rest = RestApiMetadata.class.cast(in);
            javaApi(rest.getApi(), rest.getAsyncApi());
         }
         super.fromApiMetadata(in);
         return self();
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
