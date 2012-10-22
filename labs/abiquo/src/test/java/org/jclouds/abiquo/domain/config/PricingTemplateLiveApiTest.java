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
import java.util.Collection;
import java.util.Date;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.predicates.config.PricingPredicates;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.PricingPeriod;

/**
 * Live integration tests for the {@link PricingTemplate} domain class.
 * 
 * @author Susana Acedo
 */
@Test(groups = "api", testName = "PricingTemplateLiveApiTest")
public class PricingTemplateLiveApiTest extends BaseAbiquoApiLiveApiTest {
   private PricingTemplate pricingTemplate;

   private Currency currency;

   private CostCode costcode;

   private BigDecimal zero = new BigDecimal(0);

   @BeforeClass
   public void setupPricingTemplate() {
      Iterable<Currency> currencies = env.context.getPricingService().listCurrencies();
      currency = currencies.iterator().next();

      costcode = CostCode.builder(env.context.getApiContext()).name(PREFIX + "test-costcode")
            .description("description").build();

      costcode.save();

      pricingTemplate = PricingTemplate.builder(env.context.getApiContext(), currency).name("pricing_template")
            .description("description").hdGB(zero).standingChargePeriod(zero).vlan(zero)
            .chargingPeriod(PricingPeriod.MONTH).minimumChargePeriod(zero).showChangesBefore(true)
            .showMinimumCharge(false).minimumCharge(PricingPeriod.WEEK).publicIp(zero).vcpu(zero).memoryGB(zero)
            .defaultTemplate(true).lastUpdate(new Date()).build();

      pricingTemplate.save();
   }

   @AfterClass
   public void tearDownPricingTemplate() {
      pricingTemplate.delete();
      costcode.delete();
   }

   public void testCreateRepeated() {
      PricingTemplate repeated = PricingTemplate.Builder.fromPricingTemplate(pricingTemplate).build();

      try {
         repeated.save();
         fail("Should not be able to create pricingtemplates with the same name");
      } catch (AbiquoException ex) {
         assertHasError(ex, Status.CONFLICT, "PRICINGTEMPLATE-2");
      }
   }

   public void testUpdate() {
      pricingTemplate.setName(PREFIX + "pt-updated");
      pricingTemplate.update();

      PricingTemplate apiPricingTemplate = env.context.getPricingService().findPricingTemplate(
            PricingPredicates.pricingTemplate(PREFIX + "pt-updated"));

      assertNotNull(apiPricingTemplate);
      assertEquals(PREFIX + "pt-updated", apiPricingTemplate.getName());

   }

   // when a pricing template is created, pricing cost codes for each existent
   // cost code are also
   // created with price 0
   public void getPricingCostCodes() {
      Collection<PricingCostCode> pricingCostCodes = env.context.getPricingService().getPricingCostCodes(
            pricingTemplate.getId());
      assertEquals(pricingCostCodes.size(), 1);
      assertNotNull(pricingCostCodes);
      for (PricingCostCode pc : pricingCostCodes) {
         assertEquals(pc.getPrice().compareTo(zero), 0);
      }
   }

   // when a pricing template is created, pricing tiers are also created with
   // price 0
   public void getPricingTiers() {
      Collection<PricingTier> pricingTiers =

      env.context.getPricingService().getPricingTiers(pricingTemplate.getId());
      assertEquals(pricingTiers.size(), 4);
      assertNotNull(pricingTiers);
      for (PricingTier pt : pricingTiers) {
         assertEquals(pt.getPrice().compareTo(zero), 0);
      }
   }
}
