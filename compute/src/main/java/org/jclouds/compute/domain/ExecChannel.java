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
package org.jclouds.compute.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.base.Supplier;
import com.google.common.io.Closeables;

/**
 * A current connection to an exec'd command.  Please ensure you call {@link ExecChannel#close}
 * 
 * @author Adrian Cole
 */
public class ExecChannel implements Closeable {

   private final OutputStream input;
   private final InputStream output;
   private final InputStream error;
   private final Supplier<Integer> exitStatus;
   private final Closeable closer;

   public ExecChannel(OutputStream input, InputStream output, InputStream error, Supplier<Integer> exitStatus,
            Closeable closer) {
      this.input = checkNotNull(input, "input");
      this.output = checkNotNull(output, "output");
      this.error = checkNotNull(error, "error");
      this.exitStatus = checkNotNull(exitStatus, "exitStatus");
      this.closer = checkNotNull(closer, "closer");
   }

   /**
    * 
    * @return the command's {@code stdin} stream.
    */
   public OutputStream getInput() {
      return input;
   }

   /**
    * 
    * @return the command's {@code stderr} stream.
    */
   public InputStream getError() {
      return error;
   }

   /**
    * 
    * @return the command's {@code stdout} stream.
    */
   public InputStream getOutput() {
      return output;
   }

   /**
    * 
    * @return the exit status of the command if it was received, or {@code null} if this information
    *         was not received.
    */
   public Supplier<Integer> getExitStatus() {
      return exitStatus;
   }

   /**
    * closes resources associated with this channel.
    */
   @Override
   public void close() throws IOException {
      Closeables.closeQuietly(input);
      Closeables.closeQuietly(output);
      Closeables.closeQuietly(error);
      closer.close();
   }
}
