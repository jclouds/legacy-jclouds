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
package org.jclouds.cloudstack.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.domain.AsyncJobError;
import org.jclouds.cloudstack.domain.FirewallRule;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.domain.LoadBalancerRule;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.PortForwardingRule;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.domain.Snapshot;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.TemplateExtraction;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.Volume;
import org.jclouds.cloudstack.domain.AsyncJob.Builder;
import org.jclouds.cloudstack.domain.AsyncJobError.ErrorCode;
import org.jclouds.domain.JsonBall;
import org.jclouds.json.Json;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
@Singleton
public class ParseTypedAsyncJob implements Function<AsyncJob<Map<String, JsonBall>>, AsyncJob<?>> {
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @VisibleForTesting
   @Named("jclouds.cloudstack.jobresult-type-map")
   Map<String, Class<?>> typeMap = ImmutableMap.<String, Class<?>>builder()
      .put("user", User.class)
      .put("account", Account.class)
      .put("securitygroup", SecurityGroup.class)
      .put("portforwardingrule", PortForwardingRule.class)
      .put("ipforwardingrule", IPForwardingRule.class)
      .put("firewallrule", FirewallRule.class)
      .put("network", Network.class)
      .put("ipaddress", PublicIPAddress.class)
      .put("virtualmachine", VirtualMachine.class)
      .put("loadbalancer", LoadBalancerRule.class)
      .put("snapshot", Snapshot.class)
      .put("template", Template.class)
      .put("volume", Volume.class).build();
   private final Json json;

   @Inject
   public ParseTypedAsyncJob(Json json) {
      this.json = checkNotNull(json, "json");
   }

   public AsyncJob<?> apply(AsyncJob<Map<String, JsonBall>> toParse) {
      AsyncJob<?> result = toParse;
      if (toParse.getResult() != null) {
         if (toParse.getResult().size() == 1) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Builder<?,Object> builder = AsyncJob.Builder.fromAsyncJobUntyped((AsyncJob) toParse);
            if (toParse.getResult().containsKey("success")) {
               builder.result(null);
            } else {
               Entry<String, JsonBall> entry = Iterables.get(toParse.getResult().entrySet(), 0);
               if ("template".equals(entry.getKey())) {
                  // Sometimes Cloudstack will say 'template' and the payload is a Template object.
                  // Sometimes Cloudstack will say 'template' and the payload is a TemplateExtraction object.
                  // The 'state' field only exists on TemplateExtraction, so we can test this to work out what we have actually been given.
                  Template template = json.fromJson(entry.getValue().toString(), Template.class);
                  TemplateExtraction templateExtraction = json.fromJson(entry.getValue().toString(), TemplateExtraction.class);
                  boolean isTemplate = Strings.isNullOrEmpty(templateExtraction.getState());
                  builder.result(isTemplate ? template : templateExtraction);
               } else if (typeMap.containsKey(entry.getKey())) {
                  builder.result(json.fromJson(entry.getValue().toString(), typeMap.get(entry.getKey())));
               } else {
                  logger.warn(
                     "type key %s not configured.  please override default for Map<String, Class<?>> bound to name jclouds.cloudstack.jobresult-type-map",
                     entry.getKey());
                  builder.result(entry.getValue().toString());
               }
            }
            result = builder.build();
         } else if (toParse.getResult().containsKey("errorcode")) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Builder<?, Object> builder = AsyncJob.Builder.fromAsyncJobUntyped((AsyncJob) toParse);
            builder.result(null);// avoid classcastexceptions
            builder.error(AsyncJobError.builder().errorCode(ErrorCode.fromValue(toParse.getResult().get("errorcode").toString()))
                  .errorText(toParse.getResult().containsKey("errortext") ? toParse.getResult().get("errortext").toString().replace("\"", "") : null)
                  .build());
            result = builder.build();
         } else if (toParse.getResult().size() > 1) {
            logger.warn("unexpected size of async job result; expecting a map with a single element",
               toParse.getResult());
         }
      }
      return result;
   }
}
