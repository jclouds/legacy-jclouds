package org.jclouds.deltacloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class DeltacloudCollection {
   private final URI href;
   private final String rel;
   private final Set<? extends Feature> features;

   public DeltacloudCollection(URI href, String rel) {
      this(href, rel, ImmutableSet.<Feature> of());
   }

   public DeltacloudCollection(URI href, String rel, Set<? extends Feature> features) {
      this.href = checkNotNull(href, "href");
      this.rel = checkNotNull(rel, "rel");
      this.features = ImmutableSet.copyOf(checkNotNull(features, "features"));
   }

   public URI getHref() {
      return href;
   }

   public String getRel() {
      return rel;
   }

   public Set<? extends Feature> getFeatures() {
      return features;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((features == null) ? 0 : features.hashCode());
      result = prime * result + ((href == null) ? 0 : href.hashCode());
      result = prime * result + ((rel == null) ? 0 : rel.hashCode());
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
      DeltacloudCollection other = (DeltacloudCollection) obj;
      if (features == null) {
         if (other.features != null)
            return false;
      } else if (!features.equals(other.features))
         return false;
      if (href == null) {
         if (other.href != null)
            return false;
      } else if (!href.equals(other.href))
         return false;
      if (rel == null) {
         if (other.rel != null)
            return false;
      } else if (!rel.equals(other.rel))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[href=" + href + ", rel=" + rel + ", features=" + features + "]";
   }
}