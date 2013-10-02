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
package org.jclouds.config;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.reflect.Reflection2.typeToken;

import org.jclouds.rest.ApiContext;
import org.jclouds.rest.HttpApiMetadata;
import org.jclouds.rest.internal.ApiContextImpl;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * Allows you to lookup the {@link HttpApiMetadata#getContext()} as
 * {@link ApiContext}, {@code ApiContext<Api>}, and {@code ApiContext<?>}.
 * 
 * @author Adrian Cole
 */
public class BindApiContextWithWildcardExtendsExplicitAndRawType extends AbstractModule {
   private final HttpApiMetadata<?> httpApiMetadata;

   public BindApiContextWithWildcardExtendsExplicitAndRawType(HttpApiMetadata<?> httpApiMetadata)
         throws IllegalArgumentException {
      this.httpApiMetadata = checkNotNull(httpApiMetadata, "httpApiMetadata");
      checkArgument(httpApiMetadata.getContext().getRawType().equals(ApiContext.class),
            "this does not work as %s raw type is not ApiContext", httpApiMetadata.getContext());
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      TypeToken<?> concreteType = BaseHttpApiMetadata.contextToken(typeToken(httpApiMetadata.getApi()));
      // bind explicit type
      bind(TypeLiteral.get(concreteType.getType())).to(
            TypeLiteral.class.cast(TypeLiteral.get(Types.newParameterizedType(ApiContextImpl.class,
                  httpApiMetadata.getApi()))));
      // bind potentially wildcard type
      if (!concreteType.equals(httpApiMetadata.getContext())) {
         bind(TypeLiteral.get(httpApiMetadata.getContext().getType())).to(
               TypeLiteral.class.cast(TypeLiteral.get(Types.newParameterizedType(ApiContextImpl.class,
                     httpApiMetadata.getApi()))));
      }
      // bind w/o types
      bind(TypeLiteral.get(ApiContext.class)).to(
            TypeLiteral.class.cast(TypeLiteral.get(Types.newParameterizedType(ApiContextImpl.class,
                  httpApiMetadata.getApi()))));
   }
}
