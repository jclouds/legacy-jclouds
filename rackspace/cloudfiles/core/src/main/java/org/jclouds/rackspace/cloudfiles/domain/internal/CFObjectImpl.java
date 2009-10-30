package org.jclouds.rackspace.cloudfiles.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.InputStream;

import javax.inject.Inject;

import org.jclouds.blobstore.domain.MD5InputStreamResult;
import org.jclouds.blobstore.functions.CalculateSize;
import org.jclouds.blobstore.functions.GenerateMD5;
import org.jclouds.blobstore.functions.GenerateMD5Result;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Default Implementation of {@link CFObject}.
 * 
 * @author Adrian Cole
 */
public class CFObjectImpl implements CFObject, Comparable<CFObject> {
   private final GenerateMD5Result generateMD5Result;
   private final GenerateMD5 generateMD5;
   private final CalculateSize calculateSize;
   private final MutableObjectInfoWithMetadata info;
   private Object data;
   private Multimap<String, String> allHeaders = HashMultimap.create();
   private Long contentLength;

   @Inject
   public CFObjectImpl(GenerateMD5Result generateMD5Result, GenerateMD5 generateMD5,
            CalculateSize calculateSize, MutableObjectInfoWithMetadata info) {
      this.generateMD5Result = generateMD5Result;
      this.generateMD5 = generateMD5;
      this.calculateSize = calculateSize;
      this.info = info;
   }

   /**
    * {@inheritDoc}
    */
   public void generateMD5() {
      checkState(data != null, "data");
      if (data instanceof InputStream) {
         MD5InputStreamResult result = generateMD5Result.apply((InputStream) data);
         getInfo().setHash(result.md5);
         setContentLength(result.length);
         setData(result.data);
      } else {
         getInfo().setHash(generateMD5.apply(data));
      }
   }

   /**
    * {@inheritDoc}
    */
   public Object getData() {
      return data;
   }

   /**
    * {@inheritDoc}
    */
   public void setData(Object data) {
      this.data = checkNotNull(data, "data");
      if (getContentLength() == null) {
         Long size = calculateSize.apply(data);
         if (size != null)
            this.setContentLength(size);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Long getContentLength() {
      return contentLength;
   }

   /**
    * {@inheritDoc}
    */
   public void setContentLength(long contentLength) {
      this.contentLength = contentLength;
   }

   /**
    * {@inheritDoc}
    */
   public MutableObjectInfoWithMetadata getInfo() {
      return info;
   }

   /**
    * {@inheritDoc}
    */
   public Multimap<String, String> getAllHeaders() {
      return allHeaders;
   }

   /**
    * {@inheritDoc}
    */
   public void setAllHeaders(Multimap<String, String> allHeaders) {
      this.allHeaders = checkNotNull(allHeaders, "allHeaders");
   }

   /**
    * {@inheritDoc}
    */
   public int compareTo(CFObject o) {
      if (getInfo().getName() == null)
         return -1;
      return (this == o) ? 0 : getInfo().getName().compareTo(o.getInfo().getName());
   }

}
