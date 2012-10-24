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
import static org.jclouds.abiquo.util.Assert.assertHasError;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.predicates.config.PricingPredicates;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Live integration tests for the {@link CostCode} domain class.
 * 
 * @author Susana Acedo
 */
@Test(groups = "api", testName = "CostCodeLiveApiTest")
public class CostCodeLiveApiTest extends BaseAbiquoApiLiveApiTest {
   private CostCode costcode;

   private Currency currency;

   private CostCodePrice costcodeprice;

   private List<CostCodePrice> defaultPrices;

   @BeforeClass
   public void setupCostCode() {
      currency = Currency.builder(env.context.getApiContext()).name(PREFIX + "test-currency").symbol("test-$")
            .digits(2).build();
      currency.save();

      costcode = CostCode.builder(env.context.getApiContext()).name(PREFIX + "test-costcode")
            .description("description").build();

      costcode.save();
   }

   @AfterClass
   public void tearDownCostCode() {
      currency.delete();
      costcode.delete();
   }

   public void testCreateRepeated() {
      CostCode repeated = CostCode.Builder.fromCostCode(costcode).build();

      try {
         repeated.save();
         fail("Should not be able to create costcodes with the same name");
      } catch (AbiquoException ex) {
         assertHasError(ex, Status.CONFLICT, "COSTCODE-2");
      }
   }

   public void testUpdate() {
      costcode.setName(PREFIX + "costcode-updated");
      costcode.update();

      CostCode apiCostCode = env.context.getPricingService().findCostCode(
            PricingPredicates.costCode(PREFIX + "costcode-updated"));

      assertNotNull(apiCostCode);
      assertEquals(PREFIX + "costcode-updated", apiCostCode.getName());

   }

   public void testCreateCostCodewithDefaultPrices() {
      CostCode costcode2 = CostCode.builder(env.context.getApiContext()).name(PREFIX + "ccdefaultprice")
            .description("description").build();

      costcodeprice = new CostCodePrice(currency, new BigDecimal(100));
      this.defaultPrices = new ArrayList<CostCodePrice>();
      defaultPrices.add(costcodeprice);
      costcode2.setDefaultPrices(defaultPrices);
      // When a cost code is created it is also created a costcodecurrency with
      // price 0 and after
      // that if a list of prices(CostCodePrice) has been sent this costcode is
      // updated with the
      // new price
      costcode2.save();

      // check that costcode has been created
      CostCode apiCostCode = env.context.getPricingService().findCostCode(
            PricingPredicates.costCode(PREFIX + "ccdefaultprice"));

      assertNotNull(apiCostCode);
      assertEquals(PREFIX + "ccdefaultprice", apiCostCode.getName());

      // check that the price has been modified in the
      Iterable<CostCodeCurrency> costcodecurrencies = env.context.getPricingService().getCostCodeCurrencies(
            costcode2.getId(), currency.getId());
      for (CostCodeCurrency costcodecurrency : costcodecurrencies) {
         assertEquals(costcodecurrency.getPrice().compareTo(new BigDecimal(100)), 0);
      }

      costcode2.delete();
   }
}
