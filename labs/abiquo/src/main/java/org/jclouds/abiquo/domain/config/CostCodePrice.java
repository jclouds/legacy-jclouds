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

import java.math.BigDecimal;

/**
 * Gives a price to a {@link CostCode}.
 * 
 * @author Susana Acedo
 */
public class CostCodePrice
{
    private Currency currency;

    private BigDecimal price;

    public CostCodePrice(final Currency currency, final BigDecimal price)
    {
        super();
        this.currency = currency;
        this.price = price;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public void setCurrency(final Currency currency)
    {
        this.currency = currency;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(final BigDecimal price)
    {
        this.price = price;
    }

    @Override
    public String toString()
    {
        return "CostCodePrice [currency=" + currency + ", price=" + price + "]";
    }

}
