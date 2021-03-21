package com.correvate.fileapp;

import com.correvate.fileapp.utils.FileAppUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@Configuration
@EnableWebMvc
public class FileAppInitializer {

    @Bean
    public FileAppUtils fileAppUtilsInitialize() throws Exception{
        return new FileAppUtils();
    }


}
