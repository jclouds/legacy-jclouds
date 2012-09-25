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

package org.jclouds.abiquo.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.abiquo.domain.DomainWrapper.wrap;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.config.CostCode;
import org.jclouds.abiquo.domain.config.CostCodeCurrency;
import org.jclouds.abiquo.domain.config.Currency;
import org.jclouds.abiquo.domain.config.PricingCostCode;
import org.jclouds.abiquo.domain.config.PricingTemplate;
import org.jclouds.abiquo.domain.config.PricingTier;
import org.jclouds.abiquo.features.services.PricingService;
import org.jclouds.abiquo.strategy.config.ListCostCodes;
import org.jclouds.abiquo.strategy.config.ListCurrencies;
import org.jclouds.abiquo.strategy.config.ListPricingTemplates;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.pricing.CostCodeCurrenciesDto;
import com.abiquo.server.core.pricing.PricingCostCodeDto;
import com.abiquo.server.core.pricing.PricingCostCodesDto;
import com.abiquo.server.core.pricing.PricingTierDto;
import com.abiquo.server.core.pricing.PricingTiersDto;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Provides high level Abiquo administration operations.
 * 
 * @author Ignasi Barrera
 * @author Susana Acedo
 */
@Singleton
public class BasePricingService implements PricingService
{
    @VisibleForTesting
    protected RestContext<AbiquoApi, AbiquoAsyncApi> context;

    @VisibleForTesting
    protected final ListCurrencies listCurrencies;

    @VisibleForTesting
    protected final ListCostCodes listCostCodes;

    @VisibleForTesting
    protected final ListPricingTemplates listPricingTemplates;

    @Inject
    protected BasePricingService(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
        final ListCurrencies listCurrencies, final ListCostCodes listCostCodes,
        final ListPricingTemplates listPricingTemplates)
    {
        this.context = checkNotNull(context, "context");
        this.listCurrencies = checkNotNull(listCurrencies, "listCurrencies");
        this.listCostCodes = checkNotNull(listCostCodes, "listCostCodes");
        this.listPricingTemplates = checkNotNull(listPricingTemplates, "listPricingTemplates");
    }

    /*********************** Currency ********************** */

    @Override
    public Iterable<Currency> listCurrencies()
    {
        return listCurrencies.execute();
    }

    @Override
    public Iterable<Currency> listCurrencies(final Predicate<Currency> filter)
    {
        return listCurrencies.execute(filter);
    }

    @Override
    public Currency findCurrency(final Predicate<Currency> filter)
    {
        return Iterables.getFirst(listCurrencies(filter), null);
    }

    /*********************** CostCode ********************** */

    @Override
    public Iterable<CostCode> listCostCodes()
    {
        return listCostCodes.execute();
    }

    @Override
    public Iterable<CostCode> listCostCodes(final Predicate<CostCode> filter)
    {
        return listCostCodes.execute(filter);
    }

    @Override
    public CostCode findCostCode(final Predicate<CostCode> filter)
    {
        return Iterables.getFirst(listCostCodes(filter), null);
    }

    /*********************** PricingTemplate ********************** */

    @Override
    public Iterable<PricingTemplate> listPricingTemplates()
    {
        return listPricingTemplates.execute();
    }

    @Override
    public Iterable<PricingTemplate> listPricingTemplates(final Predicate<PricingTemplate> filter)
    {
        return listPricingTemplates.execute(filter);
    }

    @Override
    public PricingTemplate findPricingTemplate(final Predicate<PricingTemplate> filter)
    {
        return Iterables.getFirst(listPricingTemplates(filter), null);
    }

    /*********************** CostCodeCurrency ********************** */

    @Override
    public Iterable<CostCodeCurrency> getCostCodeCurrencies(final Integer costcodeId,
        final Integer currencyId)
    {
        CostCodeCurrenciesDto result =
            context.getApi().getPricingApi().getCostCodeCurrencies(costcodeId, currencyId);
        return wrap(context, CostCodeCurrency.class, result.getCollection());
    }

    /*********************** Pricing Cost Code ********************** */

    @Override
    public Collection<PricingCostCode> getPricingCostCodes(final Integer pricingTemplateId)
    {
        PricingCostCodesDto result =
            context.getApi().getPricingApi().getPricingCostCodes(pricingTemplateId);
        return wrap(context, PricingCostCode.class, result.getCollection());
    }

    @Override
    public PricingCostCode getPricingCostCode(final Integer pricingTemplateId,
        final Integer pricingCostCodeId)
    {
        PricingCostCodeDto pricingcostcode =
            context.getApi().getPricingApi()
                .getPricingCostCode(pricingTemplateId, pricingCostCodeId);
        return wrap(context, PricingCostCode.class, pricingcostcode);
    }

    /*********************** Pricing Tier********************** */

    @Override
    public Collection<PricingTier> getPricingTiers(final Integer pricingTemplateId)
    {
        PricingTiersDto result =
            context.getApi().getPricingApi().getPricingTiers(pricingTemplateId);
        return wrap(context, PricingTier.class, result.getCollection());
    }

    @Override
    public PricingTier getPricingTier(final Integer pricingTemplateId, final Integer pricingTierId)
    {
        PricingTierDto pricingtier =
            context.getApi().getPricingApi().getPricingTier(pricingTemplateId, pricingTierId);
        return wrap(context, PricingTier.class, pricingtier);
    }
}
