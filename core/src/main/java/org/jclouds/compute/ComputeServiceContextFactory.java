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

import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;

import org.jclouds.rest.RestContextFactory;

/**
 * Helper class to instantiate {@code ComputeServiceContext} instances.
 * 
 * @author Adrian Cole
 */
public class ComputeServiceContextFactory extends
         RestContextFactory<ComputeServiceContext<?, ?>, ComputeServiceContextBuilder<?, ?>> {

   /**
    * Initializes with the default properties built-in to jclouds. This is typically stored in the
    * classpath resource {@code compute.properties}
    * 
    * @throws IOException
    *            if the default properties file cannot be loaded
    * @see #init
    */
   public ComputeServiceContextFactory() throws IOException {
      super("compute.properties");
   }

   /**
    * 
    * Initializes the {@code ComputeServiceContext) definitions from the specified properties.
    */
   @Inject
   public ComputeServiceContextFactory(Properties properties) {
      super(properties);
   }
}