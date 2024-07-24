package com.xiilab.serverexperiment.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
	basePackages = "com.xiilab.modulek8sdb", // MariaDB 리포지토리 경로
	entityManagerFactoryRef = "mariaDbEntityManagerFactory",
	transactionManagerRef = "mariaDbTransactionManager"
)
public class MariaDBConfiguration {
	@Primary
	@Bean(name = "mariaDbDataSource")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource mariaDbDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Primary
	@Bean(name = "mariaDbEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean mariaDbEntityManagerFactory(
		EntityManagerFactoryBuilder builder,
		@Qualifier("mariaDbDataSource") DataSource dataSource) {
		return builder
			.dataSource(dataSource)
			.packages("com.xiilab.modulek8sdb") // MariaDB 엔티티 경로
			.persistenceUnit("mariaDb")
			.build();
	}

	@Primary
	@Bean(name = "mariaDbTransactionManager")
	public PlatformTransactionManager mariaDbTransactionManager(
		@Qualifier("mariaDbEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
}
