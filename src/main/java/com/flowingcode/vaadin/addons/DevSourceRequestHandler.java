package com.flowingcode.vaadin.addons;

import com.vaadin.flow.server.RequestHandler;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

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
      response.setStatus(SC_OK);
      IOUtils.write(file, response.getOutputStream());
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
