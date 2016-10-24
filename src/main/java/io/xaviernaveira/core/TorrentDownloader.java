package io.xaviernaveira.core;

import com.frostwire.jlibtorrent.SessionManager;
import com.frostwire.jlibtorrent.TorrentInfo;
import io.xaviernaveira.wTorrentApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by xnaveira on 2016-10-21.
 */
public class TorrentDownloader {

   private static final Logger logger = LoggerFactory.getLogger(wTorrentApplication.class);

   private String torrentFileName;
   private SessionManager s;



   public TorrentDownloader(String torrentfile, SessionManager s)
   {
      this.torrentFileName = torrentfile;
      this.s = s;

   }


   public void start() throws Exception {

      File torrentFile = new File(torrentFileName);

      TorrentInfo ti = new TorrentInfo(torrentFile);
      s.download(ti, torrentFile.getParentFile());

   }


   public void stop() throws Exception {

   }
}
