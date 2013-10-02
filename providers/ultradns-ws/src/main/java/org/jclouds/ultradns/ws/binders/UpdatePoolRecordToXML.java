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
package org.jclouds.ultradns.ws.binders;

import static java.lang.String.format;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.ultradns.ws.domain.UpdatePoolRecord;

/**
 * 
 * @author Adrian Cole
 */
public class UpdatePoolRecordToXML implements MapBinder {
   private static final String HEADER = "<v01:updatePoolRecord><transactionID /><poolRecordID>%s</poolRecordID><parentPoolId /><childPoolId />";
   private static final String FOOTER = "</v01:updatePoolRecord>";

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {

      StringBuilder xml = new StringBuilder();
      xml.append(format(HEADER, postParams.get("poolRecordID")));

      UpdatePoolRecord update = UpdatePoolRecord.class.cast(postParams.get("update"));

      xml.append("<pointsTo>").append(update.getRData()).append("</pointsTo>");
      xml.append("<priority>").append(update.getPriority()).append("</priority>");
      xml.append("<failOverDelay>").append(update.getFailOverDelay()).append("</failOverDelay>");
      xml.append("<ttl>").append(update.getTTL()).append("</ttl>");
      xml.append("<weight>").append(update.getWeight()).append("</weight>");
      xml.append("<mode>").append(update.getMode()).append("</mode>");
      xml.append("<threshold>").append(update.getThreshold()).append("</threshold>");

      xml.append(FOOTER);
      return (R) request.toBuilder().payload(xml.toString()).build();
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new UnsupportedOperationException("use map form");
   }
}
