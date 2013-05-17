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
package org.jclouds.rest.config;

import static com.google.common.util.concurrent.Atomics.newReference;

import java.net.Proxy;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.internal.FilterStringsBoundToInjectorByName;
import org.jclouds.json.config.GsonModule;
import org.jclouds.location.config.LocationModule;
import org.jclouds.proxy.ProxyForURI;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.binders.BindToJsonPayloadWrappedWith;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.rest.internal.TransformerForRequest;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class RestModule extends AbstractModule {

   public static final TypeLiteral<Supplier<URI>> URI_SUPPLIER_TYPE = new TypeLiteral<Supplier<URI>>() {
   };

   protected final AtomicReference<AuthorizationException> authException = newReference();

   protected void installLocations() {
      install(new LocationModule());
   }

   @Override
   protected void configure() {
      install(new SaxParserModule());
      install(new GsonModule());
      install(new SetCaller.Module());
      install(new FactoryModuleBuilder().build(BindToJsonPayloadWrappedWith.Factory.class));
      bind(new TypeLiteral<Function<HttpRequest, Function<HttpResponse, ?>>>() {
      }).to(TransformerForRequest.class);
      bind(new TypeLiteral<org.jclouds.Fallback<Object>>() {
      }).to(MapHttp4xxCodesToExceptions.class);
      bind(new TypeLiteral<Function<Invocation, HttpRequest>>() {
      }).to(RestAnnotationProcessor.class);
      bind(IdentityFunction.class).toInstance(IdentityFunction.INSTANCE);
      // this will help short circuit scenarios that can otherwise lock out users
      bind(new TypeLiteral<AtomicReference<AuthorizationException>>() {
      }).toInstance(authException);
      bind(new TypeLiteral<Function<Predicate<String>, Map<String, String>>>() {
      }).to(FilterStringsBoundToInjectorByName.class);
      bind(new TypeLiteral<Function<URI, Proxy>>() {
      }).to(ProxyForURI.class);
      installLocations();
   }
}
