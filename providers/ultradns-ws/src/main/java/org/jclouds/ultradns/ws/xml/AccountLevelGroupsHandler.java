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
import org.jclouds.ultradns.ws.domain.AccountLevelGroup;
import org.jclouds.ultradns.ws.domain.DirectionalPool.Type;
import org.xml.sax.Attributes;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * 
 * @author Adrian Cole
 */
public class AccountLevelGroupsHandler extends
      ParseSax.HandlerForGeneratedRequestWithResult<FluentIterable<AccountLevelGroup>> {

   private final Builder<AccountLevelGroup> groups = ImmutableSet.<AccountLevelGroup> builder();

   @Override
   public FluentIterable<AccountLevelGroup> getResult() {
      return FluentIterable.from(groups.build());
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attrs) {
      if (equalsOrSuffix(qName, "AccountLevelGroups")) {
         Map<String, String> attributes = cleanseAttributes(attrs);
         groups.add(AccountLevelGroup.builder()
                                     .id(attributes.get("GroupId"))
                                     .name(attributes.get("GroupName"))
                                     .type(Type.valueOf(attributes.get("GroupType")))
                                     .recordCount(Integer.parseInt(attributes.get("RecordsCount")))
                                     .build());
      }
   }
}
