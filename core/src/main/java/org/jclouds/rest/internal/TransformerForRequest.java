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

import static com.google.common.base.Functions.compose;
import static com.google.inject.util.Types.newParameterizedType;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.net.URI;
import java.util.Set;

import javax.inject.Inject;
import javax.lang.model.type.NullType;

import org.jclouds.functions.IdentityFunction;
import org.jclouds.functions.OnlyElementOrNull;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.http.functions.ParseURIFromListOrLocationHeaderIf20x;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnInputStream;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.json.internal.GsonWrapper;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class TransformerForRequest implements Function<HttpRequest, Function<HttpResponse, ?>> {
   private final ParseSax.Factory parserFactory;
   private final Injector injector;
   private final GetAcceptHeaders getAcceptHeaders;

   @Inject
   TransformerForRequest(Injector injector, Factory parserFactory, GetAcceptHeaders getAcceptHeaders) {
      this.injector = injector;
      this.parserFactory = parserFactory;
      this.getAcceptHeaders = getAcceptHeaders;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Function<HttpResponse, ?> apply(HttpRequest in) {
      GeneratedHttpRequest request = GeneratedHttpRequest.class.cast(in);
      Function<HttpResponse, ?> transformer;
      Class<? extends HandlerWithResult<?>> handler = getSaxResponseParserClassOrNull(request.getInvocation()
            .getInvokable());
      if (handler != null) {
         transformer = parserFactory.create(injector.getInstance(handler));
      } else {
         transformer = getTransformerForMethod(request.getInvocation(), injector);
      }
      if (transformer instanceof InvocationContext<?>) {
         InvocationContext.class.cast(transformer).setContext(request);
      }
      if (request.getInvocation().getInvokable().isAnnotationPresent(Transform.class)) {
         Function<?, ?> wrappingTransformer = injector.getInstance(request.getInvocation().getInvokable()
               .getAnnotation(Transform.class).value());
         if (wrappingTransformer instanceof InvocationContext<?>) {
            ((InvocationContext<?>) wrappingTransformer).setContext(request);
         }
         transformer = compose(Function.class.cast(wrappingTransformer), transformer);
      }
      return transformer;
   }

   private static final TypeToken<ListenableFuture<Boolean>> futureBooleanToken = new TypeToken<ListenableFuture<Boolean>>() {
      private static final long serialVersionUID = 1L;
   };
   private static final TypeToken<ListenableFuture<String>> futureStringToken = new TypeToken<ListenableFuture<String>>() {
      private static final long serialVersionUID = 1L;
   };
   private static final TypeToken<ListenableFuture<Void>> futureVoidToken = new TypeToken<ListenableFuture<Void>>() {
      private static final long serialVersionUID = 1L;
   };
   private static final TypeToken<ListenableFuture<URI>> futureURIToken = new TypeToken<ListenableFuture<URI>>() {
      private static final long serialVersionUID = 1L;
   };
   private static final TypeToken<ListenableFuture<InputStream>> futureInputStreamToken = new TypeToken<ListenableFuture<InputStream>>() {
      private static final long serialVersionUID = 1L;
   };
   private static final TypeToken<ListenableFuture<HttpResponse>> futureHttpResponseToken = new TypeToken<ListenableFuture<HttpResponse>>() {
      private static final long serialVersionUID = 1L;
   };

   @SuppressWarnings("unchecked")
   @VisibleForTesting
   protected Key<? extends Function<HttpResponse, ?>> getParserOrThrowException(Invocation invocation) {
      Invokable<?, ?> invoked = invocation.getInvokable();
      Set<String> acceptHeaders = getAcceptHeaders.apply(invocation);
      ResponseParser annotation = invoked.getAnnotation(ResponseParser.class);
      Class<?> rawReturnType = invoked.getReturnType().getRawType();
      if (annotation == null) {
         if (rawReturnType.equals(void.class) || invoked.getReturnType().equals(futureVoidToken)) {
            return Key.get(ReleasePayloadAndReturn.class);
         } else if (rawReturnType.equals(boolean.class) || rawReturnType.equals(Boolean.class)
               || invoked.getReturnType().equals(futureBooleanToken)) {
            return Key.get(ReturnTrueIf2xx.class);
         } else if (rawReturnType.equals(InputStream.class)
               || invoked.getReturnType().equals(futureInputStreamToken)) {
            return Key.get(ReturnInputStream.class);
         } else if (rawReturnType.equals(HttpResponse.class)
               || invoked.getReturnType().equals(futureHttpResponseToken)) {
            return Key.get(Class.class.cast(IdentityFunction.class));
         } else if (acceptHeaders.contains(APPLICATION_JSON)) {
            return getJsonParserKeyForMethod(invoked);
         } else if (acceptHeaders.contains(APPLICATION_XML) || invoked.isAnnotationPresent(JAXBResponseParser.class)) {
            return getJAXBParserKeyForMethod(invoked);
         } else if (rawReturnType.equals(String.class) || invoked.getReturnType().equals(futureStringToken)) {
            return Key.get(ReturnStringIf2xx.class);
         } else if (rawReturnType.equals(URI.class) || invoked.getReturnType().equals(futureURIToken)) {
            return Key.get(ParseURIFromListOrLocationHeaderIf20x.class);
         } else {
            throw new IllegalStateException("You must specify a ResponseParser annotation on: " + invoked.toString());
         }
      }
      return Key.get(annotation.value());
   }

   @SuppressWarnings("unchecked")
   private static Key<? extends Function<HttpResponse, ?>> getJAXBParserKeyForMethod(Invokable<?, ?> invoked) {
      Optional<Type> configuredReturnVal = Optional.absent();
      if (invoked.isAnnotationPresent(JAXBResponseParser.class)) {
         Type configuredClass = invoked.getAnnotation(JAXBResponseParser.class).value();
         configuredReturnVal = configuredClass.equals(NullType.class) ? Optional.<Type> absent() : Optional
               .<Type> of(configuredClass);
      }
      Type returnVal = configuredReturnVal.or(getReturnTypeFor(invoked.getReturnType()));
      Type parserType = newParameterizedType(ParseXMLWithJAXB.class, returnVal);
      return (Key<? extends Function<HttpResponse, ?>>) Key.get(parserType);
   }

   @SuppressWarnings({ "unchecked" })
   private static Key<? extends Function<HttpResponse, ?>> getJsonParserKeyForMethod(Invokable<?, ?> invoked) {
      ParameterizedType parserType;
      if (invoked.isAnnotationPresent(Unwrap.class)) {
         parserType = newParameterizedType(UnwrapOnlyJsonValue.class, getReturnTypeFor(invoked.getReturnType()));
      } else {
         parserType = newParameterizedType(ParseJson.class, getReturnTypeFor(invoked.getReturnType()));
      }
      return (Key<? extends Function<HttpResponse, ?>>) Key.get(parserType);
   }

   static Type getReturnTypeFor(TypeToken<?> typeToken) {
      Type returnVal = typeToken.getType();
      if (typeToken.getRawType().getTypeParameters().length == 0) {
         returnVal = typeToken.getRawType();
      } else if (typeToken.getRawType().equals(ListenableFuture.class)) {
         ParameterizedType futureType = (ParameterizedType) typeToken.getType();
         returnVal = futureType.getActualTypeArguments()[0];
         if (returnVal instanceof WildcardType)
            returnVal = WildcardType.class.cast(returnVal).getUpperBounds()[0];
      }
      return returnVal;
   }

   // TODO: refactor this out of here
   @VisibleForTesting
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public Function<HttpResponse, ?> getTransformerForMethod(Invocation invocation, Injector injector) {
      Invokable<?, ?> invoked = invocation.getInvokable();
      Function<HttpResponse, ?> transformer;
      if (invoked.isAnnotationPresent(SelectJson.class)) {
         Type returnVal = getReturnTypeFor(invoked.getReturnType());
         if (invoked.isAnnotationPresent(OnlyElement.class))
            returnVal = newParameterizedType(Set.class, returnVal);
         transformer = new ParseFirstJsonValueNamed(injector.getInstance(GsonWrapper.class),
               TypeLiteral.get(returnVal), invoked.getAnnotation(SelectJson.class).value());
         if (invoked.isAnnotationPresent(OnlyElement.class))
            transformer = compose(new OnlyElementOrNull(), transformer);
      } else {
         transformer = injector.getInstance(getParserOrThrowException(invocation));
      }
      return transformer;
   }

   static Class<? extends HandlerWithResult<?>> getSaxResponseParserClassOrNull(Invokable<?, ?> invoked) {
      XMLResponseParser annotation = invoked.getAnnotation(XMLResponseParser.class);
      if (annotation != null) {
         return annotation.value();
      }
      return null;
   }
}
