package io.xaviernaveira.resources;

import com.codahale.metrics.annotation.Timed;
import com.frostwire.jlibtorrent.SessionHandle;
import com.frostwire.jlibtorrent.SessionManager;
import com.frostwire.jlibtorrent.TorrentHandle;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import io.xaviernaveira.core.TorrentDownloaderFile;
import io.xaviernaveira.core.TorrentDownloaderMagnet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xnaveira on 2016-10-21.
 */
@Path("/torrent")
@Produces("application/json")
public class wTorrentResources {

   private static final Logger logger = LoggerFactory.getLogger(wTorrentResources.class);

   private final SessionManager s;
   private File saveDir;

   public wTorrentResources(SessionManager s, File saveDir) {
      this.s = s;
      this.saveDir = saveDir;
   }

   @Timed
   @GET
   @Path("/hello")
   public Response helloworld() {
      return Response.ok("hi!").build();
   }

   @Timed
   @GET
   @Path("/download/{torrent}")
   public Response download(@PathParam("torrent") String torrent) throws Exception {
      Preconditions.checkNotNull(torrent);
      try {
         new TorrentDownloaderFile(torrent, s).start();
      } catch (FileNotFoundException e) {
         logger.error(e.toString());
         return Response.status(Response.Status.NOT_FOUND).entity(ImmutableMap.of("message", e.toString())).build();
      } catch (Exception e) {
         logger.error(e.toString());
         return Response.serverError().entity(ImmutableMap.of("message", e.toString())).build();
      }
      return Response.ok().entity(ImmutableMap.of("message", "Download started.")).build();
   }

   @Timed
   @POST
   @Path("/downloadmagnet")
   public Response downloadmagnet(@Valid ImmutableMap<String,String> uri) throws Exception {
      String magnet = uri.get("magnet");
      try {
         new TorrentDownloaderMagnet(magnet, s, saveDir).start();
      } catch (Exception e) {
         logger.error(e.toString());
         return Response.serverError().entity(ImmutableMap.of("message", e.toString())).build();
      }
      return Response.ok().entity(ImmutableMap.of("message", "This method is under development.")).build();
   }

   @Timed
   @GET
   @Path("/getdownloading")
   public Response getdownloading() {

      SessionHandle sessionHandle = new SessionHandle(s.swig());
      List<TorrentHandle> activeTorrentList = sessionHandle.torrents();

      activeTorrentList.stream()
         .forEach(s -> logger.info(String.format("%s: %s", s.getName(), s.infoHash())));

      List<String> activeTorrents = activeTorrentList.stream()
         .map(t -> t.getName())
         .collect(Collectors.toList());

      return Response.ok().entity(ImmutableMap.of("activetorrentslist",activeTorrents)).build();

   }

   @Timed
   @GET
   @Path("/stopdownlad/{id}")
   public Response stopdownloading() {
      return Response.ok().entity(ImmutableMap.of("message", "This method still doesn't stop anything")).build();
   }

   @Timed
   @GET
   @Path("/restartsession")
   public Response restartsession() {
      try {
         s.restart();
      } catch (Exception e) {
         logger.error(e.toString());
         return Response.serverError().entity(ImmutableMap.of("message", e.toString())).build();
      }
      return Response.ok().entity(ImmutableMap.of("message", "Session restarted")).build();
   }




}
