package org.jclouds.io;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.jclouds.encryption.internal.Base64;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

/**
 * functions related to or replacing those in {@link com.google.common.io.InputSupplier}
 * 
 * @author Adrian Cole
 */
@Beta
public class InputSuppliers {
   /**
    * base64 encodes bytes from the supplied supplier as they are read.
    */
   public static Base64InputSupplier base64Encoder(InputSupplier<? extends InputStream> supplier) throws IOException {
      return new Base64InputSupplier(supplier, Base64.ENCODE + Base64.DONT_BREAK_LINES);
   }

   /**
    * base64 decodes bytes from the supplied supplier as they are read.
    */
   public static Base64InputSupplier base64Decoder(InputSupplier<? extends InputStream> supplier) throws IOException {
      return new Base64InputSupplier(supplier, Base64.DECODE);
   }

   @VisibleForTesting
   static class Base64InputSupplier implements InputSupplier<InputStream> {

      private final InputSupplier<? extends InputStream> delegate;
      private final int mode;

      Base64InputSupplier(InputSupplier<? extends InputStream> inputSupplier, int mode) {
         this.delegate = checkNotNull(inputSupplier, "delegate");
         this.mode = mode;
      }

      @Override
      public InputStream getInput() throws IOException {
         return new Base64.InputStream(delegate.getInput(), mode);
      }

   }

   public static InputSupplier<? extends InputStream> of(final InputStream in) {
      checkNotNull(in, "in");
      return new InputSupplier<InputStream>() {

         @Override
         public InputStream getInput() throws IOException {
            return in;
         }

      };
   }

   public static InputSupplier<? extends InputStream> of(byte[] in) {
      return ByteStreams.newInputStreamSupplier(checkNotNull(in, "in"));
   }

   public static InputSupplier<? extends InputStream> of(String in) {
      return of(checkNotNull(in, "in").getBytes(Charsets.UTF_8));
   }
}
