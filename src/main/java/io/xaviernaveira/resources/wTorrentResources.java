package io.xaviernaveira.resources;

import com.codahale.metrics.annotation.Timed;
import com.frostwire.jlibtorrent.SessionManager;
import com.frostwire.jlibtorrent.swig.torrent_handle_vector;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import io.xaviernaveira.core.TorrentDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by xnaveira on 2016-10-21.
 */
@Path("/torrent")
@Produces("application/json")
public class wTorrentResources {

   private static final Logger logger = LoggerFactory.getLogger(wTorrentResources.class);

   private final SessionManager s;

   public wTorrentResources(SessionManager s) {
      this.s = s;
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
         new TorrentDownloader(torrent, s).start();
      } catch (Exception e) {
         logger.error(e.getMessage());
         return Response.serverError().entity(ImmutableMap.of("message", e.getMessage())).build();
      }
      return Response.ok().entity(ImmutableMap.of("message", "Download started.")).build();
   }

   @Timed
   @GET
   @Path("/downloadmagnet/{magnet}")
   public Response downloadmagnet(@PathParam("downloadmagnet") String magnet) {
      return Response.ok().entity(ImmutableMap.of("message", "This method still doesn't do anything.")).build();
   }

   @Timed
   @GET
   @Path("/getdownloading")
   public Response getdownloading() {
      torrent_handle_vector torrents = s.swig().get_torrents();

      if (torrents.size() == 0) {
         return Response.ok().entity(ImmutableMap.of("activetorrentslist", "[]")).build();
      }

//      List<torrent_handle> activeTorrentsList = new ArrayList<>();
//      for (int i=0;i<=torrents.size();i++) {
//         activeTorrentsList.add(torrents.get(i));
//      }
      return Response.ok().entity(ImmutableMap.of("activetorrentslist",torrents)).build();

   }

   @Timed
   @GET
   @Path("/restartsession")
   public Response restartsession() {
      try {
         s.restart();
      } catch (Exception e) {
         logger.error(e.getMessage());
         return Response.serverError().entity(ImmutableMap.of("message", e.getMessage())).build();
      }
      return Response.ok().entity(ImmutableMap.of("message", "Session restarted")).build();
   }




}
