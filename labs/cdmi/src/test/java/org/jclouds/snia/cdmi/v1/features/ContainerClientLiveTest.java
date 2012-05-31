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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.jclouds.domain.JsonBall;
import org.jclouds.snia.cdmi.v1.ObjectTypes;
import org.jclouds.snia.cdmi.v1.domain.Container;
import org.jclouds.snia.cdmi.v1.internal.BaseCDMIClientLiveTest;
import org.jclouds.snia.cdmi.v1.options.CreateContainerOptions;
import org.testng.annotations.Test;

/**
 * 
 * @author Kenneth Nagin
 */
@Test(groups = "live", testName = "ContainerClientLiveTest")
public class ContainerClientLiveTest extends BaseCDMIClientLiveTest {
/*
   @Test
   public void testGetContainer() throws Exception {
//	  String pContainerName = "NaginContainer1335695552671"; 
	  String pContainerName = System.getProperty("test.cdmi.containerName","MyContainer"); 
      ContainerClient client = cdmiContext.getApi().getContainerClient();
      Container container = client.getContainer(pContainerName);
      assertNotNull(container);
      assertEquals(container.getObjectType(), ObjectTypes.CONTAINER);
      assertNotNull(container.getObjectID());
      assertNotNull(container.getObjectName());
      assertEquals(container.getObjectName(),pContainerName+"/");
      assertNotNull(container.getChildren());
      assertNotNull(container.getMetadata());
      System.out.println(container.getMetadata());
      Iterator<String> keys = container.getMetadata().keySet().iterator();
      while ( keys.hasNext() ){
    	String key = keys.next();
    	JsonBall value = container.getMetadata().get(key);
        System.out.println( key + ":" + value );
        if(key.matches("cdmi_acl")) {
        	System.out.println("cdmi_acl here");
        	String[] cdmi_acl_array = value.toString().split("[[{}]]");
        	for(int i = 0; i < cdmi_acl_array.length; i++) {
        		System.out.println(  cdmi_acl_array[i] );        		
        	}
        	
        }
      }
   }
   */
   @Test
   public void testCreateContainer() throws Exception {
	   String pContainerName = "MyContainer"+System.currentTimeMillis();
	   Map<String,String> pContainerMetaDataIn = new HashMap<String, String>();
	   Iterator<String> keys;
	   pContainerMetaDataIn.put("containerkey1","value1");
	   pContainerMetaDataIn.put("containerkey2","value2");
	   pContainerMetaDataIn.put("containerkey3","value3");
	   CreateContainerOptions pCreateContainerOptions = CreateContainerOptions.Builder.withMetadata(pContainerMetaDataIn) ;
	   ContainerClient client = cdmiContext.getApi().getContainerClient();
	   Logger.getAnonymousLogger().info("createContainer: "+pContainerName);
	   Container container = client.createContainer(pContainerName,pCreateContainerOptions);
	   assertNotNull(container);
	   System.out.println(container);
	   Logger.getAnonymousLogger().info("getContainer: "+pContainerName);
	   container = client.getContainer(pContainerName);
	   assertNotNull(container);
	   System.out.println(container);
	   assertEquals(container.getObjectType(), ObjectTypes.CONTAINER);	   
	   assertNotNull(container.getObjectID());
	   assertNotNull(container.getObjectName());
	   assertEquals(container.getObjectName(),pContainerName+"/");
	   assertNotNull(container.getChildren());
	   assertEquals(container.getChildren().isEmpty(),true);
	   System.out.println("Children: "+container.getChildren());
	   assertNotNull(container.getMetadata());
	   System.out.println("Raw metadata: "+container.getMetadata());
	   keys = container.getMetadata().keySet().iterator();
	   while ( keys.hasNext() ){
	     String key = keys.next();
	     JsonBall value = container.getMetadata().get(key);
	     System.out.println( key + ":" + value );
	   }
	   assertNotNull(container.getUserMetadata());
	   Map<String,String> pContainerMetaDataOut = container.getUserMetadata();
	   keys = pContainerMetaDataIn.keySet().iterator();
	   while ( keys.hasNext()) {
		   String key = keys.next();
		   assertEquals(pContainerMetaDataOut.containsKey(key),true);
		   assertEquals(pContainerMetaDataOut.get(key),pContainerMetaDataIn.get(key));		   
	   }
	   System.out.println("UserMetaData: " +container.getUserMetadata());
	   assertNotNull(container.getSystemMetadata());
	   System.out.println("SystemMetaData: " +container.getSystemMetadata());
	   assertNotNull(container.getACLMetadata());
	   List<Map<String,String>>  aclMetadataOut = container.getACLMetadata();
	   System.out.println("ACLMetaData: ");
	   for(Map<String,String> aclMap: aclMetadataOut) {
		   System.out.println(aclMap);
	   }
	   container = client.getContainer("/");
	   System.out.println("root container: "+container);
	   assertEquals(container.getChildren().contains(pContainerName+"/"),true);
	   Logger.getAnonymousLogger().info("deleteContainer: "+pContainerName);	   
	   client.deleteContainer(pContainerName);
	   container = client.getContainer("/");
	   System.out.println("root container: "+container);
	   assertEquals(container.getChildren().contains(pContainerName+"/"),false);
	   
   }
   

}
