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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.config.Currency;
import org.jclouds.abiquo.features.services.PricingService;
import org.jclouds.abiquo.strategy.config.ListCurrencies;
import org.jclouds.rest.RestContext;

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

    @Inject
    protected BasePricingService(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
        final ListCurrencies listCurrencies)
    {
        this.context = checkNotNull(context, "context");
        this.listCurrencies = checkNotNull(listCurrencies, "listCurrencies");
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
}
