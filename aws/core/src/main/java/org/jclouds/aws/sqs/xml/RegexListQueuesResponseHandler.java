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
package org.jclouds.aws.sqs.xml;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.sqs.SQS;
import org.jclouds.aws.sqs.domain.Queue;
import org.jclouds.aws.sqs.xml.internal.BaseRegexQueueHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.base.Throwables;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/APIReference/Query_QueryListQueues.html"
 *      />
 * @author Adrian Cole
 */
@Singleton
public class RegexListQueuesResponseHandler extends BaseRegexQueueHandler implements
         Function<HttpResponse, Set<Queue>> {
   @Inject
   RegexListQueuesResponseHandler(@SQS Map<String, URI> regionMap) {
      super(regionMap);
   }

   @Override
   public Set<Queue> apply(HttpResponse response) {
      try {
         return parse(Utils.toStringAndClose(response.getContent()));
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         try {
            response.getContent().close();
         } catch (IOException e) {
            Throwables.propagate(e);
         }
      }
   }

}