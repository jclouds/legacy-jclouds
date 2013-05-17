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
import static org.jclouds.reflect.Types2.checkBound;

import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.rest.ApiContext;
import org.jclouds.rest.HttpApiMetadata;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

/**
 * Useful in creating http apis.
 * 
 * @author Adrian Cole
 */
@Beta
public abstract class BaseHttpApiMetadata<A> extends BaseApiMetadata implements HttpApiMetadata<A> {

   protected final Class<A> api;

   protected BaseHttpApiMetadata(Builder<A, ?> builder) {
      super(builder);
      this.api = checkNotNull(builder.api, "api");
   }

   public static Properties defaultProperties() {
      Properties props = BaseApiMetadata.defaultProperties();
      return props;
   }

   public static <S, A> TypeToken<ApiContext<A>> contextToken(TypeToken<A> apiToken) {
      return new TypeToken<ApiContext<A>>() {
         private static final long serialVersionUID = 1L;
      }.where(new TypeParameter<A>() {
      }, apiToken);
   }

   public abstract static class Builder<A, T extends Builder<A, T>> extends BaseApiMetadata.Builder<T> implements
         HttpApiMetadata.Builder<A, T> {
      protected Class<A> api;

      /**
       * Note that this ctor requires that you instantiate w/resolved generic
       * params. For example, via a subclass of a bound type, or natural
       * instantiation w/resolved type params.
       */
      @SuppressWarnings("unchecked")
      protected Builder() {
         this.api = Class.class.cast(checkBound(new TypeToken<A>(getClass()) {
            private static final long serialVersionUID = 1L;
         }).getRawType());
         init();
      }

      protected Builder(Class<A> api) {
         this.api = checkNotNull(api, "api");
         init();
      }

      private void init() {
         api(api)
         .name(api.getSimpleName())
         .context(contextToken(typeToken(api)))
         .defaultProperties(BaseHttpApiMetadata.defaultProperties());
      }

      @Override
      public T api(Class<A> api) {
         this.api = checkNotNull(api, "api");
         return self();
      }

      @SuppressWarnings("unchecked")
      @Override
      public T fromApiMetadata(ApiMetadata in) {
         if (in instanceof HttpApiMetadata) {
            HttpApiMetadata<?> http = HttpApiMetadata.class.cast(in);
            api(Class.class.cast(http.getApi()));
         }
         super.fromApiMetadata(in);
         return self();
      }

   }

   @Override
   public Class<A> getApi() {
      return api;
   }

   @Override
   protected ToStringHelper string() {
      return super.string().add("api", getApi());
   }
}
