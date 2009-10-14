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
package org.jclouds.vcloudx;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_ENDPOINT;
import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_KEY;
import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_SESSIONINTERVAL;
import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_USER;

import java.net.URI;
import java.util.Properties;

import org.jclouds.cloud.CloudContextBuilder;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.vcloudx.config.RestVCloudXAuthenticationModule;
import org.jclouds.vcloudx.reference.VCloudXConstants;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * Creates {@link VCloudXContext} or {@link Injector} instances based on the most commonly requested
 * arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole
 * @see CloudFilesContext
 */
public abstract class VCloudXContextBuilder<C extends VCloudXConnection> extends
         CloudContextBuilder<C> {

   public VCloudXContextBuilder(TypeLiteral<C> literal, URI endpoint, String id, String secret) {
      this(literal, addEndpointTo(endpoint, new Properties()));
      authenticate(this, id, secret);
      withTokenExpiration(9 * 60);
   }

   private static Properties addEndpointTo(URI endpoint, Properties properties) {
      properties.setProperty(VCloudXConstants.PROPERTY_VCLOUDX_ENDPOINT, checkNotNull(endpoint,
               "endpoint").toString());
      return properties;
   }

   public VCloudXContextBuilder(TypeLiteral<C> literal, Properties props) {
      super(literal, props);
      checkNotNull(properties.getProperty(VCloudXConstants.PROPERTY_VCLOUDX_ENDPOINT),
               VCloudXConstants.PROPERTY_VCLOUDX_ENDPOINT);
   }

   public static <C extends VCloudXConnection> void authenticate(VCloudXContextBuilder<C> builder,
            String id, String secret) {
      builder.getProperties().setProperty(PROPERTY_VCLOUDX_USER, checkNotNull(id, "user"));
      builder.getProperties().setProperty(PROPERTY_VCLOUDX_KEY, checkNotNull(secret, "key"));
   }

   public static <C extends VCloudXConnection> void addAuthenticationModule(
            VCloudXContextBuilder<C> builder) {
      builder.withModule(new RestVCloudXAuthenticationModule());
   }

   public static <C extends VCloudXConnection> VCloudXContextBuilder<C> withEndpoint(
            VCloudXContextBuilder<C> builder, URI endpoint) {
      builder.getProperties().setProperty(PROPERTY_VCLOUDX_ENDPOINT,
               checkNotNull(endpoint, "endpoint").toString());
      return builder;
   }

   public VCloudXContextBuilder<C> withTokenExpiration(long seconds) {
      getProperties().setProperty(PROPERTY_VCLOUDX_SESSIONINTERVAL, seconds + "");
      return this;
   }

   @Override
   public VCloudXContextBuilder<C> withEndpoint(URI endpoint) {
      return (VCloudXContextBuilder<C>) withEndpoint(this, endpoint);
   }
   
   @Override
   @SuppressWarnings("unchecked")
   public VCloudXContext<C> buildContext() {
      Injector injector = buildInjector();
      return (VCloudXContext<C>) injector.getInstance(Key.get(Types.newParameterizedType(
               VCloudXContext.class, connectionType.getType()))); 
   }

}
