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
package org.jclouds.twitter.config;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.twitter.Twitter;
import org.jclouds.twitter.TwitterAsyncClient;
import org.jclouds.twitter.TwitterClient;
import org.jclouds.twitter.reference.TwitterConstants;

import com.google.inject.Provides;

/**
 * Configures the Twitter connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class TwitterRestClientModule extends RestClientModule<TwitterClient, TwitterAsyncClient> {

   public TwitterRestClientModule() {
      super(TwitterClient.class, TwitterAsyncClient.class);
   }

   @Provides
   @Singleton
   public BasicAuthentication provideBasicAuthentication(
            @Named(TwitterConstants.PROPERTY_TWITTER_USER) String user,
            @Named(TwitterConstants.PROPERTY_TWITTER_PASSWORD) String password,
            EncryptionService encryptionService) throws UnsupportedEncodingException {
      return new BasicAuthentication(user, password, encryptionService);
   }

   @Provides
   @Singleton
   @Twitter
   protected URI provideURI(@Named(TwitterConstants.PROPERTY_TWITTER_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

}