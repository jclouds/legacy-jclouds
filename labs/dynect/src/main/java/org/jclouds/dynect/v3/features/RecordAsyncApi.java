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

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.http.Uris.uriBuilder;

import java.net.URI;
import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

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
import org.jclouds.dynect.v3.filters.SessionManager;
import org.jclouds.dynect.v3.functions.ToRecordIds;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @see RecordApi
 * @see <a
 *      href="https://manage.dynect.net/help/docs/api2/rest/resources/AllRecord.html">doc</a>
 * @author Adrian Cole
 */
// required for all calls
@Produces(APPLICATION_JSON)
@Headers(keys = "API-Version", values = "{jclouds.api-version}")
@RequestFilters(SessionManager.class)
public interface RecordAsyncApi {

   /**
    * @see RecordApi#list
    */
   @Named("GetAllRecord")
   @GET
   @Path("/AllRecord/{zone}")
   @ResponseParser(ToRecordIds.class)
   ListenableFuture<FluentIterable<RecordId>> list();

   /**
    * @see RecordApi#get
    */
   @Named("GetRecord")
   @GET
   @SelectJson("data")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Record<? extends Map<String, Object>>> get(@BinderParam(RecordIdBinder.class) RecordId recordId);

   static class RecordIdBinder implements Binder {
      @SuppressWarnings("unchecked")
      @Override
      public <R extends HttpRequest> R bindToRequest(R request, Object recordId) {
         RecordId valueToAppend = RecordId.class.cast(checkNotNull(recordId, "recordId"));
         URI path = uriBuilder(request.getEndpoint())
                     .appendPath("/{type}Record/{zone}/{fqdn}/{id}")
                     .build(ImmutableMap.<String, Object> builder()
                                        .put("type", valueToAppend.getType())
                                        .put("zone", valueToAppend.getZone())
                                        .put("fqdn", valueToAppend.getFQDN())
                                        .put("id", valueToAppend.getId()).build());
         return (R) request.toBuilder().endpoint(path).build();
      }
   }

   /**
    * @see RecordApi#getAAAA
    */
   @Named("GetAAAARecord")
   @GET
   @Path("/AAAARecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Record<AAAAData>> getAAAA(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId);

   /**
    * @see RecordApi#getA
    */
   @Named("GetARecord")
   @GET
   @Path("/ARecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Record<AData>> getA(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId);

   /**
    * @see RecordApi#getCNAME
    */
   @Named("GetCNAMERecord")
   @GET
   @Path("/CNAMERecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Record<CNAMEData>> getCNAME(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId);

   /**
    * @see RecordApi#getMX
    */
   @Named("GetMXRecord")
   @GET
   @Path("/MXRecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Record<MXData>> getMX(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId);

   /**
    * @see RecordApi#getNS
    */
   @Named("GetNSRecord")
   @GET
   @Path("/NSRecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Record<NSData>> getNS(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId);

   /**
    * @see RecordApi#getPTR
    */
   @Named("GetPTRRecord")
   @GET
   @Path("/PTRRecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Record<PTRData>> getPTR(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId);

   /**
    * @see RecordApi#getSOA
    */
   @Named("GetSOARecord")
   @GET
   @Path("/SOARecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<SOARecord> getSOA(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId);

   /**
    * @see RecordApi#getSRV
    */
   @Named("GetSRVRecord")
   @GET
   @Path("/SRVRecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Record<SRVData>> getSRV(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId);

   /**
    * @see RecordApi#getTXT
    */
   @Named("GetTXTRecord")
   @GET
   @Path("/TXTRecord/{zone}/{fqdn}/{id}")
   @SelectJson("data")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Record<TXTData>> getTXT(@PathParam("fqdn") String fqdn, @PathParam("id") long recordId);
}
