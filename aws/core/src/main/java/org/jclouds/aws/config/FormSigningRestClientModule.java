/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.config;

import java.util.Date;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RequestSigner;

import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
@RequiresHttp
public class FormSigningRestClientModule<S, A> extends AWSRestClientModule<S, A> {

   public FormSigningRestClientModule(Class<S> syncClientType, Class<A> asyncClientType,
         Map<Class<?>, Class<?>> delegates) {
      super(syncClientType, asyncClientType, delegates);
   }

   public FormSigningRestClientModule(Class<S> syncClientType, Class<A> asyncClientType) {
      super(syncClientType, asyncClientType);
   }

   @Provides
   @TimeStamp
   protected String provideTimeStamp(final DateService dateService,
            @Named(Constants.PROPERTY_SESSION_INTERVAL) final int expiration) {
      return dateService.iso8601DateFormat(new Date(System.currentTimeMillis() + (expiration * 1000)));
   }

   @Provides
   @Singleton
   RequestSigner provideRequestSigner(FormSigner in) {
      return in;
   }

}