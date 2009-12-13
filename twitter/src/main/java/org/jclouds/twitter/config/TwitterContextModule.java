/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.twitter.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.functions.config.ParserModule.CDateAdapter;
import org.jclouds.http.functions.config.ParserModule.DateAdapter;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.twitter.Twitter;
import org.jclouds.twitter.TwitterAsyncClient;
import org.jclouds.twitter.TwitterClient;
import org.jclouds.twitter.reference.TwitterConstants;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the Twitter connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
public class TwitterContextModule extends AbstractModule {
   @Override
   protected void configure() {
      bind(DateAdapter.class).to(CDateAdapter.class);
   }

   @Provides
   @Singleton
   RestContext<TwitterAsyncClient, TwitterClient> provideContext(Closer closer,
            TwitterAsyncClient asyncApi, TwitterClient syncApi, @Twitter URI endPoint,
            @Named(TwitterConstants.PROPERTY_TWITTER_USER) String account) {
      return new RestContextImpl<TwitterAsyncClient, TwitterClient>(closer, asyncApi, syncApi,
               endPoint, account);
   }

}