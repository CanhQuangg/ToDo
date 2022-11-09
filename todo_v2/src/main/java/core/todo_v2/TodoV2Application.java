package core.todo_v2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories("core.todo_v2.repository")
@ComponentScan("core.todo_v2.*")
public class TodoV2Application {

	public static void main(String[] args) {
		SpringApplication.run(TodoV2Application.class, args);
	}

}
