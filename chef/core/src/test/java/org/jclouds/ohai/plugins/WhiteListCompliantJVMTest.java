/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.ohai.plugins;

import static org.testng.Assert.assertEquals;

import java.net.SocketException;
import java.util.Properties;

import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.ohai.config.WhiteListCompliantOhaiJVMModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code WhiteListCompliantJVM}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ohai.WhiteListCompliantJVMTest")
public class WhiteListCompliantJVMTest {

   @Test
   public void test() throws SocketException {

      final Properties sysProperties = new Properties();

      sysProperties.setProperty("os.name", "Mac OS X");
      sysProperties.setProperty("os.version", "10.3.0");
      sysProperties.setProperty("user.name", "user");

      Injector injector = Guice.createInjector(new ChefParserModule(), new GsonModule(),
            new WhiteListCompliantOhaiJVMModule() {
               @Override
               protected Long nanoTime() {
                  return 1279992919325290l;
               }

               @Override
               protected Properties systemProperties() {
                  return sysProperties;
               }

            });
      Json json = injector.getInstance(Json.class);
      WhiteListCompliantJVM WhiteListCompliantJVM = injector.getInstance(WhiteListCompliantJVM.class);

      assertEquals(
            json.toJson(WhiteListCompliantJVM.get()),
            "{\"ohai_time\":1279992919.32529,\"java\":{\"user.name\":\"user\",\"os.version\":\"10.3.0\",\"os.name\":\"Mac OS X\"},\"platform\":\"macosx\",\"platform_version\":\"10.3.0\",\"current_user\":\"user\"}");

   }
}
