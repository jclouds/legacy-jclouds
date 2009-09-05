package org.jclouds.objectstore;

import java.io.InputStream;
import java.util.Map;

/**
 * Represents a cloud that has key-value storage functionality.
 * 
 * 
 * @author Adrian Cole
 * 
 */
public interface ObjectStoreContext<M extends Map<String, InputStream>> {

   /**
    * Creates a <code>Map<String,InputStream></code> view of the specified namespace.
    * 
    * @param namespace
    */
   M createInputStreamMap(String namespace);

}