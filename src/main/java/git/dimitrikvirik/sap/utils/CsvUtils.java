package git.dimitrikvirik.sap.utils;

import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import git.dimitrikvirik.sap.model.dto.CityCsvDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CsvUtils {

    private static class FieldInfo {
        String fieldName;
        String columnName;
        int position;

        FieldInfo(String fieldName, String columnName, int position) {
            this.fieldName = fieldName;
            this.columnName = columnName;
            this.position = position;
        }
    }

    private static List<FieldInfo> getOrderedFieldInfo(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(CsvBindByPosition.class) && field.isAnnotationPresent(CsvBindByName.class))
                .map(field -> new FieldInfo(
                        field.getName(),
                        field.getAnnotation(CsvBindByName.class).column().toLowerCase(),
                        field.getAnnotation(CsvBindByPosition.class).position()
                ))
                .sorted(Comparator.comparingInt(info -> info.position))
                .collect(Collectors.toList());
    }


    public static <T> List<T> read(Class<T> clazz, MultipartFile file) throws IllegalStateException {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(clazz)
                    .withSeparator(';')
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Failed to read CSV data: " + e.getMessage(), e);
        }
    }

    public static <T> byte[] write(List<T> entities, Class<T> dtoClass) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(baos))) {

            List<FieldInfo> fieldInfos = getOrderedFieldInfo(dtoClass);

            // Write header
            String[] header = fieldInfos.stream()
                    .map(info -> info.columnName)
                    .toArray(String[]::new);
            csvWriter.writeNext(header);

            // Set up the mapping strategy
            ColumnPositionMappingStrategy<T> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(dtoClass);
            String[] columnMapping = fieldInfos.stream()
                    .map(info -> info.fieldName)
                    .toArray(String[]::new);
            strategy.setColumnMapping(columnMapping);

            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(csvWriter)
                    .withMappingStrategy(strategy)
                    .withOrderedResults(true)
                    .build();

            beanToCsv.write(entities);
            csvWriter.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to write CSV data: " + e.getMessage(), e);
        }
    }

}
