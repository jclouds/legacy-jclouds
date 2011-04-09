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
package org.jclouds.vi.compute;

import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.vmware.vim25.mo.ServiceInstance;

/**
 * 
 * @author andrea.turli
 * 
 */
@Test(groups = "unit")
public class ViComputeServiceContextBuilderTest {

   @Test
   public void testCanBuildWithContextSpec() {
      ComputeServiceContext context = new ComputeServiceContextFactory().createContext(new ViComputeServiceContextSpec(
            "https://localhost/sdk", "Administrator", "password"));
      context.getComputeService().listNodes();

      context.close();
   }

   @Test
   public void testCanBuildWithRestProperties() {
      Properties restProperties = new Properties();
      restProperties.setProperty("vi.contextbuilder", ViComputeServiceContextBuilder.class.getName());
      restProperties.setProperty("vi.propertiesbuilder", ViPropertiesBuilder.class.getName());
      restProperties.setProperty("vi.endpoint",  "https://localhost/sdk");

      ComputeServiceContext context = new ComputeServiceContextFactory(restProperties).createContext("vi",
            "identity", "credential");
      context.close();
   }

   @Test
   public void testProviderSpecificContextIsCorrectType() {
      ComputeServiceContext context = new ViComputeServiceContextBuilder(new Properties()).buildComputeServiceContext();
      RestContext<ServiceInstance, ServiceInstance> providerContext = context.getProviderSpecificContext();

      assertEquals(providerContext.getApi().getClass(), ServiceInstance.class);

      context.close();
   }
}
