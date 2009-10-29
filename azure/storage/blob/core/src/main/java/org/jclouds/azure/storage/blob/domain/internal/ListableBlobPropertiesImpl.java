package org.jclouds.azure.storage.blob.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;

import org.jclouds.azure.storage.blob.domain.ListableBlobProperties;
import org.joda.time.DateTime;

import com.google.inject.internal.Nullable;

/**
 * Allows you to manipulate metadata.
 * 
 * @author Adrian Cole
 */
public class ListableBlobPropertiesImpl implements Serializable, ListableBlobProperties {

   /** The serialVersionUID */
   private static final long serialVersionUID = -4648755473986695062L;

   private final String name;
   private final URI url;
   private final DateTime lastModified;
   private final String eTag;
   private final long size;
   private final String contentType;
   private final String contentEncoding;
   private final String contentLanguage;

   public ListableBlobPropertiesImpl(String name, URI url, DateTime lastModified, String eTag,
            long size, String contentType, @Nullable String contentEncoding,
            @Nullable String contentLanguage) {
      this.name = checkNotNull(name, "name");
      this.url = checkNotNull(url, "url");
      this.lastModified = checkNotNull(lastModified, "lastModified");
      this.eTag = checkNotNull(eTag, "eTag");
      this.size = size;
      this.contentType = checkNotNull(contentType, "contentType");
      this.contentEncoding = contentEncoding;
      this.contentLanguage = contentLanguage;
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
      result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
      result = prime * result + ((eTag == null) ? 0 : eTag.hashCode());
      result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
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
      ListableBlobPropertiesImpl other = (ListableBlobPropertiesImpl) obj;
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