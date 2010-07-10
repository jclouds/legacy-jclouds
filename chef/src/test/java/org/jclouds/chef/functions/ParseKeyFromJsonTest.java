package org.jclouds.chef.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.Payloads;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseKeyFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "chef.ParseKeyFromJsonTest")
public class ParseKeyFromJsonTest {

   private ParseKeyFromJson handler;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ParserModule());
      handler = injector.getInstance(ParseKeyFromJson.class);
   }

   public void testRegex() {
      assertEquals(
               handler
                        .apply(new HttpResponse(
                                 200,
                                 "ok",
                                 Payloads
                                          .newPayload(Utils
                                                   .toInputStream("{\n\"uri\": \"https://api.opscode.com/users/bobo\", \"private_key\": \"RSA_PRIVATE_KEY\",}")))),
               "RSA_PRIVATE_KEY");
   }

   public void test2() {
      String key = handler.apply(new HttpResponse(200, "ok", Payloads
               .newPayload(ParseKeyFromJsonTest.class.getResourceAsStream("/newclient.txt"))));
      assert key.startsWith("-----BEGIN RSA PRIVATE KEY-----\n");
   }
}
