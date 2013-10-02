/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.scriptbuilder.functionloader.osgi;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.jclouds.scriptbuilder.functionloader.FunctionLoader;
import org.jclouds.scriptbuilder.functionloader.FunctionNotFoundException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/**
 * A {@link FunctionLoader} which searches for functions in the {@link Bundle} resources.
 */
public class BundleFunctionLoader implements FunctionLoader {

   private final BundleContext bundleContext;
   private ServiceRegistration registration;

   /**
    * Constructor
    * 
    * @param bundleContext
    */
   public BundleFunctionLoader(BundleContext bundleContext) {
      this.bundleContext = bundleContext;
   }

   /**
    * Starts the loader. Looks up for {@link Bundle} resources and registers itself in the service
    * registry. It adds a property to the service which advertise all the functions found in the
    * local resources.
    */
   public void start() {
      Bundle bundle = bundleContext.getBundle();
      Enumeration<?> entries = bundle.findEntries("/functions/", "*.*", false);
      StringBuilder sb = new StringBuilder();
      while (entries.hasMoreElements()) {
         URL url = (URL) entries.nextElement();
         String function = url.getFile();
         sb.append(function);
         if (entries.hasMoreElements()) {
            sb.append(" ");
         }
      }
      String functions = sb.toString();
      registerFunction(functions);
   }

   /**
    * Unregisters itself from the service registry.
    */
   public void stop() {
      registration.unregister();
   }

   /**
    * Loads the function from the {@link Bundle} resources.
    * 
    * @param function
    *           The function name to load.
    * @param family
    *           This operating system family of the function.
    * @return
    * @throws FunctionNotFoundException
    */
   @Override
   public String loadFunction(String function, OsFamily family) throws FunctionNotFoundException {
      try {
         return Resources.toString(bundleContext.getBundle().getResource(
                  String.format("/functions/%s.%s", function, ShellToken.SH.to(family))), Charsets.UTF_8);
      } catch (IOException e) {
         throw new FunctionNotFoundException(function, family, e);
      }
   }

   private void registerFunction(String functions) {
      Properties properties = new Properties();
      properties.put("function", functions);
      registration = bundleContext.registerService(FunctionLoader.class.getName(), this, properties);
   }

}
