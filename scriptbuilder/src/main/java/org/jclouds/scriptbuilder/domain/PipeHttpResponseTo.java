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

import static com.google.common.base.Preconditions.checkArgument;

import java.net.URI;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * Pipes the content of the http response to a shell command that accepts input from stdin
 * 
 * @author Adrian Cole
 */
public class PipeHttpResponseTo extends InterpretableStatement {
   /**
    * 
    * @param toExec
    *           what to invoke
    * @param method
    *           http method: ex GET
    * @param endpoint
    *           uri corresponding to the request
    * @param headers
    *           request headers to send
    */
   public PipeHttpResponseTo(Statement toExec, String method, URI endpoint, Multimap<String, String> headers) {
      super(String.format("%s -X %s %s %s |(%s)\n", SaveHttpResponseTo.CURL, method, Joiner.on(' ').join(
               Iterables.transform(headers.entries(), new Function<Entry<String, String>, String>() {

                  @Override
                  public String apply(Entry<String, String> from) {
                     return String.format("-H \"%s: %s\"", from.getKey(), from.getValue());
                  }

               })), endpoint.toASCIIString(), toExec.render(OsFamily.UNIX)));
   }

   public String render(OsFamily family) {
      checkArgument(family != OsFamily.WINDOWS, "windows not supported");
      return super.render(family);
   }
}
