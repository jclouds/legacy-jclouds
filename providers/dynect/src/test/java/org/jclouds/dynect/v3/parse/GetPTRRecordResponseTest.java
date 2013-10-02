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
import static org.jclouds.dynect.v3.domain.rdata.PTRData.ptr;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.dynect.v3.domain.Record;
import org.jclouds.dynect.v3.domain.rdata.PTRData;
import org.jclouds.dynect.v3.internal.BaseDynECTParseTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class GetPTRRecordResponseTest extends BaseDynECTParseTest<Record<PTRData>> {

   @Override
   public String resource() {
      return "/get_record_ptr.json";
   }

   @Override
   @SelectJson("data")
   @Consumes(MediaType.APPLICATION_JSON)
   public Record<PTRData> expected() {
      return Record.<PTRData> builder()
                   .zone("egg.org")
                   .fqdn("1.2.3.0.0.0.0.0.0.0.0.0.0.0.0.0.d.9.2.1.4.0.0.7.0.c.6.8.0.0.a.2.ip6.arpa")
                   .type("PTR")
                   .id(50959331)
                   .ttl(86400)
                   .rdata(ptr("egg.org."))
                   .build();
   }
}
