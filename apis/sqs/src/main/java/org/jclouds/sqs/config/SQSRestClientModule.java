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
package org.jclouds.sqs.config;


import static org.jclouds.reflect.Reflection2.typeToken;

import java.util.Map;

import org.jclouds.aws.config.FormSigningRestClientModule;
import org.jclouds.aws.handlers.AWSServerErrorRetryHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.sqs.SQSApi;
import org.jclouds.sqs.SQSAsyncApi;
import org.jclouds.sqs.features.MessageApi;
import org.jclouds.sqs.features.MessageAsyncApi;
import org.jclouds.sqs.features.PermissionApi;
import org.jclouds.sqs.features.PermissionAsyncApi;
import org.jclouds.sqs.features.QueueApi;
import org.jclouds.sqs.features.QueueAsyncApi;
import org.jclouds.sqs.handlers.ParseSQSErrorFromXmlContent;
import org.jclouds.sqs.handlers.SQSErrorRetryHandler;

import com.google.common.collect.ImmutableMap;

/**
 * Configures the SQS connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class SQSRestClientModule extends FormSigningRestClientModule<SQSApi, SQSAsyncApi> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
         .put(QueueApi.class, QueueAsyncApi.class)
         .put(MessageApi.class, MessageAsyncApi.class)
         .put(PermissionApi.class, PermissionAsyncApi.class)
         .build();

   public SQSRestClientModule() {
      super(typeToken(SQSApi.class), typeToken(SQSAsyncApi.class), DELEGATE_MAP);
   }
   
   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseSQSErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseSQSErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseSQSErrorFromXmlContent.class);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(SQSErrorRetryHandler.class);
      bind(HttpRetryHandler.class).annotatedWith(ServerError.class).to(AWSServerErrorRetryHandler.class);
   }

}
