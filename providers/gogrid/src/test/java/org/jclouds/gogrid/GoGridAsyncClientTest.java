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
package org.jclouds.gogrid;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.gogrid.services.BaseGoGridAsyncClientTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code GoGridAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GoGridAsyncClientTest")
public class GoGridAsyncClientTest extends BaseGoGridAsyncClientTest<GoGridAsyncClient> {

   private GoGridAsyncClient asyncClient;
   private GoGridClient syncClient;

   public void testSync() throws SecurityException, NoSuchMethodException, InterruptedException,
            ExecutionException {
      assert syncClient.getImageServices() != null;
      assert syncClient.getIpServices() != null;
      assert syncClient.getJobServices() != null;
      assert syncClient.getLoadBalancerServices() != null;
      assert syncClient.getServerServices() != null;
   }

   public void testAsync() throws SecurityException, NoSuchMethodException, InterruptedException,
            ExecutionException {
      assert asyncClient.getImageServices() != null;
      assert asyncClient.getIpServices() != null;
      assert asyncClient.getJobServices() != null;
      assert asyncClient.getLoadBalancerServices() != null;
      assert asyncClient.getServerServices() != null;
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      asyncClient = injector.getInstance(GoGridAsyncClient.class);
      syncClient = injector.getInstance(GoGridClient.class);
   }
}
