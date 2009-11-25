/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rimuhosting.miro;

import static com.google.common.base.Preconditions.checkNotNull;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rimuhosting.miro.data.CreateOptions;
import org.jclouds.rimuhosting.miro.data.NewInstance;
import org.jclouds.rimuhosting.miro.domain.*;
import org.jclouds.rimuhosting.miro.domain.internal.RunningState;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.util.SortedSet;

/**
 * Tests behavior of {@code RimuHostingClient}
 *
 * @author Ivan Meredith
 */
@Test(groups = "live", testName = "rimuhosting.RimuHostingClientLiveTest")
public class RimuHostingClientLiveTest {

   private RimuHostingClient connection;

   @BeforeGroups(groups = {"live"})
   public void setupClient() {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      connection = RimuHostingContextFactory.createContext(user, password, new Log4JLoggingModule())
              .getApi();
   }

   @Test
   public void testPricingPlans(){
      SortedSet<PricingPlan> plans = connection.getPricingPlanList();
      for(PricingPlan plan : plans){
         if(plan.getId().equalsIgnoreCase("miro1")){
            assertTrue(true);
            return;
         }
      }
      assertTrue(false);
   }

   @Test
   public void testImages(){
      SortedSet<Image> images = connection.getImageList();
      for(Image image : images){
         if(image.getId().equalsIgnoreCase("lenny")){
            assertTrue(true);
            return;
         }
      }
      assertTrue(false, "lenny not found");
   }
   @Test
   public void testLifeCycle() {
	   //Get the first image, we dont really care what it is in this test.
	   NewInstance inst = new NewInstance(new CreateOptions("test.jclouds.org",null,"lenny"),"MIRO1") ;
      NewInstanceResponse instanceResponse = connection.createInstance(inst);
      Instance instance = instanceResponse.getInstance();
      //Now we have the instance, lets restart it
      assertNotNull(instance.getId());
      InstanceInfo instanceInfo =  connection.restartInstance(instance.getId());
      connection.destroyInstance(instance.getId());
      //Should be running now.
      assertEquals(instanceInfo.getState(), RunningState.RUNNING);
      assertEquals(instance.getName(),"test.jclouds.org");
      assertEquals(instance.getImageId(), "lenny");
   }
}
