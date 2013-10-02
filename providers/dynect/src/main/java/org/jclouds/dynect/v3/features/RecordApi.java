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
package org.jclouds.dynect.v3.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.dynect.v3.DynECTExceptions.JobStillRunningException;
import org.jclouds.dynect.v3.binders.CreateRecordBinder;
import org.jclouds.dynect.v3.binders.RecordIdBinder;
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
import org.jclouds.dynect.v3.domain.rdata.SPFData;
import org.jclouds.dynect.v3.domain.rdata.SRVData;
import org.jclouds.dynect.v3.domain.rdata.SSHFPData;
import org.jclouds.dynect.v3.domain.rdata.TXTData;
import org.jclouds.dynect.v3.filters.AlwaysAddContentType;
import org.jclouds.dynect.v3.filters.SessionManager;
import org.jclouds.dynect.v3.functions.ToRecordIds;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.collect.FluentIterable;

/**
 * @author Adrian Cole
 */
@Headers(keys = "API-Version", values = "{jclouds.api-version}")
@RequestFilters({ AlwaysAddContentType.class, SessionManager.class })
public interface RecordApi {
   /**
    * Retrieves a list of resource record ids for all records of any type in the
    * given zone.
    * 
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   @Named("GetAllRecord")
   @GET
   @Path("/AllRecord/{zone}")
   @ResponseParser(ToRecordIds.class)
   FluentIterable<RecordId> list() throws JobStillRunningException;

   /**
    * Retrieves a list of resource record ids for all records of the fqdn in the
    * given zone
    * 
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   @Named("GetRecord")
   @GET
   @Path("/AllRecord/{zone}/{fqdn}")
   @ResponseParser(ToRecordIds.class)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<RecordId> listByFQDN(@PathParam("fqdn") String fqdn) throws JobStillRunningException;

   /**
    * Retrieves a list of resource record ids for all records of the fqdn and
    * type in the given zone
    * 
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   @Named("GetRecord")
   @GET
   @Path("/{type}Record/{zone}/{fqdn}")
   @ResponseParser(ToRecordIds.class)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<RecordId> listByFQDNAndType(@PathParam("fqdn") String fqdn, @PathParam("type") String type)
         throws JobStillRunningException;

   /**
    * Schedules addition of a new record into the current session. Calling
    * {@link ZoneApi#publish(String)} will publish the zone, creating the
    * record.
    * 
    * @param newRecord
    *           record to create
    * @return job relating to the scheduled creation.
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   @Named("CreateRecord")
   @POST
   @Path("/{type}Record/{zone}/{fqdn}")
   @Consumes(APPLICATION_JSON)
   @Produces(APPLICATION_JSON)
   Job scheduleCreate(@BinderParam(CreateRecordBinder.class) CreateRecord<?> newRecord) throws JobStillRunningException;

   /**
    * Schedules deletion of a record into the current session. Calling
    * {@link ZoneApi#publish(String)} will publish the changes, deleting the
    * record.
    * 
    * @param recordId
    *           record to delete
    * @return job relating to the scheduled deletion or null, if the record
    *         never existed.
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   @Nullable
   @Named("DeleteRecord")
   @DELETE
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(APPLICATION_JSON)
   Job scheduleDelete(@BinderParam(RecordIdBinder.class) RecordId recordId) throws JobStillRunningException;

   /**
    * retrieves a resource record without regard to type
    * 
    * @return null if not found
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   @Named("GetRecord")
   @GET
   @SelectJson("data")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Record<? extends Map<String, Object>> get(@BinderParam(RecordIdBinder.class) RecordId recordId)
         throws JobStillRunningException;

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
   @Named("GetAAAARecord")
   @GET
   @Path("/AAAARecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Record<AAAAData> getAAAA(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId)
         throws JobStillRunningException;

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
   @Named("GetARecord")
   @GET
   @Path("/ARecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Record<AData> getA(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId) throws JobStillRunningException;

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
   @Named("GetCNAMERecord")
   @GET
   @Path("/CNAMERecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Record<CNAMEData> getCNAME(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId)
         throws JobStillRunningException;

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
   @Named("GetMXRecord")
   @GET
   @Path("/MXRecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Record<MXData> getMX(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId) throws JobStillRunningException;

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
   @Named("GetNSRecord")
   @GET
   @Path("/NSRecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Record<NSData> getNS(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId) throws JobStillRunningException;

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
   @Named("GetPTRRecord")
   @GET
   @Path("/PTRRecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Record<PTRData> getPTR(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId)
         throws JobStillRunningException;

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
   @Named("GetSOARecord")
   @GET
   @Path("/SOARecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   SOARecord getSOA(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId) throws JobStillRunningException;

   /**
    * Gets the {@link SPFRecord} or null if not present.
    * 
    * @param fqdn
    *           {@link RecordId#getFQDN()}
    * @param recordId
    *           {@link RecordId#getId()}
    * @return null if not found
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   @Named("GetSPFRecord")
   @GET
   @Path("/SPFRecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Record<SPFData> getSPF(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId)
         throws JobStillRunningException;

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
   @Named("GetSRVRecord")
   @GET
   @Path("/SRVRecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Record<SRVData> getSRV(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId)
         throws JobStillRunningException;

   /**
    * Gets the {@link SSHFPRecord} or null if not present.
    * 
    * @param fqdn
    *           {@link RecordId#getFQDN()}
    * @param recordId
    *           {@link RecordId#getId()}
    * @return null if not found
    * @throws JobStillRunningException
    *            if a different job in the session is still running
    */
   @Named("GetSSHFPRecord")
   @GET
   @Path("/SSHFPRecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Record<SSHFPData> getSSHFP(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId)
         throws JobStillRunningException;

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
   @Named("GetTXTRecord")
   @GET
   @Path("/TXTRecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Record<TXTData> getTXT(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId)
         throws JobStillRunningException;
}
