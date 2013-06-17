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
package org.jclouds.aws.config;


import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.aws.handlers.AWSClientErrorRetryHandler;
import org.jclouds.aws.handlers.AWSServerErrorRetryHandler;
import org.jclouds.aws.handlers.ParseAWSErrorFromXmlContent;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.HttpApiModule;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provides;


/**
 * 
 * @author Adrian Cole
 * 
 * @deprecated will be removed in jclouds 1.7; use {@link AWSHttpApiModule}
 */
@Deprecated
@ConfiguresRestClient
public abstract class AWSRestClientModule<S, A> extends RestClientModule<S, A> {

   protected AWSRestClientModule(Map<Class<?>, Class<?>> delegates) {
      super(delegates);
   }

   protected AWSRestClientModule() {
   }

   protected AWSRestClientModule(TypeToken<S> syncClientType, TypeToken<A> asyncClientType) {
      super(syncClientType, asyncClientType);
   }

   protected AWSRestClientModule(TypeToken<S> syncClientType, TypeToken<A> asyncClientType,
            Map<Class<?>, Class<?>> sync2Async) {
      super(syncClientType, asyncClientType, sync2Async);
   }
   
   @Provides
   @ClientError
   @Singleton
   protected Set<String> provideRetryableCodes(){
      return ImmutableSet.of("RequestTimeout", "OperationAborted", "SignatureDoesNotMatch");
   }
   
   @Provides
   @ServerError
   @Singleton
   protected Set<String> provideRetryableServerCodes(){
      return ImmutableSet.of("RequestLimitExceeded");
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseAWSErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseAWSErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseAWSErrorFromXmlContent.class);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(AWSClientErrorRetryHandler.class);
      bind(HttpRetryHandler.class).annotatedWith(ServerError.class).to(AWSServerErrorRetryHandler.class);
   }

}
