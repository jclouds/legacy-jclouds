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

package org.jclouds.compute;

import java.util.concurrent.ConcurrentMap;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.stub.StubComputeServiceContextBuilder;
import org.jclouds.domain.Location;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "unit")
public class ComputeServiceContextFactoryTest {

   @Test
   public void testStandalone() {
      @SuppressWarnings("rawtypes")
      ComputeServiceContext context = new ComputeServiceContextFactory()
               .createContext(new StandaloneComputeServiceContextSpec<ConcurrentMap, NodeMetadata, Hardware, Image, Location>(
                        "stub", "stub", "1", "", "identity", "credential", ConcurrentMap.class,
                        StubComputeServiceContextBuilder.class, ImmutableSet.<Module> of()));

      context.getComputeService().listNodes();
   }
}
