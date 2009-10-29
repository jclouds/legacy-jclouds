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
package org.jclouds.http.internal;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.logging.Logger;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "core.WireTest")
public class WireTest {

   class BufferLogger implements Logger {
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

   public Wire setUp() throws Exception {
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

   public void testInputInputStream() throws Exception {
      Wire wire = setUp();
      InputStream in = wire.input(new ByteArrayInputStream("foo".getBytes()));
      String compare = Utils.toStringAndClose(in);
      Thread.sleep(100);
      assertEquals(compare, "foo");
      assertEquals(((BufferLogger) wire.wireLog).buff.toString(), "<< \"foo\"");
   }

   public void testInputInputStreamSynch() throws Exception {
      Wire wire = setUpSynch();
      InputStream in = wire.input(new ByteArrayInputStream("foo".getBytes()));
      String compare = Utils.toStringAndClose(in);
      assertEquals(compare, "foo");
      assertEquals(((BufferLogger) wire.wireLog).buff.toString(), "<< \"foo\"");
   }

   public void testOutputInputStream() throws Exception {
      Wire wire = setUp();
      InputStream in = wire.output(new ByteArrayInputStream("foo".getBytes()));
      String compare = Utils.toStringAndClose(in);
      Thread.sleep(100);
      assertEquals(compare, "foo");
      assertEquals(((BufferLogger) wire.wireLog).buff.toString(), ">> \"foo\"");
   }

   public void testOutputBytes() throws Exception {
      Wire wire = setUp();
      wire.output("foo".getBytes());
      assertEquals(((BufferLogger) wire.wireLog).buff.toString(), ">> \"foo\"");
   }

   public void testOutputString() throws Exception {
      Wire wire = setUp();
      wire.output("foo");
      assertEquals(((BufferLogger) wire.wireLog).buff.toString(), ">> \"foo\"");
   }
}
