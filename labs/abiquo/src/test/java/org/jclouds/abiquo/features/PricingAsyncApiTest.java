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

package org.jclouds.abiquo.features;

import static org.jclouds.abiquo.domain.DomainUtils.withHeader;
import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.abiquo.domain.PricingResources;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.abiquo.server.core.pricing.CostCodeCurrenciesDto;
import com.abiquo.server.core.pricing.CostCodeDto;
import com.abiquo.server.core.pricing.CostCodesDto;
import com.abiquo.server.core.pricing.CurrenciesDto;
import com.abiquo.server.core.pricing.CurrencyDto;
import com.abiquo.server.core.pricing.PricingCostCodeDto;
import com.abiquo.server.core.pricing.PricingCostCodesDto;
import com.abiquo.server.core.pricing.PricingTemplateDto;
import com.abiquo.server.core.pricing.PricingTemplatesDto;
import com.abiquo.server.core.pricing.PricingTierDto;
import com.abiquo.server.core.pricing.PricingTiersDto;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;

/**
 * Tests annotation parsing of {@code PricingAsyncApi}.
 * 
 * @author Ignasi Barrera
 * @author Susana Acedo
 */
@Test(groups = "unit", singleThreaded = true, testName = "PricingAsyncApiTest")
public class PricingAsyncApiTest extends BaseAbiquoAsyncApiTest<PricingAsyncApi> {
   /*********************** Currency ***********************/

   public void testListCurrencies() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "listCurrencies");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.of()));

      assertRequestLineEquals(request, "GET http://localhost/api/config/currencies HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + CurrenciesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetCurrency() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "getCurrency", Integer.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of(1)));

      assertRequestLineEquals(request, "GET http://localhost/api/config/currencies/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + CurrencyDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCreateCurrency() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "createCurrency", CurrencyDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(PricingResources.currencyPost())));

      assertRequestLineEquals(request, "POST http://localhost/api/config/currencies HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + CurrencyDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(PricingResources.currencyPostPayload()), CurrencyDto.class,
            CurrencyDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testUpdateCurrency() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "updateCurrency", CurrencyDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(PricingResources.currencyPut())));

      assertRequestLineEquals(request, "PUT http://localhost/api/config/currencies/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + CurrencyDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(PricingResources.currencyPutPayload()), CurrencyDto.class,
            CurrencyDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteCurrency() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "deleteCurrency", CurrencyDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(PricingResources.currencyPut())));

      assertRequestLineEquals(request, "DELETE http://localhost/api/config/currencies/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Cost Code ***********************/

   public void testListCostCodes() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "listCostCodes");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.of()));

      assertRequestLineEquals(request, "GET http://localhost/api/config/costcodes HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + CostCodesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetCostCode() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "getCostCode", Integer.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of(1)));

      assertRequestLineEquals(request, "GET http://localhost/api/config/costcodes/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + CostCodeDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCreateCostCode() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "createCostCode", CostCodeDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(PricingResources.costcodePost())));

      assertRequestLineEquals(request, "POST http://localhost/api/config/costcodes HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + CostCodeDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(PricingResources.costcodePostPayload()), CostCodeDto.class,
            CostCodeDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testUpdateCostCode() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "updateCostCode", CostCodeDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(PricingResources.costcodePut())));

      assertRequestLineEquals(request, "PUT http://localhost/api/config/costcodes/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + CostCodeDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(PricingResources.costcodePutPayload()), CostCodeDto.class,
            CostCodeDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteCostCode() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "deleteCostCode", CostCodeDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(PricingResources.costcodePut())));

      assertRequestLineEquals(request, "DELETE http://localhost/api/config/costcodes/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Pricing Template ***********************/

   public void testListPricingTemplates() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "listPricingTemplates");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.of()));

      assertRequestLineEquals(request, "GET http://localhost/api/config/pricingtemplates HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + PricingTemplatesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetPricingTemplate() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "getPricingTemplate", Integer.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of(1)));

      assertRequestLineEquals(request, "GET http://localhost/api/config/pricingtemplates/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + PricingTemplateDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCreatePricingTemplate() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "createPricingTemplate", PricingTemplateDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(PricingResources.pricingtemplatePost())));

      assertRequestLineEquals(request, "POST http://localhost/api/config/pricingtemplates HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + PricingTemplateDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(PricingResources.pricingtemplatePostPayload()), PricingTemplateDto.class,
            PricingTemplateDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testUpdatePricingTemplate() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "updatePricingTemplate", PricingTemplateDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(PricingResources.pricingtemplatePut())));

      assertRequestLineEquals(request, "PUT http://localhost/api/config/pricingtemplates/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + PricingTemplateDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(PricingResources.pricingtemplatePutPayload()), PricingTemplateDto.class,
            PricingTemplateDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeletePricingTemplate() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "deletePricingTemplate", PricingTemplateDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(PricingResources.pricingtemplatePut())));

      assertRequestLineEquals(request, "DELETE http://localhost/api/config/pricingtemplates/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Cost Code Currency ***********************/

   public void testGetCostCodeCurrencies() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "getCostCodeCurrencies", Integer.class, Integer.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of(1, 1)));

      assertRequestLineEquals(request, "GET http://localhost/api/config/costcodes/1/currencies?idCurrency=1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + CostCodeCurrenciesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testUpdateCostCodeCurrencies() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "updateCostCodeCurrencies", Integer.class,
            CostCodeCurrenciesDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(1, PricingResources.costcodecurrencyPut())));

      assertRequestLineEquals(request, "PUT http://localhost/api/config/costcodes/1/currencies HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + CostCodeCurrenciesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(PricingResources.costcodecurrencyPutPayload()),
            CostCodeCurrenciesDto.class, CostCodeCurrenciesDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Pricing Cost Code ***********************/

   public void testGetPricingCostCodes() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "getPricingCostCodes", Integer.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of(1)));

      assertRequestLineEquals(request, "GET http://localhost/api/config/pricingtemplates/1/costcodes HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + PricingCostCodesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetPricingCostCode() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "getPricingCostCode", Integer.class, Integer.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of(1, 1)));

      assertRequestLineEquals(request, "GET http://localhost/api/config/pricingtemplates/1/costcodes/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + PricingCostCodeDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testUpdatePricingCostCode() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "updatePricingCostCode", PricingCostCodeDto.class,
            Integer.class, Integer.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(PricingResources.pricingCostcodePut(), 1, 1)));

      assertRequestLineEquals(request, "PUT http://localhost/api/config/pricingtemplates/1/costcodes/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + PricingCostCodeDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(PricingResources.pricingCostCodePutPayload()), PricingCostCodeDto.class,
            PricingCostCodeDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Pricing Tier ***************************/

   public void testGetPricingTiers() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "getPricingTiers", Integer.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of(1)));

      assertRequestLineEquals(request, "GET http://localhost/api/config/pricingtemplates/1/tiers HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + PricingTiersDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetPricingTier() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "getPricingTier", Integer.class, Integer.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of(1, 1)));

      assertRequestLineEquals(request, "GET http://localhost/api/config/pricingtemplates/1/tiers/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + PricingTierDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testUpdatePricingTier() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(PricingAsyncApi.class, "updatePricingTier", PricingTierDto.class, Integer.class,
            Integer.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(PricingResources.pricingTierPut(), 1, 2)));

      assertRequestLineEquals(request, "PUT http://localhost/api/config/pricingtemplates/1/tiers/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + PricingTierDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(PricingResources.pricingTierPutPayload()), PricingTierDto.class,
            PricingTierDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }
}
