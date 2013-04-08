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
package org.jclouds.ultradns.ws.xml;

import static org.jclouds.util.SaxUtils.cleanseAttributes;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.ultradns.ws.domain.Region;
import org.xml.sax.Attributes;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * 
 * @author Adrian Cole
 */
public class RegionListHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Map<Integer, Region>> {

   private final Builder<Integer, Region> regions = ImmutableMap.<Integer, Region> builder();

   @Override
   public Map<Integer, Region> getResult() {
      return regions.build();
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attrs) {
      if (equalsOrSuffix(qName, "Region")) {
         Map<String, String> attributes = cleanseAttributes(attrs);
         int id = Integer.parseInt(attributes.get("RegionID"));
         Iterable<String> territories = Splitter.on(';').split(attributes.get("TerritoryName"));
         Region region = Region.builder()
                               .name(attributes.get("RegionName"))
                               .territoryNames(territories).build();
         regions.put(id, region);
      }
   }
}
