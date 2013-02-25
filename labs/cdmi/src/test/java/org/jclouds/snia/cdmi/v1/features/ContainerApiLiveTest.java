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

import org.jclouds.domain.JsonBall;
import org.jclouds.snia.cdmi.v1.ObjectTypes;
import org.jclouds.snia.cdmi.v1.domain.CDMIObjectCapability;
import org.jclouds.snia.cdmi.v1.domain.Container;
import org.jclouds.snia.cdmi.v1.internal.BaseCDMIApiLiveTest;
import org.jclouds.snia.cdmi.v1.options.CreateContainerOptions;
import org.jclouds.snia.cdmi.v1.queryparams.ContainerQueryParams;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Example Setup:
 * -Dtest.cdmi.identity=admin:Admin 
 * -Dtest.cdmi.credential=passw0rd  
 * -Dtest.cdmi.endpoint=http://stackhost:5000/v2.0/
 * -Djclouds.cdmi.authType=openstackKeystone 
 * 
 * @author Kenneth Nagin
 */

@Test(groups = "live", testName = "ContainerApiLiveTest")
public class ContainerApiLiveTest extends BaseCDMIApiLiveTest {
   static final ImmutableMap<String, String> pContainerMetaDataIn =
      new ImmutableMap.Builder<String, String>()
          .put("one", "1")
          .put("two", "2")
          .put("three", "3")
          .build();
   static final Logger logger = Logger.getAnonymousLogger();

   @Test
   public void testCreateContainer() throws Exception {
      String containerName = "MyContainer" + System.currentTimeMillis();
      Iterator<String> keys;
      CreateContainerOptions pCreateContainerOptions = CreateContainerOptions.Builder.metadata(pContainerMetaDataIn);
      ContainerApi containerApi = cdmiContext.getApi().getApi();
      assertEquals(containerApi.containerExists(containerName),false);
      assertEquals(containerApi.get().getChildren().contains(containerName+"/"),false);
      Container container = containerApi.create(containerName, pCreateContainerOptions);
      assertNotNull(container);
      try {
         logger.info(container.toString());
         assertEquals(containerApi.containerExists(containerName),true);
         assertEquals(containerApi.get().getChildren().contains(containerName+"/"),true);
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
         logger.info("UserMetaData: " + container.getUserMetadata());
         assertNotNull(container.getSystemMetadata());
         logger.info("SystemMetaData: " + container.getSystemMetadata());
         assertNotNull(container.getACLMetadata());
         List<Map<String, String>> aclMetadataOut = container.getACLMetadata();
         logger.info("ACLMetaData: ");
         for (Map<String, String> aclMap : aclMetadataOut) {
            logger.info(aclMap.toString());
         }
         logger.info("testCreateContainer() completed successfully");
      } finally {
         containerApi.delete(containerName);
         assertEquals(containerApi.containerExists(containerName),false);
         assertEquals(containerApi.get().getChildren().contains(containerName+"/"),false);
      }

   }

   @Test
   public void testGetContainer() throws Exception {
      // openstack does not allow nesting of containers beyond children of root container.
      // However other cdmi implementations do not have this restriction.
      // The api is agnostic to these restrictions but the testsuite contains tests
      // for container nesting. containerNesting allows for distinguishing.
      boolean containerNesting = Boolean.valueOf(System.getProperty("test.cdmi.containerNesting", "false"));
      String containerName = "MyContainer" + System.currentTimeMillis();
      Iterator<String> keys;
      CreateContainerOptions pCreateContainerOptions = CreateContainerOptions.Builder.metadata(pContainerMetaDataIn);
      ContainerApi containerApi = cdmiContext.getApi().getApi();

      Container container = containerApi.create(containerName, pCreateContainerOptions);
      assertNotNull(container);
      try {
         logger.info(container.toString());
         logger.info("getContainer: " + containerName);
         container = containerApi.get(containerName);
         assertNotNull(container);
         logger.info(container.toString());
         assertEquals(container.getObjectType(), ObjectTypes.CONTAINER);
         assertNotNull(container.getObjectID());
         assertNotNull(container.getObjectName());
         assertEquals(container.getObjectName(), containerName+"/");
         assertNotNull(container.getChildren());
         assertEquals(container.getChildren().isEmpty(), true);
         assertNotNull(container.getMetadata());
         keys = container.getMetadata().keySet().iterator();
         while (keys.hasNext()) {
            String key = keys.next();
            JsonBall value = container.getMetadata().get(key);
            logger.info(key + ":" + value);
         }
         assertNotNull(container.getUserMetadata());
         Map<String, String> pContainerMetaDataOut = container.getUserMetadata();
         keys = pContainerMetaDataIn.keySet().iterator();
         while (keys.hasNext()) {
            String key = keys.next();
            assertEquals(pContainerMetaDataOut.containsKey(key), true);
            assertEquals(pContainerMetaDataOut.get(key), pContainerMetaDataIn.get(key));
         }
         logger.info("UserMetaData: " + container.getUserMetadata());
         assertNotNull(container.getSystemMetadata());
         logger.info("SystemMetaData: " + container.getSystemMetadata());
         assertNotNull(container.getACLMetadata());
         List<Map<String, String>> aclMetadataOut = container.getACLMetadata();
         logger.info("ACLMetaData: ");
         for (Map<String, String> aclMap : aclMetadataOut) {
            logger.info(aclMap.toString());
         }
         container = containerApi.get(containerName, ContainerQueryParams.Builder.field("parentURI"));
         assertNotNull(container);
         logger.info(container.toString());

         container = containerApi.get(containerName, ContainerQueryParams.Builder.field("parentURI").field("objectName"));
         assertNotNull(container);
         assertEquals(container.getObjectName(), containerName+"/");

         container = containerApi.get(containerName, ContainerQueryParams.Builder.metadata());
         assertNotNull(container);
         pContainerMetaDataOut = container.getUserMetadata();
         keys = pContainerMetaDataIn.keySet().iterator();
         while (keys.hasNext()) {
            String key = keys.next();
            assertEquals(pContainerMetaDataOut.containsKey(key), true);
            assertEquals(pContainerMetaDataOut.get(key), pContainerMetaDataIn.get(key));
         }
         logger.info(container.toString());

         container = containerApi.get(containerName, ContainerQueryParams.Builder.metadata("cdmi_acl"));
         assertNotNull(container);
         logger.info(container.toString());
         assertNotNull(container.getACLMetadata());
         container = containerApi.get(containerName,
                  ContainerQueryParams.Builder.any("query1=abc").field("objectName").any("query2=anyQueryParam")
                           .metadata());
         assertNotNull(container);

         // openstack does not support containers within containers
         if (containerNesting) {
            logger.info("adding containers to container");
            String firstParentURI = containerApi.get(containerName).getObjectName();
            for (int i = 0; i < 10; i++) {
            	String childName = "childcontainer" + i;
               container = containerApi.create(firstParentURI + childName );
               assertNotNull(container);
               assertEquals(container.getParentURI(), firstParentURI);
               assertEquals(container.getObjectName(), childName + "/");
               container = containerApi.create(container.getParentURI() + container.getObjectName() + "grandchild",
                        pCreateContainerOptions);
               assertEquals(container.getParentURI(), firstParentURI+ childName + "/");
               assertEquals(container.getObjectName(), "grandchild" + "/");
               container = containerApi.get(container.getParentURI(), ContainerQueryParams.Builder.children());
               assertEquals(container.getChildren().contains("grandchild" + "/"), true);
               containerApi.delete(firstParentURI + childName + "grandchild");
            }
            container = containerApi.get(containerName, ContainerQueryParams.Builder.children());
            assertNotNull(container);
            assertEquals(container.getChildren().size(), 10);
            container = containerApi.get(containerName, ContainerQueryParams.Builder.children(0, 3));
            assertNotNull(container);
            assertNotNull(container.getChildren());
            assertEquals(container.getChildren().size(), 4);

            container = containerApi.get(containerName, ContainerQueryParams.Builder.field("parentURI").field("objectName").children().metadata());
            assertNotNull(container);
            assertNotNull(container.getChildren());
            assertEquals(container.getChildren().size(), 10);
            assertEquals(container.getParentURI(), "/");
            assertEquals(container.getObjectName(), containerName+"/");
            assertEquals(container.getParentURI(), "/");
            assertEquals(container.getACLMetadata().size(), 3);
            for (String childName : container.getChildren()) {
               containerApi.delete(container.getObjectName() + childName);
            }
            assertEquals(containerApi.get(containerName, ContainerQueryParams.Builder.children()).getChildren().isEmpty(), true);
         }
         logger.info("testGetContainer() completed successfully");

      } finally {
         logger.info("deleteContainer: " + containerName);
         containerApi.delete(containerName);
         assertEquals(containerApi.containerExists(containerName),false);
         assertEquals(containerApi.get().getChildren().contains(containerName+"/"), false);
      }
   }
   @Test
   public void testGetContainerCapabilities() throws Exception {
      ContainerApi api = cdmiContext.getApi().getApi();
      logger.info("get provider capabilites ");
      try {
      	CDMIObjectCapability cdmiObjectCapability = api.getCapabilites();
      	assertNotNull(cdmiObjectCapability);
      	logger.info("CDMIObjectCapability: "+cdmiObjectCapability);
         logger.info("testGetContainerCapabilities() completed successfully");
      } catch (Exception e) {
      	logger.info("CDMIObjectCapability: "+e.getMessage());
      	assertEquals(true,false);      	
      } 
   }

}
