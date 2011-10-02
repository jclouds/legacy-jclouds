/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
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
package org.jclouds.softlayer.compute.config;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.PowerState;
import org.jclouds.softlayer.domain.VirtualGuest;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class SoftLayerParserModule extends AbstractModule {

   @Singleton
   public static class VirtualGuestAdapter implements JsonSerializer<VirtualGuest>, JsonDeserializer<VirtualGuest> {

      public JsonElement serialize(VirtualGuest src, Type typeOfSrc, JsonSerializationContext context) {
         return context.serialize(src);
      }

      public VirtualGuest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
               throws JsonParseException {
         return apply(context.<VirtualGuestInternal> deserialize(json, VirtualGuestInternal.class));
      }

      public VirtualGuest apply(VirtualGuestInternal in) {
         return in;
      }

      /**
       * Internal class that flattens billingItem into billingItemId
       */
      public static class VirtualGuestInternal extends VirtualGuest {
         private BillingItem billingItem;

         public VirtualGuestInternal(int accountId, Date createDate, boolean dedicatedAccountHostOnly, String domain,
                  String fullyQualifiedDomainName, String hostname, int id, Date lastVerifiedDate, int maxCpu,
                  String maxCpuUnits, int maxMemory, Date metricPollDate, Date modifyDate, String notes,
                  boolean privateNetworkOnly, int startCpus, int statusId, String uuid, String primaryBackendIpAddress,
                  String primaryIpAddress, int billingItemId, OperatingSystem operatingSystem, Datacenter datacenter,
                  PowerState powerState) {
            super(accountId, createDate, dedicatedAccountHostOnly, domain, fullyQualifiedDomainName, hostname, id,
                     lastVerifiedDate, maxCpu, maxCpuUnits, maxMemory, metricPollDate, modifyDate, notes,
                     privateNetworkOnly, startCpus, statusId, uuid, primaryBackendIpAddress, primaryIpAddress,
                     billingItemId, operatingSystem, datacenter, powerState);
         }

         @Override
         public int getBillingItemId() {
            return billingItem != null ? billingItem.id : -1;
         }
      }

      public static class BillingItem {

         private int id = -1;

         // for deserializer
         BillingItem() {

         }

         public BillingItem(int id) {
            this.id = id;
         }

         @Override
         public String toString() {
            return "[id=" + id + "]";
         }
      }
   }

   @Override
   protected void configure() {
      bind(new TypeLiteral<Map<Type, Object>>() {
      }).toInstance(ImmutableMap.<Type, Object> of(VirtualGuest.class, new VirtualGuestAdapter()));
   }

}
