#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package ${package};

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.filters.BasicAuthentication;
import ${package}.${providerName}Client;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.NullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to ${providerName} via their REST API.
 * <p/>
 *
 * @see ${providerName}Client
 * @see <a href="TODO: insert URL of provider documentation" />
 * @author ${author}
 */
@RequestFilters(BasicAuthentication.class)
public interface ${providerName}AsyncClient {
   public static final String API_VERSION = "${providerApiVersion}";

   /*
    * TODO: define interface methods for ${providerName} 
    */
   
   /**
    * @see ${providerName}Client#list()
    */
   @GET
   @Path("/items")
   @Consumes(MediaType.TEXT_PLAIN)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<String> list();
   
   /**
    * @see ${providerName}Client#get(long)
    */
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(MediaType.TEXT_PLAIN)
   @Path("/items/{itemId}")
   ListenableFuture<String> get(@PathParam("itemId") long id);
   
   /**
    * @see ${providerName}Client#delete
    */
   @DELETE
   @Path("/items/{itemId}")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> delete(@PathParam("itemId") long id);
}
