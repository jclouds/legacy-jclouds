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
 * Example Setup: -Dtest.cdmi.identity=admin:Admin?authType=openstackKeystone -Dtest.cdmi.credential=passw0rd
 * -Dtest.cdmi.endpoint=http://pds-stack2:5000/v2.0/
 * 
 * @author Kenneth Nagin
 */

@Test(groups = "live", testName = "ContainerApiLiveTest")
public class ContainerApiLiveTest extends BaseCDMIApiLiveTest {

   @Test
   public void testCreateContainer() throws Exception {
      String pContainerName = "MyContainer" + System.currentTimeMillis() + "/";
      Map<String, String> pContainerMetaDataIn = Maps.newHashMap();
      Iterator<String> keys;
      pContainerMetaDataIn.put("containerkey1", "value1");
      pContainerMetaDataIn.put("containerkey2", "value2");
      pContainerMetaDataIn.put("containerkey3", "value3");
      CreateContainerOptions pCreateContainerOptions = CreateContainerOptions.Builder.metadata(pContainerMetaDataIn);
      ContainerApi api = cdmiContext.getApi().getApi();

      Logger.getAnonymousLogger().info("create: " + pContainerName);
      Container container = api.create(pContainerName, pCreateContainerOptions);
      assertNotNull(container);
      try {
         Logger.getAnonymousLogger().info(container.toString());
         assertEquals(container.getObjectType(), ObjectTypes.CONTAINER);
         assertNotNull(container.getObjectID());
         assertNotNull(container.getObjectName());
         assertNotNull(container.getUserMetadata());
         Map<String, String> pContainerMetaDataOut = container.getUserMetadata();
         keys = pContainerMetaDataIn.keySet().iterator();
         while (keys.hasNext()) {
            String key = keys.next();
            assertEquals(pContainerMetaDataOut.containsKey(key), true);
            assertEquals(pContainerMetaDataOut.get(key), pContainerMetaDataIn.get(key));
         }
         Logger.getAnonymousLogger().info("UserMetaData: " + container.getUserMetadata());
         assertNotNull(container.getSystemMetadata());
         Logger.getAnonymousLogger().info("SystemMetaData: " + container.getSystemMetadata());
         assertNotNull(container.getACLMetadata());
         List<Map<String, String>> aclMetadataOut = container.getACLMetadata();
         Logger.getAnonymousLogger().info("ACLMetaData: ");
         for (Map<String, String> aclMap : aclMetadataOut) {
            Logger.getAnonymousLogger().info(aclMap.toString());
         }
         container = api.get("/");
         assertNotNull(container);
         Logger.getAnonymousLogger().info("root container: " + container);
         assertEquals(container.getChildren().contains(pContainerName), true);

      } finally {
         Logger.getAnonymousLogger().info("deleteContainer: " + pContainerName);
         for (String containerChild : api.get(pContainerName).getChildren()) {
            Logger.getAnonymousLogger().info("Deleting " + containerChild);
            api.delete(containerChild);
         }
         api.delete(pContainerName);
         container = api.get("/");
         Logger.getAnonymousLogger().info("root container: " + container.toString());
         assertEquals(container.getChildren().contains(pContainerName), false);
      }

   }

   @Test
   public void testGetContainer() throws Exception {
      // openstack does not allow nesting of containers beyond children of root container.
      // However other cdmi implementations do not have this restriction.
      // The api is agnostic to these restrictions but the testsuite contains tests
      // for container nesting. containerNesting allows for distinguishing.
      boolean containerNesting = Boolean.valueOf(System.getProperty("test.cdmi.containerNesting", "false"));
      String pContainerName = "MyContainer" + System.currentTimeMillis() + "/";
      Map<String, String> pContainerMetaDataIn = Maps.newHashMap();
      Iterator<String> keys;
      pContainerMetaDataIn.put("containerkey1", "value1");
      pContainerMetaDataIn.put("containerkey2", "value2");
      pContainerMetaDataIn.put("containerkey3", "value3");
      CreateContainerOptions pCreateContainerOptions = CreateContainerOptions.Builder.metadata(pContainerMetaDataIn);
      ContainerApi api = cdmiContext.getApi().getApi();

      Container container = api.create(pContainerName, pCreateContainerOptions);
      assertNotNull(container);
      try {
         Logger.getAnonymousLogger().info(container.toString());
         Logger.getAnonymousLogger().info("getContainer: " + pContainerName);
         container = api.get(pContainerName);
         assertNotNull(container);
         Logger.getAnonymousLogger().info(container.toString());
         assertEquals(container.getObjectType(), ObjectTypes.CONTAINER);
         assertNotNull(container.getObjectID());
         assertNotNull(container.getObjectName());
         assertEquals(container.getObjectName(), pContainerName);
         assertNotNull(container.getChildren());
         assertEquals(container.getChildren().isEmpty(), true);
         Logger.getAnonymousLogger().info("Children: " + container.getChildren());
         assertNotNull(container.getMetadata());
         Logger.getAnonymousLogger().info("Raw metadata: " + container.getMetadata());
         keys = container.getMetadata().keySet().iterator();
         while (keys.hasNext()) {
            String key = keys.next();
            JsonBall value = container.getMetadata().get(key);
            Logger.getAnonymousLogger().info(key + ":" + value);
         }
         assertNotNull(container.getUserMetadata());
         Map<String, String> pContainerMetaDataOut = container.getUserMetadata();
         keys = pContainerMetaDataIn.keySet().iterator();
         while (keys.hasNext()) {
            String key = keys.next();
            assertEquals(pContainerMetaDataOut.containsKey(key), true);
            assertEquals(pContainerMetaDataOut.get(key), pContainerMetaDataIn.get(key));
         }
         Logger.getAnonymousLogger().info("UserMetaData: " + container.getUserMetadata());
         assertNotNull(container.getSystemMetadata());
         Logger.getAnonymousLogger().info("SystemMetaData: " + container.getSystemMetadata());
         assertNotNull(container.getACLMetadata());
         List<Map<String, String>> aclMetadataOut = container.getACLMetadata();
         Logger.getAnonymousLogger().info("ACLMetaData: ");
         for (Map<String, String> aclMap : aclMetadataOut) {
            Logger.getAnonymousLogger().info(aclMap.toString());
         }
         container = api.get("/");
         Logger.getAnonymousLogger().info("root container: " + container.toString());
         assertEquals(container.getChildren().contains(pContainerName), true);
         container = api.get(pContainerName, ContainerQueryParams.Builder.field("parentURI"));
         assertNotNull(container);
         Logger.getAnonymousLogger().info(container.toString());

         container = api.get(pContainerName, ContainerQueryParams.Builder.field("parentURI").field("objectName"));
         assertNotNull(container);
         assertEquals(container.getObjectName(), pContainerName);

         container = api.get(pContainerName, ContainerQueryParams.Builder.metadata());
         assertNotNull(container);
         pContainerMetaDataOut = container.getUserMetadata();
         keys = pContainerMetaDataIn.keySet().iterator();
         while (keys.hasNext()) {
            String key = keys.next();
            assertEquals(pContainerMetaDataOut.containsKey(key), true);
            assertEquals(pContainerMetaDataOut.get(key), pContainerMetaDataIn.get(key));
         }
         Logger.getAnonymousLogger().info(container.toString());

         container = api.get(pContainerName, ContainerQueryParams.Builder.metadata("cdmi_acl"));
         assertNotNull(container);
         Logger.getAnonymousLogger().info(container.toString());
         assertNotNull(container.getACLMetadata());
         container = api.get(pContainerName,
                  ContainerQueryParams.Builder.any("query1=abc").field("objectName").any("query2=anyQueryParam")
                           .metadata());
         assertNotNull(container);

         // openstack does not support containers within containers
         if (containerNesting) {
            Logger.getAnonymousLogger().info("adding containers to container");
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
               api.delete(firstParentURI + "childcontainer" + i + "/" + "grandchild" + "/");
            }
            container = api.get(pContainerName, ContainerQueryParams.Builder.children());
            assertNotNull(container);
            assertNotNull(container.getChildren());
            assertEquals(container.getChildren().size(), 10);
            container = api.get(pContainerName, ContainerQueryParams.Builder.children(0, 3));
            assertNotNull(container);
            assertNotNull(container.getChildren());
            assertEquals(container.getChildren().size(), 4);

            container = api.get(pContainerName, ContainerQueryParams.Builder.field("parentURI").field("objectName").children().metadata());
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
         }

      } finally {
         Logger.getAnonymousLogger().info("deleteContainer: " + pContainerName);
         for (String containerChild : api.get(pContainerName).getChildren()) {
            Logger.getAnonymousLogger().info("deleting: " + containerChild);
            api.delete(containerChild);
         }
         api.delete(pContainerName);
         container = api.get("/");
         Logger.getAnonymousLogger().info("root container: " + container);
         assertEquals(container.getChildren().contains(pContainerName), false);
      }
   }
}
