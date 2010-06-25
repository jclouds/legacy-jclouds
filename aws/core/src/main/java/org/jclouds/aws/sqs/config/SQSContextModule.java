/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.sqs.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.reference.AWSConstants;
import org.jclouds.aws.sqs.SQS;
import org.jclouds.aws.sqs.SQSAsyncClient;
import org.jclouds.aws.sqs.SQSClient;
import org.jclouds.http.functions.config.ParserModule.CDateAdapter;
import org.jclouds.http.functions.config.ParserModule.DateAdapter;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.HttpAsyncClient;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the SQS connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
public class SQSContextModule extends AbstractModule {
   public SQSContextModule(String providerName) {
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(CDateAdapter.class);
   }

   @Provides
   @Singleton
   RestContext<SQSClient, SQSAsyncClient> provideContext(Closer closer,
         HttpClient http, HttpAsyncClient asyncHttp, SQSAsyncClient defaultApi,
         SQSClient synchApi, @SQS URI endPoint,
         @Named(AWSConstants.PROPERTY_AWS_ACCESSKEYID) String account) {
      return new RestContextImpl<SQSClient, SQSAsyncClient>(closer, http,
            asyncHttp, synchApi, defaultApi, endPoint, account);
   }

}