package com.renato.vanguard_api.model;

import java.util.List;

public class PaginationResponse<T> {
    private String previous;
    private String next;
    private String url;
    private List<T> results;

    public PaginationResponse(String previous, String next, String url, List<T> results) {
        this.previous = previous;
        this.next = next;
        this.url = url;
        this.results = results;
    }

    public String getPrevious() {
        return previous;
    }

    public String getNext() {
        return next;
    }

    public String getUrl() {
        return url;
    }

    public List<T> getResults() {
        return results;
    }

}
