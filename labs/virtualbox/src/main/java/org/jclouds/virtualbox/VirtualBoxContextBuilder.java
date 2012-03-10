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

package org.jclouds.virtualbox;

import java.net.URI;
import java.util.List;
import java.util.Properties;

import org.jclouds.byon.Node;
import org.jclouds.byon.config.CacheNodeStoreModule;
import org.jclouds.compute.StandaloneComputeServiceContextBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.virtualbox.config.VirtualBoxComputeServiceContextModule;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

/**
 * Creates compute service context for VirtualBox
 * 
 * @author Mattias Holmqvist, Andrea Turli
 */
@SuppressWarnings("rawtypes")
public class VirtualBoxContextBuilder extends StandaloneComputeServiceContextBuilder<Supplier> {

   public VirtualBoxContextBuilder(Properties properties) {
      super(Supplier.class, properties);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new VirtualBoxComputeServiceContextModule());
      addHostModuleIfNotPresent(modules);
   }

   protected void addHostModuleIfNotPresent(List<Module> modules) {
      if (!Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input instanceof CacheNodeStoreModule;
         }
      })) {
         CacheNodeStoreModule hostModule = new CacheNodeStoreModule(ImmutableMap.of(
                  "host",
                  Node.builder().id("host").name("host installing virtualbox").hostname("localhost")
                           .osFamily(OsFamily.LINUX.toString()).osDescription(System.getProperty("os.name"))
                           .osVersion(System.getProperty("os.version")).group("ssh")
                           .username(System.getProperty("user.name"))
                           .credentialUrl(URI.create("file://" + System.getProperty("user.home") + "/.ssh/id_rsa"))
                           .build()));
         modules.add(hostModule);
      }
   }

}
