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

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.jclouds.scriptbuilder.functionloader.FunctionLoader;
import org.jclouds.scriptbuilder.functionloader.FunctionNotFoundException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.google.common.base.Strings;

/**
 * A {@link FunctionLoader} implementation which delegates loading to the OSGi service registry.
 */
public class ServiceFunctionLoader implements FunctionLoader {

   private final BundleContext bundleContext;

   public ServiceFunctionLoader(BundleContext bundleContext) {
      this.bundleContext = bundleContext;
   }

   /**
    * Looks up the service registry for an applicable {@link FunctionLoader} and delegates to it.
    * 
    * @param function
    *           The function name to load.
    * @param family
    *           This operating system family of the function.
    * @return The function as {@link String}
    * @throws FunctionNotFoundException
    */
   @Override
   public String loadFunction(String function, OsFamily family) throws FunctionNotFoundException {
      ServiceReference[] references = null;
      String filter = String.format("(function=*%s.%s*)", function, ShellToken.SH.to(family));
      try {
         references = bundleContext.getServiceReferences(FunctionLoader.class.getName(), filter);
        if (references != null) {
          for (ServiceReference reference : references) {
            FunctionLoader loader = (FunctionLoader) bundleContext.getService(reference);
            String f = loader.loadFunction(function, family);
            if (!Strings.isNullOrEmpty(f)) {
              return f;
            }
          }
        }
      } catch (InvalidSyntaxException e) {
         throw new FunctionNotFoundException(function, family, e);
      } finally {
         if (references != null) {
            for (ServiceReference reference : references) {
               bundleContext.ungetService(reference);
            }
         }
      }
      throw new FunctionNotFoundException(function, family);
   }

}
