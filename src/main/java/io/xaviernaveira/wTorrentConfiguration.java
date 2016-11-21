package io.xaviernaveira;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class wTorrentConfiguration extends Configuration {

   @JsonProperty
   @NotEmpty
   String saveDir;

   @JsonProperty
   @NotEmpty
   String sourceDir;

   public String getSaveDir() {
      return saveDir;
   }

   public String getSourceDir() {
      return sourceDir;
   }
}
