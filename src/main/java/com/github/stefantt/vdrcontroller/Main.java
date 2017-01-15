package com.github.stefantt.vdrcontroller;

import static spark.Spark.exception;
import static spark.Spark.port;
import static spark.Spark.staticFiles;
import static spark.Spark.threadPool;

import java.io.IOException;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.stefantt.vdrcontroller.entity.Configuration;
import com.github.stefantt.vdrcontroller.route.RouteBuilder;
import com.github.stefantt.vdrcontroller.service.ConfigurationService;
import com.github.stefantt.vdrcontroller.service.VdrService;
import com.github.stefantt.vdrcontroller.util.ContentType;
import com.github.stefantt.vdrcontroller.util.EnvironmentUtils;
import com.github.stefantt.vdrcontroller.vdr.VdrRuntimeException;

/**
 * The main class.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class Main
{
   private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

   public static void main(String[] args) throws IOException
   {
      port(4567);
      threadPool(8, 2, 30000);

      staticFiles.location("/web");
      staticFiles.expireTime(3);
      staticFiles.registerMimeType("json", ContentType.JSON);

      ConfigurationService.getInstance().load(EnvironmentUtils.getAppConfigDir() + "/vdr-controller.conf");
      Configuration config = ConfigurationService.getInstance().getConfig();

      VdrService vdrService = new VdrService(config.getVdrHost(), config.getVdrPort());

      RouteBuilder routeBuilder = new RouteBuilder(vdrService, ConfigurationService.getInstance());
      routeBuilder.build();

      setupErrorHandlers();
   }

   private static void setupErrorHandlers()
   {
      exception(VdrRuntimeException.class, (e, request, response) ->
      {
         LOGGER.error("Request failed: {}", e.getMessage(), e);

         response.type(ContentType.TEXT);
         response.status(((VdrRuntimeException) e).getHttpStatus());
         response.body(e.getMessage());
      });

      exception(RuntimeException.class, (e, request, response) ->
      {
         LOGGER.error("Request failed - internal error", e);

         response.type(ContentType.TEXT);
         response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
         response.body("Internal error: " + e.getMessage());
      });
   }
}
