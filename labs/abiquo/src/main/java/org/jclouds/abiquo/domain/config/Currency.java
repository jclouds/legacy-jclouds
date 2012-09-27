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

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.pricing.CurrencyDto;

/**
 * Adds high level functionality to {@link CurrencyDto}.
 * 
 * @author Ignasi Barrera
 * @author Susana Acedo
 */
public class Currency extends DomainWrapper<CurrencyDto>
{

    /**
     * Constructor to be used only by the builder. This resource cannot be created.
     */
    private Currency(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final CurrencyDto target)
    {
        super(context, target);
    }

    // Domain operations

    public void delete()
    {
        context.getApi().getPricingApi().deleteCurrency(target);
        target = null;
    }

    public void save()
    {
        target = context.getApi().getPricingApi().createCurrency(target);
    }

    public void update()
    {
        target = context.getApi().getPricingApi().updateCurrency(target);
    }

    // Builder

    public static Builder builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context)
    {
        return new Builder(context);
    }

    public static class Builder
    {
        private RestContext<AbiquoApi, AbiquoAsyncApi> context;

        private String name;

        private String symbol;

        private int digits;

        public Builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context)
        {
            super();
            this.context = context;
        }

        public Builder name(final String name)
        {
            this.name = name;
            return this;
        }

        public Builder symbol(final String symbol)
        {
            this.symbol = symbol;
            return this;
        }

        public Builder digits(final int digits)
        {
            this.digits = digits;
            return this;
        }

        public Currency build()
        {
            CurrencyDto dto = new CurrencyDto();
            dto.setName(name);
            dto.setSymbol(symbol);
            dto.setDigits(digits);
            Currency currency = new Currency(context, dto);

            return currency;
        }

        public static Builder fromCurrency(final Currency in)
        {
            Builder builder =
                Currency.builder(in.context).name(in.getName()).symbol(in.getSymbol())
                    .digits(in.getDigits());

            return builder;
        }
    }

    // Delegate methods

    public Integer getId()
    {
        return target.getId();
    }

    public String getName()
    {
        return target.getName();
    }

    public void setName(final String name)
    {
        target.setName(name);
    }

    public String getSymbol()
    {
        return target.getSymbol();
    }

    public void setSymbol(final String symbol)
    {
        target.setSymbol(symbol);
    }

    public int getDigits()
    {
        return target.getDigits();
    }

    public void setDigits(final int digits)
    {
        target.setDigits(digits);
    }

    @Override
    public String toString()
    {
        return "Currency [id=" + getId() + ", name=" + getName() + ", symbol=" + getSymbol()
            + ", digits=" + getDigits() + "]";
    }
}
