package io.xaviernaveira.core;

import com.frostwire.jlibtorrent.SessionManager;
import com.frostwire.jlibtorrent.TorrentInfo;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
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


//   public void start(int nthreads) throws Exception {
//
//
//      ExecutorService executor = Executors.newFixedThreadPool(nthreads);
//      Callable<byte[]> task = () -> {
//         System.out.println(String.format("Thread: %s", Thread.currentThread().getName()));
//         return s.fetchMagnet(uri, 30);
//      };
//
//      List<Callable<byte[]>> CList = new ArrayList<>();
//      IntStream.range(0,nthreads)
//         .forEach(CList.add(task));
//
//      executor.invokeAll(CList)
//         .stream()
//         .map(future -> {
//            try {
//               return future.get();
//            } catch (Exception e) {
//               throw new IllegalStateException(e);
//            }
//         })
//         .forEach(future -> System.out.println(String.format("%s",future.toString())));
//
//
//   }

   @Override
   public void start() throws Exception {

      final CompletableFuture<byte[]> future = CompletableFuture.supplyAsync(()->s.fetchMagnet(uri,30));
      File wTempFile;

      try {
         wTempFile = File.createTempFile("wTorrent", ".torrent", new File(System.getProperty("java.io.tmpdir")));
      } catch (Exception e) {
         logger.error(e.toString());
         throw e;
      }

      FileOutputStream wTempFileStream = new FileOutputStream(wTempFile);
      try {
         future.thenAcceptAsync((v) -> {
            try {
               wTempFileStream.write(v);
            } catch (Exception e) {
               logger.error(e.toString());
            }
            TorrentInfo magnetTorrent = new TorrentInfo(wTempFile);
            logger.info(String.format("Async magnet: %s", magnetTorrent.name()));
            s.download(magnetTorrent, saveDir);
         });
      } catch (Exception e) {
         logger.error(e.toString());
      }


   }

   @Override
   public void stop() throws Exception {

   }
}
