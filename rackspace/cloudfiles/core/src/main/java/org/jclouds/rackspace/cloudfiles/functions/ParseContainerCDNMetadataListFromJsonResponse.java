/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rackspace.cloudfiles.functions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.SortedSet;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rackspace.cloudfiles.domain.ContainerCDNMetadata;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This parses {@link ContainerCDNMetadata} from a gson string.
 * 
 * @author James Murty
 */
public class ParseContainerCDNMetadataListFromJsonResponse extends ParseJson<SortedSet<ContainerCDNMetadata>>
    {
   
   @Inject
   public ParseContainerCDNMetadataListFromJsonResponse(Gson gson) {
      super(gson);
   }

   public SortedSet<ContainerCDNMetadata> apply(InputStream stream) {
      String toParse;
      try {
          toParse = IOUtils.toString(stream);
      } catch (IOException e1) {
         throw new RuntimeException(e1);
      }
      // Ticket #1871 bad quoting on cdn uri
      stream = IOUtils.toInputStream(toParse.replaceAll("m,","m\","));
      Type listType = new TypeToken<SortedSet<ContainerCDNMetadata>>() {
      }.getType();
      try {
         return gson.fromJson(new InputStreamReader(stream, "UTF-8"), listType);
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}