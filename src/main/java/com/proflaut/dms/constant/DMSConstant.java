package com.proflaut.dms.constant;

public class DMSConstant {
	
	DMSConstant(){}
	
	public static final String SUCCESS="Success";
	public static final String FAILURE="Failure";
	public static final String NA="NA";
	public static final String CONSTRAINTVIOLATIONEXCEPTION ="could not execute statement; SQL [n/a]; "
			+ "constraint [uk_3q33lmunvt4qhpqd8n0qnxsex]; nested exception is org.hibernate.exception."
			+ "ConstraintViolationException: could not execute statement";
	public static final String HIBERNATEEXCEPTION = "org.hibernate.exception.ConstraintViolationException";
	public static final String ERRORMESSAGE="username already exists";
	public static final String FILE_NOT_FOUND="file not found Exception";
	public static final String FOLDER_ALREADY_EXIST="folder already exists";
	public static final String NOT_FOUND="Not Found";
	public static final String GROUPNAME_ALREADY_EXIST="Group Name Already Exist";
	public static final String USERNAME_ALREADY_EXIST="User Name Already Exist";
}
