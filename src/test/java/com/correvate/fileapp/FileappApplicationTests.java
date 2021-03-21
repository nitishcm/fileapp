package com.correvate.fileapp;

import com.correvate.fileapp.controller.FileAppController;
import com.correvate.fileapp.utils.FileAppUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class FileappApplicationTests {

	@InjectMocks
	static FileAppController fileAppController;
	@Mock
	HttpServletRequest request;
	@Mock
	HttpServletResponse response;
	@Spy
	static FileAppUtils fileAppUtils;

	ResponseEntity<Resource> responseEntity;
	ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

	@BeforeAll
	static  void setup(){
		try{
			fileAppUtils= new FileAppUtils();
			fileAppController = new FileAppController();

		} catch (Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * Check if invalid body type or empty body type
	 * @throws Exception
	 */
	@Test
	void missingOrIncorrectContentType() throws Exception {
		responseEntity = new ResponseEntity("Invalid Input body", HttpStatus.BAD_REQUEST);
		when(fileAppUtils.returnResponse(anyString())).thenCallRealMethod();

		assertEquals(responseEntity,fileAppController.filesToZip(request,response));

		when(request.getHeader("content-type")).thenReturn("application/json");
		assertEquals(responseEntity,fileAppController.filesToZip(request,response));
	}

	/**
	 * Check if no file in input gives error
	 * @throws Exception
	 */
	@Test
	void noFile() throws Exception {

			List<Part> testList = new ArrayList<>();

			responseEntity = new ResponseEntity("No file in input", HttpStatus.BAD_REQUEST);
			when(request.getHeader("content-type")).thenReturn("form-data");
			when(request.getParts()).thenReturn(testList);

			assertEquals(responseEntity,fileAppController.filesToZip(request,response));


	}

	/**
	 *  Check Empty files in input gives error message
	 * @throws Exception
	 */
	@Test
	void noValidFile() throws Exception {
			List<Part> testList = new ArrayList<>();
			URL url = FileappApplicationTests.class.getClassLoader().getResource("testfile1.txt");
			File testFile = new File(url.getPath());

			Part file = mock(Part.class);
			testList.add(file);

			responseEntity = new ResponseEntity("No file to zip", HttpStatus.BAD_REQUEST);

			when(request.getHeader("content-type")).thenReturn("form-data");
			when(request.getParts()).thenReturn(testList);
			when(file.getSubmittedFileName()).thenReturn("testfile1.txt");
			when(file.getContentType()).thenReturn("text/html");
		    when(file.getSize()).thenReturn(FileUtils.sizeOf(testFile));

			assertEquals(responseEntity,fileAppController.filesToZip(request,response));

	}
}
