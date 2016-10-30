package io.xaviernaveira;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.internal.NotNull;
import io.dropwizard.Configuration;

public class wTorrentConfiguration extends Configuration {

   @JsonProperty
   @NotNull
   String saveDir;

   public String getSaveDir() {
      return saveDir;
   }
}
