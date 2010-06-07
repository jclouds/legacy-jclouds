/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.boxdotnet;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.boxdotnet.BoxDotNetClient;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to BoxDotNet via their REST API.
 * <p/>
 *
 * @see BoxDotNetClient
 * @see <a href="TODO: insert URL of provider documentation" />
 * @author Adrian Cole
 */
@Endpoint(BoxDotNet.class)
@RequestFilters(BasicAuthentication.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface BoxDotNetAsyncClient {
   /*
    * TODO: define interface methods for BoxDotNet 
    */
   
   /**
    * @see BoxDotNetClient#list()
    */
   @GET
   @Path("/items")
   ListenableFuture<String> list();
   
   /**
    * @see BoxDotNetClient#get(long)
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/items/{itemId}")
   ListenableFuture<String> get(@PathParam("itemId") long id);
   
   /**
    * @see BoxDotNetClient#delete
    */
   @DELETE
   @Path("/items/{itemId}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> delete(@PathParam("itemId") long id);
}
