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
package org.jclouds.scriptbuilder.domain;

import static com.google.common.collect.Iterables.transform;
import static java.lang.String.format;

import java.net.URI;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;

/**
 * unzips the content into a directory
 * 
 * @author Adrian Cole
 */
public class UnzipHttpResponseIntoDirectory extends InterpretableStatement {
   /**
    * 
    * 
    * @param method
    *           http method: ex GET
    * @param endpoint
    *           uri corresponding to the request
    * @param headers
    *           request headers to send
    */
   public UnzipHttpResponseIntoDirectory(String method, URI endpoint, Multimap<String, String> headers, String dir) {
      super(
               format(
                        "({md} %s &&{cd} %s &&curl -X -L %s -s --retry 20 %s %s >extract.zip && unzip -o -qq extract.zip&& rm extract.zip)\n",
                        dir, dir, method, Joiner.on(' ').join(
                                 transform(headers.entries(), new Function<Entry<String, String>, String>() {

                                    @Override
                                    public String apply(Entry<String, String> from) {
                                       return String.format("-H \"%s: %s\"", from.getKey(), from.getValue());
                                    }

                                 })), endpoint.toASCIIString()));
   }
}
