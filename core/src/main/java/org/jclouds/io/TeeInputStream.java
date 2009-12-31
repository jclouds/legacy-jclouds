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
package org.jclouds.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Ported from org.apache.commons.io.input.TeeInputStream
 * 
 * InputStream proxy that transparently writes a copy of all bytes read from the delegate stream to
 * a given OutputStream. Using {@link #skip(long)} or {@link #mark(int)}/{@link #reset()} on the
 * stream will result on some bytes from the input stream being skipped or duplicated in the output
 * stream.
 * <p>
 * The delegate input stream is closed when the {@link #close()} method is called on this proxy. It
 * is configurable whether the associated output stream will also closed.
 * 
 * @see Commons IO 1.4
 * @see org.apache.commons.io.input.TeeInputStream
 */
public class TeeInputStream extends InputStream {

   /**
    * The input stream to delegate to.
    */
   private final InputStream delegate;

   /**
    * The output stream that will receive a copy of all bytes read from the delegate input stream.
    */
   private final OutputStream branch;

   /**
    * Flag for closing also the associated output stream when this stream is closed.
    */
   private final boolean closeBranch;

   /**
    * Creates a TeeInputStream that delegates to the given {@link InputStream} and copies all read
    * bytes to the given {@link OutputStream}. The given output stream will not be closed when this
    * stream gets closed.
    * 
    * @param input
    *           input stream to be delegate
    * @param branch
    *           output stream that will receive a copy of all bytes read
    */
   public TeeInputStream(InputStream delegate, OutputStream branch) {
      this(delegate, branch, false);
   }

   /**
    * Creates a TeeInputStream that proxies the given {@link InputStream} and copies all read bytes
    * to the given {@link OutputStream}. The given output stream will be closed when this stream
    * gets closed if the closeBranch parameter is <code>true</code>.
    * 
    * @param input
    *           input stream to be delegate
    * @param branch
    *           output stream that will receive a copy of all bytes read
    * @param closeBranch
    *           flag for closing also the output stream when this stream is closed
    */
   public TeeInputStream(InputStream delegate, OutputStream branch, boolean closeBranch) {
      this.delegate = delegate;
      this.branch = branch;
      this.closeBranch = closeBranch;
   }

   /**
    * Closes the delegate input stream and, if so configured, the associated output stream. An
    * exception thrown from one stream will not prevent closing of the other stream.
    * 
    * @throws IOException
    *            if either of the streams could not be closed
    */
   @Override
   public void close() throws IOException {
      try {
         delegate.close();
      } finally {
         if (closeBranch) {
            branch.close();
         }
      }
   }

   /**
    * Reads a single byte from the delegate input stream and writes it to the associated output
    * stream.
    * 
    * @return next byte from the stream, or -1 if the stream has ended
    * @throws IOException
    *            if the stream could not be read (or written)
    */
   public int read() throws IOException {
      int ch = delegate.read();
      if (ch != -1) {
         branch.write(ch);
      }
      return ch;
   }

   /**
    * Reads bytes from the delegate input stream and writes the read bytes to the associated output
    * stream.
    * 
    * @param bts
    *           byte buffer
    * @param st
    *           start offset within the buffer
    * @param end
    *           maximum number of bytes to read
    * @return number of bytes read, or -1 if the stream has ended
    * @throws IOException
    *            if the stream could not be read (or written)
    */
   @Override
   public int read(byte[] bts, int st, int end) throws IOException {
      int n = delegate.read(bts, st, end);
      if (n != -1) {
         branch.write(bts, st, n);
      }
      return n;
   }

   /**
    * Reads bytes from the delegate input stream and writes the read bytes to the associated output
    * stream.
    * 
    * @param bts
    *           byte buffer
    * @return number of bytes read, or -1 if the stream has ended
    * @throws IOException
    *            if the stream could not be read (or written)
    */
   @Override
   public int read(byte[] bts) throws IOException {
      int n = delegate.read(bts);
      if (n != -1) {
         branch.write(bts, 0, n);
      }
      return n;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int available() throws IOException {
      return delegate.available();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public synchronized void mark(int readlimit) {
      delegate.mark(readlimit);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean markSupported() {
      return delegate.markSupported();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public synchronized void reset() throws IOException {
      delegate.reset();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public long skip(long n) throws IOException {
      return delegate.skip(n);
   }

}
