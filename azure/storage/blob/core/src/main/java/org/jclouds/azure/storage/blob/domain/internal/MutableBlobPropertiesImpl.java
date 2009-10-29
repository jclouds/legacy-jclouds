package org.jclouds.azure.storage.blob.domain.internal;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;

import org.jclouds.azure.storage.blob.domain.ListableBlobProperties;
import org.jclouds.azure.storage.blob.domain.MutableBlobProperties;
import org.joda.time.DateTime;

import com.google.common.collect.Maps;

/**
 * Allows you to manipulate metadata.
 * 
 * @author Adrian Cole
 */
public class MutableBlobPropertiesImpl implements Serializable, MutableBlobProperties {

   /** The serialVersionUID */
   private static final long serialVersionUID = -4648755473986695062L;

   private String name;
   private URI url;
   private DateTime lastModified;
   private String eTag;
   private long size;
   private String contentType;
   private byte[] contentMD5;
   private String contentEncoding;
   private String contentLanguage;
   private Map<String, String> metadata = Maps.newHashMap();

   public MutableBlobPropertiesImpl() {
   }

   /**
    *{@inheritDoc}
    */
   public String getName() {
      return name;
   }

   /**
    *{@inheritDoc}
    */
   public String getContentEncoding() {
      return contentEncoding;
   }

   /**
    *{@inheritDoc}
    */
   public String getContentType() {
      return contentType;
   }

   /**
    *{@inheritDoc}
    */
   public DateTime getLastModified() {
      return lastModified;
   }

   /**
    *{@inheritDoc}
    */
   public String getETag() {
      return eTag;
   }

   /**
    *{@inheritDoc}
    */
   public Long getSize() {
      return size;
   }

   /**
    *{@inheritDoc}
    */
   public int compareTo(ListableBlobProperties o) {
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }

   /**
    *{@inheritDoc}
    */
   public Map<String, String> getMetadata() {
      return metadata;
   }

   /**
    *{@inheritDoc}
    */
   public byte[] getContentMD5() {
      if (contentMD5 != null) {
         byte[] retval = new byte[contentMD5.length];
         System.arraycopy(this.contentMD5, 0, retval, 0, contentMD5.length);
         return retval;
      } else {
         return null;
      }
   }

   /**
    *{@inheritDoc}
    */
   public void setContentEncoding(String encoding) {
      this.contentEncoding = encoding;
   }

   /**
    *{@inheritDoc}
    */
   public void setContentMD5(byte[] md5) {
      if (md5 != null) {
         byte[] retval = new byte[md5.length];
         System.arraycopy(md5, 0, retval, 0, md5.length);
         this.contentMD5 = md5;
      }
   }

   /**
    *{@inheritDoc}
    */
   public void setContentType(String contentType) {
      this.contentType = contentType;
   }

   /**
    *{@inheritDoc}
    */
   public void setETag(String eTag) {
      this.eTag = eTag;
   }

   /**
    *{@inheritDoc}
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    *{@inheritDoc}
    */
   public void setLastModified(DateTime lastModified) {
      this.lastModified = lastModified;
   }

   /**
    *{@inheritDoc}
    */
   public void setSize(long size) {
      this.size = size;
   }

   /**
    *{@inheritDoc}
    */
   public void setMetadata(Map<String, String> metadata) {
      this.metadata = metadata;
   }

   public void setContentLanguage(String contentLanguage) {
      this.contentLanguage = contentLanguage;
   }

   public void setUrl(URI url) {
      this.url = url;
   }

   public String getContentLanguage() {
      return contentLanguage;
   }

   public URI getUrl() {
      return url;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((contentEncoding == null) ? 0 : contentEncoding.hashCode());
      result = prime * result + ((contentLanguage == null) ? 0 : contentLanguage.hashCode());
      result = prime * result + Arrays.hashCode(contentMD5);
      result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
      result = prime * result + ((eTag == null) ? 0 : eTag.hashCode());
      result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + (int) (size ^ (size >>> 32));
      result = prime * result + ((url == null) ? 0 : url.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      MutableBlobPropertiesImpl other = (MutableBlobPropertiesImpl) obj;
      if (contentEncoding == null) {
         if (other.contentEncoding != null)
            return false;
      } else if (!contentEncoding.equals(other.contentEncoding))
         return false;
      if (contentLanguage == null) {
         if (other.contentLanguage != null)
            return false;
      } else if (!contentLanguage.equals(other.contentLanguage))
         return false;
      if (!Arrays.equals(contentMD5, other.contentMD5))
         return false;
      if (contentType == null) {
         if (other.contentType != null)
            return false;
      } else if (!contentType.equals(other.contentType))
         return false;
      if (eTag == null) {
         if (other.eTag != null)
            return false;
      } else if (!eTag.equals(other.eTag))
         return false;
      if (lastModified == null) {
         if (other.lastModified != null)
            return false;
      } else if (!lastModified.equals(other.lastModified))
         return false;
      if (metadata == null) {
         if (other.metadata != null)
            return false;
      } else if (!metadata.equals(other.metadata))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (size != other.size)
         return false;
      if (url == null) {
         if (other.url != null)
            return false;
      } else if (!url.equals(other.url))
         return false;
      return true;
   }

}