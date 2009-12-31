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
package org.jclouds.aws.s3;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jclouds.aws.s3.config.S3ContextModule;
import org.jclouds.aws.s3.config.S3RestClientModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rest.RestContextBuilder;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Creates {@link S3Context} or {@link Injector} instances based on the most commonly requested
 * arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see S3Context
 */
public class S3ContextBuilder extends RestContextBuilder<S3AsyncClient, S3Client> {

   public S3ContextBuilder(Properties props) {
      super(new TypeLiteral<S3AsyncClient>() {
      }, new TypeLiteral<S3Client>() {
      }, props);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new S3ContextModule());
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new S3RestClientModule());
   }

   @Override
   public S3ContextBuilder withExecutorService(ExecutorService service) {
      return (S3ContextBuilder) super.withExecutorService(service);
   }

   @Override
   public S3ContextBuilder withModules(Module... modules) {
      return (S3ContextBuilder) super.withModules(modules);
   }

}
