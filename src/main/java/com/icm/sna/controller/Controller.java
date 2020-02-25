package com.icm.sna.controller;

import com.icm.sna.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * 由于一些bug，service层的单元测试未能成功运行，于是写了一个控制器，
 * 使用浏览器访问运行本程序
 */
@RestController
public class Controller {
    @Autowired
    private EventService eventService;

    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public void starter(int id) {
        for (int i = 1; i < 2; i++) {
            eventService.getPassingEvent(i, 0, 3000, "1H");
//            eventService.getPassingEvent(i, 1500, 1500, "1H");
            eventService.getPassingEvent(i, 0, 3000, "2H");
//            eventService.getPassingEvent(i, 1500, 1500, "2H");
        }
    }

    @RequestMapping(value = "/vector", method = RequestMethod.GET)
    public void drawVector(int id) throws ExecutionException, InterruptedException {
        eventService.getPassingEvent2(id, 0, 3000);
    }
}
