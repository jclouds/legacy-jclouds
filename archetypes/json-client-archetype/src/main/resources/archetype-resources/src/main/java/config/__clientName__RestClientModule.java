#set( $ucaseClientName = ${clientName.toUpperCase()} )
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
package ${package}.config;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.RequiresHttp;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientFactory;
import ${package}.${clientName};
import ${package}.${clientName}Client;
import ${package}.reference.${clientName}Constants;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the ${clientName} connection.
 * 
 * @author ${author}
 */
@RequiresHttp
@ConfiguresRestClient
public class ${clientName}RestClientModule extends AbstractModule {

   @Override
   protected void configure() {
      bindErrorHandlers();
      bindRetryHandlers();
   }

   @Provides
   @Singleton
   public BasicAuthentication provideBasicAuthentication(
            @Named(${clientName}Constants.PROPERTY_${ucaseClientName}_USER) String user,
            @Named(${clientName}Constants.PROPERTY_${ucaseClientName}_PASSWORD) String password)
            throws UnsupportedEncodingException {
      return new BasicAuthentication(user, password);
   }

   @Provides
   @Singleton
   protected ${clientName}Client provideClient(RestClientFactory factory) {
      return factory.create(${clientName}Client.class);
   }

   @Provides
   @Singleton
   @${clientName}
   protected URI provideURI(@Named(${clientName}Constants.PROPERTY_${ucaseClientName}_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   protected void bindErrorHandlers() {
      // TODO
   }

   protected void bindRetryHandlers() {
      // TODO
   }

}