package io.xaviernaveira.core;

import com.frostwire.jlibtorrent.SessionManager;
import com.frostwire.jlibtorrent.TorrentInfo;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Created by xnaveira on 2016-10-29.
 */
public class TorrentDownloaderMagnet implements TorrentDownloader {

   private static final Logger logger = LoggerFactory.getLogger(TorrentDownloaderMagnet.class);


   private String uri;
   private SessionManager s;
   private File saveDir;

   public TorrentDownloaderMagnet(String uri, SessionManager s, File saveDir) {
      Preconditions.checkNotNull(uri, "Error: uri can't be null when creating TorrentDownloaderMagnet.");
      Preconditions.checkNotNull(s, "Error: sessiom can't be null when creating TorrentDownloaderMagnet.");
      this.uri = uri;
      this.s = s;
      this.saveDir = saveDir;
   }


   @Override
   public void start() throws IOException {

      final CompletableFuture<byte[]> future = CompletableFuture.supplyAsync(() -> s.fetchMagnet(uri,30));
      File wTempFile;

      try {
         wTempFile = File.createTempFile("wTorrent", ".torrent", new File(System.getProperty("java.io.tmpdir")));
      } catch (IOException e) {
         logger.error(e.toString());
         throw e;
      }

      FileOutputStream wTempFileStream = new FileOutputStream(wTempFile);
      future.thenAcceptAsync((v) -> {
         try {
            wTempFileStream.write(v);
         } catch (IOException e) {
            logger.error(e.toString());
            throw new RuntimeException("Error creating torrent file from magnet.");
         }
         TorrentInfo magnetTorrent = new TorrentInfo(wTempFile);
         logger.info(String.format("Async magnet: %s", magnetTorrent.name()));
         s.download(magnetTorrent, saveDir);
      });



   }
}
