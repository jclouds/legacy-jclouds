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
package org.jclouds.compute.callables;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Supplier;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class InitScriptConfigurationForTasks {
   public static final String PROPERTY_INIT_SCRIPT_PATTERN = "jclouds.compute.init-script-pattern";

   public static InitScriptConfigurationForTasks create() {
      return new InitScriptConfigurationForTasks();
   }

   private String basedir = "/tmp";
   private String initScriptPattern = basedir + "/init-%s";
   private Supplier<String> suffixSupplier;

   protected InitScriptConfigurationForTasks() {
      appendCurrentTimeMillisToAnonymousTaskNames();
   }

   @Inject(optional = true)
   public InitScriptConfigurationForTasks initScriptPattern(
            @Named(PROPERTY_INIT_SCRIPT_PATTERN) String initScriptPattern) {
      this.initScriptPattern = checkNotNull(initScriptPattern, "initScriptPattern ex. /tmp/init-%s");
      this.basedir = new File(initScriptPattern).getParent();
      return this;
   }

   public InitScriptConfigurationForTasks appendCurrentTimeMillisToAnonymousTaskNames() {
      this.suffixSupplier = new Supplier<String>() {

         @Override
         public String get() {
            return System.currentTimeMillis() + "";
         }

         @Override
         public String toString() {
            return "currentTimeMillis()";
         }
      };
      return this;
   }

   public InitScriptConfigurationForTasks appendIncrementingNumberToAnonymousTaskNames() {
      this.suffixSupplier = new Supplier<String>() {
         private final AtomicInteger integer = new AtomicInteger();

         @Override
         public String get() {
            return integer.getAndIncrement() + "";
         }

         @Override
         public String toString() {
            return "incrementingNumber()";
         }
      };
      return this;
   }

   /**
    * Directory where the init script is stored. the runtime directory of the process will be in
    * this dir/taskName
    */
   public String getBasedir() {
      return basedir;
   }

   /**
    * 
    * @return the naming convention of init scripts. ex. {@code /tmp/init-%s}, noting logs are under
    *         the basedir/%s where %s is the taskName
    * @see InitScript#getHomeDir
    * @see InitScript#getLogDir
    */
   public String getInitScriptPattern() {
      return initScriptPattern;
   }

   /**
    * @return suffix where the taskName isn't set. by default this is
    *         {@link System#currentTimeMillis}
    * @see #appendCurrentTimeMillisToAnonymousTaskNames
    * @see #appendIncrementingNumberToAnonymousTaskNames
    */
   public Supplier<String> getAnonymousTaskSuffixSupplier() {
      return suffixSupplier;
   }
}
