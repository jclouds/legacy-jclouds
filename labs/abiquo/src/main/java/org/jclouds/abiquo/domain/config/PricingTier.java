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

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.infrastructure.Tier;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.rest.RestContext;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.pricing.PricingTierDto;

/**
 * Associates an storage {@link Tier} with a {@link PricingTemplate}.
 * 
 * @author Susana Acedo
 */
public class PricingTier extends DomainWrapper<PricingTierDto> {
   private Tier tier;

   private PricingTemplate pricingTemplate;

   protected PricingTier(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final PricingTierDto target) {
      super(context, target);
   }

   // Builder

   public static Builder builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
         final PricingTemplate pricingtemplate, final Tier tier) {
      return new Builder(context, pricingtemplate, tier);
   }

   public static class Builder {
      private RestContext<AbiquoApi, AbiquoAsyncApi> context;

      private Integer id;

      private PricingTemplate pricingTemplate;

      private Tier tier;

      private BigDecimal price;

      public Builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final PricingTemplate pricingTemplate,
            final Tier tier) {
         super();
         this.pricingTemplate = checkNotNull(pricingTemplate, ValidationErrors.NULL_RESOURCE + PricingTemplate.class);
         this.tier = checkNotNull(tier, ValidationErrors.NULL_RESOURCE + Tier.class);
         this.context = context;
      }

      public Builder price(final BigDecimal price) {
         this.price = price;
         return this;
      }

      public PricingTier build() {
         PricingTierDto dto = new PricingTierDto();
         dto.setId(id);
         dto.setPrice(price);

         RESTLink link = tier.unwrap().searchLink("edit");
         checkNotNull(link, ValidationErrors.MISSING_REQUIRED_LINK);
         dto.addLink(new RESTLink("tier", link.getHref()));

         PricingTier pricingTier = new PricingTier(context, dto);
         pricingTier.pricingTemplate = pricingTemplate;
         pricingTier.tier = tier;

         return pricingTier;
      }

      public static Builder fromPricingTier(final PricingTier in) {
         return PricingTier.builder(in.context, in.pricingTemplate, in.tier).price(in.getPrice());
      }
   }

   // Delegate methods

   public Integer getId() {
      return target.getId();
   }

   public BigDecimal getPrice() {
      return target.getPrice();
   }

   @Override
   public String toString() {
      return "PricingTier [id=" + getId() + ", price=" + getPrice() + "]";
   }
}
