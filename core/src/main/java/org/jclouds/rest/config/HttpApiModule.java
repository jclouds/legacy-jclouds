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

import static org.jclouds.reflect.Types2.checkBound;
import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

import org.jclouds.reflect.Invocation;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.HttpAsyncClient;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.internal.InvokeHttpMethod;

import com.google.common.base.Function;
import com.google.common.reflect.TypeToken;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresHttpApi
public class HttpApiModule<A> extends RestModule {
   protected final Class<A> api;

   /**
    * Note that this ctor requires that you instantiate w/resolved generic
    * params. For example, via a subclass of a bound type, or natural
    * instantiation w/resolved type params.
    */
   @SuppressWarnings("unchecked")
   protected HttpApiModule() {
      this.api = Class.class.cast(checkBound(new TypeToken<A>(getClass()) {
         private static final long serialVersionUID = 1L;
      }).getRawType());
   }

   public HttpApiModule(Class<A> api) {
     this.api = api;
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<Function<Invocation, Object>>() {
      }).to(InvokeHttpMethod.class);
      bindHttpApi(binder(), api);
      bindHttpApi(binder(), HttpClient.class);
      // TODO: remove when references are gone
      bindHttpApi(binder(), HttpAsyncClient.class);
      bindErrorHandlers();
      bindRetryHandlers();
   }

   /**
    * overrides this to change the default retry handlers for the http engine
    * 
    * ex.
    * 
    * <pre>
    * bind(HttpRetryHandler.class).annotatedWith(Redirection.class).to(AWSRedirectionRetryHandler.class);
    * bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(AWSClientErrorRetryHandler.class);
    * </pre>
    * 
    */
   protected void bindRetryHandlers() {
   }

   /**
    * overrides this to change the default error handlers for the http engine
    * 
    * ex.
    * 
    * <pre>
    * bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseAWSErrorFromXmlContent.class);
    * bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseAWSErrorFromXmlContent.class);
    * bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseAWSErrorFromXmlContent.class);
    * </pre>
    * 
    * 
    */
   protected void bindErrorHandlers() {

   }

}
