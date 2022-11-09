package core.todo_v2.controller;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.MongoException;

import core.todo_v2.helper.ResponseObject;
import core.todo_v2.model.Task;
import core.todo_v2.service.TaskService;

@RestController
@RequestMapping(value = "/task")
public class TaskController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

	@Autowired
	TaskService service;

	/**
	 * @author Quang get all Tasks
	 */
	@GetMapping(value = "/all")
	ResponseEntity<ResponseObject> getAllTasks() {
		try {
			List<Task> data = service.getAllTasks();
			if (data.isEmpty()) {
				return ResponseEntity.status(404).body(new ResponseObject(404, "Data not exists", ""));
			}
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(200, "Success", data));
		} catch (MongoException e) {
			LOGGER.info("==> Exeception: Get All Tasks: {}", e);
			return ResponseEntity.status(500).body(new ResponseObject(500, "Server Error", ""));
		}
	}

	/**
	 * @author Quang add new task
	 * @param body
	 * @return
	 */

	@PostMapping(value = "/add")
	ResponseEntity<ResponseObject> addTask(@RequestBody Map<String, Object> body) {
		try {
			LOGGER.info("TaskController: add new task");
			if (body.isEmpty() || !body.containsKey("des")) {
				LOGGER.info("TaskController: Empty Params");
				return ResponseEntity.status(500).body(new ResponseObject(500, "Invalid value", ""));
			}
			if (service.addTask(body)) {
				LOGGER.info("success");
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(200, "Success", ""));
			} else {
				LOGGER.info("existed");
				return ResponseEntity.status(HttpStatus.ALREADY_REPORTED)
						.body(new ResponseObject(205, "Task has already existed", ""));
			}
		} catch (MongoException e) {
			LOGGER.info("TaskController: Error");
			return ResponseEntity.status(500).body(new ResponseObject(500, "Server Error", ""));
		}
	}

	/**
	 * @author Quang search task by description
	 * @param des
	 */
	@GetMapping(value = "/search")
	ResponseEntity<ResponseObject> getTasksByDes(@RequestBody Map<String, Object> body) {
		try {
			LOGGER.info("TaskController: search tasks");
			if (body.isEmpty() || !body.containsKey("des")) {
				LOGGER.info("TaskController: Empty Params");
				return ResponseEntity.status(500).body(new ResponseObject(500, "Invalid value", ""));
			}
			List<Task> data = service.getTaskByDes(body);
			if (data.isEmpty() || data == null) {
				return ResponseEntity.status(204).body(new ResponseObject(204, "No content", ""));
			}
			return ResponseEntity.status(200).body(new ResponseObject(200, "Success", data));
		} catch (MongoException e) {
			LOGGER.info("TaskController: Error");
			return ResponseEntity.status(500).body(new ResponseObject(500, "Server Error", ""));
		}
	}
}