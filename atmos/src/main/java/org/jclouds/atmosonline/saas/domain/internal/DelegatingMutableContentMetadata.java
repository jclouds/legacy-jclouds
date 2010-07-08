package org.jclouds.atmosonline.saas.domain.internal;

import org.jclouds.atmosonline.saas.domain.MutableContentMetadata;

/**
 * 
 * @author Adrian Cole
 */
public class DelegatingMutableContentMetadata extends MutableContentMetadata {
   private final MutableContentMetadata delegate;

   public DelegatingMutableContentMetadata(MutableContentMetadata delegate) {
      this.delegate = delegate;
   }

   @Override
   public Long getContentLength() {
      return delegate.getContentLength();
   }

   @Override
   public byte[] getContentMD5() {
      return delegate.getContentMD5();
   }

   @Override
   public String getContentType() {
      return delegate.getContentType();
   }

   @Override
   public String getName() {
      return delegate.getName();
   }

   @Override
   public void setContentLength(Long contentLength) {
      delegate.setContentLength(contentLength);
   }

   @Override
   public void setContentMD5(byte[] contentMD5) {
      delegate.setContentMD5(contentMD5);
   }

   @Override
   public void setContentType(String contentType) {
      delegate.setContentType(contentType);
   }

   @Override
   public void setName(String name) {
      delegate.setName(name);
   }

   @Override
   public boolean equals(Object obj) {
      return delegate.equals(obj);
   }

   @Override
   public int hashCode() {
      return delegate.hashCode();
   }

   @Override
   public String toString() {
      return delegate.toString();
   }

   public MutableContentMetadata getDelegate() {
      return delegate;
   }

}