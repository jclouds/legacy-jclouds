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

package org.jclouds.servermanager.compute;

import java.util.List;
import java.util.Properties;

import org.jclouds.compute.StandaloneComputeServiceContextBuilder;
import org.jclouds.compute.config.StandaloneComputeServiceContextModule;
import org.jclouds.servermanager.compute.strategy.ServerManagerAddNodeWithTagStrategy;
import org.jclouds.servermanager.compute.strategy.ServerManagerDestroyNodeStrategy;
import org.jclouds.servermanager.compute.strategy.ServerManagerGetAndListNodesStrategy;
import org.jclouds.servermanager.compute.strategy.ServerManagerRebootNodeStrategy;
import org.jclouds.servermanager.compute.suppliers.ServerManagerHardwareSupplier;
import org.jclouds.servermanager.compute.suppliers.ServerManagerImageSupplier;
import org.jclouds.servermanager.compute.suppliers.ServerManagerLocationSupplier;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class ServerManagerComputeServiceContextBuilder extends StandaloneComputeServiceContextBuilder {

   public ServerManagerComputeServiceContextBuilder(Properties props) {
      super(props);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(createContextModule());
   }

   public static StandaloneComputeServiceContextModule createContextModule() {
      return StandaloneComputeServiceContextModule.builder()
            .defineAddNodeWithTagStrategy(ServerManagerAddNodeWithTagStrategy.class)
            .defineDestroyNodeStrategy(ServerManagerDestroyNodeStrategy.class)
            .defineGetNodeMetadataStrategy(ServerManagerGetAndListNodesStrategy.class)
            .defineListNodesStrategy(ServerManagerGetAndListNodesStrategy.class)
            .defineRebootNodeStrategy(ServerManagerRebootNodeStrategy.class)
            .defineHardwareSupplier(ServerManagerHardwareSupplier.class)
            .defineLocationSupplier(ServerManagerLocationSupplier.class)
            .defineImageSupplier(ServerManagerImageSupplier.class).build();
   }

}
