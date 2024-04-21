package com.stepup.MmlnTask_04.interfaces;

import com.stepup.MmlnTask_04.entities.DataFromFiles;

import java.io.IOException;
import java.util.List;

public interface Handler01FileReaderable {
    List<DataFromFiles> readFromFile(String strPath) throws IOException;
    public String getPath();
}