package org.example.Utility;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Colors {
    private static final Logger LOGGER = Logger.getLogger(Colors.class.getName());
    private static final Map<Integer, String> idToColor = new HashMap<>();
    private static final Map<String, Integer> colorToId = new HashMap<>();

    static {
        idToColor.put(1, "blau");
        idToColor.put(2, "grün");
        idToColor.put(3, "violett");
        idToColor.put(4, "rot");
        idToColor.put(5, "gelb");
        idToColor.put(6, "türkis");
        idToColor.put(7, "weiß");

        for (Map.Entry<Integer, String> entry : idToColor.entrySet()) {
            colorToId.put(entry.getValue(), entry.getKey());
        }
    }

    public static String convertColor(int id) {
        String color = idToColor.get(id);

        if (color == null) {
            LOGGER.log(Level.WARNING, "Color not found. Returning id 1 color.");
            color = idToColor.get(1);
        }

        return color;
    }

    public static int convertColor(String color) {
        Integer id = colorToId.get(color);

        if (id == null) {
            LOGGER.log(Level.WARNING, "Color not found. Returning id 1 color.");
            id = colorToId.get(idToColor.get(1));
        }

        return id;
    }
}
