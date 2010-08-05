package org.jclouds.chef;

import java.io.IOException;
import java.io.InputStream;

import org.jclouds.chef.domain.Node;
import org.jclouds.chef.internal.BaseChefService;

import com.google.common.base.Predicate;
import com.google.common.io.InputSupplier;
import com.google.inject.ImplementedBy;

/**
 * Provides high level chef operations
 * 
 * @author Adrian Cole
 */
@ImplementedBy(BaseChefService.class)
public interface ChefService {
   /**
    * @return a reference to the context that created this.
    */
   ChefContext getContext();

   byte[] encrypt(InputSupplier<? extends InputStream> supplier) throws IOException;

   byte[] decrypt(InputSupplier<? extends InputStream> supplier) throws IOException;

   void cleanupStaleNodesAndClients(String prefix, int secondsStale);

   void createNodeAndPopulateAutomaticAttributes(String nodeName, Iterable<String> runList);

   void deleteAllNodesInList(Iterable<String> names);

   Iterable<? extends Node> getNodes();

   Iterable<? extends Node> getNodesWithNamesMatching(Predicate<String> nodeNameSelector);

   Iterable<? extends Node> getNodesNamed(Iterable<String> names);

   void updateAutomaticAttributesOnNode(String nodeName);
}
