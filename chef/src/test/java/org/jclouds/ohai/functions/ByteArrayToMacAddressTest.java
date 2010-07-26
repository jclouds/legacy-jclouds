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
package org.jclouds.ohai.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.encryption.EncryptionService;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ByteArrayToMacAddress}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "ohai.ByteArrayToMacAddressTest")
public class ByteArrayToMacAddressTest {

   private ByteArrayToMacAddress converter;
   private EncryptionService encryptionService;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector();
      converter = injector.getInstance(ByteArrayToMacAddress.class);
      encryptionService = injector.getInstance(EncryptionService.class);
   }

   public void test() {
      assertEquals(converter.apply(encryptionService.fromHex("0026bb09e6c4")), "00:26:bb:09:e6:c4");
   }
}
