package io.xaviernaveira;

import com.frostwire.jlibtorrent.SessionManager;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.xaviernaveira.core.SessionManagerFactory;
import io.xaviernaveira.resources.wTorrentResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class wTorrentApplication extends Application<wTorrentConfiguration> {

    static {
        System.loadLibrary("jlibtorrent");
    }

    private static final Logger logger = LoggerFactory.getLogger(wTorrentApplication.class);

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

        final SessionManager s = new SessionManagerFactory().build(logger);
        //final Client client = new JerseyClientBuilder().createClient();
        //ConcurrentHashMap<String, TorrentDownloader> torrentDownloaders = new ConcurrentHashMap<>();
        //webChecker webchecker = new webChecker(client);
        //environment.lifecycle().manage(webchecker);

        environment.jersey().register(new wTorrentResources(s, new File(configuration.getSaveDir())));
    }

}
