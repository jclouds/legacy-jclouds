package org.jclouds.http;

import java.io.File;
import java.io.InputStream;

/**
 * 
 * @author Adrian Cole
 */
public interface PayloadEnclosing {

   /**
    * Sets payload for the request or the content from the response. If size isn't set, this will
    * attempt to discover it.
    * 
    * @param data
    *           typically InputStream for downloads, or File, byte [], String, or InputStream for
    *           uploads.
    */
   void setPayload(Payload data);

   void setPayload(File data);

   void setPayload(byte[] data);

   void setPayload(InputStream data);

   void setPayload(String data);

   Payload getPayload();

   /**
    * @return InputStream, if downloading, or whatever was set during {@link #setPayload(Object)}
    */
   InputStream getContent();


   void setContentLength(long contentLength);

   /**
    * Returns the total size of the downloaded object, or the chunk that's available.
    * <p/>
    * Chunking is only used when org.jclouds.http.GetOptions is called with options like tail,
    * range, or startAt.
    * 
    * @return the length in bytes that can be be obtained from {@link #getContent()}
    * @see org.jclouds.http.HttpHeaders#CONTENT_LENGTH
    * @see GetObjectOptions
    */
   Long getContentLength();
   
   /**
    * generate an MD5 Hash for the current data.
    * <p/>
    * <h2>Note</h2>
    * <p/>
    * If this is an InputStream, it will be converted to a byte array first.
    */
   void generateMD5();

}