package com.sysu.sjk.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sjk on 16-10-21.
 */
public class ApiResponse<T> implements Serializable {

    private boolean error;
    private List<T> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
