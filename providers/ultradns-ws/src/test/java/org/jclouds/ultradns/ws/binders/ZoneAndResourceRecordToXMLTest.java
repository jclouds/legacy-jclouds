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
package org.jclouds.ultradns.ws.binders;

import static org.testng.Assert.assertEquals;

import org.jclouds.ultradns.ws.domain.ResourceRecord;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ZoneAndResourceRecordToXMLTest")
public class ZoneAndResourceRecordToXMLTest {

   String A = "<v01:createResourceRecord><transactionID /><resourceRecord ZoneName=\"jclouds.org.\" Type=\"1\" DName=\"www.jclouds.org.\" TTL=\"3600\"><InfoValues Info1Value=\"1.1.1.1\" /></resourceRecord></v01:createResourceRecord>";

   public void testA() {
      assertEquals(ZoneAndResourceRecordToXML.toXML("jclouds.org.", ResourceRecord.rrBuilder()
                                                                                  .name("www.jclouds.org.")
                                                                                  .type(1)
                                                                                  .ttl(3600)
                                                                                  .rdata("1.1.1.1").build()), A);
   }

   String MX = "<v01:createResourceRecord><transactionID /><resourceRecord ZoneName=\"jclouds.org.\" Type=\"15\" DName=\"mail.jclouds.org.\" TTL=\"1800\"><InfoValues Info1Value=\"10\" Info2Value=\"maileast.jclouds.org.\" /></resourceRecord></v01:createResourceRecord>";

   public void testMX() {
      assertEquals(ZoneAndResourceRecordToXML.toXML("jclouds.org.", ResourceRecord.rrBuilder()
                                                                                  .name("mail.jclouds.org.")
                                                                                  .type(15)
                                                                                  .ttl(1800)
                                                                                  .infoValue(10)
                                                                                  .infoValue("maileast.jclouds.org.").build()), MX);
   }

   String A_UPDATE = "<v01:updateResourceRecord><transactionID /><resourceRecord Guid=\"ABCDEF\" ZoneName=\"jclouds.org.\" Type=\"1\" DName=\"www.jclouds.org.\" TTL=\"3600\"><InfoValues Info1Value=\"1.1.1.1\" /></resourceRecord></v01:updateResourceRecord>";

   public void testUpdate() {
      assertEquals(ZoneAndResourceRecordToXML.update("ABCDEF", A), A_UPDATE);
   }
}
