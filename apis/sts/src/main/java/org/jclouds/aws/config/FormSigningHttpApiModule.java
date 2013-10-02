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

import javax.inject.Singleton;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.RequestSigner;

import com.google.inject.Provides;

/**
 * Configures signature process and dependencies needed for AWS Query apis
 * (which we sent as POST requests, hence the name Form).
 * 
 * @author Adrian Cole
 */
@ConfiguresHttpApi
public abstract class FormSigningHttpApiModule<A> extends AWSHttpApiModule<A> {
   protected FormSigningHttpApiModule() {

   }

   protected FormSigningHttpApiModule(Class<A> api) {
      super(api);
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
