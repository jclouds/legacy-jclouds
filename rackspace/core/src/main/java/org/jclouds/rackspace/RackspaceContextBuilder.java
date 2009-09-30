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
package org.jclouds.rackspace;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_ENDPOINT;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_KEY;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_USER;

import java.net.URI;
import java.util.List;
import java.util.Properties;

import org.jclouds.cloud.CloudContextBuilder;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rackspace.config.RestRackspaceAuthenticationModule;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Creates {@link RackspaceContext} or {@link Injector} instances based on the most commonly
 * requested arguments.
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
public abstract class RackspaceContextBuilder<C> extends CloudContextBuilder<C> {

   public RackspaceContextBuilder(TypeLiteral<C> defaultApiClass, String id, String secret) {
      this(defaultApiClass, new Properties());
      authenticate(this, id, secret);
   }

   public RackspaceContextBuilder(TypeLiteral<C> defaultApiClass, Properties props) {
      super(defaultApiClass, props);
      initialize(this);
   }

   @Override
   protected void addConnectionModule(List<Module> modules) {
      addAuthenticationModule(this);
   }

   public static <C> void authenticate(CloudContextBuilder<C> builder, String id, String secret) {
      builder.getProperties().setProperty(PROPERTY_RACKSPACE_USER, checkNotNull(id, "user"));
      builder.getProperties().setProperty(PROPERTY_RACKSPACE_KEY, checkNotNull(secret, "key"));
   }

   public static <C> void initialize(CloudContextBuilder<C> builder) {
      builder.getProperties().setProperty(PROPERTY_RACKSPACE_ENDPOINT, "https://api.mosso.com");
   }

   public static <C> void addAuthenticationModule(CloudContextBuilder<C> builder) {
      builder.withModule(new RestRackspaceAuthenticationModule());
   }

   public static <C> CloudContextBuilder<C> withEndpoint(CloudContextBuilder<C> builder,
            URI endpoint) {
      builder.getProperties().setProperty(PROPERTY_RACKSPACE_ENDPOINT,
               checkNotNull(endpoint, "endpoint").toString());
      return builder;
   }

   @Override
   public RackspaceContextBuilder<C> withEndpoint(URI endpoint) {
      return (RackspaceContextBuilder<C>) withEndpoint(this, endpoint);
   }

}
