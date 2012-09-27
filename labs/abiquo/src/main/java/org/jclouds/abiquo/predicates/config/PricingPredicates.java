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

package org.jclouds.abiquo.predicates.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import org.jclouds.abiquo.domain.config.CostCode;
import org.jclouds.abiquo.domain.config.Currency;
import org.jclouds.abiquo.domain.config.PricingTemplate;

import com.google.common.base.Predicate;

/**
 * Container for pricing related filters.
 * 
 * @author Ignasi Barrera
 * @author Susana Acedo
 */
public class PricingPredicates
{
    public static Predicate<Currency> currency(final String... names)
    {
        checkNotNull(names, "names must be defined");

        return new Predicate<Currency>()
        {
            @Override
            public boolean apply(final Currency currency)
            {
                return Arrays.asList(names).contains(currency.getName());
            }
        };
    }

    public static Predicate<CostCode> costCode(final String... names)
    {
        checkNotNull(names, "names must be defined");

        return new Predicate<CostCode>()
        {
            @Override
            public boolean apply(final CostCode costcode)
            {
                return Arrays.asList(names).contains(costcode.getName());
            }
        };
    }

    public static Predicate<PricingTemplate> pricingTemplate(final String... names)
    {
        checkNotNull(names, "names must be defined");

        return new Predicate<PricingTemplate>()
        {
            @Override
            public boolean apply(final PricingTemplate pricingTemplate)
            {
                return Arrays.asList(names).contains(pricingTemplate.getName());
            }
        };
    }
}
