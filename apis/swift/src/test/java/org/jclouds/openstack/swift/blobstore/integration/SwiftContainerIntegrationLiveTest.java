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
package org.jclouds.openstack.swift.blobstore.integration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Properties;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.integration.internal.BaseContainerIntegrationTest;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.options.CreateContainerOptions;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author James Murty
 * @author Adrian Cole
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
      BlobStore blobStore = view.getBlobStore();
      RestContext<CommonSwiftClient, CommonSwiftAsyncClient> swift = blobStore.getContext().unwrap();
      String containerName = getContainerName();
      
      assertTrue(swift.getApi().createContainer(containerName));
      
      CreateContainerOptions options = CreateContainerOptions.Builder
         .withPublicAccess()
         .withMetadata(ImmutableMap.<String, String> of(
            "key1", "value1",
            "key2", "value2")); 

      assertTrue(swift.getApi().setContainerMetadata(containerName, options));

      ContainerMetadata containerMetadata = swift.getApi().getContainerMetadata(containerName);
      
      assertEquals(containerMetadata.getMetadata().get("key1"), "value1");
      assertEquals(containerMetadata.getMetadata().get("key2"), "value2");
   }
}
