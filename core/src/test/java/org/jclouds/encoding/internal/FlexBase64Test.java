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
package org.jclouds.encoding.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.primitives.Bytes;


/**
 * @author Jason T. Greene
 */
public class FlexBase64Test {

   public static final String TOWEL = "A towel, it says, is about the most massively useful thing an interstellar " +
         "hitchhiker can have. Partly it has great practical value - you can wrap it around you for warmth as you " +
         "bound across the cold moons of Jaglan Beta; you can lie on it on the brilliant marble-sanded beaches of " +
         "Santraginus V, inhaling the heady sea vapours; you can sleep under it beneath the stars which shine so " +
         "redly on the desert world of Kakrafoon; use it to sail a mini raft down the slow heavy river Moth; wet " +
         "it for use in hand-to- hand-combat; wrap it round your head to ward off noxious fumes or to avoid the " +
         "gaze of the Ravenous Bugblatter Beast of Traal (a mindboggingly stupid animal, it assumes that if you " +
         "can't see it, it can't see you - daft as a bush, but very ravenous); you can wave your towel in " +
         "emergencies as a distress signal, and of course dry yourself off with it if it still seems to be clean " +
         "enough." +
         "\n\n" +
         "More importantly, a towel has immense psychological value. For some reason, if a strag " +
         "(strag: non-hitch hiker) discovers that a hitch hiker has his towel with him, he will automatically " +
         "assume that he is also in possession of a toothbrush, face flannel, soap, tin of biscuits, flask, compass, " +
         "map, ball of string, gnat spray, wet weather gear, space suit etc., etc. Furthermore, the strag will then " +
         "happily lend the hitch hiker any of these or a dozen other items that the hitch hiker might accidentally " +
         "have \"lost\". What the strag will think is that any man who can hitch the length and breadth of the " +
         "galaxy, rough it, slum it, struggle against terrible odds, win through, and still knows where his towel " +
         "is is clearly a man to be reckoned with.\n";

   public static final String TOWEL_BASE64 =
         "QSB0b3dlbCwgaXQgc2F5cywgaXMgYWJvdXQgdGhlIG1vc3QgbWFzc2l2ZWx5IHVzZWZ1bCB0aGlu\r\n"+
         "ZyBhbiBpbnRlcnN0ZWxsYXIgaGl0Y2hoaWtlciBjYW4gaGF2ZS4gUGFydGx5IGl0IGhhcyBncmVh\r\n"+
         "dCBwcmFjdGljYWwgdmFsdWUgLSB5b3UgY2FuIHdyYXAgaXQgYXJvdW5kIHlvdSBmb3Igd2FybXRo\r\n"+
         "IGFzIHlvdSBib3VuZCBhY3Jvc3MgdGhlIGNvbGQgbW9vbnMgb2YgSmFnbGFuIEJldGE7IHlvdSBj\r\n"+
         "YW4gbGllIG9uIGl0IG9uIHRoZSBicmlsbGlhbnQgbWFyYmxlLXNhbmRlZCBiZWFjaGVzIG9mIFNh\r\n"+
         "bnRyYWdpbnVzIFYsIGluaGFsaW5nIHRoZSBoZWFkeSBzZWEgdmFwb3VyczsgeW91IGNhbiBzbGVl\r\n"+
         "cCB1bmRlciBpdCBiZW5lYXRoIHRoZSBzdGFycyB3aGljaCBzaGluZSBzbyByZWRseSBvbiB0aGUg\r\n"+
         "ZGVzZXJ0IHdvcmxkIG9mIEtha3JhZm9vbjsgdXNlIGl0IHRvIHNhaWwgYSBtaW5pIHJhZnQgZG93\r\n"+
         "biB0aGUgc2xvdyBoZWF2eSByaXZlciBNb3RoOyB3ZXQgaXQgZm9yIHVzZSBpbiBoYW5kLXRvLSBo\r\n"+
         "YW5kLWNvbWJhdDsgd3JhcCBpdCByb3VuZCB5b3VyIGhlYWQgdG8gd2FyZCBvZmYgbm94aW91cyBm\r\n"+
         "dW1lcyBvciB0byBhdm9pZCB0aGUgZ2F6ZSBvZiB0aGUgUmF2ZW5vdXMgQnVnYmxhdHRlciBCZWFz\r\n"+
         "dCBvZiBUcmFhbCAoYSBtaW5kYm9nZ2luZ2x5IHN0dXBpZCBhbmltYWwsIGl0IGFzc3VtZXMgdGhh\r\n"+
         "dCBpZiB5b3UgY2FuJ3Qgc2VlIGl0LCBpdCBjYW4ndCBzZWUgeW91IC0gZGFmdCBhcyBhIGJ1c2gs\r\n"+
         "IGJ1dCB2ZXJ5IHJhdmVub3VzKTsgeW91IGNhbiB3YXZlIHlvdXIgdG93ZWwgaW4gZW1lcmdlbmNp\r\n"+
         "ZXMgYXMgYSBkaXN0cmVzcyBzaWduYWwsIGFuZCBvZiBjb3Vyc2UgZHJ5IHlvdXJzZWxmIG9mZiB3\r\n"+
         "aXRoIGl0IGlmIGl0IHN0aWxsIHNlZW1zIHRvIGJlIGNsZWFuIGVub3VnaC4KCk1vcmUgaW1wb3J0\r\n"+
         "YW50bHksIGEgdG93ZWwgaGFzIGltbWVuc2UgcHN5Y2hvbG9naWNhbCB2YWx1ZS4gRm9yIHNvbWUg\r\n"+
         "cmVhc29uLCBpZiBhIHN0cmFnIChzdHJhZzogbm9uLWhpdGNoIGhpa2VyKSBkaXNjb3ZlcnMgdGhh\r\n"+
         "dCBhIGhpdGNoIGhpa2VyIGhhcyBoaXMgdG93ZWwgd2l0aCBoaW0sIGhlIHdpbGwgYXV0b21hdGlj\r\n"+
         "YWxseSBhc3N1bWUgdGhhdCBoZSBpcyBhbHNvIGluIHBvc3Nlc3Npb24gb2YgYSB0b290aGJydXNo\r\n"+
         "LCBmYWNlIGZsYW5uZWwsIHNvYXAsIHRpbiBvZiBiaXNjdWl0cywgZmxhc2ssIGNvbXBhc3MsIG1h\r\n"+
         "cCwgYmFsbCBvZiBzdHJpbmcsIGduYXQgc3ByYXksIHdldCB3ZWF0aGVyIGdlYXIsIHNwYWNlIHN1\r\n"+
         "aXQgZXRjLiwgZXRjLiBGdXJ0aGVybW9yZSwgdGhlIHN0cmFnIHdpbGwgdGhlbiBoYXBwaWx5IGxl\r\n"+
         "bmQgdGhlIGhpdGNoIGhpa2VyIGFueSBvZiB0aGVzZSBvciBhIGRvemVuIG90aGVyIGl0ZW1zIHRo\r\n"+
         "YXQgdGhlIGhpdGNoIGhpa2VyIG1pZ2h0IGFjY2lkZW50YWxseSBoYXZlICJsb3N0Ii4gV2hhdCB0\r\n"+
         "aGUgc3RyYWcgd2lsbCB0aGluayBpcyB0aGF0IGFueSBtYW4gd2hvIGNhbiBoaXRjaCB0aGUgbGVu\r\n"+
         "Z3RoIGFuZCBicmVhZHRoIG9mIHRoZSBnYWxheHksIHJvdWdoIGl0LCBzbHVtIGl0LCBzdHJ1Z2ds\r\n"+
         "ZSBhZ2FpbnN0IHRlcnJpYmxlIG9kZHMsIHdpbiB0aHJvdWdoLCBhbmQgc3RpbGwga25vd3Mgd2hl\r\n"+
         "cmUgaGlzIHRvd2VsIGlzIGlzIGNsZWFybHkgYSBtYW4gdG8gYmUgcmVja29uZWQgd2l0aC4K\r\n";
   
   private static final String KNOWLEDGE =
         "Man is distinguished, not only by his reason, but by this singular passion from " +
         "other animals, which is a lust of the mind, that by a perseverance of delight " +
         "in the continued and indefatigable generation of knowledge, exceeds the short " +
         "vehemence of any carnal pleasure.";

   private static final String KNOWLEDGE_ENCODED =
         "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz\r\n" +
         "IHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2Yg\r\n" +
         "dGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGlu\r\n" +
         "dWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRo\r\n" +
         "ZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=\r\n";
   @Test
   public void testEncoderDecoder() throws IOException {
      byte[] nums = new byte[32768];
      for (int i = 0; i < 32768; i++) {
         nums[i] = (byte) (i % 255);
      }

      byte[] output = new byte[65535];
      FlexBase64.Encoder encoder = FlexBase64.createEncoder(true);
      int last = encoder.encode(nums, 0, nums.length, output, 0, output.length);
      last = encoder.complete(output, last);

      byte[] decode = new byte[nums.length];
      FlexBase64.Decoder decoder = FlexBase64.createDecoder();
      last = decoder.decode(output, 0, last, decode, 0, decode.length);

      Assert.assertEquals(nums.length, last);

      for (int i = 0; i < last; i++) {
         Assert.assertEquals(nums[i], decode[i]);
      }
   }

   @Test
   public void testEncoderDecoderBuffer() throws IOException {
      byte[] nums = new byte[32768];
      for (int i = 0; i < 32768; i++) {
         nums[i] = (byte) (i % 255);
      }

      ByteBuffer source = ByteBuffer.wrap(nums);
      ByteBuffer target = ByteBuffer.allocate(65535);

      FlexBase64.Encoder encoder = FlexBase64.createEncoder(true);
      encoder.encode(source, target);
      encoder.complete(target);

      ByteBuffer decoded = ByteBuffer.allocate(nums.length);
      FlexBase64.Decoder decoder = FlexBase64.createDecoder();
      target.flip();
      decoder.decode(target, decoded);

      decoded.flip();

      Assert.assertEquals(nums.length, decoded.remaining());

      for (int i = 0; i < nums.length; i++) {
         Assert.assertEquals(nums[i], decoded.get());
      }
   }

   @Test
   public void testDrain() throws IOException {
      byte[] bytes = "c3VyZS4=\r\n\r\n!".getBytes("US-ASCII");
      ByteBuffer source = ByteBuffer.wrap(bytes);
      ByteBuffer target = ByteBuffer.allocateDirect(100);
      FlexBase64.createDecoder().decode(source, target);
      Assert.assertEquals((byte) '\r' & 0xFF, source.get() & 0xFF);
      Assert.assertEquals((byte) '\n' & 0xFF, source.get() & 0xFF);
      Assert.assertEquals((byte) '!' & 0xFF, source.get() & 0xFF);

      byte[] dest = new byte[100];
      FlexBase64.Decoder decoder = FlexBase64.createDecoder();
      decoder.decode(bytes, 0, bytes.length, dest, 0, dest.length);
      Assert.assertEquals(10, decoder.getLastInputPosition());

      bytes = "YXN1cmUu\r\n\r\nA".getBytes("US-ASCII");
      dest = new byte[6];
      decoder = FlexBase64.createDecoder();
      decoder.decode(bytes, 0, bytes.length, dest, 0, dest.length);
      Assert.assertEquals(12, decoder.getLastInputPosition());
   }

   @Test
   public void testEncoderDecoderBufferLoops() throws IOException {
      byte[] nums = new byte[32768];
      for (int i = 0; i < 32768; i++) {
         nums[i] = (byte) (i % 255);
      }
      ByteBuffer source = ByteBuffer.wrap(nums);
      ByteBuffer target = ByteBuffer.allocate(65535);

      FlexBase64.Encoder encoder = FlexBase64.createEncoder(true);
      int limit = target.limit();
      target.limit(100);
      while (source.remaining() > 0) {
         encoder.encode(source, target);
         int add = limit - target.position();
         add = add < 100 ? add : 100;
         target.limit(target.limit() + add);
      }
      encoder.complete(target);

      ByteBuffer decoded = ByteBuffer.allocate(nums.length);
      FlexBase64.Decoder decoder = FlexBase64.createDecoder();
      target.flip();

      limit = decoded.limit();
      decoded.limit(100);
      while (target.remaining() > 0) {
         decoder.decode(target, decoded);
         int add = limit - decoded.position();
         add = add < 100 ? add : 100;
         decoded.limit(decoded.position() + add);
      }

      decoded.flip();

      Assert.assertEquals(nums.length, decoded.remaining());

      for (int i = 0; i < nums.length; i++) {
         Assert.assertEquals(nums[i], decoded.get());
      }
   }

   @Test
   public void testEncoderDecoderLoopWithOffset() throws IOException {
      byte[] nums = new byte[32768];
      for (int i = 0; i < 32768; i++) {
         nums[i] = (byte) (i % 255);
      }

      byte[] output = new byte[65535];
      FlexBase64.Encoder encoder = FlexBase64.createEncoder(true);

      int opos = 5;
      int pos = 0;
      while (pos < 32768) {
         opos = encoder.encode(nums, pos, nums.length, output, opos, opos + 10000);
         pos = encoder.getLastInputPosition();
      }
      opos = encoder.complete(output, opos);

      byte[] decode = new byte[nums.length];
      FlexBase64.Decoder decoder = FlexBase64.createDecoder();
      int stop = opos;
      pos = 5;
      int last = 0;
      while (pos < stop) {
         last = decoder.decode(output, pos, stop, decode, last, last + 10000);
         pos = decoder.getLastInputPosition();
      }

      Assert.assertEquals(nums.length, last);

      for (int i = 0; i < last; i++) {
         Assert.assertEquals(nums[i], decode[i]);
      }
   }

   @Test
   public void testEncodeString() throws Exception {
      byte[] data = ("Man is distinguished, not only by his reason, but by this singular passion from "
            + "other animals, which is a lust of the mind, that by a perseverance of delight "
            + "in the continued and indefatigable generation of knowledge, exceeds the short "
            + "vehemence of any carnal pleasure.").getBytes("US-ASCII");

      String expect = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz\r\n"
            + "IHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2Yg\r\n"
            + "dGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGlu\r\n"
            + "dWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRo\r\n"
            + "ZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=\r\n";

      Assert.assertEquals(expect, FlexBase64.encodeString(data, true));
      Assert.assertEquals(expect, FlexBase64.encodeString(ByteBuffer.wrap(data), true));

      byte[] data2 = new byte[data.length + 10];
      System.arraycopy(data, 0, data2, 5, data.length);
      Assert.assertEquals(expect, FlexBase64.encodeString(data2, 5, data.length + 5, true));

   }

   @Test
   public void testEncodeBytes() throws Exception {
      byte[] data = ("Man is distinguished, not only by his reason, but by this singular passion from "
            + "other animals, which is a lust of the mind, that by a perseverance of delight "
            + "in the continued and indefatigable generation of knowledge, exceeds the short "
            + "vehemence of any carnal pleasure.").getBytes("US-ASCII");

      byte[] expect = ("TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz\r\n"
            + "IHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2Yg\r\n"
            + "dGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGlu\r\n"
            + "dWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRo\r\n"
            + "ZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=\r\n").getBytes("US-ASCII");

      Assert.assertEquals(Bytes.asList(expect), Bytes.asList(FlexBase64.encodeBytes(data, 0, data.length, true)));

   }

   @Test
   public void testDecodeString() throws Exception {
      String expect = "Man is distinguished, not only by his reason, but by this singular passion from "
            + "other animals, which is a lust of the mind, that by a perseverance of delight "
            + "in the continued and indefatigable generation of knowledge, exceeds the short "
            + "vehemence of any carnal pleasure.";

      String encoded = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz\r\n"
            + "IHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2Yg\r\n"
            + "dGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGlu\r\n"
            + "dWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRo\r\n"
            + "ZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=\r\n";

      ByteBuffer buffer = FlexBase64.decode(encoded);
      Assert.assertEquals(expect, new String(buffer.array(), buffer.arrayOffset(), buffer.limit(), "US-ASCII"));

      buffer = FlexBase64.decode(ByteBuffer.wrap(encoded.getBytes("US-ASCII")));
      Assert.assertEquals(expect, new String(buffer.array(), buffer.arrayOffset(), buffer.limit(), "US-ASCII"));

      buffer = FlexBase64.decode(encoded.getBytes("US-ASCII"), 0, encoded.length());
      Assert.assertEquals(expect, new String(buffer.array(), buffer.arrayOffset(), buffer.limit(), "US-ASCII"));

      byte[] output = new byte[expect.length()];
      FlexBase64.createDecoder().decode(encoded, output);
      Assert.assertEquals(expect, new String(output, 0, output.length, "US-ASCII"));
   }

   @Test
   public void testURLString() throws Exception {
       byte[] source = {0x6b, (byte) 0xf6, (byte) 0xfe};
       Assert.assertEquals("a_b-", FlexBase64.encodeURLString(source, 0, 3));
       Assert.assertEquals(Bytes.asList(source), Bytes.asList(FlexBase64.decode("a_b-").array()));
       String actual = FlexBase64.encodeURLString("test".getBytes("UTF-8"), 0, 4);
       Assert.assertEquals("dGVzdA", actual);
       ByteBuffer decode = FlexBase64.decode(actual);
       Assert.assertEquals("test", new String(decode.array(), 0, decode.limit(), "UTF-8"));
       byte[] bytes = TOWEL.getBytes("UTF-8");
       Assert.assertEquals(TOWEL_BASE64.replace("\r\n",""), FlexBase64.encodeURLString(ByteBuffer.wrap(bytes)));
       bytes = KNOWLEDGE.getBytes("UTF-8");
       String replace = KNOWLEDGE_ENCODED.replace("\r\n", "");
       Assert.assertEquals(replace.substring(0, replace.length() - 1), FlexBase64.encodeURLString(ByteBuffer.wrap(bytes)));
   }

   @Test
   public void testEncoderInputStream() throws Exception {
      FlexBase64.EncoderInputStream encoderInputStream = FlexBase64.createEncoderInputStream(new ByteArrayInputStream(
            TOWEL.getBytes("US-ASCII")));
      ByteBuffer base64 = ByteBuffer.wrap(TOWEL_BASE64.getBytes("US-ASCII"));
      verifyStreamContents(encoderInputStream, base64);

      encoderInputStream = FlexBase64.createEncoderInputStream(new ByteArrayInputStream(TOWEL.getBytes("US-ASCII")),
            8192, false);
      base64 = ByteBuffer.wrap(TOWEL_BASE64.replace("\r\n", "").getBytes("US-ASCII"));
      verifyStreamContents(encoderInputStream, base64);

      encoderInputStream = FlexBase64.createEncoderInputStream(new ByteArrayInputStream(TOWEL.getBytes("US-ASCII")));
      base64 = ByteBuffer.wrap(TOWEL_BASE64.getBytes("US-ASCII"));
      verifyStreamContentsOneByte(encoderInputStream, base64);

      encoderInputStream = FlexBase64.createEncoderInputStream(new ByteArrayInputStream(TOWEL.getBytes("US-ASCII")),
            8192, false);
      base64 = ByteBuffer.wrap(TOWEL_BASE64.replace("\r\n", "").getBytes("US-ASCII"));
      verifyStreamContentsOneByte(encoderInputStream, base64);
   }

   @Test
   public void testDecoderInputStream() throws Exception {
      FlexBase64.DecoderInputStream stream = FlexBase64.createDecoderInputStream(new ByteArrayInputStream(TOWEL_BASE64
            .getBytes("US-ASCII")));
      ByteBuffer base64 = ByteBuffer.wrap(TOWEL.getBytes("US-ASCII"));
      verifyStreamContents(stream, base64);

      stream = FlexBase64.createDecoderInputStream(new ByteArrayInputStream(TOWEL_BASE64.replace("\r\n", "").getBytes(
            "US-ASCII")));
      base64 = ByteBuffer.wrap(TOWEL.getBytes("US-ASCII"));
      verifyStreamContents(stream, base64);

      stream = FlexBase64.createDecoderInputStream(new ByteArrayInputStream(TOWEL_BASE64.getBytes("US-ASCII")));
      base64 = ByteBuffer.wrap(TOWEL.getBytes("US-ASCII"));
      verifyStreamContentsOneByte(stream, base64);

      stream = FlexBase64.createDecoderInputStream(new ByteArrayInputStream(TOWEL_BASE64.replace("\r\n", "").getBytes(
            "US-ASCII")));
      base64 = ByteBuffer.wrap(TOWEL.replace("\r\n", "").getBytes("US-ASCII"));
      verifyStreamContentsOneByte(stream, base64);

      stream = FlexBase64.createDecoderInputStream(
            new ByteArrayInputStream(TOWEL_BASE64.replace("\r\n", "").getBytes("US-ASCII")), 10);
      base64 = ByteBuffer.wrap(TOWEL.replace("\r\n", "").getBytes("US-ASCII"));
      verifyStreamContentsOneByte(stream, base64);
   }

   @Test
   public void testEncoderOutputStream() throws Exception {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      FlexBase64.EncoderOutputStream stream = FlexBase64.createEncoderOutputStream(baos);
      byte[] towel = TOWEL.getBytes("US-ASCII");
      stream.write(towel);
      stream.close();
      Assert.assertEquals(Bytes.asList(TOWEL_BASE64.getBytes("US-ASCII")), Bytes.asList(baos.toByteArray()));

      baos.reset();
      stream = FlexBase64.createEncoderOutputStream(baos, 8192, false);
      stream.write(towel);
      stream.close();
      Assert.assertEquals(Bytes.asList(TOWEL_BASE64.replace("\r\n", "").getBytes("US-ASCII")), Bytes.asList(baos.toByteArray()));

      baos.reset();
      stream = FlexBase64.createEncoderOutputStream(baos, 8192, true);
      chunkWrite(stream, towel);
      Assert.assertEquals(Bytes.asList(TOWEL_BASE64.getBytes("US-ASCII")), Bytes.asList(baos.toByteArray()));

      baos.reset();
      stream = FlexBase64.createEncoderOutputStream(baos, 8192, false);
      chunkWrite(stream, towel);
      Assert.assertEquals(Bytes.asList(TOWEL_BASE64.replace("\r\n", "").getBytes("US-ASCII")), Bytes.asList(baos.toByteArray()));

      baos.reset();
      stream = FlexBase64.createEncoderOutputStream(baos, 8192, true);
      oneByteWrite(stream, towel);
      Assert.assertEquals(Bytes.asList(TOWEL_BASE64.getBytes("US-ASCII")), Bytes.asList(baos.toByteArray()));

      baos.reset();
      stream = FlexBase64.createEncoderOutputStream(baos, 8192, false);
      oneByteWrite(stream, towel);
      Assert.assertEquals(Bytes.asList(TOWEL_BASE64.replace("\r\n", "").getBytes("US-ASCII")), Bytes.asList(baos.toByteArray()));
   }

   @Test
   public void testDecoderOutputStream() throws Exception {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      FlexBase64.DecoderOutputStream stream = FlexBase64.createDecoderOutputStream(baos);
      byte[] towel = TOWEL_BASE64.getBytes("US-ASCII");
      byte[] towelStrip = TOWEL_BASE64.replace("\r\n", "").getBytes("US-ASCII");

      stream.write(towel);
      stream.close();
      Assert.assertEquals(Bytes.asList(TOWEL.getBytes("US-ASCII")), Bytes.asList(baos.toByteArray()));

      baos.reset();
      stream = FlexBase64.createDecoderOutputStream(baos);
      stream.write(towelStrip);
      stream.close();
      Assert.assertEquals(Bytes.asList(TOWEL.getBytes("US-ASCII")), Bytes.asList(baos.toByteArray()));

      baos.reset();
      stream = FlexBase64.createDecoderOutputStream(baos);
      chunkWrite(stream, towel);
      Assert.assertEquals(Bytes.asList(TOWEL.getBytes("US-ASCII")), Bytes.asList(baos.toByteArray()));

      baos.reset();
      stream = FlexBase64.createDecoderOutputStream(baos);
      chunkWrite(stream, towelStrip);
      Assert.assertEquals(Bytes.asList(TOWEL.getBytes("US-ASCII")), Bytes.asList(baos.toByteArray()));

      baos.reset();
      stream = FlexBase64.createDecoderOutputStream(baos);
      oneByteWrite(stream, towel);
      Assert.assertEquals(Bytes.asList(TOWEL.getBytes("US-ASCII")), Bytes.asList(baos.toByteArray()));

      baos.reset();
      stream = FlexBase64.createDecoderOutputStream(baos);
      oneByteWrite(stream, towelStrip);
      Assert.assertEquals(Bytes.asList(TOWEL.getBytes("US-ASCII")), Bytes.asList(baos.toByteArray()));

      baos.reset();
      stream = FlexBase64.createDecoderOutputStream(baos, 10);
      chunkWrite(stream, towelStrip);
      Assert.assertEquals(Bytes.asList(TOWEL.getBytes("US-ASCII")), Bytes.asList(baos.toByteArray()));

   }

   private void chunkWrite(OutputStream stream, byte[] towel) throws IOException {
      ByteBuffer wrap = ByteBuffer.wrap(towel);
      int remaining = wrap.remaining();
      while (remaining > 0) {
         int left = remaining < 100 ? remaining : 100;
         byte[] chunk = new byte[left];
         wrap.get(chunk);
         stream.write(chunk);
         remaining = wrap.remaining();
      }

      stream.close();
   }

   private void oneByteWrite(OutputStream stream, byte[] towel) throws IOException {
      ByteBuffer wrap = ByteBuffer.wrap(towel);
      while (wrap.remaining() > 0) {
         stream.write(wrap.get() & 0xFF);
      }

      stream.close();
   }

   private void verifyStreamContentsOneByte(InputStream inputStream, ByteBuffer base64) throws IOException {
      int read = inputStream.read();
      while (read > -1) {
         byte expected = base64.get();
         Assert.assertEquals(expected, read);

         read = inputStream.read();
      }

      Assert.assertEquals(0, base64.remaining());
   }

   private void verifyStreamContents(InputStream inputStream, ByteBuffer base64) throws IOException {
      byte[] buffer = new byte[100];
      int read = inputStream.read(buffer);
      while (read > -1) {
         for (int i = 0; i < read; i++) {
            byte expected = base64.get();
            byte actual = buffer[i];
            Assert.assertEquals(expected, actual);
         }

         read = inputStream.read(buffer);
      }

      Assert.assertEquals(0, base64.remaining());
   }

}