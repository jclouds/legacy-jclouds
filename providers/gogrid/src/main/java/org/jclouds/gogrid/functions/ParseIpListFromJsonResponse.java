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
package org.jclouds.gogrid.functions;

import java.util.SortedSet;

import javax.inject.Inject;

import org.jclouds.gogrid.domain.Ip;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;

import com.google.common.base.Function;
import com.google.inject.Singleton;

/**
 * Parses {@link org.jclouds.gogrid.domain.Ip ips} from a json string.
 * 
 * @author Oleksiy Yarmula
 */
@Singleton
public class ParseIpListFromJsonResponse implements
      Function<HttpResponse, SortedSet<Ip>> {

   private final ParseJson<GenericResponseContainer<Ip>> json;

   @Inject
   ParseIpListFromJsonResponse(ParseJson<GenericResponseContainer<Ip>> json) {
      this.json = json;
   }

   @Override
   public SortedSet<Ip> apply(HttpResponse arg0) {
      return json.apply(arg0).getList();
   }

}
