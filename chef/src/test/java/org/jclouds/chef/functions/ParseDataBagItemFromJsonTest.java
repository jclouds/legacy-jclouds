package org.jclouds.chef.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.chef.domain.DataBagItem;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * @author AdrianCole
 */
@Test(groups = "unit", testName = "chef.ParseDataBagItemFromJsonTest")
public class ParseDataBagItemFromJsonTest {
   private ParseJson<DataBagItem> handler;
   private Gson mapper;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ChefParserModule(), new GsonModule());
      handler = injector.getInstance(Key.get(new TypeLiteral<ParseJson<DataBagItem>>() {
      }));
      mapper = injector.getInstance(Gson.class);
   }

   public void test1() {
      String json = "{\"my_key\":\"my_data\",\"id\":\"item1\"}";
      DataBagItem item = new DataBagItem("item1", json);
      assertEquals(handler.apply(new HttpResponse(200, "ok", Payloads.newStringPayload(json))), item);
      assertEquals(mapper.toJson(item), json);
   }
}
