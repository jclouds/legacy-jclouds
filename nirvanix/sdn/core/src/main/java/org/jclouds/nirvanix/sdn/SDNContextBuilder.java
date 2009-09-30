/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.nirvanix.sdn;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;
import java.util.Properties;

import org.jclouds.cloud.CloudContextBuilder;
import org.jclouds.nirvanix.sdn.config.RestSDNAuthenticationModule;
import org.jclouds.nirvanix.sdn.reference.SDNConstants;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public abstract class SDNContextBuilder<C> extends CloudContextBuilder<C> {

   public SDNContextBuilder(TypeLiteral<C> defaultApiClass, String apikey, String id, String secret) {
      this(defaultApiClass, new Properties());
      authenticate(this, apikey, id, secret);
   }

   public SDNContextBuilder(TypeLiteral<C> defaultApiClass, Properties props) {
      super(defaultApiClass, props);
      initialize(this);
   }

   @Override
   protected void addConnectionModule(List<Module> modules) {
      addAuthenticationModule(this);
   }

   public static <C> void authenticate(CloudContextBuilder<C> builder, String appkey, String id,
            String secret) {
      builder.getProperties().setProperty(SDNConstants.PROPERTY_SDN_APPKEY, checkNotNull(appkey, "appkey"));
      builder.getProperties().setProperty(SDNConstants.PROPERTY_SDN_USERNAME, checkNotNull(id, "user"));
      builder.getProperties().setProperty(SDNConstants.PROPERTY_SDN_PASSWORD, checkNotNull(secret, "key"));
   }

   public static <C> void initialize(CloudContextBuilder<C> builder) {
      builder.getProperties().setProperty(SDNConstants.PROPERTY_SDN_ENDPOINT, "http://services.nirvanix.com/ws");
   }

   public static <C> void addAuthenticationModule(CloudContextBuilder<C> builder) {
      builder.withModule(new RestSDNAuthenticationModule());
   }

   public static <C> CloudContextBuilder<C> withEndpoint(CloudContextBuilder<C> builder,
            URI endpoint) {
      builder.getProperties().setProperty(SDNConstants.PROPERTY_SDN_ENDPOINT,
               checkNotNull(endpoint, "endpoint").toString());
      return builder;
   }

   @Override
   public SDNContextBuilder<C> withEndpoint(URI endpoint) {
      return (SDNContextBuilder<C>) withEndpoint(this, endpoint);
   }

}
