/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.http.httpnio.pool;

import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.testng.Assert.assertEquals;

import java.net.InetSocketAddress;
import java.net.URI;

import org.apache.http.nio.protocol.AsyncNHttpClientHandler;
import org.apache.http.params.HttpParams;
import org.testng.annotations.Test;

/**
 * Tests parsing of nio
 * 
 * @author Adrian Cole
 */
@Test(testName = "httpnio.NioHttpCommandConnectionPool")
public class NioHttpCommandConnectionPoolTest {

   public void testConstructorGoodPort() throws Exception {
      NioHttpCommandConnectionPool pool = new NioHttpCommandConnectionPool(null, null, null, null,
               createNiceMock(AsyncNHttpClientHandler.class), null,
               createNiceMock(HttpParams.class), URI.create("http://localhost:80"), 1, 1);
      assertEquals(pool.getTarget(), new InetSocketAddress("localhost", 80));
   }

   public void testConstructorGoodSSLPort() throws Exception {
      NioHttpCommandConnectionPool pool = new NioHttpCommandConnectionPool(null, null, null, null,
               createNiceMock(AsyncNHttpClientHandler.class), null,
               createNiceMock(HttpParams.class), URI.create("https://localhost:443"), 1, 1);
      assertEquals(pool.getTarget(), new InetSocketAddress("localhost", 443));
   }

   public void testConstructorUnspecifiedPort() throws Exception {
      NioHttpCommandConnectionPool pool = new NioHttpCommandConnectionPool(null, null, null, null,
               createNiceMock(AsyncNHttpClientHandler.class), null,
               createNiceMock(HttpParams.class), URI.create("http://localhost"), 1, 1);
      assertEquals(pool.getTarget(), new InetSocketAddress("localhost", 80));
   }

   public void testConstructorUnspecifiedSSLPort() throws Exception {
      NioHttpCommandConnectionPool pool = new NioHttpCommandConnectionPool(null, null, null, null,
               createNiceMock(AsyncNHttpClientHandler.class), null,
               createNiceMock(HttpParams.class), URI.create("https://localhost"), 1, 1);
      assertEquals(pool.getTarget(), new InetSocketAddress("localhost", 443));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testConstructorNullURI() throws Exception {
      new NioHttpCommandConnectionPool(null, null, null, null,
               createNiceMock(AsyncNHttpClientHandler.class), null,
               createNiceMock(HttpParams.class), null, 1, 1);
   }

   public void testConstructorWeirdName() throws Exception {
      NioHttpCommandConnectionPool pool = new NioHttpCommandConnectionPool(null, null, null, null,
               createNiceMock(AsyncNHttpClientHandler.class), null,
               createNiceMock(HttpParams.class), URI
                        .create("http://adriancole.blobstore1138eu.s3-external-3.amazonaws.com"),
               1, 1);
      assertEquals(pool.getTarget(), new InetSocketAddress(
               "adriancole.blobstore1138eu.s3-external-3.amazonaws.com", 80));
   }

}
