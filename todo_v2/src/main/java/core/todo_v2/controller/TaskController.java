package core.todo_v2.controller;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.MongoException;

import core.todo_v2.helper.ResponseObject;
import core.todo_v2.helper.Utils;
import core.todo_v2.service.TaskService;

@RestController
@RequestMapping(value = "/task")
public class TaskController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

	@Autowired
	TaskService service;

	@Autowired
	Utils utils;

	/**
	 * @author Quang get all Tasks
	 */
	@GetMapping(value = "/all")
	ResponseEntity<ResponseObject> getAllTasks(
			@RequestParam(value = "limit", required = false, defaultValue = "30") Integer limit,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page) {
		try {
			limit = utils.limitValid(limit);
			page = utils.pageValid(page, limit);
			List<Document> data = service.getAllTasks(limit, page);
			if (data.isEmpty()) {
				return ResponseEntity.status(205).body(new ResponseObject(205, "Data not exists", ""));
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
			body = utils.checkParams(body);
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
	 * @author Quang search task by description, id
	 * @param des
	 */
	@GetMapping(value = "/search")
	ResponseEntity<ResponseObject> searchTask(@RequestBody Map<String, Object> body) {
		try {
			LOGGER.info("TaskController: search tasks");
			body = utils.checkParams(body);
			if (body.isEmpty()) {
				LOGGER.info("TaskController: Empty Params");
				System.out.println((String) body.get("des"));
				return ResponseEntity.status(205).body(new ResponseObject(205, "Invalid value", ""));
			}
			List<Document> data = service.searchTasks(body);
			if (data == null) {
				return ResponseEntity.status(400).body(new ResponseObject(204, "No content", ""));
			}
			return ResponseEntity.status(200).body(new ResponseObject(200, "Success", data));
		} catch (MongoException e) {
			LOGGER.info("TaskController: Error");
			return ResponseEntity.status(500).body(new ResponseObject(500, "Server Error", ""));
		}
	}

	/**
	 * @author Quang set status of task is complete
	 * @param List<String> ids
	 */
	@PutMapping(value = "/setComplete")
	ResponseEntity<ResponseObject> setComplete(@RequestBody Map<String, Object> body) {
		try {
			LOGGER.info("TaskController: set complete tasks");
			body = utils.checkParams(body);
			if (body.isEmpty() || !body.containsKey("ids")) {
				LOGGER.info("TaskController: Empty Params");
				return ResponseEntity.status(500).body(new ResponseObject(500, "Invalid value", ""));
			}
			List<String> ids = (List<String>) body.get("ids");
			LOGGER.info("TaskController: set complete tasks with id " + ids);
			Boolean checkUpdate = service.setComplete(ids);
			if (checkUpdate) {
				return ResponseEntity.status(200)
						.body(new ResponseObject(200, String.format("Task %s was updated", ids), ""));
			}
			return ResponseEntity.status(200)
					.body(new ResponseObject(204, String.format("Cannot update Task %s ", ids), ""));
		} catch (MongoException e) {
			LOGGER.info("TaskController: Error");
			return ResponseEntity.status(500).body(new ResponseObject(500, "Server Error", ""));
		}
	}

	/**
	 * @author Quang delete exist tasks
	 * @param list<String> ids
	 */
	@DeleteMapping(value = "/delete")
	ResponseEntity<ResponseObject> deleteTasks(@RequestBody Map<String, Object> body) {
		try {
			LOGGER.info("TaskController: Delete a Task or list Tasks by id");
			body = utils.checkParams(body);
			if (body.isEmpty() || !body.containsKey("ids")) {
				LOGGER.info("TaskController: Empty Params");
				return ResponseEntity.status(205).body(new ResponseObject(205, "Invalid value", ""));
			}
			List<String> ids = (List<String>) body.get("ids");
			Boolean checkUpdate = service.deleteTasks(ids);
			if (checkUpdate) {
				return ResponseEntity.status(200).body(new ResponseObject(200, "Success", ids));
			}
			return ResponseEntity.status(200).body(new ResponseObject(204, "Cannot delete Tasks", ""));
		} catch (MongoException e) {
			LOGGER.info("TaskController: Error");
			return ResponseEntity.status(500).body(new ResponseObject(500, "Server Error", ""));
		}
	}
}
