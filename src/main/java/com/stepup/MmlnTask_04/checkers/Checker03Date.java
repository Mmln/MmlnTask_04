package com.stepup.MmlnTask_04.checkers;

import com.stepup.MmlnTask_04.interfaces.Checker03Dateable;
import com.stepup.MmlnTask_04.loggers.LogTransformation;
import org.springframework.stereotype.Component;
import com.stepup.MmlnTask_04.entities.DataFromFiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Checker03Date implements Checker03Dateable {
    private final String logFileName = "EmptyDateChecking.log";

    private void writeFile(List<String> linesToWrite, String fullFileName) throws IOException {
        Path textFile = Paths.get(fullFileName);
        Files.write(textFile, linesToWrite, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,  StandardOpenOption.TRUNCATE_EXISTING);
    }
    private String getStrWrite(DataFromFiles datas) {
        return datas.getFileName() + ": " +
                datas.getUsername() + " " +
                datas.getFio() + " {" + Calendar.getInstance().getTime().toString() + "} - error: access_date is empty";
    }

    @LogTransformation("LogTransformation.log")
    @Override
    public List<DataFromFiles> checkDate(List<DataFromFiles> datas) throws IOException {
        List<String> linesToFile = new ArrayList<>();
        List<DataFromFiles> dataOut = datas.stream().filter(x-> (!x.getAccessDate().isEmpty())).
                collect(Collectors.toList());
        List<DataFromFiles> linesToFileDate = datas.stream().filter(x-> (x.getAccessDate().isEmpty())).toList();
        if (!linesToFileDate.isEmpty()) {
            for (DataFromFiles dates : linesToFileDate){
                linesToFile.add(getStrWrite(dates));
            }

            writeFile(linesToFile, logFileName );
        }
        return dataOut;
    }


}
