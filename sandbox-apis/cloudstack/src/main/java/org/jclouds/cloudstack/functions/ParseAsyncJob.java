/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.domain.AsyncJob.Builder;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.PortForwardingRule;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.domain.JsonBall;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.json.Json;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
public class ParseAsyncJob implements Function<HttpResponse, AsyncJob<?>> {
   @Resource
   protected Logger logger = Logger.NULL;

   private final Json json;
   private final UnwrapOnlyJsonValue<AsyncJob<Map<String, JsonBall>>> parser;

   @Inject(optional = true)
   @VisibleForTesting
   @Named("jclouds.cloudstack.jobresult-type-map")
   Map<String, Class<?>> typeMap = ImmutableMap.<String, Class<?>> builder().put("securitygroup", SecurityGroup.class)
         .put("portforwardingrule", PortForwardingRule.class).put("template", Template.class)
         .put("network", Network.class).put("ipaddress", PublicIPAddress.class)
         .put("virtualmachine", VirtualMachine.class).build();

   @Inject
   public ParseAsyncJob(Json json, UnwrapOnlyJsonValue<AsyncJob<Map<String, JsonBall>>> parser) {
      this.json = checkNotNull(json, "json");
      this.parser = checkNotNull(parser, "parser");
   }

   public AsyncJob<?> apply(HttpResponse response) {
      checkNotNull(response, "response");
      AsyncJob<Map<String, JsonBall>> toParse = parser.apply(response);
      checkNotNull(toParse, "parsed result from %s", response);
      AsyncJob<?> result = toParse;
      if (toParse.getResult() != null) {
         if (toParse.getResult().size() == 1) {
            Entry<String, JsonBall> entry = Iterables.get(toParse.getResult().entrySet(), 0);
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Builder<Object> builder = AsyncJob.Builder.fromAsyncJobUntyped((AsyncJob) toParse);
            if (typeMap.containsKey(entry.getKey())) {
               builder.result(json.fromJson(entry.getValue().toString(), typeMap.get(entry.getKey())));
            } else {
               logger.warn(
                     "type key % not configured.  please override default for Map<String, Class<?>> bound to name jclouds.cloudstack.jobresult-type-map",
                     entry.getKey());
               builder.result(entry.getValue().toString());
            }
            result = builder.build();
         } else if (toParse.getResult().size() > 1) {
            logger.warn("unexpected size of async job result; expecting a map with a single element",
                  toParse.getResult());
         }
      }
      return result;
   }
}
