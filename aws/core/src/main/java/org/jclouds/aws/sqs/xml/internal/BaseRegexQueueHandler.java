/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.sqs.xml.internal;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.sqs.domain.Queue;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Sets;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/APIReference/Query_QueryListQueues.html"
 *      />
 * @author Adrian Cole
 */
@Singleton
public class BaseRegexQueueHandler {
   private final ImmutableBiMap<URI, Region> uriToRegion;
   Pattern pattern = Pattern.compile("<QueueUrl>(https://[\\S&&[^<]]+)</QueueUrl>");

   @Inject
   protected BaseRegexQueueHandler(Map<Region, URI> regionMap) {
      this.uriToRegion = ImmutableBiMap.copyOf(regionMap).inverse();
   }

   public Set<Queue> parse(String in) {
      Set<Queue> queues = Sets.newLinkedHashSet();
      Matcher matcher = pattern.matcher(in);
      while (matcher.find()) {
         String uriText = matcher.group(1);
         String queueName = uriText.substring(uriText.lastIndexOf('/') + 1);
         URI location = URI.create(uriText);
         String regionString = uriText.substring(0, uriText.indexOf(".com/") + 4);
         URI regionURI = URI.create(regionString);
         Region region = uriToRegion.get(regionURI);
         queues.add(new Queue(region, queueName, location));
      }
      return queues;
   }

}