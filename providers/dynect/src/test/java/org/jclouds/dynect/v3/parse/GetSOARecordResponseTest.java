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

import static org.jclouds.dynect.v3.domain.Zone.SerialStyle.INCREMENT;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.dynect.v3.domain.SOARecord;
import org.jclouds.dynect.v3.domain.rdata.SOAData;
import org.jclouds.dynect.v3.internal.BaseDynECTParseTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class GetSOARecordResponseTest extends BaseDynECTParseTest<SOARecord> {

   @Override
   public String resource() {
      return "/get_record_soa.json";
   }

   @Override
   @SelectJson("data")
   @Consumes(MediaType.APPLICATION_JSON)
   public SOARecord expected() {
      return SOARecord.builder()
                      .zone("adrianc.zone.dynecttest.jclouds.org")
                      .fqdn("adrianc.zone.dynecttest.jclouds.org")
                      .type("SOA")
                      .id(50976579l)
                      .ttl(3600)
                      .serialStyle(INCREMENT)
                      .rdata(SOAData.builder()
                                    .rname("1\\.5\\.7-SNAPSHOT@jclouds.org.")
                                    .retry(600)
                                    .mname("ns1.p28.dynect.net.")
                                    .minimum(60)
                                    .refresh(3600)
                                    .expire(604800)
                                    .serial(1).build()).build();
   }
}
