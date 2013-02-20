/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.dynect.v3.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.logging.Logger.getAnonymousLogger;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.jclouds.dynect.v3.domain.Record;
import org.jclouds.dynect.v3.domain.RecordId;
import org.jclouds.dynect.v3.domain.SOARecord;
import org.jclouds.dynect.v3.domain.rdata.AAAAData;
import org.jclouds.dynect.v3.domain.rdata.AData;
import org.jclouds.dynect.v3.domain.rdata.CNAMEData;
import org.jclouds.dynect.v3.domain.rdata.MXData;
import org.jclouds.dynect.v3.domain.rdata.NSData;
import org.jclouds.dynect.v3.domain.rdata.PTRData;
import org.jclouds.dynect.v3.domain.rdata.SOAData;
import org.jclouds.dynect.v3.domain.rdata.SRVData;
import org.jclouds.dynect.v3.domain.rdata.TXTData;
import org.jclouds.dynect.v3.internal.BaseDynECTApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "RecordApiLiveTest")
public class RecordApiLiveTest extends BaseDynECTApiLiveTest {

   private void checkRecordId(RecordId record) {
      assertTrue(record.getId() > 0, "Id cannot be zero for RecordId: " + record);
      checkNotNull(record.getType(), "Type cannot be null for RecordId: %s", record);
      checkNotNull(record.getFQDN(), "FQDN cannot be null for RecordId: %s", record);
      checkNotNull(record.getZone(), "Zone cannot be null for RecordId: %s", record);
   }

   private void checkRecord(Record<? extends Map<String, Object>> record) {
      checkRecordId(record);
      assertTrue(record.getRData().size() > 0, "RData entries should be present for cannot be zero for Record: "
            + record);
      checkNotNull(record.getTTL(), "TTL cannot be null for RecordId: %s", record);
   }

   @Test
   protected void testListAndGetRecords() {
      for (String zone : context.getApi().getZoneApi().list()) {
         RecordApi api = context.getApi().getRecordApiForZone(zone);
         ImmutableList<RecordId> records = api.list().toList();
         getAnonymousLogger().info("zone: " + zone + " record count: " + records.size());

         for (RecordId recordId : records) {
            Record<? extends Map<String, Object>> record;
            if ("AAAA".equals(recordId.getType())) {
               record = checkAAAARecord(api.getAAAA(recordId.getFQDN(), recordId.getId()));
            } else if ("A".equals(recordId.getType())) {
               record = checkARecord(api.getA(recordId.getFQDN(), recordId.getId()));
            } else if ("CNAME".equals(recordId.getType())) {
               record = checkCNAMERecord(api.getCNAME(recordId.getFQDN(), recordId.getId()));
            } else if ("MX".equals(recordId.getType())) {
               record = checkMXRecord(api.getMX(recordId.getFQDN(), recordId.getId()));
            } else if ("NS".equals(recordId.getType())) {
               record = checkNSRecord(api.getNS(recordId.getFQDN(), recordId.getId()));
            } else if ("PTR".equals(recordId.getType())) {
               record = checkPTRRecord(api.getPTR(recordId.getFQDN(), recordId.getId()));
            } else if ("SOA".equals(recordId.getType())) {
               record = checkSOARecord(api.getSOA(recordId.getFQDN(), recordId.getId()));
            } else if ("SRV".equals(recordId.getType())) {
               record = checkSRVRecord(api.getSRV(recordId.getFQDN(), recordId.getId()));
            } else if ("TXT".equals(recordId.getType())) {
               record = checkTXTRecord(api.getTXT(recordId.getFQDN(), recordId.getId()));
            } else {
               record = api.get(recordId);
            }
            assertEquals(record, recordId);
            checkRecord(record);
         }
      }
   }

   private Record<AAAAData> checkAAAARecord(Record<AAAAData> record) {
      AAAAData rdata = record.getRData();
      checkNotNull(rdata.getAddress(), "rdata.address cannot be null for AAAARecord: %s", record);
      return record;
   }

   private Record<AData> checkARecord(Record<AData> record) {
      AData rdata = record.getRData();
      checkNotNull(rdata.getAddress(), "rdata.address cannot be null for ARecord: %s", record);
      return record;
   }

   private Record<CNAMEData> checkCNAMERecord(Record<CNAMEData> record) {
      CNAMEData rdata = record.getRData();
      checkNotNull(rdata.getCname(), "rdata.cname cannot be null for CNAMERecord: %s", record);
      return record;
   }

   private Record<MXData> checkMXRecord(Record<MXData> record) {
      MXData rdata = record.getRData();
      checkNotNull(rdata.getPreference(), "rdata.preference cannot be null for MXRecord: %s", record);
      checkNotNull(rdata.getExchange(), "rdata.exchange cannot be null for MXRecord: %s", record);
      return record;
   }

   private Record<NSData> checkNSRecord(Record<NSData> record) {
      NSData rdata = record.getRData();
      checkNotNull(rdata.getNsdname(), "rdata.nsdname cannot be null for NSRecord: %s", record);
      return record;
   }

   private Record<PTRData> checkPTRRecord(Record<PTRData> record) {
      PTRData rdata = record.getRData();
      checkNotNull(rdata.getPtrdname(), "rdata.ptrdname cannot be null for PTRRecord: %s", record);
      return record;
   }

   private SOARecord checkSOARecord(SOARecord record) {
      checkNotNull(record.getSerialStyle(), "SerialStyle cannot be null for SOARecord: %s", record);
      SOAData rdata = record.getRData();
      checkNotNull(rdata.getMname(), "rdata.mname cannot be null for SOARecord: %s", record);
      checkNotNull(rdata.getRname(), "rdata.rname cannot be null for SOARecord: %s", record);
      checkNotNull(rdata.getSerial(), "rdata.serial cannot be null for SOARecord: %s", record);
      checkNotNull(rdata.getRefresh(), "rdata.refresh cannot be null for SOARecord: %s", record);
      checkNotNull(rdata.getRetry(), "rdata.retry cannot be null for SOARecord: %s", record);
      checkNotNull(rdata.getExpire(), "rdata.expire cannot be null for SOARecord: %s", record);
      checkNotNull(rdata.getMinimum(), "rdata.minimum cannot be null for SOARecord: %s", record);
      return record;
   }

   private Record<SRVData> checkSRVRecord(Record<SRVData> record) {
      SRVData rdata = record.getRData();
      checkNotNull(rdata.getPriority(), "rdata.priority cannot be null for SRVRecord: %s", record);
      checkNotNull(rdata.getWeight(), "rdata.weight cannot be null for SRVRecord: %s", record);
      checkNotNull(rdata.getPort(), "rdata.port cannot be null for SRVRecord: %s", record);
      checkNotNull(rdata.getTarget(), "rdata.target cannot be null for SRVRecord: %s", record);
      return record;
   }

   private Record<TXTData> checkTXTRecord(Record<TXTData> record) {
      TXTData rdata = record.getRData();
      checkNotNull(rdata.getTxtdata(), "rdata.txtdata cannot be null for TXTRecord: %s", record);
      return record;
   }
}
