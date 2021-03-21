package com.correvate.fileapp.utils;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.Part;
import java.io.*;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;



public class FileAppUtils {

    Logger logger = Logger.getLogger(FileAppUtils.class);
    private Properties props;
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");

    /**
     * Constructor to load properties file
     * @throws Exception
     */
    public FileAppUtils() throws Exception{
        InputStream input = FileAppUtils.class.getClassLoader().getResourceAsStream("message.properties");
        props = new Properties();
        props.load(input);
    }


    /**
     * Validate each file to check if valid file and size > 0
     * @param inputFiles
     * @return List<Part>
     */


    public List<Part> validFileList(List<Part> inputFiles){
        List<Part> returnFiles = new ArrayList<Part>();
        for(Part file : inputFiles){
            if(file.getContentType() != null && file.getSize() > 0){
                returnFiles.add(file);
                logger.info("*****Input File******");
                logger.info(" Name " + file.getSubmittedFileName());
                logger.info(" type " + file.getContentType());
                logger.info(" size " + file.getSize());
            }
        }
        return returnFiles;
    }

    /**
     * This function zips all the valid files from list
     * @param inputFiles
     * @param fileName
     * @return File
     */
    public File zipFiles(List<Part> inputFiles, String fileName){

        ArchiveOutputStream archive = null;
        OutputStream archiveStream = null;
        File returnFile = null;
        try{
            returnFile = new File(fileName);
            if(returnFile.exists()){
                returnFile.delete();
            }
            archiveStream = new FileOutputStream(returnFile);
            archive = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, archiveStream);

            for(Part file : inputFiles){
                if(file.getContentType() != null && file.getSize() > 0){
                    logger.info("*****Zipping File******");
                    logger.info(" Name " + file.getSubmittedFileName());
                    logger.info(" type " + file.getContentType());
                    logger.info(" size " + file.getSize());

                    //Add file entry to zip archive
                    ZipArchiveEntry entry = new ZipArchiveEntry(file.getSubmittedFileName());
                    archive.putArchiveEntry(entry);

                    BufferedInputStream input = new BufferedInputStream(file.getInputStream());

                    IOUtils.copy(input, archive);
                    input.close();

                    archive.closeArchiveEntry();

                }
            }
            // Complete archive entry addition.
            archive.finish();

        }catch(Exception e){
            e.printStackTrace();
            returnFile = null;
        }finally {
            if( archiveStream != null){
                try{
                    archiveStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                    logger.error("files stream not closed");
                }

            }
        }

        return returnFile;
    }

    /**
     *  To fetch message from message.properties file
     * @param key
     * @return String
     */


    public String fetchPropertiesValue(String key){
        String resp = "";
        try{
            if(key != null && !key.isEmpty()){
                resp = props.getProperty(key);
            }

        }catch(Exception e){
            e.printStackTrace();
            logger.error(" No value for  " + key);
        }
        return resp;
    }

    /**
     * To generate response in case of error
     * @param key
     * @return ResponseEntity
     */


    public ResponseEntity returnResponse(String key){
       return new ResponseEntity(fetchPropertiesValue(key),HttpStatus.BAD_REQUEST);
    }

    /**
     * Format timestamp as per format in FORMATTER variable
     * @param ts
     * @return String
     */


    public String convertTS(Timestamp ts) {
        return ts.toLocalDateTime().format(FORMATTER);
    }

}
