/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.nirvanix.sdn.config;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.http.RequiresHttp;
import org.jclouds.nirvanix.sdn.SDNAuthAsyncClient;
import org.jclouds.nirvanix.sdn.SessionToken;
import org.jclouds.nirvanix.sdn.reference.SDNConstants;
import org.jclouds.rest.AsyncClientFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the SDN authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
public class SDNAuthRestClientModule extends AbstractModule {

   @Override
   protected void configure() {
   }

   @Provides
   @Singleton
   @Named(SDNConstants.PROPERTY_SDN_APPKEY)
   public String credentials1(@Named(Constants.PROPERTY_IDENTITY) String identity) {
      List<String> parts = Lists.newArrayList(Splitter.on('/').split(identity));
      if (parts.size() != 3) {
         throw new IllegalArgumentException("identity syntax is appkey/appname/username");
      }
      return parts.get(0);
   }

   @Provides
   @Singleton
   @Named(SDNConstants.PROPERTY_SDN_APPNAME)
   public String credentials2(@Named(Constants.PROPERTY_IDENTITY) String identity) {
      List<String> parts = Lists.newArrayList(Splitter.on('/').split(identity));
      if (parts.size() != 3) {
         throw new IllegalArgumentException("identity syntax is appkey/appname/username");
      }
      return parts.get(1);
   }

   @Provides
   @Singleton
   @Named(SDNConstants.PROPERTY_SDN_USERNAME)
   public String credentials3(@Named(Constants.PROPERTY_IDENTITY) String identity) {
      List<String> parts = Lists.newArrayList(Splitter.on('/').split(identity));
      if (parts.size() != 3) {
         throw new IllegalArgumentException("identity syntax is appkey/appname/username");
      }
      return parts.get(2);
   }

   @Provides
   @SessionToken
   protected String provideSessionToken(AsyncClientFactory factory,
            @Named(SDNConstants.PROPERTY_SDN_APPKEY) String appKey,
            @Named(SDNConstants.PROPERTY_SDN_USERNAME) String username,
            @Named(Constants.PROPERTY_CREDENTIAL) String password) throws InterruptedException,
            ExecutionException, TimeoutException {
      return factory.create(SDNAuthAsyncClient.class).authenticate(appKey, username, password).get(
               20, TimeUnit.SECONDS);
   }

}
