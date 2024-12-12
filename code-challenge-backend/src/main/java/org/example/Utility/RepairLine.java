package org.example.Utility;

import java.util.ArrayList;
import java.util.List;

public class RepairLine {
    private List<String> line;
    private int id;
    private boolean isRepairing;

    public RepairLine() {
        this.reset();
    }

    public void reset() {
        this.line = new ArrayList<>();
        this.id = -1;
        this.isRepairing = false;
    }

    public void addLine(int id, List<String> newLine) {
        line.addAll(newLine);

        if (!isRepairing) {
            this.isRepairing = true;
            this.id = id;
        }
    }

    public List<String> getLine() {
        return this.line;
    }

    public int getId() {
        return id;
    }
}
