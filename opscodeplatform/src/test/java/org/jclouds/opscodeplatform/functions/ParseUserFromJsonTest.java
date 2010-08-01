package org.jclouds.opscodeplatform.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.jclouds.opscodeplatform.domain.User;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseUserFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "chef.ParseUserFromJsonTest")
public class ParseUserFromJsonTest {

   private ParseJson<User> handler;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ChefParserModule(), new GsonModule());
      handler = injector.getInstance(Key.get(new TypeLiteral<ParseJson<User>>() {
      }));
   }

   public void test() {

      User user = new User("bobo");
      user.setFirstName("Bobo");
      user.setMiddleName("Tiberion");
      user.setLastName("Clown");
      user.setDisplayName("Bobo T. Clown");
      user.setEmail("bobo@clownco.com");

      String toParse = "{\n\"username\": \"bobo\",\n\"first_name\": \"Bobo\",\n\"middle_name\": \"Tiberion\",\n\"last_name\": \"Clown\",\n\"display_name\": \"Bobo T. Clown\",\n\"email\": \"bobo@clownco.com\" \n}";

      assertEquals(handler.apply(new HttpResponse(200, "ok", Payloads.newPayload(Utils.toInputStream(toParse)))), user);
   }
}
