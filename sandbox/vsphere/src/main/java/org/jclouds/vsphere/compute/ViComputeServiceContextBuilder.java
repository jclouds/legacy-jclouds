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

package org.jclouds.vsphere.compute;

import static org.jclouds.vsphere.ViConstants.PROPERTY_LIBVIRT_DOMAIN_DIR;

import java.util.List;
import java.util.Properties;

import org.jclouds.compute.StandaloneComputeServiceContextBuilder;
import org.jclouds.compute.config.StandaloneComputeServiceContextModule;
import org.jclouds.vsphere.Datacenter;
import org.jclouds.vsphere.Image;
import org.jclouds.vsphere.compute.domain.ViComputeServiceContextModule;

import com.google.inject.Module;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * 
 * @author Adrian Cole
 */
public class ViComputeServiceContextBuilder extends StandaloneComputeServiceContextBuilder {

   public ViComputeServiceContextBuilder(Properties props) {
      super(props);
      
      if (!properties.containsKey(PROPERTY_LIBVIRT_DOMAIN_DIR))
          properties.setProperty(PROPERTY_LIBVIRT_DOMAIN_DIR, "/etc/libvirt/qemu");
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(createContextModule());
   }

   public  StandaloneComputeServiceContextModule<VirtualMachine, VirtualMachine, Image, Datacenter> createContextModule() {
      return new ViComputeServiceContextModule();
   }

}
