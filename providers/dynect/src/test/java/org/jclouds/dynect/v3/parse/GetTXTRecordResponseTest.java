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
import static org.jclouds.dynect.v3.domain.rdata.TXTData.txt;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.dynect.v3.domain.Record;
import org.jclouds.dynect.v3.domain.rdata.TXTData;
import org.jclouds.dynect.v3.internal.BaseDynECTParseTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class GetTXTRecordResponseTest extends BaseDynECTParseTest<Record<TXTData>> {

   @Override
   public String resource() {
      return "/get_record_txt.json";
   }

   @Override
   @SelectJson("data")
   @Consumes(MediaType.APPLICATION_JSON)
   public Record<TXTData> expected() {
      return Record.<TXTData> builder()
                   .zone("egg.org")
                   .fqdn("sm._domainkey.email.egg.org")
                   .type("TXT")
                   .id(50959331)
                   .ttl(86400)
                   .rdata(txt("k=rsa\\; p=4KAtUdsUGRtjPHE1rsyFYs8XVzvdke8pXnoo+80Kj5b6C37rnyCmZ0w1R5LY=="))
                   .build();
   }
}
