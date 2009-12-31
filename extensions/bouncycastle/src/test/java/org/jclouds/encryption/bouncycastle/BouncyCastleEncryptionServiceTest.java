/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.encryption.bouncycastle;

import org.jclouds.encryption.EncryptionService;
import org.jclouds.encryption.EncryptionServiceTest;
import org.jclouds.encryption.bouncycastle.config.BouncyCastleEncryptionServiceModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * This tests the performance of Digest commands.
 * 
 * @author Adrian Cole
 */
@Test(groups = "performance", sequential = true, testName = "jclouds.BouncyCastleEncryptionServiceTest")
public class BouncyCastleEncryptionServiceTest extends EncryptionServiceTest {

   @BeforeTest
   protected void createEncryptionService() {
      Injector i = Guice.createInjector(new BouncyCastleEncryptionServiceModule());
      encryptionService = i.getInstance(EncryptionService.class);
      assert encryptionService instanceof BouncyCastleEncryptionService;
   }
}
