package io.xaviernaveira.resources;

import com.codahale.metrics.annotation.Timed;
import com.frostwire.jlibtorrent.SessionHandle;
import com.frostwire.jlibtorrent.SessionManager;
import com.frostwire.jlibtorrent.Sha1Hash;
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
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
   private File sourceDir;

   public wTorrentResources(SessionManager s, File saveDir, File sourceDir) {
      this.s = s;
      this.saveDir = saveDir;
      this.sourceDir = sourceDir;
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
         new TorrentDownloaderFile(torrent, s, saveDir, sourceDir).start();
      } catch (FileNotFoundException e) {
         logger.error(e.toString());
         return Response.status(Response.Status.NOT_FOUND).entity(ImmutableMap.of("message", e.toString())).build();
      }
      return Response.ok().entity(ImmutableMap.of("message", "Download query sent.")).build();
   }

   /**
    * Puts the magnet to download using the {@link com.frostwire.jlibtorrent.SessionManager}
    *
    * @param  uri  A JSON containing the key "magnet" with the uri as value
    *
    */
   @Timed
   @POST
   @Path("/downloadmagnet")
   public Response downloadmagnet(@Valid Map<String,String> uri) {
      String magnet = uri.get("magnet");
      Preconditions.checkNotNull(magnet);
      try {
         new TorrentDownloaderMagnet(magnet, s, saveDir).start();
      } catch (IOException e) {
         logger.error(e.toString());
         return Response.serverError().entity(ImmutableMap.of("message", e.toString())).build();
      }
      return Response.ok().entity(ImmutableMap.of("message", "Add magnet query sent.")).build();
   }

   @Timed
   @GET
   @Path("/getdownloading")
   public Response getdownloading() {

      SessionHandle sessionHandle = new SessionHandle(s.swig());
      List<TorrentHandle> activeTorrentList = sessionHandle.torrents();
      List<Map> torrents = activeTorrentList.stream()
         .map(torrent -> ImmutableMap.of("name", torrent.getName(), "infohash", torrent.infoHash().toString()))
         .collect(Collectors.toList());
      return Response.ok().entity(torrents).build();

   }

   @Timed
   @GET
   @Path("/stopdownload/{infohash}")
   public Response stopdownloading(@PathParam("infohash") String infohash) {

      SessionHandle sessionHandle = new SessionHandle(s.swig());
      TorrentHandle torrent = sessionHandle.findTorrent(new Sha1Hash(infohash));
      s.remove(torrent, SessionHandle.Options.DELETE_FILES);
      return Response.ok().entity(ImmutableMap.of("message", "Remove query sent.")).build();
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
