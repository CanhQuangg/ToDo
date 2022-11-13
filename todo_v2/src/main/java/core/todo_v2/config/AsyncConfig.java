package core.todo_v2.config;

import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
	private static final Logger LOG = LogManager.getLogger(AsyncConfig.class);

	@Autowired
	private ServerProperties serverProperties;

	@Bean("asyncExecutor")
	@Override
	public Executor getAsyncExecutor() {
		int process = Runtime.getRuntime().availableProcessors();
		LOG.debug("---> SERVER PROCESS: {}", process);
		int maxPool = serverProperties.getTomcat().getThreads().getMax();
		LOG.debug("---> SERVER MAX POOL: {}", maxPool);
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(maxPool / 2);
		executor.setMaxPoolSize(maxPool);
		executor.setQueueCapacity(maxPool * 3);
		executor.initialize();
		return executor;
	}
}
