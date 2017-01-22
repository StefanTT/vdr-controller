package com.github.stefantt.vdrcontroller.route;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.eclipse.jetty.http.HttpStatus;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.resource.ExternalResource;
import spark.staticfiles.MimeType;

/**
 * A route for a single page.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class SinglePageRoute implements Route
{
    private final URL pageUrl;
    private final String contentType;

    /**
     * Create a route for serving a single page.
     *
     * @param pageFile The filename of the page to serve
     */
    public SinglePageRoute(String pageFile)
    {
        pageUrl = getClass().getClassLoader().getResource(pageFile);
        Validate.notNull(pageUrl, "file not found in classpath: " + pageFile);

        contentType = contentTypeOf(pageUrl.getPath());
    }

    @Override
    public Object handle(Request request, Response response) throws Exception
    {
        InputStream in = null;
        try
        {
            in = pageUrl.openStream();

            response.status(HttpStatus.OK_200);
            response.type((contentType));

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
