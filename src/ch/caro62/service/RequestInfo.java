/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.caro62.service;

import java.time.LocalDateTime;
import okhttp3.Request;

/**
 *
 * @author Karalina Chureyna
 */
public class RequestInfo {

    private final LocalDateTime started;
    private final String ref;    
    
    public RequestInfo(Request req) {
        this.started = LocalDateTime.now();
        this.ref = req.url().toString();
    }
    
    public LocalDateTime getStarted() {
        return started;
    }
    
    public String getRef() {
        return ref;
    }
    
}
