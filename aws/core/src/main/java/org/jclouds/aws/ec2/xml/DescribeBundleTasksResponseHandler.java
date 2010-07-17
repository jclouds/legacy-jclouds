package org.jclouds.aws.ec2.xml;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.BundleTask;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class DescribeBundleTasksResponseHandler extends ParseSax.HandlerWithResult<Set<BundleTask>> {

   private Set<BundleTask> bundleTasks = Sets.newLinkedHashSet();
   private final BundleTaskHandler bundleTaskHandler;

   @Inject
   public DescribeBundleTasksResponseHandler(BundleTaskHandler bundleTaskHandler) {
      this.bundleTaskHandler = bundleTaskHandler;
   }

   public Set<BundleTask> getResult() {
      return bundleTasks;
   }

   @Override
   public HandlerWithResult<Set<BundleTask>> setContext(HttpRequest request) {
      bundleTaskHandler.setContext(request);
      return super.setContext(request);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (!qName.equals("item"))
         bundleTaskHandler.startElement(uri, localName, qName, attributes);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equals("item")) {
         bundleTasks.add(bundleTaskHandler.getResult());
      }
      bundleTaskHandler.endElement(uri, localName, qName);
   }

   public void characters(char ch[], int start, int length) {
      bundleTaskHandler.characters(ch, start, length);
   }

}
