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
package org.jclouds.openstack.nova.live.compute;

import static org.jclouds.openstack.nova.live.PropertyHelper.setupOverrides;
import static org.jclouds.openstack.nova.live.PropertyHelper.setupProperties;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Not intended to be run with maven and does not performs a cleanup after tests
 *
 * @author Dmitri Babaev
 */
public class ComputeServiceCheck {
   private ComputeServiceContextFactory contextFactory;
   private ComputeServiceContext context;
   private String testImageId;

   @BeforeTest
   public void setupClient() throws IOException {
      contextFactory = new ComputeServiceContextFactory();
      Properties properties = setupOverrides(setupProperties(this.getClass()));
      context = contextFactory.createContext("nova",
            ImmutableSet.of(new SshjSshClientModule(), new SLF4JLoggingModule()), properties);
      testImageId = properties.getProperty("test.nova.image.id");
   }

   @Test
   public void testLists() {
      ComputeService cs = context.getComputeService();

      System.out.println(cs.listImages());
      System.out.println(cs.listHardwareProfiles());
      System.out.println(cs.listAssignableLocations());
      System.out.println(cs.listNodes());
   }

   @Test
   public void testCreateServer() throws RunNodesException {
      ComputeService cs = context.getComputeService();

      TemplateOptions options = new TemplateOptions().blockUntilRunning(false);
      Template template = cs.templateBuilder().imageId(testImageId).hardwareId("2").options(options).build();
      Set<? extends NodeMetadata> metedata = cs.createNodesInGroup("test", 1, template);
      System.out.println(metedata);
   }
   
   @Test(expectedExceptions = NoSuchElementException.class)
   public void testDefaultTempateDoesNotSpecifyTheOS() {
      ComputeService cs = context.getComputeService();
      Template template = cs.templateBuilder().build();
      System.out.println(template);
   }

   @AfterTest
   public void after() {
      context.close();
   }

   //curl -v -H "X-Auth-User:admin" -H "X-Auth-Key: d744752f-20d3-4d75-979f-f62f16033b07" http://dragon004.hw.griddynamics.net:8774/v1.0/
   //curl -v -H "X-Auth-Token: c97b10659008d5a9ce91462f8c6a5c2c80439762" http://dragon004.hw.griddynamics.net:8774/v1.0/images/detail?format=json

}
