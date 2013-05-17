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

import java.util.Map;

import javax.inject.Inject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/APIReference/Query_QueryDeleteMessageBatch.html"
 *      />
 * 
 * @author Adrian Cole
 */
public class IdHandler extends TextFromSingleElementHandler<Map.Entry<String, String>> {
   @Inject
   protected IdHandler(String elementName) {
      super("Id");
   }

   @Override
   public Map.Entry<String, String> apply(String in) {
      return Iterables.getOnlyElement(ImmutableMap.of(in, in).entrySet());
   }

}
