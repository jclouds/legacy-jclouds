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
package org.jclouds.openstack.swift.blobstore.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Properties;

import org.jclouds.blobstore.integration.internal.BaseContainerIntegrationTest;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.options.CreateContainerOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @author James Murty
 * @author Adrian Cole
 * @author Everett Toews
 */
@Test(groups = "live")
public class SwiftContainerIntegrationLiveTest extends BaseContainerIntegrationTest {
   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setIfTestSystemPropertyPresent(props, KeystoneProperties.CREDENTIAL_TYPE);
      return props;
   }
   
   public SwiftContainerIntegrationLiveTest() {
      provider = System.getProperty("test.swift.provider", "swift");
   }

   @Test(groups = "live")
   public void testSetGetContainerMetadata() throws InterruptedException {
      CommonSwiftClient swift = view.utils().injector().getInstance(CommonSwiftClient.class);

      String containerName = getContainerName();
      
      assertTrue(swift.createContainer(containerName));
      
      ImmutableMap<String, String> metadata = ImmutableMap.<String, String> of(
            "key1", "value1",
            "key2", "value2"); 

      assertTrue(swift.setContainerMetadata(containerName, metadata));

      ContainerMetadata containerMetadata = swift.getContainerMetadata(containerName);
      
      assertEquals(containerMetadata.getMetadata().get("key1"), "value1");
      assertEquals(containerMetadata.getMetadata().get("key2"), "value2");
   }

   @Test(groups = "live")
   public void testCreateDeleteContainerMetadata() throws InterruptedException {
      CommonSwiftClient swift = view.utils().injector().getInstance(CommonSwiftClient.class);

      String containerName = getContainerName();
      CreateContainerOptions options = CreateContainerOptions.Builder
         .withPublicAccess()
         .withMetadata(ImmutableMap.<String, String> of(
            "key1", "value1",
            "key2", "value2",
            "key3", "value3")); 

      assertTrue(swift.createContainer(containerName, options));
      
      ContainerMetadata containerMetadata = swift.getContainerMetadata(containerName);
      
      assertEquals(containerMetadata.getMetadata().size(), 3);
      assertEquals(containerMetadata.getMetadata().get("key1"), "value1");
      assertEquals(containerMetadata.getMetadata().get("key2"), "value2");
      assertEquals(containerMetadata.getMetadata().get("key3"), "value3");

      assertTrue(swift.deleteContainerMetadata(containerName, ImmutableList.<String> of("key2","key3")));

      containerMetadata = swift.getContainerMetadata(containerName);
      
      assertEquals(containerMetadata.getMetadata().size(), 1);
      assertEquals(containerMetadata.getMetadata().get("key1"), "value1");
   }
}
