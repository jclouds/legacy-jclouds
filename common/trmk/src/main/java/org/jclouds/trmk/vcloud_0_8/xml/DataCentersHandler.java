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
package org.jclouds.trmk.vcloud_0_8.xml;

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.trmk.vcloud_0_8.domain.DataCenter;
import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @author Adrian Cole
 */
public class DataCentersHandler extends ParseSax.HandlerWithResult<Set<DataCenter>> {
   protected Builder<DataCenter> dataCenters = ImmutableSet.builder();

   protected final DataCenterHandler handler;

   public Set<DataCenter> getResult() {
      try {
         return dataCenters.build();
      } finally {
         dataCenters = ImmutableSet.<DataCenter> builder();
      }
   }

   @Inject
   public DataCentersHandler(DataCenterHandler handler) {
      this.handler = handler;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) {
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (equalsOrSuffix(qName, "DataCenter")) {
         dataCenters.add(handler.getResult());
      } else {
         handler.endElement(uri, localName, qName);
      }
   }

   @Override
   public void characters(char ch[], int start, int length) {
      handler.characters(ch, start, length);
   }

}
