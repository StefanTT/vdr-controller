package com.github.stefantt.vdrcontroller.route;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;

import spark.Request;

/**
 * A specialized version of the {@link MappedResourcesRoute} that iterates over the
 * browsers preferred languages and tries to load the file matching the language.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class LocalizedResourceRoute extends MappedResourcesRoute
{
   /**
    * Create a route for serving localized files from JARs.
    *
    * @param pathPrefix The URL path prefix to be removed from requests
    * @param mappingsFile The name of the resource mapping configuration file
    */
   public LocalizedResourceRoute(String pathPrefix, String mappingsFile)
   {
      super(pathPrefix, mappingsFile);
   }

   @Override
   protected String getResourcePath(String urlPath, Request request)
   {
       String resourcePath = super.getResourcePath(urlPath, request);
       if (resourcePath == null) return null;

       int idx = resourcePath.lastIndexOf('.');
       String ext = resourcePath.substring(idx);
       String basePath = resourcePath.substring(0, idx) + '_';

       for (String lang : StringUtils.split(request.headers("Accept-Language"), ','))
       {
           idx = lang.indexOf(';');
           if (idx >= 0) lang = lang.substring(0, idx);

           URL url = getClass().getClassLoader().getResource(basePath + lang.toLowerCase() + ext);
           if (url != null) return url.toExternalForm().replaceAll("^jar:.*!/", "");
       }

       return null;
   }
}
