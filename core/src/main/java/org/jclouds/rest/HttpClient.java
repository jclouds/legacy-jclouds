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
package org.jclouds.rest;

import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.io.Payload;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.ResponseParser;

/**
 * Simple client
 * 
 * @author Adrian Cole
 */
public interface HttpClient {
   /**
    * @return eTag
    */
   @PUT
   @ResponseParser(ParseETagHeader.class)
   String put(@EndpointParam URI location, Payload payload);

   /**
    * @return eTag
    */
   @POST
   @ResponseParser(ParseETagHeader.class)
   String post(@EndpointParam URI location, Payload payload);

   /**
    * @see HttpClient#exists
    */
   @HEAD
   @Fallback(FalseOnNotFoundOr404.class)
   boolean exists(@EndpointParam URI location);

   /**
    * @return null if the resource didn't exist.
    */
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   InputStream get(@EndpointParam URI location);

   /**
    * 
    * @param request
    * @return response, but make sure you consume its content.
    */
   HttpResponse invoke(HttpRequest request);

   /**
    * @return false if the resource didn't exist.
    */
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   boolean delete(@EndpointParam URI location);

}
