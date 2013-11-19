package io.ei.jsontoxls.util;

import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class ExcelUtils {

    public StreamingOutput generateExcelWorkbook(Map<String, Object> beans, String excelTemplate) throws InvalidFormatException, IOException {
        FileInputStream inputStream = new FileInputStream(excelTemplate);
        XLSTransformer transformer = new XLSTransformer();
        Workbook workbook = transformer.transformXLS(inputStream, beans);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return getExcelAsOutputStream(outputStream.toByteArray());
    }

    private StreamingOutput getExcelAsOutputStream(final byte[] excelBytes) {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream out) throws IOException, WebApplicationException {
                out.write(excelBytes);
            }
        };
    }
}