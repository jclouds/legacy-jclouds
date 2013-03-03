/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
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

import java.util.Map;

import org.jclouds.dynect.v3.DynECTExceptions.JobStillRunningException;
import org.jclouds.dynect.v3.domain.CreateRecord;
import org.jclouds.dynect.v3.domain.Job;
import org.jclouds.dynect.v3.domain.Record;
import org.jclouds.dynect.v3.domain.RecordId;
import org.jclouds.dynect.v3.domain.SOARecord;
import org.jclouds.dynect.v3.domain.rdata.AAAAData;
import org.jclouds.dynect.v3.domain.rdata.AData;
import org.jclouds.dynect.v3.domain.rdata.CNAMEData;
import org.jclouds.dynect.v3.domain.rdata.MXData;
import org.jclouds.dynect.v3.domain.rdata.NSData;
import org.jclouds.dynect.v3.domain.rdata.PTRData;
import org.jclouds.dynect.v3.domain.rdata.SRVData;
import org.jclouds.dynect.v3.domain.rdata.TXTData;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.FluentIterable;

/**
 * @see RecordAsyncApi
 * @author Adrian Cole
 */
public interface RecordApi {
   /**
    * Retrieves a list of resource record ids for all records of any type in the given zone.
    * 
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   FluentIterable<RecordId> list() throws JobStillRunningException;

   /**
    * Retrieves a list of resource record ids for all records of the fqdn and type in the given zone
    * 
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   FluentIterable<RecordId> listByFQDNAndType(String fqdn, String type) throws JobStillRunningException;

   /**
    * Schedules addition of a new record into the current session. Calling {@link ZoneApi#publish(String)} will publish
    * the zone, creating the record.
    * 
    * @param newRecord
    *           record to create
    * @return job relating to the scheduled creation.
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   Job scheduleCreate(CreateRecord<?> newRecord) throws JobStillRunningException;

   /**
    * Schedules deletion of a record into the current session. Calling {@link ZoneApi#publish(String)} will publish the
    * changes, deleting the record.
    * 
    * @param recordId
    *           record to delete
    * @return job relating to the scheduled deletion or null, if the record never existed.
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   @Nullable
   Job scheduleDelete(RecordId recordId) throws JobStillRunningException;

   /**
    * retrieves a resource record without regard to type
    * 
    * @return null if not found
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   Record<? extends Map<String, Object>> get(RecordId recordId) throws JobStillRunningException;

   /**
    * Gets the {@link AAAARecord} or null if not present.
    * 
    * @param fqdn
    *           {@link RecordId#getFQDN()}
    * @param recordId
    *           {@link RecordId#getId()}
    * @return null if not found
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   Record<AAAAData> getAAAA(String fqdn, long recordId) throws JobStillRunningException;

   /**
    * Gets the {@link ARecord} or null if not present.
    * 
    * @param fqdn
    *           {@link RecordId#getFQDN()}
    * @param recordId
    *           {@link RecordId#getId()}
    * @return null if not found
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   Record<AData> getA(String fqdn, long recordId) throws JobStillRunningException;

   /**
    * Gets the {@link CNAMERecord} or null if not present.
    * 
    * @param fqdn
    *           {@link RecordId#getFQDN()}
    * @param recordId
    *           {@link RecordId#getId()}
    * @return null if not found
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   Record<CNAMEData> getCNAME(String fqdn, long recordId) throws JobStillRunningException;

   /**
    * Gets the {@link MXRecord} or null if not present.
    * 
    * @param fqdn
    *           {@link RecordId#getFQDN()}
    * @param recordId
    *           {@link RecordId#getId()}
    * @return null if not found
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   Record<MXData> getMX(String fqdn, long recordId) throws JobStillRunningException;

   /**
    * Gets the {@link NSRecord} or null if not present.
    * 
    * @param fqdn
    *           {@link RecordId#getFQDN()}
    * @param recordId
    *           {@link RecordId#getId()}
    * @return null if not found
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   Record<NSData> getNS(String fqdn, long recordId) throws JobStillRunningException;

   /**
    * Gets the {@link PTRRecord} or null if not present.
    * 
    * @param fqdn
    *           {@link RecordId#getFQDN()}
    * @param recordId
    *           {@link RecordId#getId()}
    * @return null if not found
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   Record<PTRData> getPTR(String fqdn, long recordId) throws JobStillRunningException;

   /**
    * Gets the {@link SOARecord} or null if not present.
    * 
    * @param fqdn
    *           {@link RecordId#getFQDN()}
    * @param recordId
    *           {@link RecordId#getId()}
    * @return null if not found
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   SOARecord getSOA(String fqdn, long recordId) throws JobStillRunningException;

   /**
    * Gets the {@link SRVRecord} or null if not present.
    * 
    * @param fqdn
    *           {@link RecordId#getFQDN()}
    * @param recordId
    *           {@link RecordId#getId()}
    * @return null if not found
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   Record<SRVData> getSRV(String fqdn, long recordId) throws JobStillRunningException;

   /**
    * Gets the {@link TXTRecord} or null if not present.
    * 
    * @param fqdn
    *           {@link RecordId#getFQDN()}
    * @param recordId
    *           {@link RecordId#getId()}
    * @return null if not found
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   Record<TXTData> getTXT(String fqdn, long recordId) throws JobStillRunningException;
}