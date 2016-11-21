package io.xaviernaveira.core;

import com.frostwire.jlibtorrent.SessionManager;
import com.frostwire.jlibtorrent.TorrentInfo;
import com.google.common.base.Preconditions;
import io.xaviernaveira.wTorrentApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by xnaveira on 2016-10-21.
 */
public class TorrentDownloaderFile implements TorrentDownloader {

   private static final Logger logger = LoggerFactory.getLogger(wTorrentApplication.class);

   private String torrentFileName;
   private SessionManager s;
   private File saveDir;
   private File sourceDir;



   public TorrentDownloaderFile(String torrentfile, SessionManager s, File saveDir, File sourceDir)
   {
      Preconditions.checkNotNull(torrentfile, "Error: File can't be nul when creating TorrentDownloaderFile");
      Preconditions.checkNotNull(s, "Error: session can't be null when creating TorrentDownloaderFile");
      this.saveDir = saveDir;
      this.sourceDir = sourceDir;
      this.torrentFileName = sourceDir + "/" + torrentfile;
      this.s = s;

   }


   @Override
   public void start() throws FileNotFoundException {
      File torrentFile;
      torrentFile = new File(torrentFileName);
      if ( !torrentFile.isFile() ) {
         logger.error(String.format("File not found: %s", torrentFileName));
         throw new FileNotFoundException();
      }
      s.download(new TorrentInfo(torrentFile), saveDir);
   }
}
