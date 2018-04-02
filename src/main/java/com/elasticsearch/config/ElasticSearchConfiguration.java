package com.elasticsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * Configuration of Rest High Level Client, it exposes ES API specific methods which accept request objects as argument and 
 * return response objects 
 */

@Configuration
public class ElasticSearchConfiguration extends AbstractFactoryBean<RestHighLevelClient> {
	
	@Value("${spring.data.elasticsearch.cluster-nodes}")
	private String clusterNodes;
	@Value("${spring.data.elasticsearch.cluster-name}")
	private String clusterName;
	@Value("${spring.elasticsearch.jest.proxy.host}")
	private String host;
	@Value("${spring.elasticsearch.jest.proxy.port}")
	private int port;
	
	private RestHighLevelClient restHighLevelClient;
	private static final Logger logger = LoggerFactory.getLogger(ElasticSearchConfiguration.class);

	@Override
	public RestHighLevelClient createInstance() {
		return buildClient();
	}

	@Override
	public void destroy() {
		try {
			if (restHighLevelClient != null)
				restHighLevelClient.close();
		} catch (final Exception e) {
			logger.error("Error closing ElasticSearch client: ", e);
		}
	}

	@Override
	public Class<RestHighLevelClient> getObjectType() {
		return RestHighLevelClient.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	// Build Rest High Level Client
	private RestHighLevelClient buildClient() {
		try {
			restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, "http")));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return restHighLevelClient;
	}

}
