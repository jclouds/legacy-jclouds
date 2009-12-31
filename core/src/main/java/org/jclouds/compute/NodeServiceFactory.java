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
package org.jclouds.compute;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Properties;

import javax.inject.Inject;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpPropertiesBuilder;
import org.jclouds.rest.RestContextBuilder;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class NodeServiceFactory {
   private final Properties properties;

   @Inject
   public NodeServiceFactory(Properties properties) {
      this.properties = properties;
   }

   public NodeService create(URI provider, Module... modules) {
      return create(provider, Credentials.parse(provider), modules);
   }

   @SuppressWarnings("unchecked")
   public NodeService create(URI provider, Credentials creds, Module... modules) {
      String hint = checkNotNull(provider.getHost(), "host");
      String account = checkNotNull(creds.account, "account");
      String key = creds.key;
      String propertiesBuilderKey = String.format("%s.propertiesbuilder", hint);
      String propertiesBuilderClassName = checkNotNull(
               properties.getProperty(propertiesBuilderKey), propertiesBuilderKey);

      String contextBuilderKey = String.format("%s.contextbuilder", hint);
      String contextBuilderClassName = checkNotNull(properties.getProperty(contextBuilderKey),
               contextBuilderKey);

      try {
         Class<HttpPropertiesBuilder> propertiesBuilderClass = (Class<HttpPropertiesBuilder>) Class
                  .forName(propertiesBuilderClassName);
         Class<RestContextBuilder<?, ?>> contextBuilderClass = (Class<RestContextBuilder<?, ?>>) Class
                  .forName(contextBuilderClassName);

         HttpPropertiesBuilder builder = propertiesBuilderClass.getConstructor(String.class,
                  String.class).newInstance(account, key);
         return contextBuilderClass.getConstructor(Properties.class).newInstance(builder.build())
                  .withModules(modules).buildInjector().getInstance(NodeService.class);
      } catch (Exception e) {
         throw new RuntimeException("error instantiating " + contextBuilderClassName, e);
      }
   }
}
