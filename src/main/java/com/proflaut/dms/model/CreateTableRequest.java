package com.proflaut.dms.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CreateTableRequest {

	@NotBlank(message = "File extension cannot be blank")
	private String fileExtension;
	private String metadataId;
	@NotBlank(message = "Table Name cannot be blank")
	private String tableName;
	@NotNull
	@Valid
	private List<FieldDefnition> fields;

}
