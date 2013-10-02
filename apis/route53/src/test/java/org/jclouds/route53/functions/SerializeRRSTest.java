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
package org.jclouds.route53.functions;

import static org.testng.Assert.assertEquals;

import org.jclouds.route53.domain.ResourceRecordSet;
import org.jclouds.route53.domain.ResourceRecordSet.RecordSubset.Weighted;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class SerializeRRSTest {

   @Test
   void roundRobinRRSetToXML() {
      assertEquals(
            new SerializeRRS().apply(ResourceRecordSet.builder()
                                                      .name("dom1.foo.com.")
                                                      .type("A")
                                                      .add("1.2.3.4")
                                                      .add("5.6.7.8").build()),
            "<ResourceRecordSet><Name>dom1.foo.com.</Name><Type>A</Type><TTL>300</TTL><ResourceRecords><ResourceRecord><Value>1.2.3.4</Value></ResourceRecord><ResourceRecord><Value>5.6.7.8</Value></ResourceRecord></ResourceRecords></ResourceRecordSet>");
   }

   @Test
   void roundWeightedRRSetToXML() {
      assertEquals(new SerializeRRS().apply(Weighted.builder()
                                                    .id("dom1")
                                                    .weight(1)
                                                    .name("dom.foo.com.")
                                                    .type("CNAME")
                                                    .add("dom1.foo.com.").build()),
            "<ResourceRecordSet><Name>dom.foo.com.</Name><Type>CNAME</Type><SetIdentifier>dom1</SetIdentifier><Weight>1</Weight><TTL>300</TTL><ResourceRecords><ResourceRecord><Value>dom1.foo.com.</Value></ResourceRecord></ResourceRecords></ResourceRecordSet>");
   }
}
