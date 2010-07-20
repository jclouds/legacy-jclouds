package org.jclouds.chef.functions;

import static org.jclouds.io.Payloads.newStringPayload;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.chef.domain.Client;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseClientFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "chef.ParseClientFromJsonTest")
public class ParseClientFromJsonTest {

   private ParseJson<Client> handler;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ParserModule());
      handler = injector.getInstance(Key.get(new TypeLiteral<ParseJson<Client>>() {
      }));
   }

   public void test() {

      Client user = new Client("-----BEGIN CERTIFICATE-----dXQ==-----END CERTIFICATE-----", "jclouds",
            "adrian-jcloudstest", "adrian-jcloudstest", false);

      String toParse = "{ \"certificate\":\"-----BEGIN CERTIFICATE-----dXQ==-----END CERTIFICATE-----\", \"orgname\":\"jclouds\", \"clientname\":\"adrian-jcloudstest\",   \"name\": \"adrian-jcloudstest\",\"validator\": false }";
      System.out.println(toParse);
      assertEquals(handler.apply(new HttpResponse(200, "ok", newStringPayload(toParse))), user);
   }
}
