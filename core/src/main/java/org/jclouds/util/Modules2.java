/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.util;

import static com.google.common.base.Splitter.on;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;

import java.util.Properties;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;



/**
 * 
 * 
 * @author Adrian Cole
 */
public class Modules2 {

   public static Iterable<Module> modulesFromCommaDelimitedString(String moduleClasses) {
      Iterable<Module> modules = ImmutableSet.of();
      if (moduleClasses != null) {
         Iterable<String> transformer = ImmutableList.copyOf(on(',').split(moduleClasses));
         modules = transform(transformer, new Function<String, Module>() {
   
            @Override
            public Module apply(String from) {
               try {
                  return (Module) Class.forName(from).newInstance();
               } catch (InstantiationException e) {
                  throw new RuntimeException("error instantiating " + from, e);
               } catch (IllegalAccessException e) {
                  throw new RuntimeException("error instantiating " + from, e);
               } catch (ClassNotFoundException e) {
                  throw new RuntimeException("error instantiating " + from, e);
               }
            }
   
         });
      }
      return modules;
   }

   public static Iterable<Module> modulesForProviderInProperties(String providerName, Properties props) {
      return concat(modulesFromProperty(props, "jclouds.modules"),
            modulesFromProperty(props, providerName + ".modules"));
   }

   public static Iterable<Module> modulesFromProperty(Properties props, String property) {
      return modulesFromCommaDelimitedString(props.getProperty(property, null));
   }

}
