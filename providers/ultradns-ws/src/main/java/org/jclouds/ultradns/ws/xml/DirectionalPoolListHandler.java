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
package org.jclouds.ultradns.ws.xml;

import static org.jclouds.util.SaxUtils.cleanseAttributes;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.ultradns.ws.domain.DirectionalPool;
import org.jclouds.ultradns.ws.domain.DirectionalPool.TieBreak;
import org.jclouds.ultradns.ws.domain.DirectionalPool.Type;
import org.xml.sax.Attributes;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * 
 * @author Adrian Cole
 */
public class DirectionalPoolListHandler extends ParseSax.HandlerForGeneratedRequestWithResult<FluentIterable<DirectionalPool>> {

   private final Builder<DirectionalPool> pools = ImmutableSet.<DirectionalPool> builder();

   @Override
   public FluentIterable<DirectionalPool> getResult() {
      return FluentIterable.from(pools.build());
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attrs) {
      if (equalsOrSuffix(qName, "DirectionalPoolData")) {
         Map<String, String> attributes = cleanseAttributes(attrs);

         DirectionalPool.Builder pool = DirectionalPool.builder()
                                                       .zoneId(attributes.get("Zoneid"))
                                                       .id(attributes.get("dirpoolid"))
                                                       .dname(attributes.get("Pooldname"))
                                                       .name(attributes.get("Description"));

         String type = attributes.get("DirPoolType");
         if (type != null)
            pool.type(Type.valueOf(type));
         
         String tieBreak = attributes.get("TieBreak");
         if (tieBreak != null)
            pool.tieBreak(TieBreak.valueOf(tieBreak));

         pools.add(pool.build());
      }
   }
}
