/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.config;

import java.util.List;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.rest.RestContext;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.pricing.CostCodeCurrenciesDto;
import com.abiquo.server.core.pricing.CostCodeCurrencyDto;
import com.abiquo.server.core.pricing.CostCodeDto;
import com.google.common.collect.Lists;

/**
 * A cost code is a kind of label where concrete prices can be assigned.
 * <p>
 * Cloud administrators can create several cost codes and assign a price to each
 * one, to have a flexible way to configure custom billings for each resource.
 * <p>
 * Cost codes can be assigned to {@link VirtualMachineTemplate}s and other
 * resources to provide pricing information about them.
 * 
 * @author Ignasi Barrera
 * @author Susana Acedo
 */
public class CostCode extends DomainWrapper<CostCodeDto> {

   private List<CostCodePrice> defaultPrices;

   /**
    * Constructor to be used only by the builder. This resource cannot be
    * created.
    */
   private CostCode(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final CostCodeDto target) {
      super(context, target);
   }

   // Domain operations

   public void delete() {
      context.getApi().getPricingApi().deleteCostCode(target);
      target = null;
   }

   public void save() {
      target = context.getApi().getPricingApi().createCostCode(target);

      if (defaultPrices != null && !defaultPrices.isEmpty()) {
         CostCodeCurrenciesDto costcodecurrencies = new CostCodeCurrenciesDto();
         for (CostCodePrice ccp : defaultPrices) {
            CostCodeCurrencyDto costcodecurrency = new CostCodeCurrencyDto();
            Currency currency = ccp.getCurrency();

            costcodecurrency.addLink(new RESTLink("currency", currency.unwrap().getEditLink().getHref()));
            costcodecurrency.setPrice(ccp.getPrice());
            costcodecurrencies.add(costcodecurrency);
         }
         context.getApi().getPricingApi().updateCostCodeCurrencies(getId(), costcodecurrencies);
      }

   }

   public void update() {
      target = context.getApi().getPricingApi().updateCostCode(target);
   }

   // Builder

   public static Builder builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context) {
      return new Builder(context);
   }

   public static class Builder {
      private RestContext<AbiquoApi, AbiquoAsyncApi> context;

      private String name;

      private String description;

      private List<CostCodePrice> defaultPrices = Lists.newArrayList();

      public Builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context) {
         super();
         this.context = context;
      }

      public Builder name(final String name) {
         this.name = name;
         return this;
      }

      public Builder description(final String description) {
         this.description = description;
         return this;
      }

      public Builder defaultPrices(final List<CostCodePrice> prices) {
         this.defaultPrices.addAll(prices);
         return this;
      }

      public CostCode build() {
         CostCodeDto dto = new CostCodeDto();
         dto.setName(name);
         dto.setDescription(description);
         CostCode costcode = new CostCode(context, dto);
         costcode.setDefaultPrices(defaultPrices);
         return costcode;
      }

      public static Builder fromCostCode(final CostCode in) {
         Builder builder = CostCode.builder(in.context).name(in.getName()).description(in.getDescription())
               .defaultPrices(in.getDefaultPrices());
         return builder;
      }
   }

   // Delegate methods

   public Integer getId() {
      return target.getId();
   }

   public String getName() {
      return target.getName();
   }

   public void setName(final String name) {
      target.setName(name);
   }

   public String getDescription() {
      return target.getDescription();
   }

   public void setDescription(final String description) {
      target.setDescription(description);
   }

   public List<CostCodePrice> getDefaultPrices() {
      return defaultPrices;
   }

   public void setDefaultPrices(final List<CostCodePrice> defaultPrices) {
      this.defaultPrices = defaultPrices;
   }

   @Override
   public String toString() {
      return "CostCode [id=" + getId() + ", name=" + getName() + ", description=" + getDescription() + "]";
   }
}
