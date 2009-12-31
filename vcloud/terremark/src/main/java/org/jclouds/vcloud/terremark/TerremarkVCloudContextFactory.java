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
package org.jclouds.vcloud.terremark;

import java.util.Properties;

import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rest.RestContext;

import com.google.inject.Module;

/**
 * Creates {@link TerremarkVCloudContext} instances based on the most commonly requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole
 * @see TerremarkVCloudContext
 */
public class TerremarkVCloudContextFactory {

   public static RestContext<TerremarkVCloudAsyncClient, TerremarkVCloudClient> createContext(
            String username, String password, Module... modules) {
      return new TerremarkVCloudContextBuilder(new TerremarkVCloudPropertiesBuilder(username,
               password).build()).withModules(modules).buildContext();
   }

   public static RestContext<TerremarkVCloudAsyncClient, TerremarkVCloudClient> createContext(
            Properties props, Module... modules) {
      return new TerremarkVCloudContextBuilder(new TerremarkVCloudPropertiesBuilder(props).build())
               .withModules(modules).buildContext();
   }

}
