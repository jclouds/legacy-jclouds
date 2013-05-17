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
package org.jclouds.ultradns.ws.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.ultradns.ws.domain.PoolRecordSpec;
import org.jclouds.ultradns.ws.xml.PoolRecordSpecHandler;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(testName = "GetPoolRecordSpecResponseTest")
public class GetPoolRecordSpecResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/poolrecordspec.xml");

      PoolRecordSpec expected = expected();

      PoolRecordSpecHandler handler = injector.getInstance(PoolRecordSpecHandler.class);
      PoolRecordSpec result = factory.create(handler).parse(is);

      assertEquals(result, expected);
      assertEquals(result.hashCode(), expected.hashCode());
      assertEquals(result.toString(), expected.toString());
   }

   public PoolRecordSpec expected() {
      return PoolRecordSpec.builder()
                           .description("foo")
                           .state("Normal-NoTest")
                           .probingEnabled(false)
                           .allFailEnabled(false)
                           .weight(2)
                           .failOverDelay(0)
                           .threshold(1)
                           .ttl(120).build();
   }

}
