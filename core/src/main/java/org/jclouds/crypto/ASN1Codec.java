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
package org.jclouds.crypto;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterators.filter;
import static com.google.common.io.ByteStreams.limit;
import static com.google.common.io.ByteStreams.toByteArray;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Iterator;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.primitives.Bytes;

/**
 * 
 * This codec is based on code from {@code bouncycastle.org}. It simplifies extraction of RSA keys from {@code ASN.1}
 * encoding. This only parses the {@code BigInteger} elements needed to generate {@link KeySpec}
 * 
 * @author Adrian Cole
 */
@Beta
final class ASN1Codec {

   private ASN1Codec() {
   };

   @SuppressWarnings("unchecked")
   static RSAPublicKeySpec decodeRSAPublicKey(byte[] bytes) {
      List<Object> seq = createASN1Sequence(bytes);
      checkArgument(seq.size() == 2, "expected 2 components of ASN1Sequence: %s", seq);
      if (seq.get(1) instanceof List) {
         seq = List.class.cast(seq.get(1));
      }
      return new RSAPublicKeySpec(bigIntAt(seq, 0), bigIntAt(seq, 1));
   }

   static RSAPrivateCrtKeySpec decodeRSAPrivateKey(byte[] bytes) {
      List<Object> seq = createASN1Sequence(bytes);
      checkArgument(seq.size() >= 9, "not enough elements (%s) for a private key", seq.size(), seq);
      int version = bigIntAt(seq, 0).intValue();
      checkArgument(version == 0 || version == 1, "wrong version %s for RSA private key", version);
      return new RSAPrivateCrtKeySpec(bigIntAt(seq, 1), bigIntAt(seq, 2), bigIntAt(seq, 3), bigIntAt(seq, 4), bigIntAt(
            seq, 5), bigIntAt(seq, 6), bigIntAt(seq, 7), bigIntAt(seq, 8));
   }

   private static BigInteger bigIntAt(List<Object> seq, int index) {
      return BigInteger.class.cast(seq.get(index));
   }

   private static final int TAG = 0x02;
   private static final int CONSTRUCTED = 0x20;
   private static final int INTEGER = 0x02;
   private static final int BIT_STRING = 0x03;
   private static final int SEQUENCE = 0x10;

   @SuppressWarnings("unchecked")
   private static List<Object> createASN1Sequence(byte[] input) {
      Object out = create(new ByteArrayInputStream(input), input.length).get();
      checkArgument(out instanceof List, "expected List not %s", out);
      return List.class.cast(out);
   }

   /**
    * skips most {@code ASN.1} tags as we are only interested in the integers. As such, this either creates a
    * {@code BigInteger}, a list of them, or a nested list.
    */
   private static Optional<Object> buildObject(int tag, int tagNo, InputStream in, int limit) {
      boolean isConstructed = (tag & CONSTRUCTED) != 0;
      InputStream limited = limit(in, limit);
      if (isConstructed && tagNo == SEQUENCE) {
         return Optional.<Object> of(buildEncodableList(limited, limit));
      }
      byte[] bytes = toArray(limited);
      switch (tagNo) {
      case BIT_STRING:
         return Optional.<Object> of(nestedASN1Sequence(bytes));
      case INTEGER:
         return Optional.<Object> of(new BigInteger(bytes));
      }
      return Optional.absent();
   }

   private static List<Object> nestedASN1Sequence(byte[] bytes) {
      checkArgument(bytes.length >= 1, "truncated BIT_STRING detected");
      byte[] data = new byte[bytes.length - 1];
      System.arraycopy(bytes, 1, data, 0, bytes.length - 1);
      return createASN1Sequence(data);
   }

   private static List<Object> buildEncodableList(final InputStream in, final int limit) {
      return ImmutableList.copyOf(filter(new Iterator<Object>() {

         boolean hasNext = true;

         public boolean hasNext() {
            return hasNext;
         }

         public Object next() {
            int tag = read(in);
            if (tag == -1) {
               hasNext = false;
               return null;
            }
            checkArgument(tag != 0, "invalid tag %s", tag);
            int tagNo = tag & 0x1f;
            int length = readLength(in, limit);
            checkArgument(length >= 0, "indefinite length not supported");
            return buildObject(tag, tagNo, in, length).orNull();
         }

         public void remove() {
         }

      }, notNull()));
   }

   private static Optional<Object> create(InputStream in, int limit) {
      int tag = read(in);
      if (tag == -1)
         return Optional.absent();
      checkArgument(tag != 0, "invalid tag %s", tag);
      int tagNo = tag & 0x1f;
      int length = readLength(in, limit);
      checkArgument(length >= 0, "indefinite length not supported");
      return buildObject(tag, tagNo, in, length);
   }

   private static int readLength(InputStream s, int limit) {
      int length = read(s);
      checkArgument(length >= 0, "EOF found when length expected");
      checkArgument(length != 0x80, "indefinite-length encoding not supported");
      if (length > 127) {
         int size = length & 0x7f;
         checkArgument(size <= 4, "DER length more than 4 bytes: %s", size);
         length = 0;
         for (int i = 0; i < size; i++) {
            int next = read(s);
            checkArgument(next >= 0, "EOF found reading length");
            length = (length << 8) + next;
         }
         checkArgument(length >= 0, "corrupted stream - negative length %s found", length);
         checkArgument(length < limit, "corrupted stream - length %s out of bounds %s", length, limit);
      }
      return length;
   }

   private static int read(InputStream s) {
      try {
         return s.read();
      } catch (IOException e) {
         throw propagate(e);// impossible as we are only using a byte array
      }
   }

   private static byte[] toArray(InputStream limited) {
      try {
         return toByteArray(limited);
      } catch (IOException e) {
         throw propagate(e); // impossible as we are only using a byte array
      }
   }

   static byte[] encode(RSAPrivateCrtKey key) {
      List<BigInteger> seq = ImmutableList.<BigInteger> builder()
                                          .add(BigInteger.valueOf(0)) // version
                                          .add(key.getModulus())
                                          .add(key.getPublicExponent())
                                          .add(key.getPrivateExponent())
                                          .add(key.getPrimeP())
                                          .add(key.getPrimeQ())
                                          .add(key.getPrimeExponentP())
                                          .add(key.getPrimeExponentQ())
                                          .add(key.getCrtCoefficient()).build();
      int length = 0;
      for (BigInteger part : seq) {
         byte[] bytes = part.toByteArray();
         length += 1 + calculateBodyLength(bytes.length) + bytes.length;
      }

      Builder<Byte> output = ImmutableList.<Byte> builder();
      output.add((byte) (SEQUENCE | CONSTRUCTED));
      writeLength(output, length);
      for (BigInteger part : seq) {
         byte[] bytes = part.toByteArray();
         output.add((byte) TAG);
         writeLength(output, bytes.length);
         output.addAll(Bytes.asList(bytes));
      }
      return Bytes.toArray(output.build());
   }

   private static void writeLength(Builder<Byte> output, int length) {
      if (length > 127) {
         int size = 1;
         int val = length;
         while ((val >>>= 8) != 0) {
            size++;
         }
         output.add((byte) (size | 0x80));
         for (int i = (size - 1) * 8; i >= 0; i -= 8) {
            output.add((byte) (length >> i));
         }
      } else {
         output.add((byte) length);
      }
   }

   private static int calculateBodyLength(int length) {
      int count = 1;
      if (length > 127) {
         int size = 1;
         int val = length;
         while ((val >>>= 8) != 0) {
            size++;
         }
         for (int i = (size - 1) * 8; i >= 0; i -= 8) {
            count++;
         }
      }
      return count;
   }

}
