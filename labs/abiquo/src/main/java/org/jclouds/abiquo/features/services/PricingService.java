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

package org.jclouds.abiquo.features.services;

import java.util.Collection;

import org.jclouds.abiquo.domain.config.CostCode;
import org.jclouds.abiquo.domain.config.CostCodeCurrency;
import org.jclouds.abiquo.domain.config.Currency;
import org.jclouds.abiquo.domain.config.PricingCostCode;
import org.jclouds.abiquo.domain.config.PricingTemplate;
import org.jclouds.abiquo.domain.config.PricingTier;
import org.jclouds.abiquo.internal.BasePricingService;

import com.google.common.base.Predicate;
import com.google.inject.ImplementedBy;

/**
 * Provides high level Abiquo administration operations.
 * 
 * @author Ignasi Barrera
 * @author Susana Acedo
 */
@ImplementedBy(BasePricingService.class)
public interface PricingService {

   /*********************** Currency ***********************/

   /**
    * Get the list of currencies.
    */
   Iterable<Currency> listCurrencies();

   /**
    * Get the list of currencies matching the given filter.
    */
   Iterable<Currency> listCurrencies(final Predicate<Currency> filter);

   /**
    * Get the first currencies that matches the given filter or
    * <code>null</code> if none is found.
    */
   Currency findCurrency(final Predicate<Currency> filter);

   /*********************** CostCode ***********************/

   /**
    * Get the list of costcodes.
    */
   Iterable<CostCode> listCostCodes();

   /**
    * Get the list of costcodes matching the given filter.
    */
   Iterable<CostCode> listCostCodes(final Predicate<CostCode> filter);

   /**
    * Get the first costcodes that matches the given filter or <code>null</code>
    * if none is found.
    */
   CostCode findCostCode(final Predicate<CostCode> filter);

   /*********************** PricingTemplate ***********************/

   /**
    * Get the list of pricingtemplates.
    */
   public Iterable<PricingTemplate> listPricingTemplates();

   /**
    * Get the list of pricingtemplates matching the given filter.
    */
   public Iterable<PricingTemplate> listPricingTemplates(final Predicate<PricingTemplate> filter);

   /**
    * Get the first pricingtemplates that matches the given filter or
    * <code>null</code> if none is found.
    */
   public PricingTemplate findPricingTemplate(final Predicate<PricingTemplate> filter);

   /*********************** CostCodeCurrency ***********************/

   /**
    * Get a cost code currency
    */
   public Iterable<CostCodeCurrency> getCostCodeCurrencies(Integer costcodeid, Integer currencyid);

   /*********************** PricingCostCode ***********************/

   /**
    * Get pricing cost codes
    */
   public Collection<PricingCostCode> getPricingCostCodes(Integer pricingTemplateId);

   /**
    * Get a pricing cost code
    */
   PricingCostCode getPricingCostCode(Integer pricingTemplateId, Integer pricingCostCodeId);

   /*********************** PricingTier ***********************/

   /**
    * Get pricing tiers
    */
   Collection<PricingTier> getPricingTiers(Integer pricingTemplateId);

   /**
    * Get a pricing tier
    */
   PricingTier getPricingTier(Integer pricingTemplateId, Integer pricingTierId);

}
