/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 - 2023 Flowing Code
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.flowingcode.vaadin.addons;

import com.vaadin.flow.server.RequestHandler;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class DevSourceRequestHandler implements RequestHandler {

  private static final int SC_OK = 200;

  private static final int SC_NOT_FOUND = 404;

  @Override
  public boolean handleRequest(VaadinSession session, VaadinRequest request,
      VaadinResponse response) throws IOException {

    if (VaadinService.getCurrent().getDeploymentConfiguration().isProductionMode()) {
      return false;
    }

    String path = request.getPathInfo();
    if (!path.startsWith("/src/")) {
      return false;
    }

    if (fileExists(path)) {
      byte file[] = FileUtils.readFileToByteArray(getFile(path));
      int j = 0;
      for (int i = 0; i < file.length; i++) {
        if (file[i] != '\r') {
          file[j++] = file[i];
        }
      }
      response.setStatus(SC_OK);
      response.getOutputStream().write(file, 0, j);
    } else {
      response.setStatus(SC_NOT_FOUND);
    }

    return true;
  }

  public static boolean fileExists(String path) {
    File file = getFile(path);
    return file.exists() && !file.isDirectory();
  }

  private static File getFile(String path) {
    return new File(path.substring(1));
  }

}
