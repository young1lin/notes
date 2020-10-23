package me.young1lin.spring.practice.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/23 5:19 下午
 */
@RestController
@Slf4j
@RequestMapping("/file")
public class FileController {

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        File convertFile = new File("/var/tmp/" + file.getOriginalFilename());
        convertFile.createNewFile();
        try (FileOutputStream fout = new FileOutputStream(convertFile)) {
            fout.write(file.getBytes());
        } catch (IOException e) {
            log.info(Arrays.toString(e.getStackTrace()));
        }
        return "上传文件成功";
    }


    @GetMapping(value = "/download")
    public ResponseEntity<Object> downloadFile() throws IOException  {
        String filename = "/var/tmp/mysql.png";
        File file = new File(filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Disposition", String.format("attachment; filename=\\%s\\", file.getName()));
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        ResponseEntity<Object>
                responseEntity = ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(
                MediaType.parseMediaType("application/txt")).body(resource);

        return responseEntity;
    }


}
