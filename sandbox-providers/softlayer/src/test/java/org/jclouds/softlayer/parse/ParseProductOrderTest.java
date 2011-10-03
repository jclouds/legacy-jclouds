/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.softlayer.parse;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.http.HttpResponse;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.softlayer.compute.functions.ProductItems;
import org.jclouds.softlayer.config.SoftLayerParserModule;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.jclouds.softlayer.domain.ProductOrder;
import org.jclouds.softlayer.predicates.ProductItemPredicates;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * 
 * @author Jason King
 */
@Test(groups = "unit", testName = "ParseProductOrderTest")
public class ParseProductOrderTest extends BaseItemParserTest<ProductOrder> {

   @Override
   public String resource() {
      return "/product_order_template.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ProductOrder expected() {

      Set<ProductItemPrice> prices = ImmutableSet.<ProductItemPrice>builder()
         .add(ProductItemPrice.builder().id(1962).itemId(1045).recurringFee(0F).hourlyRecurringFee(0F).build())
         .add(ProductItemPrice.builder().id(1644).itemId(861).recurringFee(0F).hourlyRecurringFee(0F).build())
         .add(ProductItemPrice.builder().id(905).itemId(503).recurringFee(0F).hourlyRecurringFee(0F).build())
         .add(ProductItemPrice.builder().id(274).itemId(188).recurringFee(0F).hourlyRecurringFee(0F).build())
         .add(ProductItemPrice.builder().id(1800).itemId(439).recurringFee(0F).hourlyRecurringFee(0F).build())
         .add(ProductItemPrice.builder().id(21).itemId(15).recurringFee(0F).hourlyRecurringFee(0F).build())
         .add(ProductItemPrice.builder().id(1639).itemId(865).recurringFee(0F).hourlyRecurringFee(0F).build())
         .add(ProductItemPrice.builder().id(1693).itemId(884).recurringFee(0F).hourlyRecurringFee(0F).build())
         .add(ProductItemPrice.builder().id(55).itemId(49).recurringFee(0F).hourlyRecurringFee(0F).build())
         .add(ProductItemPrice.builder().id(57).itemId(51).recurringFee(0F).hourlyRecurringFee(0F).build())
         .add(ProductItemPrice.builder().id(58).itemId(52).recurringFee(0F).hourlyRecurringFee(0F).build())
         .add(ProductItemPrice.builder().id(420).itemId(309).recurringFee(0F).hourlyRecurringFee(0F).build())
         .add(ProductItemPrice.builder().id(418).itemId(307).recurringFee(0F).hourlyRecurringFee(0F).build())
         .build();

      ProductOrder order =  ProductOrder.builder()
            .quantity(0)
            .packageId(46)
            .useHourlyPricing(true)
            .prices(prices)
            .build();

      return order;
   }

   @Test
   public void test() {
      ProductOrder expects = expected();
      Function<HttpResponse, ProductOrder> parser = parser(injector());
      ProductOrder response = parser.apply(new HttpResponse(200, "ok", payload()));
      assertEquals(response,expects);
      hasOs(response);
   }

   private void hasOs(ProductOrder order) {
       Iterable<ProductItem> items = Iterables.transform(order.getPrices(), ProductItems.item());
       ProductItem os = Iterables.find(ImmutableSet.copyOf(items), ProductItemPredicates.categoryCode("os"));
       assertNotNull(os);
   }

   protected Injector injector() {
      return Guice.createInjector(new SoftLayerParserModule(), new GsonModule() {

         @Override
         protected void configure() {
            bind(DateAdapter.class).to(Iso8601DateAdapter.class);
            super.configure();
         }

      });
   }

}
