package com.github.stefantt.vdrcontroller.route;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.resource.ExternalResource;
import spark.staticfiles.MimeType;

/**
 * A route for serving files using a mappings configuration.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class MappedResourcesRoute implements Route
{
   private static final Logger LOGGER = LoggerFactory.getLogger(MappedResourcesRoute.class);

   private final String pathPrefix;
   private final Properties mappings = new Properties();

   /**
    * Create a route for serving files from JARs.
    *
    * @param pathPrefix The URL path prefix to be removed from requests
    * @param mappingsFile The name of the resource mapping configuration file
    */
   public MappedResourcesRoute(String pathPrefix, String mappingsFile)
   {
      if (!pathPrefix.startsWith("/"))
         pathPrefix = '/' + pathPrefix;
      if (!pathPrefix.endsWith("/"))
         pathPrefix = pathPrefix + '/';
      this.pathPrefix = pathPrefix;

      InputStream in = getClass().getClassLoader().getResourceAsStream(mappingsFile);
      Validate.notNull(in, "cannot find mappings file " + mappingsFile);
      try
      {
          mappings.load(in);
      }
      catch (IOException e)
      {
          throw new RuntimeException("Failed to load resource mappings file " + mappingsFile);
      }
   }

   /**
    * Search through the mappings for a resource path configuration that matches
    * the given URL path.
    *
    * @param urlPath The URL path to find
    * @return The resource path, null if not found
    */
   private String getResourcePath(String urlPath)
   {
      @SuppressWarnings("unchecked")
      Enumeration<String> e = (Enumeration<String>) mappings.propertyNames();

      String match = "";
      while (e.hasMoreElements())
      {
         String key = e.nextElement();
         if (urlPath.startsWith(key + '/') && key.length() > match.length())
            match = key;
      }

      if (StringUtils.isEmpty(match))
         return null;

      return mappings.getProperty(match) + urlPath.substring(match.length());
   }

   @Override
   public Object handle(Request request, Response response) throws Exception
   {
      String path = request.pathInfo().replaceAll("\\.\\.", "__");
      Validate.isTrue(path.startsWith(pathPrefix));
      path = path.substring(pathPrefix.length());
      LOGGER.debug("Serving static resource for {}", path);

      String mappedPath = getResourcePath(path);
      if (mappedPath == null)
      {
         response.status(HttpStatus.NOT_FOUND_404);
         return "";
      }

      InputStream in = null;
      try
      {
         in = getClass().getClassLoader().getResourceAsStream(mappedPath);
         if (in == null)
         {
            LOGGER.warn("Static resource {} was identified as {} but does not exist",
               pathPrefix + path, mappedPath);

            response.status(HttpStatus.NOT_FOUND_404);
            return "";
         }

         response.status(HttpStatus.OK_200);
         response.type(contentTypeOf(path));
         //response.header("Cache-Control", "public, max-age=60");

         IOUtils.copy(in, response.raw().getOutputStream());

         return "";
      }
      finally
      {
         IOUtils.closeQuietly(in);
      }
   }

   private String contentTypeOf(String path)
   {
      path = path.toLowerCase();

      if (path.endsWith(".json"))
         return "application/json; charset=UTF-8";

      return MimeType.fromResource(new ExternalResource(path));
   }
}
