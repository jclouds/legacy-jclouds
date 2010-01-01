package org.jclouds.aws.ec2.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ConvertUnencodedBytesToBase64EncodedString}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.ConvertUnencodedBytesToBase64EncodedStringTest")
public class ConvertUnencodedBytesToBase64EncodedStringTest {
   Injector injector = Guice.createInjector();

   public void testDefault() throws IOException {
      ConvertUnencodedBytesToBase64EncodedString function = injector
               .getInstance(ConvertUnencodedBytesToBase64EncodedString.class);

      assertEquals("dGVzdA==", function.apply("test".getBytes()));
   }

}
