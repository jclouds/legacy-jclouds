package org.jclouds.aws.simpledb.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.util.Collection;
import java.util.Iterator;

import org.jclouds.aws.simpledb.domain.AttributePair;
import org.jclouds.aws.simpledb.domain.Item;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;

/**
 * 
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class BindAttributesToIndexedFormParams implements Binder {

   private final String attributeName = "Attribute.%d.Name";
   private final String attributeValue = "Attribute.%d.Value";
   private final String attributeReplace = "Attribute.%d.Replace";

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Item, "this binder is only valid for AttributeMap");
      Item attributeMap = (Item) input;

      Builder<String, String> builder = ImmutableMultimap.<String, String> builder();
      int amazonOneBasedIndex = 1; // according to docs, counters must start with 1
      for (String itemName : attributeMap.getAttributes().keySet()) {

         Collection<AttributePair> c = attributeMap.getAttributes().get(itemName);
         Iterator<AttributePair> it = c.iterator();
         while (it.hasNext()) {
            AttributePair attr = it.next();
            // not null by contract

            String value = attr.getValue();

            if (value != null) {
               builder.put(format(attributeName, amazonOneBasedIndex), attr.getKey());
               builder.put(format(attributeValue, amazonOneBasedIndex), value);
               builder.put(format(attributeReplace, amazonOneBasedIndex), String.valueOf(attr.isReplace()));

            }
            amazonOneBasedIndex++;
         }

      }
      ImmutableMultimap<String, String> forms = builder.build();
      return forms.size() == 0 ? request : ModifyRequest.putFormParams(request, forms);
   }

}
