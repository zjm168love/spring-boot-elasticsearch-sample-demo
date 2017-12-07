package springbootelastic.configuration;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.net.InetAddress;

/**
 * @author FCB
 */
@Configuration
@Slf4j
public class ElasticsearchConfiguration {

  @Bean
  public ElasticsearchOperations elasticsearchTemplate() throws Exception {
    return new ElasticsearchTemplate(client());
  }

  private Client client() throws Exception {
    TransportClient client;
    Settings esSettings = Settings.settingsBuilder()
      .put("cluster.name", "ymq")
      .build();
    try {
      client = TransportClient.builder()
        //.settings(esSettings)
        .build()
        .addTransportAddress(
          new InetSocketTransportAddress(InetAddress.getByName("localhost"), Integer.valueOf("9300")));
      return client;
    } catch (Exception e) {
      log.error("error Elasticsearch connect", e);
      return null;
    }
  }
}