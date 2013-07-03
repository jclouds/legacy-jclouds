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

import org.jclouds.aws.config.FormSigningHttpApiModule;
import org.jclouds.aws.handlers.AWSServerErrorRetryHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.sqs.SQSApi;
import org.jclouds.sqs.features.MessageApi;
import org.jclouds.sqs.features.PermissionApi;
import org.jclouds.sqs.features.QueueApi;
import org.jclouds.sqs.handlers.ParseSQSErrorFromXmlContent;
import org.jclouds.sqs.handlers.SQSErrorRetryHandler;

import com.google.common.collect.ImmutableMap;

/**
 * Configures the SQS connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresHttpApi
public class SQSHttpApiModule extends FormSigningHttpApiModule<SQSApi> {

   public SQSHttpApiModule() {
      super(SQSApi.class);
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
