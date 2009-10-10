/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.http.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.HttpUtils.MD5InputStreamResult;
import org.jclouds.logging.Logger;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "core.WireLiveTest")
public class WireLiveTest {

   private static final String sysHttpStreamUrl = System.getProperty("jclouds.wire.httpstream.url");
   private static final String sysHttpStreamMd5 = System.getProperty("jclouds.wire.httpstream.md5");

   private static class ConnectionTester implements Callable<Void> {
      private final InputStream fromServer;

      private ConnectionTester(InputStream fromServer) {
         this.fromServer = fromServer;
      }

      public Void call() throws Exception {
         Wire wire = setUp();
         InputStream in = wire.input(fromServer);
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         IOUtils.copy(in, out);
         MD5InputStreamResult compare = HttpUtils.generateMD5Result(new ByteArrayInputStream(out
                  .toByteArray()));
         Thread.sleep(100);
         assertEquals(HttpUtils.toHexString(compare.eTag), checkNotNull(sysHttpStreamMd5,
                  sysHttpStreamMd5));
         assertEquals(((BufferLogger) wire.wireLog).buff.toString().getBytes().length, 3331484);
         return null;
      }
   }

   static class BufferLogger implements Logger {
      StringBuffer buff = new StringBuffer();

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

   public static Wire setUp() throws Exception {
      ExecutorService service = Executors.newCachedThreadPool();
      BufferLogger bufferLogger = new BufferLogger();
      Wire wire = new Wire(service);
      wire.wireLog = bufferLogger;
      return wire;
   }

   public Wire setUpSynch() throws Exception {
      ExecutorService service = new WithinThreadExecutorService();
      BufferLogger bufferLogger = new BufferLogger();
      Wire wire = new Wire(service);
      wire.wireLog = bufferLogger;
      return wire;
   }

   @Test(groups = "live")
   public void testRemoteInputInputStream() throws Exception {
      URL url = new URL(checkNotNull(sysHttpStreamUrl, "sysHttpStreamUrl"));
      URLConnection connection = url.openConnection();
      Wire wire = setUp();
      InputStream in = wire.input(connection.getInputStream());
      MD5InputStreamResult compare = HttpUtils.generateMD5Result(in);
      Thread.sleep(100);
      assertEquals(HttpUtils.toHexString(compare.eTag), checkNotNull(sysHttpStreamMd5,
               sysHttpStreamMd5));
      assertEquals(((BufferLogger) wire.wireLog).buff.toString().getBytes().length, 3331484);
   }

   @Test(groups = "live")
   public void testCopyRemoteInputInputStream() throws Exception {
      URL url = new URL(checkNotNull(sysHttpStreamUrl, "sysHttpStreamUrl"));
      URLConnection connection = url.openConnection();
      Callable<Void> callable = new ConnectionTester(connection.getInputStream());
      Future<Void> result = Executors.newCachedThreadPool().submit(callable);
      result.get(30, TimeUnit.SECONDS);
   }

   @Test(groups = "live")
   public void testRemoteInputInputStreamSynch() throws Exception {
      URL url = new URL(checkNotNull(sysHttpStreamUrl, "sysHttpStreamUrl"));
      URLConnection connection = url.openConnection();
      Wire wire = setUpSynch();
      InputStream in = wire.input(connection.getInputStream());
      MD5InputStreamResult compare = HttpUtils.generateMD5Result(in);
      Thread.sleep(100);
      assertEquals(HttpUtils.toHexString(compare.eTag), checkNotNull(sysHttpStreamMd5,
               sysHttpStreamMd5));
      assertEquals(((BufferLogger) wire.wireLog).buff.toString().getBytes().length, 3331484);
   }

}
