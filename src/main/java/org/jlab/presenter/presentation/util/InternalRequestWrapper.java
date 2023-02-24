package org.jlab.presenter.presentation.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class InternalRequestWrapper extends HttpServletRequestWrapper {

    private LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();

    public InternalRequestWrapper(HttpServletRequest request, LinkedHashMap<String, List<String>> params) {
        super(request);
        
        this.params = params;
    }

    @Override
    public String getParameter(String name) {
        List<String> values = params.get(name);
        String value = null;
        if (values != null && values.size() > 0) {
            value = values.get(0);
        }
        return value;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> paramArrays = new LinkedHashMap<String, String[]>();
        
        for(String key: params.keySet()) {
            paramArrays.put(key, params.get(key).toArray(new String[0]));
        }
        
        return paramArrays;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return params.get(name).toArray(new String[0]);
    }

    public void addParameter(String name, String value) {
        List<String> values = params.get(name);

        if (values == null) {
            values = new ArrayList<String>();
        } 
        
        values.add(value);
        
        params.put(name, values);
    }
}
