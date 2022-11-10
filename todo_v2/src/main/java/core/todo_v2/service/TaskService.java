package core.todo_v2.service;

import java.sql.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
	 * cần thêm các tính năng: - search theo id (done) - search trong khoảng thời
	 * gian từ ngày nào tới ngày nào - search xem task được hoàn thành hay chưa
	 * 
	 * @param body
	 * @return
	 */
	public List<Task> searchTasks(Map<String, Object> body) {
		try {
			List<Task> lstTasks = new ArrayList<>();

			// Search Task by Id
			if (body.containsKey("id")) {
				ObjectId id = new ObjectId((String) body.get("id"));
				Task task = mongoTemplate.findById(id, Task.class);
				lstTasks.add(task);
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

	public Boolean setComplete(String id) {
		try {
			Optional<Task> checkTask = repository.findById(id);
			if (checkTask.isPresent()) {
				LocalDateTime modified = LocalDateTime.now();
				Document filter = new Document("_id", new ObjectId(id));
				Document update = new Document("$set", new Document("complete", true).append("modified", modified));
				getCollectionTasks().updateOne(filter, update);
				return true;
			}
			return false;
		} catch (MongoException e) {
			LOGGER.info("TaskService: {}", e);
			return false;
		}
	}
	
	public Boolean deleteTasks(List<String> ids) {
		try {
			List<ObjectId> lstId = new ArrayList<>();
			for (String id : ids) {
				Optional<Task> checkTask = repository.findById(id);
				if (checkTask.isPresent()) {
					lstId.add(new ObjectId(id));
				}
			}
			if (lstId.size() < 1) {
				return false;
			}
			Document filter = new Document("_id", new Document("$in",lstId));
			getCollectionTasks().deleteMany(filter);
			return true;
		} catch (MongoException e) {
			LOGGER.info("TaskService: {}", e);
			return false;
		}
	}
}
