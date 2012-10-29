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
package org.jclouds.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/**
 * {@link InputStream} implementation that allows chaining of various streams for seamless
 * sequential reading
 * 
 * @author Adrian Cole
 * @author Tomas Varaneckas <tomas.varaneckas@gmail.com>
 */
public class InputStreamChain extends InputStream {

   /**
    * Input stream chain
    */
   private final LinkedList<InputStream> streams = new LinkedList<InputStream>();

   /**
    * Currently active stream
    */
   private InputStream current;

   public InputStreamChain(InputStream... inputStreams) {
      for (InputStream stream : inputStreams) {
         addInputStream(stream);
      }
   }

   /**
    * Adds input stream to the end of chain
    * 
    * @param stream
    *           InputStream to add to chain
    * @return instance of self (for fluent calls)
    */
   public InputStreamChain addInputStream(final InputStream stream) {
      streams.addLast(stream);
      if (current == null) {
         current = streams.removeFirst();
      }
      return this;
   }

   /**
    * Adds a String to the end of chain
    * 
    * @param value
    *           String to add to the chain
    * @return instance of self (for fluent calls)
    */
   public InputStreamChain addAsInputStream(final String value) {
      return addInputStream(Strings2.toInputStream(value));
   }

   @Override
   public int read() throws IOException {
      int bit = current.read();
      if (bit == -1 && streams.size() > 0) {
         try {
            current.close();
         } catch (final IOException e) {
            // replace this with a call to logging facility
            e.printStackTrace();
         }
         current = streams.removeFirst();
         bit = read();
      }
      return bit;
   }

   @Override
   public int available() throws IOException {
      int available = current.available();
      for (InputStream stream : streams) {
         available += stream.available();
      }
      return available;
   }

   @Override
   public void close() throws IOException {
      current.close();
   }

   @Override
   public boolean markSupported() {
      return current.markSupported();
   }

   @Override
   public synchronized void mark(int i) {
      current.mark(i);
   }

   @Override
   public synchronized void reset() throws IOException {
      current.reset();
   }

   @Override
   public long skip(long l) throws IOException {
      return current.skip(l);
   }

}
