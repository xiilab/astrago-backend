package com.xiilab.serverexperiment.configuration;

import java.util.concurrent.TimeUnit;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@EnableMongoRepositories(basePackages = "com.xiilab.serverexperiment.repository", // MongoDB 리포지토리 경로
	mongoTemplateRef = "mongoTemplate")
public class MongoDbConfig extends AbstractMongoClientConfiguration {
	@Value("${spring.data.mongodb.url}")
	private String connectionString;

	@Override
	protected String getDatabaseName() {
		return "astrago";
	}

	@Override
	public MongoClient mongoClient() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
			MongoClientSettings.getDefaultCodecRegistry(),
			CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		String url = this.connectionString;
		ConnectionString connectionString = new ConnectionString(this.connectionString);
		MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
			.applyToConnectionPoolSettings(builder -> builder.maxConnectionIdleTime(10, TimeUnit.SECONDS)) //최대 유휴 시간
			.applyConnectionString(connectionString)
			.build();

		MongoClient mongoClient = MongoClients.create(mongoClientSettings);
		mongoClient.getDatabase("astrago").withCodecRegistry(codecRegistry);
		return mongoClient;

	}

	@Bean
	@Primary
	public MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongoClient(), getDatabaseName());
	}
}

