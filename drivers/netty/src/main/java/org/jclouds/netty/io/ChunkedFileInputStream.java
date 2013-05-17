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
package org.jclouds.netty.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.stream.ChunkedFile;

/**
 * 
 * 
 * 
 * 
 * @author Tibor Kiss
 */
public class ChunkedFileInputStream extends InputStream {

   private static final int CHUNK_SIZE = 8192;

   private ChunkedFile chunks;
   private ChannelBuffer chunk;

   private IOException ex;

   public ChunkedFileInputStream(String filename, long offset, long length) {
      this(new File(filename), offset, length);
   }

   public ChunkedFileInputStream(File file, long offset, long length) {
      try {
         this.chunks = new ChunkedFile(new RandomAccessFile(file, "r"), offset, length, CHUNK_SIZE);
      } catch (IOException ex) {
         this.ex = ex;
      }
   }

   private ChannelBuffer getChunk() throws Exception {
      if (ex != null) {
         throw ex;
      }
      if (chunk == null) {
         chunk = ChannelBuffer.class.cast(chunks.nextChunk());
      }
      if (chunk != null) {
         if (chunk.readableBytes() < 1 && chunks.hasNextChunk()) {
            chunk = ChannelBuffer.class.cast(chunks.nextChunk());
            if (chunk.readableBytes() < 1) {
               return null;
            }
         }
      } else {
         return null;
      }
      return chunk;
   }

   @Override
   public int read() throws IOException {
      try {
         ChannelBuffer chunk = getChunk();
         if (chunk == null)
            return -1;
         if (chunk.readableBytes() < 1)
            return -1;
         int readIndex = chunk.readerIndex();
         byte abyte = chunk.getByte(readIndex);
         chunk.readerIndex(readIndex + 1);
         return (int) abyte;
      } catch (Exception e) {
         throw new IOException(e);
      }
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOException {
      try {
         ChannelBuffer chunk = getChunk();
         if (chunk == null)
            return -1;
         int readable = chunk.readableBytes();
         if (readable < 1)
            return -1;
         if (readable > len) {
            readable = len;
         }
         int readIndex = chunk.readerIndex();
         chunk.getBytes(readIndex, b, off, readable);
         chunk.readerIndex(readIndex + readable);
         return readable;
      } catch (Exception e) {
         throw new IOException(e);
      }
   }

   @Override
   public void close() throws IOException {
      try {
         chunks.close();
      } catch (Exception e) {
         throw new IOException(e);
      }
   }

}
