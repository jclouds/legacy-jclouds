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
package org.jclouds.trmk.vcloud_0_8.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Set;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.trmk.vcloud_0_8.domain.InternetService;
import org.jclouds.trmk.vcloud_0_8.domain.Protocol;
import org.jclouds.trmk.vcloud_0_8.domain.PublicIpAddress;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code InternetServicesHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "InternetServicesHandlerTest")
public class InternetServicesHandlerTest extends BaseHandlerTest {

   public void test() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/InternetServices.xml");

      Set<InternetService> result = factory.create(injector.getInstance(InternetServicesHandler.class)).parse(is);
      assertEquals(result.toString(), ImmutableSet.of(new InternetService("IS_for_Jim2", URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/InternetServices/524"),
               new PublicIpAddress("10.1.22.159", URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/PublicIps/4208")), 45,
               Protocol.HTTP, false, 1, "Some test service")).toString());
   }

   public void test2() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/InternetServices-2.xml");

      Set<InternetService> result = factory.create(injector.getInstance(InternetServicesHandler.class)).parse(is);
      assertEquals(result.size(), 6);
   }
}
