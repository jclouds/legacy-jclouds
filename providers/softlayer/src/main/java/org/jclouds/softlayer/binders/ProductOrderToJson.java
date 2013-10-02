/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.softlayer.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.Binder;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.jclouds.softlayer.domain.ProductOrder;
import org.jclouds.softlayer.domain.VirtualGuest;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Converts a ProductOrder into a json string valid for placing an order via the softlayer api The
 * String is set into the payload of the HttpRequest
 * 
 * @author Jason King
 */
public class ProductOrderToJson implements Binder {

   private Json json;

   @Inject
   public ProductOrderToJson(Json json) {
      this.json = json;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkNotNull(input, "order");
      ProductOrder order = ProductOrder.class.cast(input);
      request.setPayload(buildJson(order));
      return request;
   }

   /**
    * Builds a Json string suitable for sending to the softlayer api
    * 
    * @param order
    * @return
    */
   String buildJson(ProductOrder order) {

      Iterable<Price> prices = Iterables.transform(order.getPrices(), new Function<ProductItemPrice, Price>() {
         @Override
         public Price apply(ProductItemPrice productItemPrice) {
            return new Price(productItemPrice.getId());
         }
      });

      Iterable<HostnameAndDomain> hosts = Iterables.transform(order.getVirtualGuests(),
               new Function<VirtualGuest, HostnameAndDomain>() {
                  @Override
                  public HostnameAndDomain apply(VirtualGuest virtualGuest) {
                     return new HostnameAndDomain(virtualGuest.getHostname(), virtualGuest.getDomain());
                  }
               });

      OrderData data = new OrderData(order.getPackageId(), order.getLocation(), Sets.newLinkedHashSet(prices), Sets
               .newLinkedHashSet(hosts), order.getQuantity(), order.getUseHourlyPricing());

      return json.toJson(ImmutableMap.of("parameters", ImmutableList.<OrderData> of(data)));
   }

   @SuppressWarnings("unused")
   private static class OrderData {
      private String complexType = "SoftLayer_Container_Product_Order_Virtual_Guest";
      private long packageId = -1;
      private String location;
      private Set<Price> prices;
      private Set<HostnameAndDomain> virtualGuests;
      private long quantity;
      private boolean useHourlyPricing;

      public OrderData(long packageId, String location, Set<Price> prices, Set<HostnameAndDomain> virtualGuests,
               long quantity, boolean useHourlyPricing) {
         this.packageId = packageId;
         this.location = location;
         this.prices = prices;
         this.virtualGuests = virtualGuests;
         this.quantity = quantity;
         this.useHourlyPricing = useHourlyPricing;
      }

   }

   @SuppressWarnings("unused")
   private static class HostnameAndDomain {
      private String hostname;
      private String domain;

      public HostnameAndDomain(String hostname, String domain) {
         this.hostname = hostname;
         this.domain = domain;
      }

   }

   @SuppressWarnings("unused")
   private static class Price {
      private long id;

      public Price(long id) {
         this.id = id;
      }
   }

}
