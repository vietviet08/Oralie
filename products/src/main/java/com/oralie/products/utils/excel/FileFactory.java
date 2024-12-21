package com.oralie.products.utils.excel;

import com.oralie.products.utils.obj.PathTemplate;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Component
public class FileFactory {

    public static File createFile(String fileName, Workbook workbook) throws Exception {

        workbook = new XSSFWorkbook();

        OutputStream out = new FileOutputStream(PathTemplate.PATH_TEMPLATE + fileName);

        workbook.write(out);

        return ResourceUtils.getFile(PathTemplate.PATH_TEMPLATE + fileName);
    }


    public static Workbook getWorkbookStream(MultipartFile importFile){
        InputStream inputStream = null;
        try {
            inputStream = importFile.getInputStream();

            Workbook workbook = WorkbookFactory.create(inputStream);

            return workbook;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}