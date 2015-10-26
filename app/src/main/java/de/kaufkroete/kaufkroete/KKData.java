package de.kaufkroete.kaufkroete;

import java.net.URL;

public class KKData {

    int sid;
    String name;
    URL imageUrl;
    String info, detail;

    public boolean matchesFilter(String filter) {
        return !filter.isEmpty() && name.contains(filter);
    }

    public String getName() {
        return name;
    }

    public URL getImageUrl() {
        return imageUrl;
    }

    public int getId() {
        return sid;
    }

    public String getInfo() {
        return info;
    }

    public String getDetail() {
        return detail;
    }

}
