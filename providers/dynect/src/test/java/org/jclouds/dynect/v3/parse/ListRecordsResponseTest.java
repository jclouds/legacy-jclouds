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
package org.jclouds.dynect.v3.parse;

import static org.jclouds.dynect.v3.domain.RecordId.recordIdBuilder;

import org.jclouds.dynect.v3.domain.RecordId;
import org.jclouds.dynect.v3.domain.RecordId.Builder;
import org.jclouds.dynect.v3.functions.ToRecordIds;
import org.jclouds.dynect.v3.internal.BaseDynECTParseTest;
import org.jclouds.rest.annotations.ResponseParser;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListRecordsResponseTest extends BaseDynECTParseTest<FluentIterable<RecordId>> {

   @Override
   public String resource() {
      return "/list_records.json";
   }

   @Override
   @ResponseParser(ToRecordIds.class)
   public FluentIterable<RecordId> expected() {
      Builder<?> builder = recordIdBuilder()
                                   .zone("adrianc.zone.dynecttest.jclouds.org")
                                   .fqdn("adrianc.zone.dynecttest.jclouds.org");
      return FluentIterable.from(ImmutableSet.<RecordId> builder()
                                             .add(builder.type("SOA").id(50976579l).build())
                                             .add(builder.type("NS").id(50976580l).build())
                                             .add(builder.type("NS").id(50976581l).build())
                                             .add(builder.type("NS").id(50976582l).build())
                                             .add(builder.type("NS").id(50976583l).build())
                                             .build());
   }
}
