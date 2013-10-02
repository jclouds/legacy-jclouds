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

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.ultradns.ws.domain.ResourceRecordDetail;
import org.xml.sax.Attributes;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
public class ResourceRecordListHandler extends
      ParseSax.HandlerForGeneratedRequestWithResult<FluentIterable<ResourceRecordDetail>> {

   private final ResourceRecordDetailHandler resourceRecordHandler;

   private Builder<ResourceRecordDetail> rrs = ImmutableSet.<ResourceRecordDetail> builder();

   @Inject
   public ResourceRecordListHandler(ResourceRecordDetailHandler resourceRecordHandler) {
      this.resourceRecordHandler = resourceRecordHandler;
   }

   @Override
   public FluentIterable<ResourceRecordDetail> getResult() {
      return FluentIterable.from(rrs.build());
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
      resourceRecordHandler.startElement(url, name, qName, attributes);
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "ResourceRecord")) {
         rrs.add(resourceRecordHandler.getResult());
      }
   }
}
