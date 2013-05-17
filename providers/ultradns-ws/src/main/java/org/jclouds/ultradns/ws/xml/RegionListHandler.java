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
import org.jclouds.ultradns.ws.domain.IdAndName;
import org.xml.sax.Attributes;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 */
public class RegionListHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Multimap<IdAndName, String>> {

   private final Builder<IdAndName, String> regions = ImmutableMultimap.<IdAndName, String> builder();

   @Override
   public Multimap<IdAndName, String> getResult() {
      return regions.build();
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attrs) {
      if (equalsOrSuffix(qName, "Region")) {
         Map<String, String> attributes = cleanseAttributes(attrs);
         IdAndName region = IdAndName.create(attributes.get("RegionID"), attributes.get("RegionName"));
         regions.putAll(region, Splitter.on(';').split(attributes.get("TerritoryName")));
      }
   }
}
