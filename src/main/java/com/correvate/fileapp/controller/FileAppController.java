package com.correvate.fileapp.controller;

/*
Name : Nitish Chandra Mathur
Email : nitishchandramathur@gmail.com
 */

import com.correvate.fileapp.utils.FileAppUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.List;


@RestController
public class FileAppController {

    @Autowired
    FileAppUtils fileAppUtils;
    Logger logger = Logger.getLogger(FileAppController.class);

    File responseZip = null;

    /**
     * Upload file method
     * @param request
     * @param response
     * @return ResponseEntity<Resource>
     */


    @RequestMapping(path = "/fileUpload", method = RequestMethod.POST)

        public ResponseEntity<Resource> filesToZip(HttpServletRequest request, HttpServletResponse response){
        try{
            String fileName = "compressed_";
            // Check if file are present
            if(request.getHeader("content-type") != null && request.getHeader("content-type").contains("form-data") && request.getParts() != null){

                List<Part> inputFiles = (List<Part>) request.getParts();

                if(inputFiles.size() > 0) {
                    // Fetch files which are valid for zipping
                    inputFiles = fileAppUtils.validFileList(inputFiles);

                    if(inputFiles != null && inputFiles.size() > 0){

                        fileName = (fileName.concat( fileAppUtils.convertTS(new Timestamp(System.currentTimeMillis())))).concat(".zip");

                        //Zip file and return
                         responseZip = fileAppUtils.zipFiles(inputFiles,fileName);

                        //Check if zip if created or not
                        if(responseZip != null && responseZip.isFile() ){
                            //Check if custom file name provided
                            if( request.getParameterMap() != null &&  request.getParameterMap().containsKey("zipfilename")){
                                String val = request.getParameterMap().get("zipfilename")[0];
                                if(!val.isEmpty() && val.matches("^\\w+(\\.)zip$")){
                                    fileName = val;
                                }
                            }
                            response.setHeader("Content-Disposition", "attachment; filename="
                                    + fileName);
                            response.setHeader("Content-Type",MediaType.APPLICATION_OCTET_STREAM_VALUE);
                            InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(responseZip));
                            return  ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(inputStreamResource);
                        } else{
                            return fileAppUtils.returnResponse("error.no.zip");
                        }
                    }
                }else{
                    return fileAppUtils.returnResponse("error.input.no.file");
                }
            } else{
                // If any other input type for body or no file
                return fileAppUtils.returnResponse("error.input.body.type");
                }
        }catch (IllegalStateException e){
           e.printStackTrace();
           return fileAppUtils.returnResponse("error.filelimit.exception");
        } catch(Exception e){
            e.printStackTrace();
            return fileAppUtils.returnResponse("error.exception");
        }

       return fileAppUtils.returnResponse("default.response");

    }

}
