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
package org.jclouds.config;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
 */
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
      TypeToken<?> concreteType = BaseRestApiMetadata.contextToken(TypeToken.of(restApiMetadata.getApi()), TypeToken
               .of(restApiMetadata.getAsyncApi()));
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
