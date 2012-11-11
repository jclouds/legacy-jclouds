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

package org.jclouds.abiquo.domain;

import static org.jclouds.abiquo.domain.DomainUtils.link;

import java.math.BigDecimal;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.pricing.CostCodeCurrenciesDto;
import com.abiquo.server.core.pricing.CostCodeCurrencyDto;
import com.abiquo.server.core.pricing.CostCodeDto;
import com.abiquo.server.core.pricing.CurrencyDto;
import com.abiquo.server.core.pricing.PricingCostCodeDto;
import com.abiquo.server.core.pricing.PricingTemplateDto;
import com.abiquo.server.core.pricing.PricingTierDto;

/**
 * Enterprise domain utilities.
 * 
 * @author Ignasi Barrera
 * @author Susana Acedo
 */
public class PricingResources {

   public static CurrencyDto currencyPost() {
      CurrencyDto currency = new CurrencyDto();
      currency.setName("yuan");
      currency.setSymbol("DUMMY");
      currency.setDigits(3);
      return currency;
   }

   public static CurrencyDto currencyPut() {
      CurrencyDto currency = new CurrencyDto();
      currency.setName("yuan");
      currency.setSymbol("DUMMY");
      currency.setDigits(3);
      currency.setId(1);
      currency.addLink(new RESTLink("edit", "http://localhost/api/config/currencies/1"));
      return currency;
   }

   public static String currencyPostPayload() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<currency>");
      buffer.append("<symbol>DUMMY</symbol>");
      buffer.append("<digits>3</digits>");
      buffer.append("<name>yuan</name>");
      buffer.append("</currency>");
      return buffer.toString();
   }

   public static String currencyPutPayload() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<currency>");
      buffer.append(link("/config/currencies/1", "edit"));
      buffer.append("<symbol>DUMMY</symbol>");
      buffer.append("<digits>3</digits>");
      buffer.append("<id>1</id>");
      buffer.append("<name>yuan</name>");
      buffer.append("</currency>");
      return buffer.toString();
   }

   public static Object costcodePost() {
      CostCodeDto costcode = new CostCodeDto();
      costcode.setName("cost code");
      costcode.setDescription("description");
      return costcode;
   }

   public static Object costcodePut() {
      CostCodeDto costcode = new CostCodeDto();
      costcode.setName("cost code");
      costcode.setDescription("description");
      costcode.setId(1);
      costcode.addLink(new RESTLink("edit", "http://localhost/api/config/costcodes/1"));
      return costcode;
   }

   public static String costcodePostPayload() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<costCode>");
      buffer.append("<name>cost code</name>");
      buffer.append("<description>description</description>");
      buffer.append("</costCode>");
      return buffer.toString();
   }

   public static String costcodePutPayload() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<costCode>");
      buffer.append(link("/config/costcodes/1", "edit"));
      buffer.append("<description>description</description>");
      buffer.append("<id>1</id>");
      buffer.append("<name>cost code</name>");
      buffer.append("</costCode>");
      return buffer.toString();
   }

   public static Object pricingtemplatePost() {
      PricingTemplateDto pricingtemplate = new PricingTemplateDto();
      pricingtemplate.setName("pricing template");
      pricingtemplate.setDescription("pt_description");
      pricingtemplate.setHdGB(new BigDecimal(0));
      pricingtemplate.setStandingChargePeriod(new BigDecimal(0));
      pricingtemplate.setVlan(new BigDecimal(0));
      pricingtemplate.setChargingPeriod(1);
      pricingtemplate.setMinimumChargePeriod(new BigDecimal(0));
      pricingtemplate.setShowChangesBefore(true);
      pricingtemplate.setShowMinimumCharge(false);
      pricingtemplate.setMinimumCharge(2);
      pricingtemplate.setPublicIp(new BigDecimal(0));
      pricingtemplate.setVcpu(new BigDecimal(0));
      pricingtemplate.setMemoryGB(new BigDecimal(0));
      pricingtemplate.setDefaultTemplate(true);
      pricingtemplate.addLink(new RESTLink("currency", "http://localhost/api/config/currencies/1"));
      return pricingtemplate;
   }

   public static Object pricingtemplatePut() {
      PricingTemplateDto pricingtemplate = new PricingTemplateDto();
      pricingtemplate.setName("pricing template");
      pricingtemplate.setDescription("pt_description");
      pricingtemplate.setHdGB(new BigDecimal(0));
      pricingtemplate.setStandingChargePeriod(new BigDecimal(0));
      pricingtemplate.setVlan(new BigDecimal(0));
      pricingtemplate.setChargingPeriod(1);
      pricingtemplate.setMinimumChargePeriod(new BigDecimal(0));
      pricingtemplate.setShowChangesBefore(true);
      pricingtemplate.setShowMinimumCharge(false);
      pricingtemplate.setMinimumCharge(2);
      pricingtemplate.setPublicIp(new BigDecimal(0));
      pricingtemplate.setVcpu(new BigDecimal(0));
      pricingtemplate.setMemoryGB(new BigDecimal(0));
      pricingtemplate.setDefaultTemplate(true);
      pricingtemplate.addLink(new RESTLink("currency", "http://localhost/api/config/currencies/1"));
      pricingtemplate.setId(1);
      pricingtemplate.addLink(new RESTLink("edit", "http://localhost/api/config/pricingtemplates/1"));
      return pricingtemplate;
   }

   public static String pricingtemplatePostPayload() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<pricingTemplate>");
      buffer.append("<name>pricing template</name>");
      buffer.append("<description>pt_description</description>");
      buffer.append("<hdGB>0</hdGB>");
      buffer.append("<standingChargePeriod>0</standingChargePeriod>");
      buffer.append("<vlan>0</vlan>");
      buffer.append("<chargingPeriod>1</chargingPeriod>");
      buffer.append("<minimumChargePeriod>0</minimumChargePeriod>");
      buffer.append("<showChangesBefore>true</showChangesBefore>");
      buffer.append("<showMinimumCharge>false</showMinimumCharge>");
      buffer.append("<minimumCharge>2</minimumCharge>");
      buffer.append("<memoryGB>0</memoryGB>");
      buffer.append("<publicIp>0</publicIp>");
      buffer.append("<vcpu>0</vcpu>");
      buffer.append("<memoryMB>0</memoryMB>");
      buffer.append("<defaultTemplate>true</defaultTemplate>");
      buffer.append("<link href='http://localhost/api/config/currencies/1' rel='currency'/>");
      buffer.append("</pricingTemplate>");
      return buffer.toString();
   }

   public static String pricingtemplatePutPayload() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<pricingTemplate>");
      buffer.append("<name>pricing template</name>");
      buffer.append("<description>pt_description</description>");
      buffer.append("<hdGB>0</hdGB>");
      buffer.append("<standingChargePeriod>0</standingChargePeriod>");
      buffer.append("<vlan>0</vlan>");
      buffer.append("<chargingPeriod>1</chargingPeriod>");
      buffer.append("<minimumChargePeriod>0</minimumChargePeriod>");
      buffer.append("<showChangesBefore>true</showChangesBefore>");
      buffer.append("<showMinimumCharge>false</showMinimumCharge>");
      buffer.append("<minimumCharge>2</minimumCharge>");
      buffer.append("<memoryGB>0</memoryGB>");
      buffer.append("<publicIp>0</publicIp>");
      buffer.append("<vcpu>0</vcpu>");
      buffer.append("<memoryMB>0</memoryMB>");
      buffer.append("<defaultTemplate>true</defaultTemplate>");
      buffer.append("<link href='http://localhost/api/config/currencies/1' rel='currency'/>");
      buffer.append("<id>1</id>");
      buffer.append(link("/config/pricingtemplates/1", "edit"));
      buffer.append("</pricingTemplate>");
      return buffer.toString();
   }

   public static Object costcodecurrencyPut() {
      CostCodeCurrencyDto costcodecurrency = new CostCodeCurrencyDto();
      costcodecurrency.addLink(new RESTLink("edit", "http://localhost/api/config/costcodes/1/currencies"));
      costcodecurrency.addLink(new RESTLink("currency", "http://localhost/api/config/currencies/1"));
      costcodecurrency.setPrice(new BigDecimal("300"));
      CostCodeCurrenciesDto costcodecurrencies = new CostCodeCurrenciesDto();
      costcodecurrencies.add(costcodecurrency);
      return costcodecurrencies;
   }

   public static String costcodecurrencyPutPayload() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<costCodeCurrencies>");
      buffer.append("<costCodeCurrency>");
      buffer.append("<price>300</price>");
      buffer.append("<link href='http://localhost/api/config/costcodes/1/currencies' rel='edit'/>");
      buffer.append("<link href='http://localhost/api/config/currencies/1' rel='currency'/>");
      buffer.append("</costCodeCurrency>");

      buffer.append("</costCodeCurrencies>");
      return buffer.toString();
   }

   public static Object pricingCostcodePut() {
      PricingCostCodeDto pricingcostcode = new PricingCostCodeDto();
      pricingcostcode.setId(1);
      pricingcostcode.setPrice(new BigDecimal("400"));
      pricingcostcode.addLink(new RESTLink("costcode", "http://localhost/api/config/costcodes/1"));
      pricingcostcode.addLink(new RESTLink("pricingtemplate", "http://localhost/api/config/pricingtemplates/1"));
      pricingcostcode.addLink(new RESTLink("edit", "http://localhost/api/config/pricingtemplates/1/costcodes/1"));
      return pricingcostcode;
   }

   public static String pricingCostCodePutPayload() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<pricingCostCode>");
      buffer.append("<link href='http://localhost/api/config/costcodes/1' rel='costcode'/>");
      buffer.append("<link href='http://localhost/api/config/pricingtemplates/1' rel='pricingtemplate'/>");
      buffer.append("<price>400</price>");
      buffer.append("<id>1</id>");
      buffer.append(link("/config/pricingtemplates/1/costcodes/1", "edit"));
      buffer.append("</pricingCostCode>");
      return buffer.toString();
   }

   public static Object pricingTierPut() {
      PricingTierDto pricingtier = new PricingTierDto();
      pricingtier.setId(1);
      pricingtier.setPrice(new BigDecimal("600"));
      pricingtier.addLink(new RESTLink("tier", "http://localhost/api/admin/datacenters/1/storage/tiers/2"));
      pricingtier.addLink(new RESTLink("pricingtemplate", "http://localhost/api/config/pricingtemplates/1"));
      pricingtier.addLink(new RESTLink("edit", "http://localhost/api/config/pricingtemplates/1/tiers/2"));
      return pricingtier;
   }

   public static String pricingTierPutPayload() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<pricingTier>");
      buffer.append("<link href='http://localhost/api/admin/datacenters/1/storage/tiers/2' rel='tier'/>");
      buffer.append("<link href='http://localhost/api/config/pricingtemplates/1' rel='pricingtemplate'/>");
      buffer.append("<price>600</price>");
      buffer.append("<id>1</id>");
      buffer.append(link("/config/pricingtemplates/1/tiers/2", "edit"));
      buffer.append("</pricingTier>");
      return buffer.toString();
   }

}
