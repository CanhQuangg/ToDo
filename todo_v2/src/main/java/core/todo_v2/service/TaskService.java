package core.todo_v2.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;

import core.todo_v2.model.Task;
import core.todo_v2.repository.TaskRepository;

@Service
public class TaskService {
	@Autowired
	TaskRepository repository;

	@Autowired
	private MongoTemplate mongoTemplate;

	private static Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

	private MongoCollection<Document> getCollectionTasks() {
		return mongoTemplate.getCollection("tasks");
	}

	public List<Task> getAllTasks() {
		LOGGER.info("TaskService - Get all Tasks");
		List<Task> lstTasks = mongoTemplate.findAll(Task.class);
		return lstTasks;
	}

	public Boolean addTask(Map<String, Object> newTask) {
		String des = (String) newTask.get("des");
		Document filter = new Document("des", des);
		List<Document> desExist = getCollectionTasks().find(filter).into(new ArrayList<Document>());
		if (desExist.size() > 0) {
			return false;
		}
		Task task = new Task(des);
		mongoTemplate.insert(task);
		LOGGER.info("TaskService: New task was added");
		return true;
	}

	/**
	 * cần thêm các tính năng: - search theo id - search trong khoảng thời gian từ
	 * ngày nào tới ngày nào - search xem task được hoàn thành hay chưa
	 * 
	 * @param body
	 * @return
	 */
	public List<Task> getTaskByDes(Map<String, Object> body) {
		try {
			List<Task> lstTasks = new ArrayList<>();
			if (body.containsKey("id")) {
				ObjectId id = new ObjectId((String)body.get("id"));
				lstTasks = (List<Task>) mongoTemplate.findById(id, Task.class);
				return lstTasks;
			}
			String des = (String) body.get("des");
			Query query = new Query();
			query.addCriteria(Criteria.where("des").regex(des, "i"));
			lstTasks = mongoTemplate.find(query, Task.class);
			return lstTasks;
		} catch (MongoException e) {
			LOGGER.info("TaskService: {}", e);
			return null;
		}
	}
}
