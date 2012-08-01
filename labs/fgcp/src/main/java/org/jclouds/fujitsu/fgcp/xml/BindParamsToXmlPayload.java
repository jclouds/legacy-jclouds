/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.fujitsu.fgcp.xml;

import com.google.common.base.Strings;
import org.jclouds.fujitsu.fgcp.reference.RequestParameters;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Not currently used but leaving for reference when implementing multipart POST
 * methods.
 * 
 * @author Dies Koper
 */
public class BindParamsToXmlPayload extends BindToStringPayload implements
        MapBinder {

    @Override
    public <R extends HttpRequest> R bindToRequest(R request,
            Map<String, Object> mapParams) {
        String action = checkNotNull(
                mapParams.remove(RequestParameters.ACTION),
                RequestParameters.ACTION).toString();
        String version = Strings.nullToEmpty((String) mapParams
                .remove(RequestParameters.VERSION));

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        xml.append("<OViSSRequest>\r\n");
        xml.append("  <Action>" + action + "</Action>\r\n");

        for (Map.Entry<String, Object> entry : mapParams.entrySet()) {
            String key = entry.getKey();
            xml.append("  <" + key + ">" + checkNotNull(mapParams.get(key))
                    + "</" + key + ">\r\n");
        }

        xml.append("  <Version>" + version + "</Version>\r\n");
        xml.append("  <Locale></Locale>\r\n"); // value inserted in
                                               // RequestAuthenticator#filter
        xml.append("  <AccessKeyId></AccessKeyId>\r\n"); // value inserted in
                                                         // RequestAuthenticator#filter
        xml.append("  <Signature></Signature>\r\n"); // value inserted in
                                                     // RequestAuthenticator#filter
        xml.append("</OViSSRequest>");

        request = super.bindToRequest(request, xml);
        request.getPayload().getContentMetadata()
                .setContentType(MediaType.TEXT_XML);

        // remove version query param if set as it was moved to the xml body
        URI uri = request.getEndpoint();
        URI uriWithoutQueryParams;
        try {
            uriWithoutQueryParams = new URI(uri.getScheme(), uri.getUserInfo(),
                    uri.getHost(), uri.getPort(), uri.getPath(), null,
                    uri.getFragment());
        } catch (URISyntaxException e) {
            // should never happen as we're copying the components from a URI
            uriWithoutQueryParams = uri;
        }

        return (R) request.toBuilder().endpoint(uriWithoutQueryParams).build();
    }

    @Override
    public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
        throw new IllegalArgumentException(
                "BindParamsToXmlPayload needs bind parameters");
    }
}
