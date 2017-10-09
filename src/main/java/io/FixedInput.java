package io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

//todo: to improve, not so good piece of code
public class FixedInput extends AppListReader {
    private String app;

    public FixedInput(String splitBy, String app) {
        super(splitBy);
        this.app = app;
        try {
            extractAppList();
        } catch (Exception ex) {ex.printStackTrace();}
    }

    @Override
    protected void extractAppList() throws FileNotFoundException, IOException {
        this.appList = new ArrayList<>();
        this.appList.add(app);
    }
}
