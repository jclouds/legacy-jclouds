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
package org.jclouds.scriptbuilder.functionloader;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.getResource;
import static java.lang.String.format;
import static org.jclouds.scriptbuilder.domain.ShellToken.SH;

import java.io.IOException;

import org.jclouds.scriptbuilder.domain.OsFamily;

import com.google.common.io.Resources;

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
         return Resources.toString(
               getResource(BasicFunctionLoader.class, format("/functions/%s.%s", function, SH.to(family))), UTF_8);
      } catch (IOException e) {
         throw new FunctionNotFoundException(function, family, e);
      }
   }
}
