package org.jclouds.domain;

import java.net.URI;
import java.util.Map;

import org.jclouds.domain.internal.MutableResourceMetadataImpl;

import com.google.inject.ImplementedBy;

/**
 * Used to construct new resources or modify existing ones.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(MutableResourceMetadataImpl.class)
public interface MutableResourceMetadata<T extends Enum<T>> extends ResourceMetadata<T> {
   /**
    * @see #getType
    */
   void setType(T type);

   /**
    * @see #getId
    */
   void setId(String id);

   /**
    * @see #getName
    */
   void setName(String name);

   /**
    * @see #getLocation
    */
   void setLocation(String location);

   /**
    * @see #getUri
    */
   void setUri(URI url);

   /**
    * @see #getUserMetadata
    */
   void setUserMetadata(Map<String, String> userMetadata);


}