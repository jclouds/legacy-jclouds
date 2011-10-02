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
package org.jclouds.softlayer.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.google.common.collect.Iterables;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

import java.util.Set;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "SoftLayerExperimentLiveTest")
public class SoftLayerExperimentLiveTest {
   protected String provider = "softlayer";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   @BeforeClass
   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   @Test
   public void testAndExperiment() {
      ComputeServiceContext context = null;
      try {
         String identity = checkNotNull(System.getProperty("test.softlayer.identity"), "test.softlayer.identity");
         String credential = checkNotNull(System.getProperty("test.softlayer.credential"), "test.softlayer.credential");

         context = new ComputeServiceContextFactory().createContext("softlayer", identity, credential, ImmutableSet
                  .<Module> of(new Log4JLoggingModule(), new SshjSshClientModule()));

         assertEquals(context.getComputeService().listAssignableLocations().size(), 6);

      } finally {
         if (context != null)
            context.close();
      }
   }

   @Test
   public void testCreateAndDestoryNode() throws RunNodesException {
      ComputeServiceContext context = null;
      try {
         String identity = checkNotNull(System.getProperty("test.softlayer.identity"), "test.softlayer.identity");
         String credential = checkNotNull(System.getProperty("test.softlayer.credential"), "test.softlayer.credential");

         context = new ComputeServiceContextFactory().createContext("softlayer", identity, credential, ImmutableSet
                  .<Module> of(new Log4JLoggingModule(), new SshjSshClientModule()));

         Template template = context.getComputeService().templateBuilder()
            .locationId("3") // the default (singapore) doesn't work.
            .build();

         Set<? extends NodeMetadata> nodes = context.getComputeService().createNodesInGroup("computeservice",1,template);
         assertTrue(nodes.size() == 1);
         NodeMetadata data = Iterables.get(nodes,0);
         context.getComputeService().destroyNode(data.getId());

      } finally {
         if (context != null)
            context.close();
      }
   }

}