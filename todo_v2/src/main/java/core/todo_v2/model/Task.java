package core.todo_v2.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "tasks")
@Setter
@Getter
public class Task {
	@Id
	private String id;
	
	@NotNull
	@NotBlank
	private String des; //description
	
	private LocalDateTime created;
	
	private LocalDateTime modified;
	
	private Boolean complete;
	
	public Task(String des) {
		LocalDateTime date = LocalDateTime.now();
		this.created = date;
		this.modified = date;
		this.des = des;
		this.complete = false;
	}
}
