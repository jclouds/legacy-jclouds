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

import java.io.IOException;
import java.io.StringReader;

import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jclouds.fujitsu.fgcp.domain.VServerWithDetails;
import org.jclouds.fujitsu.fgcp.xml.internal.ListServerTypeResponse;
import org.jclouds.fujitsu.fgcp.xml.internal.StatusQuerable;
import org.jclouds.http.HttpException;
import org.jclouds.xml.XMLParser;

/**
 * Parses XML documents using JAXB.
 * 
 * @author Dies Koper
 * @see org.jclouds.http.functions.ParseXMLWithJAXB
 */
@Singleton
public class FGCPJAXBParser implements XMLParser {
    JAXBContext context;

    public FGCPJAXBParser() throws JAXBException {
        context = JAXBContext.newInstance(VServerWithDetails.class.getPackage()
                .getName()
                + ":"
                + ListServerTypeResponse.class.getPackage().getName(),
                VServerWithDetails.class.getClassLoader());
    }

    @Override
    public String toXML(final Object src) throws IOException {
        return toXML(src, src.getClass());
    }

    @Override
    public <T> String toXML(final Object src, final Class<T> type)
            throws IOException {
        throw new UnsupportedOperationException(
                "only marshaling from XML is implemented");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T fromXML(final String xml, final Class<T> type)
            throws IOException {
        T response = null;
        try {
            StringReader reader = new StringReader(xml);

            Unmarshaller unmarshaller = context.createUnmarshaller();

            response = (T) unmarshaller.unmarshal(reader);
        } catch (Exception ex) {
            throw new IOException("Could not unmarshal document", ex);
        }

        if (((StatusQuerable) response).isError()) {
            throw new HttpException(
                    ((StatusQuerable) response).getResponseMessage());
        }

        return response;
    }

}
