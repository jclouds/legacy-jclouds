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
package org.jclouds.rimuhosting.miro;

import com.google.inject.Module;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rest.RestContext;

import java.util.Properties;

/**
 * Creates {@link RestContext} for {@link RimuHostingClient} instances based on the most commonly
 * requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 *
 * @author Adrian Cole
 * @see RestContext
 * @see RimuHostingClient
 * @see RimuHostingAsyncClient
 */
public class RimuHostingContextFactory {

   public static RestContext<RimuHostingAsyncClient, RimuHostingClient> createContext(String apikey,
                                                                                      Module... modules) {
      return new RimuHostingContextBuilder(new RimuHostingPropertiesBuilder(apikey).build())
              .withModules(modules).buildContext();
   }

   public static RestContext<RimuHostingAsyncClient, RimuHostingClient> createContext(Properties properties, Module... modules) {
      return new RimuHostingContextBuilder(new RimuHostingPropertiesBuilder(properties).build())
              .withModules(modules).buildContext();
   }

}
