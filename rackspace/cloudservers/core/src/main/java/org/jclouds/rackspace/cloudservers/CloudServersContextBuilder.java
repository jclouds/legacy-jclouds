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
package org.jclouds.rackspace.cloudservers;

import java.util.List;
import java.util.Properties;

import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rackspace.RackspaceContextBuilder;
import org.jclouds.rackspace.cloudservers.config.RestCloudServersConnectionModule;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Creates {@link CloudServersContext} or {@link Injector} instances based on the most commonly
 * requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole
 * @see CloudServersContext
 */
public class CloudServersContextBuilder extends RackspaceContextBuilder<CloudServersContext> {

   public CloudServersContextBuilder(Properties props) {
      super(props);
   }

   public static CloudServersContextBuilder newBuilder(String id, String secret) {
      Properties properties = new Properties();
      CloudServersContextBuilder builder = new CloudServersContextBuilder(properties);
      builder.authenticate(id, secret);
      return builder;
   }

   @Override
   public void addApiModule(List<Module> modules) {
      super.addApiModule(modules);
      modules.add(new RestCloudServersConnectionModule());
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      // TODO
   }

   @Override
   public CloudServersContext buildContext() {
      return buildInjector().getInstance(CloudServersContext.class);
   }

}
