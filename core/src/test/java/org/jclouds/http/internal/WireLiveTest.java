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
package org.jclouds.http.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.hash.Hashing.md5;
import static com.google.common.io.BaseEncoding.base16;
import static com.google.common.io.ByteStreams.copy;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.jclouds.io.ByteSources.asByteSource;
import static org.testng.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jclouds.logging.Logger;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public class WireLiveTest {

   private static final String sysHttpStreamUrl = System.getProperty("jclouds.wire.httpstream.url");
   private static final String sysHttpStreamMd5 = System.getProperty("jclouds.wire.httpstream.md5");

   private static class ConnectionTester implements Callable<Void> {
      private final InputStream fromServer;

      private ConnectionTester(InputStream fromServer) {
         this.fromServer = fromServer;
      }

      public Void call() throws Exception {
         HttpWire wire = setUp();
         InputStream in = wire.input(fromServer);
         ByteArrayOutputStream out = new ByteArrayOutputStream();// TODO
         copy(in, out);
         byte[] compare = md5().hashBytes(out.toByteArray()).asBytes();
         Thread.sleep(100);
         assertEquals(base16().lowerCase().encode(compare), checkNotNull(sysHttpStreamMd5, sysHttpStreamMd5));
         assertEquals(((BufferLogger) wire.getWireLog()).buff.toString().getBytes().length, 3331484);
         return null;
      }
   }

   static class BufferLogger implements Logger {
      StringBuilder buff = new StringBuilder();

      public void debug(String message, Object... args) {
         buff.append(message);
      }

      public void error(String message, Object... args) {
      }

      public void error(Throwable throwable, String message, Object... args) {
      }

      public String getCategory() {
         return null;
      }

      public void info(String message, Object... args) {
      }

      public boolean isDebugEnabled() {
         return true;
      }

      public boolean isErrorEnabled() {
         return false;
      }

      public boolean isInfoEnabled() {
         return false;
      }

      public boolean isTraceEnabled() {
         return false;
      }

      public boolean isWarnEnabled() {
         return false;
      }

      public void trace(String message, Object... args) {
      }

      public void warn(String message, Object... args) {
      }

      public void warn(Throwable throwable, String message, Object... args) {
      }

   }

   public static HttpWire setUp() throws Exception {
      BufferLogger bufferLogger = new BufferLogger();
      HttpWire wire = new HttpWire();
      wire.wireLog = (bufferLogger);
      return wire;
   }

   public HttpWire setUpSynch() throws Exception {
      BufferLogger bufferLogger = new BufferLogger();
      HttpWire wire = new HttpWire();
      wire.wireLog = (bufferLogger);
      return wire;
   }

   @Test(groups = "live")
   public void testRemoteInputStream() throws Exception {
      try {
         URL url = new URL(checkNotNull(sysHttpStreamUrl, "sysHttpStreamUrl"));
         URLConnection connection = url.openConnection();
         HttpWire wire = setUp();
         InputStream in = wire.input(connection.getInputStream());
         byte[] compare = asByteSource(in).hash(md5()).asBytes();
         Thread.sleep(100);
         assertEquals(base16().lowerCase().encode(compare), checkNotNull(sysHttpStreamMd5, sysHttpStreamMd5));
         assertEquals(((BufferLogger) wire.getWireLog()).buff.toString().getBytes().length, 3331484);
      } catch (UnknownHostException e) {
         // probably in offline mode
      }
   }

   @Test(groups = "live", enabled = false)
   public void testCopyRemoteInputStream() throws Exception {
      URL url = new URL(checkNotNull(sysHttpStreamUrl, "sysHttpStreamUrl"));
      URLConnection connection = url.openConnection();
      Callable<Void> callable = new ConnectionTester(connection.getInputStream());
      Future<Void> result = newCachedThreadPool().submit(callable);
      result.get(30, TimeUnit.SECONDS);
   }

   @Test(groups = "live")
   public void testRemoteInputStreamSynch() throws Exception {
      try {
         URL url = new URL(checkNotNull(sysHttpStreamUrl, "sysHttpStreamUrl"));
         URLConnection connection = url.openConnection();
         HttpWire wire = setUpSynch();
         InputStream in = wire.input(connection.getInputStream());
         byte[] compare = asByteSource(in).hash(md5()).asBytes();
         Thread.sleep(100);
         assertEquals(base16().lowerCase().encode(compare), checkNotNull(sysHttpStreamMd5, sysHttpStreamMd5));
         assertEquals(((BufferLogger) wire.getWireLog()).buff.toString().getBytes().length, 3331484);
      } catch (UnknownHostException e) {
         // probably in offline mode
      }
   }

}
