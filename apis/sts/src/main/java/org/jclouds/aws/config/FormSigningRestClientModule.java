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

import java.util.Date;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RequestSigner;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public abstract class FormSigningRestClientModule<S, A> extends AWSRestClientModule<S, A> {

   protected FormSigningRestClientModule(Map<Class<?>, Class<?>> delegates) {
      super(delegates);
   }

   protected FormSigningRestClientModule() {
   }

   protected FormSigningRestClientModule(TypeToken<S> syncClientType, TypeToken<A> asyncClientType) {
      super(syncClientType, asyncClientType);
   }

   protected FormSigningRestClientModule(TypeToken<S> syncClientType, TypeToken<A> asyncClientType,
            Map<Class<?>, Class<?>> sync2Async) {
      super(syncClientType, asyncClientType, sync2Async);
   }
   
   @Provides
   @TimeStamp
   protected String provideTimeStamp(DateService dateService) {
      return dateService.iso8601DateFormat(new Date(System.currentTimeMillis()));
   }

   @Provides
   @Singleton
   RequestSigner provideRequestSigner(FormSigner in) {
      return in;
   }

}
