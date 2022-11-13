package core.todo_v2.service;

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
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;

import core.todo_v2.helper.Utils;
import core.todo_v2.model.Task;
import core.todo_v2.repository.TaskRepository;

@Service
public class TaskService {
	@Autowired
	TaskRepository repository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	Utils utils;

	private static Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

	private MongoCollection<Document> getCollectionTasks() {
		return mongoTemplate.getCollection("tasks");
	}

	/**
	 * @author Quang
	 * @return List<Document> all task in db
	 */
	public List<Document> getAllTasks(int limit, int skip) {
		LOGGER.info("TaskService - Get all Tasks");
		Document projection = new Document().append("_class", 0);
		List<Document> lstTasks = getCollectionTasks().find()
				.sort(new Document("created", -1)).limit(limit)
				.projection(projection)
				.skip(skip)
				.into(new ArrayList<Document>());
		lstTasks = utils.convertId(lstTasks);
		return lstTasks;
	}

	/**
	 * @author Quang
	 * @param newTask: String des
	 * @return true if add task success
	 */
	public Boolean addTask(Map<String, Object> newTask) {
		try {
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
		} catch (MongoException e) {
			LOGGER.info("TaskService: add new task fail: {}", e);
			return false;
		}
	}

	/**
	 * cần thêm các tính năng: - search theo id (done) - search trong khoảng thời
	 * gian từ ngày nào tới ngày nào(done) - search xem task được hoàn thành hay
	 * chưa
	 * 
	 * @param body: id, des, frDate, toDate: Date format: yyyy-mm-dd
	 * @return
	 */
	public List<Document> searchTasks(Map<String, Object> body) {
		try {
			List<Document> lstTasks = new ArrayList<>();
			Document projection = new Document("_class", 0);

			// Search Task by Id
			if (body.containsKey("id")) {
				Optional<Task> checkTask = repository.findById((String) body.get("id"));
				if (checkTask.isPresent()) {					
				ObjectId id = new ObjectId((String) body.get("id"));
				Document task = getCollectionTasks().find(new Document("_id", id)).projection(projection).first();
				lstTasks.add(task);
				return utils.convertId(lstTasks);
				} else {
					return null;
				}
			}

			// filter chứa các query trong $and
			Document filterAppend = new Document();

			// Search Task by Des
			String des = (String) body.get("des");
			if (body.containsKey("des") && !des.isBlank() && !des.isEmpty()) {
				filterAppend.append("des", new Document("$regex", des).append("$options", "i"));
			}

			// Search Task by date
			if (body.containsKey("frDate") || body.containsKey("toDate")) {
				// from date
				if (body.containsKey("frDate")) {
					String tmpFrDate = (String) body.get("frDate");
					Date frDate = utils.parseStringToISODate(tmpFrDate);
					filterAppend.append("created", new Document("$gte", frDate));
				}

				// to date
				if (body.containsKey("toDate")) {
					String tmpToDate = (String) body.get("toDate");
					Date toDate = utils.parseStringToISODate(tmpToDate);
					filterAppend.append("created", new Document("$lte", toDate));
				}
			}

			if (body.containsKey("isComplete")) {
				filterAppend.append("complete", true);
			}

			if (filterAppend.size() < 1) {
				return null;
			}
			Document filter = new Document("$and", Arrays.asList(filterAppend));
			lstTasks = getCollectionTasks().find(filter).projection(projection).into(new ArrayList<Document>());
			return utils.convertId(lstTasks);
		} catch (MongoException e) {
			LOGGER.info("TaskService: {}", e);
			return null;
		}
	}

	public Boolean setComplete(List<String> ids) {
		try {
			LocalDateTime modified = LocalDateTime.now();
			List<ObjectId> idsUpdate = new ArrayList<>();
			for (String id : ids) {
				Optional<Task> checkTask = repository.findById(id);
				if (checkTask.isPresent()) {
					idsUpdate.add(new ObjectId(id));
				}
			}
			
			if (ids.size() > 0) {
				Document filter = new Document("_id", new Document("$in", idsUpdate));
				Document update = new Document("$set", new Document("complete", true).append("modified", modified));
				getCollectionTasks().updateMany(filter, update);
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
			Document filter = new Document("_id", new Document("$in", lstId));
			getCollectionTasks().deleteMany(filter);
			return true;
		} catch (MongoException e) {
			LOGGER.info("TaskService: {}", e);
			return false;
		}
	}
}
