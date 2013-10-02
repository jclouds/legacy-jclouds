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
package org.jclouds.route53.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.route53.domain.ResourceRecordSet;
import org.jclouds.route53.domain.ResourceRecordSetIterable;
import org.jclouds.route53.xml.ListResourceRecordSetsResponseHandler;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "ListResourceRecordSetsResponseTest")
public class ListResourceRecordSetsResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/rrsets.xml");

      ResourceRecordSetIterable expected = expected();

      ListResourceRecordSetsResponseHandler handler = injector.getInstance(ListResourceRecordSetsResponseHandler.class);
      ResourceRecordSetIterable result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public ResourceRecordSetIterable expected() {
      return ResourceRecordSetIterable.builder()
            .add(ResourceRecordSet.builder()
                                  .name("example.com.")
                                  .type("SOA")
                                  .ttl(900)
                                  .add("ns-2048.awsdns-64.net. hostmaster.awsdns.com. 1 7200 900 1209600 86400")
                                  .build())
            .add(ResourceRecordSet.builder()
                                  .name("example.com.")
                                  .type("NS")
                                  .ttl(172800)
                                  .add("ns-2048.awsdns-64.com.")
                                  .add("ns-2049.awsdns-65.net.")
                                  .add("ns-2050.awsdns-66.org.")
                                  .add("ns-2051.awsdns-67.co.uk.")
                                  .build())
            .nextRecordName("testdoc2.example.com")
            .nextRecordType("NS").build();
   }
}
