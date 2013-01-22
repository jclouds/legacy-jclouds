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
package org.jclouds.snia.cdmi.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.common.collect.Maps;

import org.jclouds.domain.JsonBall;
import org.jclouds.snia.cdmi.v1.ObjectTypes;
import org.jclouds.snia.cdmi.v1.domain.Container;
import org.jclouds.snia.cdmi.v1.internal.BaseCDMIApiLiveTest;
import org.jclouds.snia.cdmi.v1.options.CreateContainerOptions;
import org.jclouds.snia.cdmi.v1.queryparams.ContainerQueryParams;
import org.testng.annotations.Test;

/**
 * 
 * @author Kenneth Nagin
 */

@Test(groups = "live", testName = "ContainerApiLiveTest")
public class ContainerApiLiveTest extends BaseCDMIApiLiveTest {

   @Test
   public void testCreateContainer() throws Exception {
      String pContainerName = "MyContainer" + System.currentTimeMillis() + "/";
      Map<String, String> pContainerMetaDataIn = Maps.newHashMap();
      pContainerMetaDataIn.put("containerkey1", "value1");
      pContainerMetaDataIn.put("containerkey2", "value2");
      pContainerMetaDataIn.put("containerkey3", "value3");

      CreateContainerOptions pCreateContainerOptions = CreateContainerOptions.Builder.metadata(pContainerMetaDataIn);
      ContainerApi api = cdmiContext.getApi().getApi();

      Logger.getAnonymousLogger().info("create: " + pContainerName);

      Container container = api.create(pContainerName, pCreateContainerOptions);
      assertNotNull(container);
      try {
         System.out.println(container);
         Logger.getAnonymousLogger().info("get: " + pContainerName);
         container = api.get(pContainerName);
         assertNotNull(container);
         System.out.println(container);
         assertEquals(container.getObjectType(), ObjectTypes.CONTAINER);
         assertNotNull(container.getObjectID());
         assertNotNull(container.getObjectName());
         assertEquals(container.getObjectName(), pContainerName);
         assertEquals(container.getParentURI(), "/");
         assertNotNull(container.getChildren());
         assertEquals(container.getChildren().isEmpty(), true);
         System.out.println("Children: " + container.getChildren());
         assertNotNull(container.getMetadata());
         System.out.println("Raw metadata: " + container.getMetadata());
         for (Map.Entry<String, JsonBall> entry : container.getMetadata().entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
         }
         assertNotNull(container.getUserMetadata());
         Map<String, String> pContainerMetaDataOut = container.getUserMetadata();
         for (Map.Entry<String, String> entry : pContainerMetaDataIn.entrySet()) {
            String key = entry.getKey();
            assertEquals(pContainerMetaDataOut.containsKey(key), true);
            assertEquals(pContainerMetaDataOut.get(key), entry.getValue());
         }
         System.out.println("UserMetaData: " + container.getUserMetadata());
         assertNotNull(container.getSystemMetadata());
         System.out.println("SystemMetaData: " + container.getSystemMetadata());
         assertNotNull(container.getACLMetadata());
         assertEquals(container.getACLMetadata().size(), 3);
         List<Map<String, String>> aclMetadataOut = container.getACLMetadata();
         System.out.println("ACLMetaData: ");
         for (Map<String, String> aclMap : aclMetadataOut) {
            System.out.println(aclMap);
         }
         container = api.get("/");
         System.out.println("root container: " + container);
         assertEquals(container.getChildren().contains(pContainerName), true);
         System.out.println("adding containers to container");
         String firstParentURI = api.get(pContainerName).getObjectName();
         for (int i = 0; i < 10; i++) {
            // container = api.create(firstParentURI+"childcontainer"+i+"/");
            container = api.create(pContainerName + "childcontainer" + i + "/");
            assertNotNull(container);
            System.out.println(container);
            assertEquals(container.getParentURI(), pContainerName);
            assertEquals(container.getObjectName(), "childcontainer" + i + "/");
            container = api.create(container.getParentURI() + container.getObjectName() + "grandchild/");
            assertEquals(container.getParentURI(), pContainerName + "childcontainer" + i + "/");
            assertEquals(container.getObjectName(), "grandchild/");
            System.out.println(container);
         }
         container = api.get(pContainerName);
         assertNotNull(container);
         assertNotNull(container.getChildren());
         assertEquals(container.getChildren().size(), 10);

      } finally {
         Logger.getAnonymousLogger().info("delete: " + pContainerName);
         api.delete(pContainerName);
         container = api.get("/");
         System.out.println("root container: " + container);
         assertEquals(container.getChildren().contains(pContainerName), false);
      }

   }

   @Test
   public void testGetContainer() throws Exception {
      String pContainerName = "MyContainer" + System.currentTimeMillis() + "/";
      Map<String, String> pContainerMetaDataIn = Maps.newHashMap();
      pContainerMetaDataIn.put("containerkey1", "value1");
      pContainerMetaDataIn.put("containerkey2", "value2");
      pContainerMetaDataIn.put("containerkey3", "value3");
      CreateContainerOptions pCreateContainerOptions = CreateContainerOptions.Builder.metadata(pContainerMetaDataIn);
      ContainerApi api = cdmiContext.getApi().getApi();

      Logger.getAnonymousLogger().info("create: " + pContainerName);

      Container container = api.create(pContainerName, pCreateContainerOptions);
      assertNotNull(container);
      try {
         System.out.println(container);
         Logger.getAnonymousLogger().info("get: " + pContainerName);
         container = api.get(pContainerName);
         assertNotNull(container);
         System.out.println(container);
         assertEquals(container.getObjectType(), ObjectTypes.CONTAINER);
         assertNotNull(container.getObjectID());
         assertNotNull(container.getObjectName());
         assertEquals(container.getObjectName(), pContainerName);
         assertEquals(container.getParentURI(), "/");
         assertNotNull(container.getChildren());
         assertEquals(container.getChildren().isEmpty(), true);
         System.out.println("Children: " + container.getChildren());
         assertNotNull(container.getMetadata());
         System.out.println("Raw metadata: " + container.getMetadata());
         for (Map.Entry<String, JsonBall> entry : container.getMetadata().entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
         }
         assertNotNull(container.getUserMetadata());
         Map<String, String> pContainerMetaDataOut = container.getUserMetadata();
         for (Map.Entry<String, String> entry : pContainerMetaDataIn.entrySet()) {
            String key = entry.getKey();
            assertEquals(pContainerMetaDataOut.containsKey(key), true);
            assertEquals(pContainerMetaDataOut.get(key), entry.getValue());
         }
         System.out.println("UserMetaData: " + container.getUserMetadata());
         assertNotNull(container.getSystemMetadata());
         System.out.println("SystemMetaData: " + container.getSystemMetadata());
         assertNotNull(container.getACLMetadata());
         List<Map<String, String>> aclMetadataOut = container.getACLMetadata();
         System.out.println("ACLMetaData: ");
         for (Map<String, String> aclMap : aclMetadataOut) {
            System.out.println(aclMap);
         }
         container = api.get("/");
         System.out.println("root container: " + container);
         assertEquals(container.getChildren().contains(pContainerName), true);
         container = api.get(pContainerName, ContainerQueryParams.Builder.field("parentURI"));
         assertNotNull(container);
         assertEquals(container.getParentURI(), "/");
         System.out.println(container);

         container = api.get(pContainerName, ContainerQueryParams.Builder.field("parentURI").field("objectName"));
         assertNotNull(container);
         assertEquals(container.getParentURI(), "/");
         assertEquals(container.getObjectName(), pContainerName);

         container = api.get(pContainerName, ContainerQueryParams.Builder.metadata());
         assertNotNull(container);
         pContainerMetaDataOut = container.getUserMetadata();
         for (Map.Entry<String, String> entry : pContainerMetaDataIn.entrySet()) {
            String key = entry.getKey();
            assertEquals(pContainerMetaDataOut.containsKey(key), true);
            assertEquals(pContainerMetaDataOut.get(key), entry.getValue());
         }
         System.out.println(container);

         System.out.println("GetContainerOptions.Builder.metadata(cdmi_acl)");
         container = api.get(pContainerName, ContainerQueryParams.Builder.metadata("cdmi_acl"));
         assertNotNull(container);
         System.out.println(container);
         assertNotNull(container.getACLMetadata());
         assertEquals(container.getACLMetadata().size(), 3);

         System.out.println("adding containers to container");
         String firstParentURI = api.get(pContainerName).getObjectName();
         for (int i = 0; i < 10; i++) {
            container = api.create(firstParentURI + "childcontainer" + i + "/");
            assertNotNull(container);
            assertEquals(container.getParentURI(), pContainerName);
            assertEquals(container.getObjectName(), "childcontainer" + i + "/");
            container = api.create(container.getParentURI() + container.getObjectName() + "grandchild/",
                     pCreateContainerOptions);
            assertEquals(container.getParentURI(), pContainerName + "childcontainer" + i + "/");
            assertEquals(container.getObjectName(), "grandchild" + "/");
            container = api.get(container.getParentURI(), ContainerQueryParams.Builder.children());
            assertEquals(container.getChildren().contains("grandchild" + "/"), true);
         }
         container = api.get(pContainerName, ContainerQueryParams.Builder.children());
         assertNotNull(container);
         assertNotNull(container.getChildren());
         assertEquals(container.getChildren().size(), 10);
         container = api.get(pContainerName, ContainerQueryParams.Builder.children(0, 3));
         assertNotNull(container);
         assertNotNull(container.getChildren());
         assertEquals(container.getChildren().size(), 4);

         container = api.get(pContainerName, ContainerQueryParams.Builder.field("parentURI").field("objectName")
                  .children().metadata());
         assertNotNull(container);
         assertNotNull(container.getChildren());
         assertEquals(container.getChildren().size(), 10);
         assertEquals(container.getParentURI(), "/");
         assertEquals(container.getObjectName(), pContainerName);
         assertEquals(container.getParentURI(), "/");
         assertEquals(container.getACLMetadata().size(), 3);
         for (String childName : container.getChildren()) {
            api.delete(container.getObjectName() + childName);
         }
         assertEquals(api.get(pContainerName, ContainerQueryParams.Builder.children()).getChildren().isEmpty(), true);

      } finally {
         Logger.getAnonymousLogger().info("delete: " + pContainerName);
         api.delete(pContainerName);
         container = api.get("/");
         System.out.println("root container: " + container);
         assertEquals(container.getChildren().contains(pContainerName), false);
      }

   }

}
