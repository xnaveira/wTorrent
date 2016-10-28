package io.xaviernaveira.core;

import com.frostwire.jlibtorrent.AlertListener;
import com.frostwire.jlibtorrent.SessionManager;
import com.frostwire.jlibtorrent.alerts.Alert;
import com.frostwire.jlibtorrent.alerts.AlertType;
import com.frostwire.jlibtorrent.alerts.TorrentAddedAlert;
import org.slf4j.Logger;

/**
 * Created by xnaveira on 2016-10-25.
 */
public class SessionManagerFactory {


   public static SessionManager build(Logger logger) {

      SessionManager s = new SessionManager();
      s.addListener(new AlertListener() {
         @Override
         public int[] types() {
            return null;
         }

         @Override
         public void alert(Alert<?> alert) {
            AlertType type = alert.type();

            switch (type) {
            case TORRENT_ADDED:
               logger.info("Torrent added");
               ((TorrentAddedAlert) alert).handle().resume();
               break;
            //                case BLOCK_FINISHED:
            //                    BlockFinishedAlert a = (BlockFinishedAlert) alert;
            //                    int p = (int) (a.handle().status().progress() * 100);
            //                    logger.info("Progress: " + p + " for download name: " + a.torrentName());
            //                    logger.info("TotalDownload: %d", s.stats().totalDownload());
            //                    break;
            case TORRENT_FINISHED:
               logger.info("Torrent finished");
               break;
            }
         }
      });

      s.start();
      return s;
   }
}
