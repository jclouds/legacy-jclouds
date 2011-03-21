package org.jclouds.savvis.vpdc.xml;

import static org.jclouds.savvis.vpdc.util.Utils.cleanseAttributes;
import static org.jclouds.savvis.vpdc.util.Utils.newResource;

import java.util.Map;
import java.util.Set;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @author Adrian Cole
 */
public class OrgListHandler extends ParseSax.HandlerWithResult<Set<Resource>> {

   private Builder<Resource> org = ImmutableSet.<Resource> builder();

   public Set<Resource> getResult() {
      try {
         return org.build();
      } finally {
         org = ImmutableSet.<Resource> builder();
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (qName.endsWith("Org")) {
         org.add(newResource(attributes));
      }
   }
}
