package core.todo_v2.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import core.todo_v2.model.Task;

public interface TaskRepository extends MongoRepository<Task, String>{
	
}
