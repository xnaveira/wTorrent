package io.xaviernaveira;

import com.frostwire.jlibtorrent.AlertListener;
import com.frostwire.jlibtorrent.SessionManager;
import com.frostwire.jlibtorrent.alerts.Alert;
import com.frostwire.jlibtorrent.alerts.AlertType;
import com.frostwire.jlibtorrent.alerts.TorrentAddedAlert;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.xaviernaveira.resources.wTorrentResources;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;

public class wTorrentApplication extends Application<wTorrentConfiguration> {

    static {
        System.loadLibrary("jlibtorrent");
    }

    private static final Logger logger = LoggerFactory.getLogger(wTorrentApplication.class);
    protected static final SessionManager s = new SessionManager();



    public static void main(final String[] args) throws Exception {
        new wTorrentApplication().run(args);
    }

    @Override
    public String getName() {
        return "wTorrent";
    }

    @Override
    public void initialize(final Bootstrap<wTorrentConfiguration> bootstrap) {


    }

    @Override
    public void run(final wTorrentConfiguration configuration,
                    final Environment environment) {



        final Client client = new JerseyClientBuilder().createClient();


        //webChecker webchecker = new webChecker(client);

        //environment.lifecycle().manage(webchecker);


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

        environment.jersey().register(new wTorrentResources(s));
    }

}
