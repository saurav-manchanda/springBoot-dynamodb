package com.cg.dynamodb.repository;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.cg.dynamodb.model.Student;

@Repository
public class DynamoDbRepo {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbRepo.class);
	
	@Autowired
	private DynamoDBMapper mapper;
	
	public void insertIntoDynamoDB(Student student) {
		mapper.save(student);
	}
	
	public Student getOneStudentDetails(String studentId,String lastName) {
		return mapper.load(Student.class,studentId, lastName);
	}
	
	public void updateStudentDetails(Student student) {
		try {
			mapper.save(student, buildDynamoDBSaveExpression(student));;
		}catch(ConditionalCheckFailedException exception){
		LOGGER.error("invalid data- "+ exception.getMessage());
		}
	}

	public void deleteStudentDetails(Student student) {
		mapper.delete(student);
	}
	
	private DynamoDBSaveExpression buildDynamoDBSaveExpression(Student student) {
		DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
		Map<String, ExpectedAttributeValue> expected = new HashMap<>();
		expected.put("id", new ExpectedAttributeValue(new AttributeValue(student.getStudentId()))
				.withComparisonOperator(ComparisonOperator.EQ));
		saveExpression.setExpected(expected);
		return saveExpression;
	}

}
