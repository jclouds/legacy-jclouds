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
package org.jclouds.sqs.xml;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.sqs.xml.internal.BaseRegexQueueHandler;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/APIReference/Query_QueryListQueues.html"
 *      />
 * @author Adrian Cole
 */
@Singleton
public class RegexQueueHandler extends BaseRegexQueueHandler implements Function<HttpResponse, URI> {
   private final ReturnStringIf2xx returnStringIf200;

   @Inject
   public RegexQueueHandler(ReturnStringIf2xx returnStringIf200) {
      this.returnStringIf200 = returnStringIf200;
   }

   @Override
   public URI apply(HttpResponse response) {
      return Iterables.getOnlyElement(parse(returnStringIf200.apply(response)));
   }
}
