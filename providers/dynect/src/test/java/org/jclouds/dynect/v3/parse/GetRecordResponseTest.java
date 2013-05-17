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

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.dynect.v3.domain.Record;
import org.jclouds.dynect.v3.internal.BaseDynECTParseTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class GetRecordResponseTest extends BaseDynECTParseTest<Record<Map<String, Object>>> {

   @Override
   public String resource() {
      return "/get_record_soa.json";
   }

   @Override
   @SelectJson("data")
   @Consumes(MediaType.APPLICATION_JSON)
   public Record<Map<String, Object>> expected() {
      return Record.<Map<String, Object>> builder()
                   .zone("adrianc.zone.dynecttest.jclouds.org")
                   .fqdn("adrianc.zone.dynecttest.jclouds.org")
                   .type("SOA")
                   .id(50976579l)
                   .ttl(3600)
                   // TODO: default parse to unsigned
                   .rdata(ImmutableMap.<String, Object> builder()
                                      .put("rname", "1\\.5\\.7-SNAPSHOT@jclouds.org.")
                                      .put("retry", 600.0)
                                      .put("mname", "ns1.p28.dynect.net.")
                                      .put("minimum", 60.0)
                                      .put("refresh", 3600.0)
                                      .put("expire", 604800.0)
                                      .put("serial", 1.0).build()).build();
   }
}
