package org.jclouds.chef.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.chef.domain.User;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseUserFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "chef.ParseUserFromJsonTest")
public class ParseUserFromJsonTest {

   private ParseUserFromJson handler;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ParserModule());
      handler = injector.getInstance(ParseUserFromJson.class);
   }

   public void test() {

      User user = new User();
      user.setUsername("bobo");
      user.setFirstName("Bobo");
      user.setMiddleName("Tiberion");
      user.setLastName("Clown");
      user.setDisplayName("Bobo T. Clown");
      user.setEmail("bobo@clownco.com");

      String toParse = "{\n\"username\": \"bobo\",\n\"first_name\": \"Bobo\",\n\"middle_name\": \"Tiberion\",\n\"last_name\": \"Clown\",\n\"display_name\": \"Bobo T. Clown\",\n\"email\": \"bobo@clownco.com\" \n}";

      assertEquals(handler.apply(new HttpResponse(Utils.toInputStream(toParse))), user);
   }
}
