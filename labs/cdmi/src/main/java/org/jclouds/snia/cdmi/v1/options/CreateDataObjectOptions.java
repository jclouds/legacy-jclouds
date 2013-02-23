package org.jclouds.snia.cdmi.v1.options;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

/**
 * CreateDataObjectOptions options supported in the REST API for the CREATE CDMI Data Object
 * operation. <h2>
 * 
 * @author Kenneth Nagin
 */
public class CreateDataObjectOptions extends CreateCDMIObjectOptions {

   public CreateDataObjectOptions() {
      jsonObjectBody.addProperty("value", "");
   }

   /**
    * Create CDMI data object with metadata
    * 
    * @param metadata
    * @return CreateDataObjectOptions
    */
   public CreateDataObjectOptions metadata(Map<String, String> metadata) {
      super.metadata(metadata);
      return this;
   }

   /**
    * Create CDMI data object with mimetype
    * 
    * @param mimetype
    * @return CreateDataObjectOptions
    */
   public CreateDataObjectOptions mimetype(String mimetype) {
      jsonObjectBody.addProperty("mimetype", mimetype);
      this.payload = jsonObjectBody.toString();
      return this;
   }

   /**
    * Create CDMI data object with value equal to empty string
    * 
    * @return CreateDataObjectOptions
    */
   public CreateDataObjectOptions value() {
      this.payload = jsonObjectBody.toString();
      return this;
   }

   /**
    * Create CDMI data object with String value
    * 
    * @param value
    *           String value
    * @return CreateDataObjectOptions
    */
   public CreateDataObjectOptions value(String value) {
      jsonObjectBody.addProperty("value", (value == null) ? "" : value);
      this.payload = jsonObjectBody.toString();
      return this;
   }

   /**
    * Create CDMI data object with byte array value
    * 
    * @param value
    *           byte array value byte array is converted to a String value
    * @return CreateDataObjectOptions
    */
   public CreateDataObjectOptions value(byte[] value) throws IOException {
      jsonObjectBody.addProperty("value", (value == null) ? "" : new DataInputStream(
               new ByteArrayInputStream(value)).readUTF());
      this.payload = jsonObjectBody.toString();
      return this;
   }

   /**
    * Create CDMI data object with file value
    * 
    * @param value
    *           File File is converted to a String value with charset UTF_8
    * @return CreateDataObjectOptions
    */
   public CreateDataObjectOptions value(File value) throws IOException {
      jsonObjectBody.addProperty("value", (value == null) ? "" : Files.toString(value, Charsets.UTF_8));
      this.payload = jsonObjectBody.toString();
      return this;
   }

   /**
    * Create CDMI data object with file value
    * 
    * @param value
    *           File
    * @param charset
    *           character set of file File is converted to a String value
    * @return CreateDataObjectOptions
    */
   public CreateDataObjectOptions value(File value, Charset charset) throws IOException {
      jsonObjectBody.addProperty("value", (value == null) ? "" : Files.toString(value, charset));
      this.payload = jsonObjectBody.toString();
      return this;
   }

   /**
    * Create CDMI data object with InputStream value
    * 
    * @param value
    *           InputSteam InputSteam is converted to a String value with charset UTF_8
    * @return CreateDataObjectOptions
    */
   public CreateDataObjectOptions value(InputStream value) throws IOException {
      jsonObjectBody.addProperty("value",
               (value == null) ? "" : CharStreams.toString(new InputStreamReader(value, Charsets.UTF_8)));
      this.payload = jsonObjectBody.toString();
      return this;
   }

   /**
    * Create CDMI data object with InputStream value
    * 
    * @param value
    *           InputSteam
    * @param charset
    *           character set of input stream InputSteam is converted to a String value with charset
    *           UTF_8
    * @return CreateDataObjectOptions
    */
   public CreateDataObjectOptions value(InputStream value, Charset charset) throws IOException {
      jsonObjectBody.addProperty("value",
               (value == null) ? "" : CharStreams.toString(new InputStreamReader(value, charset)));
      this.payload = jsonObjectBody.toString();
      return this;
   }

   public static class Builder {
      public static CreateDataObjectOptions metadata(Map<String, String> metadata) {
         CreateDataObjectOptions options = new CreateDataObjectOptions();
         return (CreateDataObjectOptions) options.metadata(metadata);
      }

      public static CreateDataObjectOptions mimetype(String mimetype) {
         CreateDataObjectOptions options = new CreateDataObjectOptions();
         return (CreateDataObjectOptions) options.mimetype(mimetype);
      }

      public static CreateDataObjectOptions value() {
         CreateDataObjectOptions options = new CreateDataObjectOptions();
         return (CreateDataObjectOptions) options.value();
      }

      public static CreateDataObjectOptions value(String value) {
         CreateDataObjectOptions options = new CreateDataObjectOptions();
         return (CreateDataObjectOptions) options.value(value);
      }

      public static CreateDataObjectOptions value(byte[] value) throws IOException {
         CreateDataObjectOptions options = new CreateDataObjectOptions();
         return (CreateDataObjectOptions) options.value(value);
      }

      public static CreateDataObjectOptions value(File value) throws IOException {
         CreateDataObjectOptions options = new CreateDataObjectOptions();
         return (CreateDataObjectOptions) options.value(value);
      }

      public static CreateDataObjectOptions value(File value, Charset charset) throws IOException {
         CreateDataObjectOptions options = new CreateDataObjectOptions();
         return (CreateDataObjectOptions) options.value(value, charset);
      }

      public static CreateDataObjectOptions value(InputStream value) throws IOException {
         CreateDataObjectOptions options = new CreateDataObjectOptions();
         return (CreateDataObjectOptions) options.value(value);
      }

      public static CreateDataObjectOptions value(InputStream value, Charset charset) throws IOException {
         CreateDataObjectOptions options = new CreateDataObjectOptions();
         return (CreateDataObjectOptions) options.value(value, charset);
      }

   }
}
