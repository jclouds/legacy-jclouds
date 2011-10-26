package org.jclouds.softlayer.binders;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.json.internal.GsonWrapper;
import org.jclouds.rest.Binder;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.jclouds.softlayer.domain.ProductOrder;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;

/**
 * Tests behavior of {@code ProductOrderToJsonTest}
 *
 * @author Jason King
 */
@Test(groups = "unit")
public class ProductOrderToJsonTest {

   private static final String FORMAT =
         "{'parameters':[{'complexType':'SoftLayer_Container_Product_Order_Virtual_Guest'," +
                         "'packageId':%d," +
                         "'location':'%s'," +
                         "'prices':[{'id':%d},{'id':%d}]," +
                         "'virtualGuests':[{'hostname':'%s','domain':'%s'}]," +
                         "'quantity':%d," +
                         "'useHourlyPricing':%b}" +
                       "]}";

   private HttpRequest request;
   private Binder binder;

   @BeforeGroups(groups = { "unit" })
   public void setup() {
      request = HttpRequest.builder().method("GET").endpoint(URI.create("http://momma")).build();
      Json json = new GsonWrapper(new Gson());
      binder = new ProductOrderToJson(json);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullOrder() {
      binder.bindToRequest(request, null);
   }

   @Test
   public void testCorrect() {

      ProductItemPrice price1 = ProductItemPrice.builder().id(100).build();
      ProductItemPrice price2 = ProductItemPrice.builder().id(101).build();

      VirtualGuest guest = VirtualGuest.builder().hostname("myhost")
                                                 .domain("mydomain")
                                                 .build();

      ProductOrder order = ProductOrder.builder()
                                       .packageId(123)
                                       .location("loc456")
                                       .quantity(99)
                                       .useHourlyPricing(true)
                                       .prices(ImmutableSet.of(price1,price2))
                                       .virtualGuest(guest)
                                       .build();
      
      String expected = String.format(FORMAT.replaceAll("'","\""),
                                      123,"loc456",100,101,"myhost","mydomain",99,true);

      HttpRequest req = binder.bindToRequest(request, order);

      assertEquals(req.getPayload().getRawContent(), expected);

   }
}
