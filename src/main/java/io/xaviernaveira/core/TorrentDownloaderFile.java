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


   public TorrentDownloaderFile(String torrentfile, SessionManager s)
   {
      Preconditions.checkNotNull(torrentfile, "Error: File can't be nul when creating TorrentDownloaderFile");
      Preconditions.checkNotNull(s, "Error: session can't be null when creating TorrentDownloaderFile");
      this.torrentFileName = torrentfile;
      this.s = s;

   }


   @Override
   public void start() throws Exception {

      File torrentFile;

      torrentFile = new File(torrentFileName);

      if ( !torrentFile.isFile() ) {
         throw new FileNotFoundException();
      }

      TorrentInfo ti = new TorrentInfo(torrentFile);
      s.download(ti, torrentFile.getParentFile());

   }

   @Override
   public void stop() throws Exception {

   }

}
