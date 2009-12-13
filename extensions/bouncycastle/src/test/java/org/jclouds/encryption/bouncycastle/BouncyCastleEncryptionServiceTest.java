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
