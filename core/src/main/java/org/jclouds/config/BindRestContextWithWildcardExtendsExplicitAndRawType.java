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

import org.jclouds.rest.RestApiMetadata;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * Allows you to lookup the {@link RestApiMetadata#getContext()} as {@link RestContext}, {@code RestContext<Client, AsyncClient>}, and {@code
 *  
 * @author Adrian Cole
 * @deprecated please use {@link BindApiContextWithWildcardExtendsExplicitAndRawType} as
 *             async interface will be removed in jclouds 1.7.
 */
@Deprecated
public class BindRestContextWithWildcardExtendsExplicitAndRawType extends AbstractModule {
   private final RestApiMetadata restApiMetadata;

   public BindRestContextWithWildcardExtendsExplicitAndRawType(RestApiMetadata restApiMetadata)
            throws IllegalArgumentException {
      this.restApiMetadata = checkNotNull(restApiMetadata, "restApiMetadata");
      checkArgument(restApiMetadata.getContext().getRawType().equals(RestContext.class),
               "this does not work as %s raw type is not RestContext", restApiMetadata.getContext());
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      TypeToken<?> concreteType = BaseRestApiMetadata.contextToken(typeToken(restApiMetadata.getApi()),
            typeToken(restApiMetadata.getAsyncApi()));
      // bind explicit type
      bind(TypeLiteral.get(concreteType.getType())).to(
            TypeLiteral.class.cast(TypeLiteral.get(Types.newParameterizedType(RestContextImpl.class,
                  restApiMetadata.getApi(), restApiMetadata.getAsyncApi()))));
      // bind potentially wildcard type
      if (!concreteType.equals(restApiMetadata.getContext())) {
         bind(TypeLiteral.get(restApiMetadata.getContext().getType())).to(
               TypeLiteral.class.cast(TypeLiteral.get(Types.newParameterizedType(RestContextImpl.class,
                     restApiMetadata.getApi(), restApiMetadata.getAsyncApi()))));
      }
      // bind w/o types
      bind(TypeLiteral.get(RestContext.class)).to(
            TypeLiteral.class.cast(TypeLiteral.get(Types.newParameterizedType(RestContextImpl.class,
                  restApiMetadata.getApi(), restApiMetadata.getAsyncApi()))));
   }
}
