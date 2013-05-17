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
package org.jclouds.glesys.compute.functions;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.OsFamilyVersion64Bit;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.json.internal.GsonWrapper;
import org.jclouds.util.Strings2;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseOsFamilyVersion64BitFromImageNameTest")
public class ParseOsFamilyVersion64BitFromImageNameTest {
   Json json = new GsonWrapper(new Gson());

   //TODO: update osmatches corresponding with server_templates.json, this may imply an update to ComputeServiceConstants and/or OsFamily
   @DataProvider(name = "data")
   public Object[][] createData() throws IOException {
      InputStream is = ParseOsFamilyVersion64BitFromImageNameTest.class.getResourceAsStream("/osmatches.json");
      Map<String, OsFamilyVersion64Bit> values = json.fromJson(Strings2.toStringAndClose(is),
            new TypeLiteral<Map<String, OsFamilyVersion64Bit>>() {
            }.getType());

      return newArrayList(
            transform(values.entrySet(), new Function<Map.Entry<String, OsFamilyVersion64Bit>, Object[]>() {

               @Override
               public Object[] apply(Entry<String, OsFamilyVersion64Bit> input) {
                  return new Object[] { input.getKey(), input.getValue() };
               }

            })).toArray(new Object[][] {});
   }

   ParseOsFamilyVersion64BitFromImageName parser = new ParseOsFamilyVersion64BitFromImageName(new BaseComputeServiceContextModule() {
      }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
            .getInstance(Json.class)));

   @Test(dataProvider = "data")
   public void test(String input, OsFamilyVersion64Bit expected) {
      assertEquals(parser.apply(input), expected);
   }
}
