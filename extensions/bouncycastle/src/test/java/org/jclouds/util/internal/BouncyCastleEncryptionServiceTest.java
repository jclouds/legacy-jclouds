package org.jclouds.util.internal;

import org.jclouds.util.EncryptionServiceTest;
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
      Injector i = Guice.createInjector();
      encryptionService = i.getInstance(BouncyCastleEncryptionService.class);
   }
}
