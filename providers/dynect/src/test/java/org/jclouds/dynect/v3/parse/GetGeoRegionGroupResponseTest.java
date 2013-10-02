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

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.dynect.v3.domain.RecordSet;
import org.jclouds.dynect.v3.domain.RecordSet.Value;
import org.jclouds.dynect.v3.domain.GeoRegionGroup;
import org.jclouds.dynect.v3.internal.BaseDynECTParseTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class GetGeoRegionGroupResponseTest extends BaseDynECTParseTest<GeoRegionGroup> {

   @Override
   public String resource() {
      return "/get_geo_regiongroup.json";
   }

   @Override
   @SelectJson("data")
   @Consumes(MediaType.APPLICATION_JSON)
   public GeoRegionGroup expected() {
      return GeoRegionGroup
            .builder()
            .name("Everywhere Else")
            .countries(ImmutableList.of("11", "16", "12", "17", "15", "14"))
            .serviceName("CCS")
            .addRecordSet(
                  RecordSet
                        .builder()
                        .ttl(0)
                        .type("CNAME")
                        .add(Value
                              .builder()
                              .rdata(
                                    ImmutableMap.<String, Object> builder()
                                          .put("cname", "srv-938089264.us-east-1.elb.amazonaws.com.").build()).build())
                        .build()).build();
   }
}
