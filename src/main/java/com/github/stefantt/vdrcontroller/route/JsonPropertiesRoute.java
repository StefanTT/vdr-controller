package com.github.stefantt.vdrcontroller.route;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A route for serving properties files in JSON format.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class JsonPropertiesRoute implements Route
{
   private static final Logger LOGGER = LoggerFactory.getLogger(JsonPropertiesRoute.class);

   private final String localPathPrefix;
   private final Gson gson = new Gson();

   /**
    * Create a route for serving properties files in JSON format.
    *
    * @param localPathPrefix The local path that is prepended to the requested URL path
    */
   public JsonPropertiesRoute(String localPathPrefix)
   {
      this.localPathPrefix = localPathPrefix + '/';
   }

   @Override
   public Object handle(Request request, Response response) throws Exception
   {
      String path = request.pathInfo().replaceAll("\\.\\.", "__");
      String localPath = localPathPrefix + path;

      InputStream in = null;
      try
      {
         in = getResourceStream(localPath);
         if (in == null)
         {
            response.status(HttpStatus.NOT_FOUND_404);
            return "";
         }

         Properties props = new Properties();
         props.load(in);

         response.status(HttpStatus.OK_200);
         response.type("application/json; charset=UTF-8");

         return gson.toJson(props);
      }
      finally
      {
         IOUtils.closeQuietly(in);
      }
   }

   protected InputStream getResourceStream(String path)
   {
      InputStream in = getClass().getClassLoader().getResourceAsStream(path);
      if (in == null)
      {
         LOGGER.warn("Properties file not found: {}", path);
      }
      return in;
   }
}
