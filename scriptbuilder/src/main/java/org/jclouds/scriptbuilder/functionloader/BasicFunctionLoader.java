/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.scriptbuilder.functionloader;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.jclouds.util.ClassLoadingUtils;

import java.io.IOException;

/**
 * A {@link FunctionLoader} implementation which loads the target function from the classpath.
 */
public enum BasicFunctionLoader implements FunctionLoader {
   INSTANCE;

   /**
    * Loads a function from the classpath using the current or the Thread Context Class Loader.
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
      try {
         return Resources.toString(ClassLoadingUtils.loadResource(
                  BasicFunctionLoader.class, String.format("/functions/%s.%s", function, ShellToken.SH.to(family))),
                  Charsets.UTF_8);
      } catch (IOException e) {
         throw new FunctionNotFoundException(function, family, e);
      }
   }
}
