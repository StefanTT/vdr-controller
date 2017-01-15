package com.github.stefantt.vdrcontroller.route;

import static spark.Spark.*;

import static java.net.URLDecoder.decode;
import java.util.UUID;
import java.util.regex.Pattern;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.stefantt.vdrcontroller.dto.DetailedRecoding;
import com.github.stefantt.vdrcontroller.entity.Configuration;
import com.github.stefantt.vdrcontroller.entity.VdrRecording;
import com.github.stefantt.vdrcontroller.entity.VirtualFolder;
import com.github.stefantt.vdrcontroller.service.ConfigurationService;
import com.github.stefantt.vdrcontroller.service.VdrService;
import com.github.stefantt.vdrcontroller.util.ContentType;
import com.github.stefantt.vdrcontroller.util.VdrUtils;
import com.google.gson.Gson;

/**
 * Configures the application's routes.
 *
 * @author Stefan Taferner
 */
public class RouteBuilder
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteBuilder.class);
    private static final Pattern UUID_PATTERN = Pattern.compile("^[-a-fA-F0-9]+$");

    private final ConfigurationService configService;
    private final VdrService vdrService;

    private final Gson gson = new Gson();

    /**
     * Create a route builder that configures the application's routes.
     *
     * @param vdrService The VDR service to use in the routes
     * @param configService The configuration service
     */
    public RouteBuilder(VdrService vdrService, ConfigurationService configService)
    {
        this.vdrService = vdrService;
        this.configService = configService;
    }

    /**
     * Setup the routes.
     */
    public void build()
    {
        get("/lib/*", new MappedResourcesRoute("/lib", "static-resources.properties"));
        get("/i18n/*", new I18nResourceRoute("."));

        post("/rest/clearCaches", (request, response) ->
        {
            vdrService.clearCaches();

            response.status(HttpStatus.ACCEPTED_202);
            return "Ok";
        });

        get("/rest/config", (request, response) ->
        {
            return gson.toJson(configService.getConfig());
        });

        post("/rest/config", (request, response) ->
        {
            Configuration config = gson.fromJson(request.body(), Configuration.class);
            configService.setConfig(config);
            configService.save();

            vdrService.setConnection(config.getVdrHost(), config.getVdrPort());

            response.status(HttpStatus.ACCEPTED_202);
            return "Ok";
        });

        get("/rest/vdr/capabilities", (request, response) ->
        {
            return gson.toJson(vdrService.getCapabilities());
        });

        get("/rest/vdr/osd", (request, response) ->
        {
            return gson.toJson(vdrService.getOsdProxy().getItems());
        });

        post("/rest/vdr/osd/:key", (request, response) ->
        {
            String key = decode(request.params(":key"), "UTF-8");
            vdrService.getOsdProxy().sendKey(key);

            response.status(HttpStatus.ACCEPTED_202);
            return "Ok";
        });

        get("/rest/vdr/recordings", (request, response) ->
        {
            response.type(ContentType.JSON);
            return gson.toJson(VdrUtils.toRecordingsOverviewList(vdrService.getRecordingsFolder("")));
        });

        get("/rest/vdr/recordings/:path", (request, response) ->
        {
            String path = decode(request.params(":path"), "UTF-8");
            LOGGER.debug("Get recordings of {}", path);
            VirtualFolder<VdrRecording> folder = vdrService.getRecordingsFolder(path);
            if (folder == null)
            {
                response.status(HttpStatus.NOT_FOUND_404);
                return "Folder not found: " + path;
            }

            response.type(ContentType.JSON);
            return gson.toJson(VdrUtils.toRecordingsOverviewList(folder));
        });

        get("/rest/vdr/recording/:id", (request, response) ->
        {
            UUID id = UUID.fromString(request.params(":id"));
            response.type(ContentType.JSON);
            VdrRecording rec = vdrService.getRecording(id);
            if (rec == null)
            {
                response.status(HttpStatus.NOT_FOUND_404);
                return "";
            }
            return gson.toJson(new DetailedRecoding(rec));
        });

        delete("/rest/vdr/recording/:path", (request, response) ->
        {
            String path = request.params(":path");

            if (UUID_PATTERN.matcher(path).matches())
                vdrService.deleteRecording(UUID.fromString(path));
            else vdrService.deleteRecordings(decode(path, "UTF-8"));

            response.type(ContentType.JSON);
            return "";
        });

        get("/rest/vdr/timer", (request, response) ->
        {
            response.type(ContentType.JSON);
            return gson.toJson(VdrUtils.toBriefTimers(vdrService.getTimers()));
        });
    }
}