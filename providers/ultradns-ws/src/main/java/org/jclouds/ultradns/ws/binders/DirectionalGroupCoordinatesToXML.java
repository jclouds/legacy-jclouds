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

import org.jclouds.http.HttpRequest;
import org.jclouds.http.UriTemplates;
import org.jclouds.rest.Binder;
import org.jclouds.ultradns.ws.domain.DirectionalGroupCoordinates;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 */
public class DirectionalGroupCoordinatesToXML implements Binder {
   private static final String TEMPLATE = "<v01:getDirectionalDNSRecordsForGroup><groupName>{groupName}</groupName><hostName>{recordName}</hostName><zoneName>{zoneName}</zoneName><poolRecordType>{recordType}</poolRecordType></v01:getDirectionalDNSRecordsForGroup>";

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object in) {
      DirectionalGroupCoordinates group = DirectionalGroupCoordinates.class.cast(in);
      ImmutableMap<String, Object> variables = ImmutableMap.<String, Object> builder()
                                                           .put("zoneName", group.getZoneName())
                                                           .put("recordName", group.getRecordName())
                                                           .put("recordType", group.getRecordType())
                                                           .put("groupName", group.getGroupName()).build();
      return (R) request.toBuilder().payload(UriTemplates.expand(TEMPLATE, variables)).build();
   }
}
