package com.github.stefantt.vdrcontroller.route;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A specialized version of the {@link JsonPropertiesRoute} that tries
 * the file as specified and, if not found, removes the country part from
 * the filename and tries that file.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class I18nResourceRoute extends JsonPropertiesRoute
{
   private static final Logger LOGGER = LoggerFactory.getLogger(I18nResourceRoute.class);

   /**
    * Create a route for getting localized resources.
    *
    * @param localPathPrefix The local path that is prepended to the requested URL path
    */
   public I18nResourceRoute(String localPathPrefix)
   {
      super(localPathPrefix);
   }

   @Override
   protected InputStream getResourceStream(String path)
   {
      InputStream in = getClass().getClassLoader().getResourceAsStream(path);
      if (in != null) return in;

      String extension = path.replaceFirst("^.*\\.", ".");
      String altPath = path.replaceFirst("-[A-Z]+\\.[a-z]+$", extension);

      in = getClass().getClassLoader().getResourceAsStream(altPath);
      if (in != null) return in;

      LOGGER.warn("Properties file not found: {} (also tried {})", path, altPath);
      return null;
   }
}
