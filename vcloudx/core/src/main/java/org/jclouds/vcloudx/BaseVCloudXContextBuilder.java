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

import java.net.URI;
import java.util.List;
import java.util.Properties;

import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.vcloudx.config.BaseRestVCloudXConnectionModule;
import org.jclouds.vcloudx.config.BaseVCloudXContextModule;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Creates {@link VCloudXContext} or {@link Injector} instances based on the most commonly
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
public class BaseVCloudXContextBuilder extends VCloudXContextBuilder<VCloudXConnection> {

   public BaseVCloudXContextBuilder(URI endpoint, String id, String secret) {
      super(new TypeLiteral<VCloudXConnection>(){}, endpoint, id, secret);

   }
   public BaseVCloudXContextBuilder(Properties props) {
      super(new TypeLiteral<VCloudXConnection>(){}, props);

   }

   @Override
   protected void addConnectionModule(List<Module> modules) {
      addAuthenticationModule(this);
      modules.add(new BaseRestVCloudXConnectionModule());
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new BaseVCloudXContextModule());
   }

}
