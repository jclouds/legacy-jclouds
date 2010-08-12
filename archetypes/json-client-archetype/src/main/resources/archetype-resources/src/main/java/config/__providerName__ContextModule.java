/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

#set( $ucaseProviderName = ${providerName.toUpperCase()} )
package ${package}.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import ${package}.${providerName};
import ${package}.${providerName}AsyncClient;
import ${package}.${providerName}Client;
import ${package}.reference.${providerName}Constants;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the ${providerName} connection, including logging and http transport.
 * 
 * @author ${author}
 */
public class ${providerName}ContextModule extends AbstractModule {
   
   public ${providerName}ContextModule(String providerName) {
      // providerName ignored right now
   }
   
   @Override
   protected void configure() {
      // example of how to customize bindings
      // bind(DateAdapter.class).to(CDateAdapter.class);
   }

   @Provides
   @Singleton
   RestContext<${providerName}Client, ${providerName}AsyncClient> provideContext(Closer closer, ${providerName}AsyncClient asyncApi,
            ${providerName}Client syncApi, @${providerName} URI endPoint, @Named(${providerName}Constants.PROPERTY_${ucaseProviderName}_USER) String identity) {
      return new RestContextImpl<${providerName}Client, ${providerName}AsyncClient>(closer, asyncApi, syncApi, endPoint, identity);
   }

}