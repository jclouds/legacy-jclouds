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

package org.jclouds.aws.ec2;

import java.io.Closeable;
import java.util.Properties;

import javax.inject.Singleton;

import org.jclouds.aws.elb.ELBAsyncClient;
import org.jclouds.aws.elb.ELBClient;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
public class ConfigureELBModule extends AbstractModule {
   private final Iterable<Module> infra;
   private final Properties properties;

   ConfigureELBModule(Iterable<Module> infra, Properties properties) {
      this.infra = infra;
      this.properties = properties;
   }

   @Override
   protected void configure() {

   }

   /**
    * setup ELB with the same parameters as EC2
    */
   @Provides
   @Singleton
   ELBClient provideELBClient(Closer closer) {
      final RestContext<ELBClient, ELBAsyncClient> context = new RestContextFactory()
               .createContext("elb", infra, properties);
      closer.addToClose(new Closeable() {

         @Override
         public void close() {
            if (context != null)
               context.close();
         }

      });
      return context.getApi();
   }
}