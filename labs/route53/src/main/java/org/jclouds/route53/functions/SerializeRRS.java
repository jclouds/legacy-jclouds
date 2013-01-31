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
package org.jclouds.route53.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.route53.domain.RecordSet;
import org.jclouds.route53.domain.RecordSet.RecordSubset;
import org.jclouds.route53.domain.RecordSet.RecordSubset.Latency;
import org.jclouds.route53.domain.RecordSet.RecordSubset.Weighted;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 * @see ParamParser
 */
public class SerializeRRS implements Function<Object, String> {
   @Override
   public String apply(Object in) {
      RecordSet rrs = RecordSet.class.cast(checkNotNull(in, "rrs"));
      StringBuilder builder = new StringBuilder().append("<ResourceRecordSet>");
      builder.append("<Name>").append(rrs.getName()).append("</Name>");
      builder.append("<Type>").append(rrs.getType()).append("</Type>");
      if (rrs instanceof RecordSubset) {
         String id = RecordSubset.class.cast(rrs).getId();
         builder.append("<ResourceRecordSubset>").append(id).append("</ResourceRecordSubset>");
      }
      if (rrs instanceof Weighted)
         builder.append("<Weight>").append(Weighted.class.cast(rrs).getWeight()).append("</Weight>");
      if (rrs instanceof Latency)
         builder.append("<Region>").append(Latency.class.cast(rrs).getRegion()).append("</Region>");
      if (rrs.getAliasTarget().isPresent()) {
         builder.append("<AliasTarget>");
         builder.append("<HostedZoneId>").append(rrs.getAliasTarget().get().getZoneId()).append("</HostedZoneId>");
         builder.append("<DNSName>").append(rrs.getAliasTarget().get().getDNSName()).append("</DNSName>");
         builder.append("</AliasTarget>");
      } else {
         builder.append("<TTL>").append(rrs.getTTL().or(0)).append("</TTL>");
         builder.append("<ResourceRecords>");
         for (String record : rrs.getValues())
            builder.append("<ResourceRecord>").append("<Value>").append(record).append("</Value>")
                  .append("</ResourceRecord>");
         builder.append("</ResourceRecords>");
      }
      return builder.append("</ResourceRecordSet>").toString();
   }
}
