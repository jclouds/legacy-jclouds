/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.parse;

import java.util.Calendar;
import java.util.Set;
import java.util.TimeZone;

import org.jclouds.cloudstack.domain.Alert;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Richard Downer
 */
@Test(groups = "unit")
public class ListAlertsResponseTest extends BaseSetParserTest<Alert> {

   @Override
   protected Injector injector() {
      return Guice.createInjector(new GsonModule() {

         @Override
         protected void configure() {
            bind(DateAdapter.class).to(Iso8601DateAdapter.class);
            super.configure();
         }

      });

   }

   @Override
   public String resource() {
      return "/listalertsresponse.json";
   }

   @Override
   @SelectJson("alert")
   public Set<Alert> expected() {
      Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+02:00"));
      c.set(Calendar.YEAR, 2011);
      c.set(Calendar.MONTH, Calendar.DECEMBER);
      c.set(Calendar.DAY_OF_MONTH, 4);
      c.set(Calendar.HOUR_OF_DAY, 10);
      c.set(Calendar.MINUTE, 5);
      c.set(Calendar.SECOND, 2);
      return ImmutableSet.of(Alert.builder()
         .id("20").description("Failed to deploy Vm with Id: 52").sent(c.getTime()).type("7").build());
   }

}
