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

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpResponse;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseCredentialsFromJsonResponse implements
      Function<HttpResponse, Credentials> {

   private final ParseServerNameToCredentialsMapFromJsonResponse parser;

   @Inject
   ParseCredentialsFromJsonResponse(
         ParseServerNameToCredentialsMapFromJsonResponse parser) {
      this.parser = parser;
   }

   @Override
   public Credentials apply(HttpResponse input) {
      Map<String, Credentials> returnVal = parser.apply(input);
      checkState(!(returnVal.size() > 1),
            "expecting only 1 credential in response, but had more: "
                  + returnVal.keySet());
      return (returnVal.size() > 0) ? Iterables.getOnlyElement(returnVal.values()) : null;
   }
}
