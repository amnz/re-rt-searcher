/* *****************************************************************************
 * Copyright (C) Movatoss co.,ltd.
 *      http://movatoss.jp/
 *
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */

package jp.wda.mutsumi.framework;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.glassfish.jersey.server.mvc.spi.ResolvedViewable;

/**
 *
 *
 *
 * $Id$
 * @author		$Author$
 * @revision	$Revision$
 * @date		$Date$
 */
@SuppressWarnings("all")
public class MayaaRequestDispatcherWrapper implements RequestDispatcher {

    final static String BASE_PATH_ATTRIBUTE_NAME = "_basePath";
    final static String OLD_MODEL_ATTRIBUTE_NAME = "it";
    final static String MODEL_ATTRIBUTE_NAME = "model";
    final static String RESOLVING_CLASS_ATTRIBUTE_NAME = "resolvingClass";
    final static String REQUEST_ATTRIBUTE_NAME = "_request";
    final static String RESPONSE_ATTRIBUTE_NAME = "_response";

    private final RequestDispatcher dispatcher;

    private final String basePath;

    private final ResolvedViewable viewable;

    /**
     * Creates new {@code RequestDispatcherWrapper} responsible for setting request attributes and forwarding the processing to
     * the given dispatcher.
     *
     * @param dispatcher dispatcher processing the request after all the request attributes were set.
     * @param basePath base path of all JSP set to {@value #BASE_PATH_ATTRIBUTE_NAME} request attribute.
     * @param viewable viewable to obtain model and resolving class from.
     */
    public MayaaRequestDispatcherWrapper(
            final RequestDispatcher dispatcher, final String basePath, final ResolvedViewable viewable) {
        this.dispatcher = dispatcher;
        this.basePath = basePath;
        this.viewable = viewable;
    }

    @Override
    public void forward(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {
        final Object oldIt = request.getAttribute(MODEL_ATTRIBUTE_NAME);
        final Object oldResolvingClass = request.getAttribute(RESOLVING_CLASS_ATTRIBUTE_NAME);

        request.setAttribute(RESOLVING_CLASS_ATTRIBUTE_NAME, viewable.getResolvingClass());

        request.setAttribute(OLD_MODEL_ATTRIBUTE_NAME, viewable.getModel());
        request.setAttribute(MODEL_ATTRIBUTE_NAME, viewable.getModel());

        request.setAttribute(BASE_PATH_ATTRIBUTE_NAME, basePath);
        request.setAttribute(REQUEST_ATTRIBUTE_NAME, request);
        request.setAttribute(RESPONSE_ATTRIBUTE_NAME, response);

        dispatcher.forward(request, response);

        request.setAttribute(RESOLVING_CLASS_ATTRIBUTE_NAME, oldResolvingClass);
        request.setAttribute(MODEL_ATTRIBUTE_NAME, oldIt);
    }

    @Override
    public void include(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

}
