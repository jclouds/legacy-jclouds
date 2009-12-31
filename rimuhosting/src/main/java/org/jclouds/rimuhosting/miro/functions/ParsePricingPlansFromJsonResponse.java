/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.rimuhosting.miro.functions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rimuhosting.miro.domain.PricingPlan;
import org.jclouds.rimuhosting.miro.domain.internal.RimuHostingResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.SortedSet;

/**
 * @author Ivan Meredith
 */
@Singleton
public class ParsePricingPlansFromJsonResponse extends ParseJson<SortedSet<PricingPlan>> {
   @Inject
   public ParsePricingPlansFromJsonResponse(Gson gson) {
      super(gson);
   }

   private static class PlansResponse extends RimuHostingResponse {
      private SortedSet<PricingPlan> pricing_plan_infos;

      public SortedSet<PricingPlan> getPricingPlanInfos() {
         return pricing_plan_infos;
      }

      public void setPricingPlanInfos(SortedSet<PricingPlan> pricing_plan_infos) {
         this.pricing_plan_infos = pricing_plan_infos;
      }
   }
   @Override
   protected SortedSet<PricingPlan> apply(InputStream stream) {
      Type setType = new TypeToken<Map<String, PlansResponse>>() {
      }.getType();
      try {
         Map<String, PlansResponse> responseMap = gson.fromJson(new InputStreamReader(stream, "UTF-8"), setType);
         return responseMap.values().iterator().next().getPricingPlanInfos();
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}