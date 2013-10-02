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
package org.jclouds.dynect.v3.functions;

import static org.jclouds.dynect.v3.domain.RecordId.recordIdBuilder;
import static org.testng.Assert.assertEquals;

import org.jclouds.dynect.v3.domain.RecordId;
import org.jclouds.dynect.v3.functions.ToRecordIds.ParsePath;
import org.jclouds.json.internal.GsonWrapper;
import org.testng.annotations.Test;

import com.google.gson.Gson;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ToRecordIdsTest {
   ToRecordIds fn = new ToRecordIds(new GsonWrapper(new Gson()));
   RecordId recordId = recordIdBuilder()
                               .id(50976583)
                               .type("NS")
                               .zone("adrianc.zone.dynecttest.jclouds.org")
                               .fqdn("adrianc.zone.dynecttest.jclouds.org")
                               .build();

   public void testParsePath() {
      assertEquals(
            ParsePath.INSTANCE
                  .apply("/REST/NSRecord/adrianc.zone.dynecttest.jclouds.org/adrianc.zone.dynecttest.jclouds.org/50976583"),
            recordId);
   }
}
