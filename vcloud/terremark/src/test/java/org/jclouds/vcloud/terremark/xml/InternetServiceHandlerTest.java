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
package org.jclouds.vcloud.terremark.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.PublicIpAddress;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code InternetServiceHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.InternetServiceHandlerTest")
public class InternetServiceHandlerTest extends BaseHandlerTest {

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
   }

   public void test1() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/terremark/InternetService.xml");

      InternetService result = (InternetService) factory.create(
               injector.getInstance(InternetServiceHandler.class)).parse(is);
      assertEquals(result, new InternetService(523 + "", "IS_for_Jim", null, new PublicIpAddress(
               4208, InetAddress.getByName("10.1.22.159"), null), 80, "HTTP", false, 1,
               "Some test service"));
   }

   public void test2() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/terremark/InternetService2.xml");

      InternetService result = (InternetService) factory.create(
               injector.getInstance(InternetServiceHandler.class)).parse(is);
      assertEquals(
               result,
               new InternetService(
                        524 + "",
                        "IS_for_Jim2",
                        URI
                                 .create("https://services.vcloudexpress.terremark.com/api/v0.8/InternetServices/524"),
                        new PublicIpAddress(
                                 4208,
                                 InetAddress.getByName("10.1.22.159"),
                                 URI
                                          .create("https://services.vcloudexpress.terremark.com/api/v0.8/PublicIps/4208")),
                        45, "HTTP", false, 1, "Some test service"));
   }
}
