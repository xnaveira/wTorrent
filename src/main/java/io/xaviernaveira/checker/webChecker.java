package io.xaviernaveira.checker;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.dropwizard.lifecycle.Managed;
import io.xaviernaveira.wTorrentApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by xnaveira on 2016-10-21.
 */
public class webChecker implements Managed {

   private static final Logger logger = LoggerFactory.getLogger(wTorrentApplication.class);

   private WebTarget target;
   private Client client;
   private ScheduledExecutorService executor;


   public webChecker(Client client) {
      this.client = client;
      }

   @Override
   public void start() {
      target = client.target("http://www.google.com");
      executor = Executors.newSingleThreadScheduledExecutor(
         new ThreadFactoryBuilder().setNameFormat("wec-checker").setDaemon(true).build());
      executor.scheduleWithFixedDelay(() -> check(), 1, 10, TimeUnit.SECONDS);

   }

   public void check() {
      Preconditions.checkNotNull(target);
      Response res = target.request().get();
      logger.info(res.toString());

   }

   @Override
   public void stop() {
      client.close();
   }
}
