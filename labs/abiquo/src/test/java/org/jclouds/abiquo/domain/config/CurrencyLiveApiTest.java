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

import static org.jclouds.abiquo.reference.AbiquoTestConstants.PREFIX;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.predicates.config.PricingPredicates;
import org.testng.annotations.Test;

/**
 * Live integration tests for the {@link Currency} domain class.
 * 
 * @author Susana Acedo
 */
@Test(groups = "api", testName = "CurrencyLiveApiTest")
public class CurrencyLiveApiTest extends BaseAbiquoApiLiveApiTest
{
    public void testCreateAndGet()
    {
        Currency currency =
            Currency.builder(env.context.getApiContext()).name(PREFIX + "test-currency")
                .symbol("test-$").digits(2).build();
        currency.save();

        Currency apiCurrency =
            env.context.getPricingService().findCurrency(
                PricingPredicates.currency(PREFIX + "test-currency"));
        assertNotNull(apiCurrency);
        assertEquals(currency.getName(), apiCurrency.getName());

        apiCurrency.delete();
    }

    @Test(dependsOnMethods = "testCreateAndGet")
    public void testUpdate()
    {
        Iterable<Currency> currencies = env.context.getPricingService().listCurrencies();
        assertNotNull(currencies);

        Currency currency = currencies.iterator().next();
        String name = currency.getName();

        currency.setName(PREFIX + "t-currency-upd");
        currency.update();

        Currency apiCurrency =
            env.context.getPricingService().findCurrency(
                PricingPredicates.currency(PREFIX + "t-currency-upd"));

        assertNotNull(apiCurrency);
        assertEquals(PREFIX + "t-currency-upd", apiCurrency.getName());

        currency.setName(name);
        currency.update();
    }
}
