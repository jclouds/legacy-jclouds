package org.jclouds.nirvanix.sdn.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;

import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.util.DateService;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseSessionTokenFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "sdn.ParseSessionTokenFromJsonResponseTest")
public class ParseSessionTokenFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());
   DateService dateService = new DateService();

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/login.json");

      ParseSessionTokenFromJsonResponse parser = new ParseSessionTokenFromJsonResponse(i
               .getInstance(Gson.class));
      String response = parser.apply(is);
      assertEquals(response, "e4b08449-4501-4b7a-af6a-d4e1e1bd7919");
   }

}
