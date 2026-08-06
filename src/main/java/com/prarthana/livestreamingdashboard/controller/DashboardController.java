package com.prarthana.livestreamingdashboard.controller;

import com.prarthana.livestreamingdashboard.service.StreamService;
import com.prarthana.livestreamingdashboard.entity.Stream;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
public class DashboardController {


    private final StreamService service;



    @GetMapping("/")
    public String dashboard(Model model){


        model.addAttribute(
                "streams",
                service.getAllStreams()
        );


        addCounts(model);


        return "dashboard";
    }





    @GetMapping("/search")
    public String search(
            @RequestParam String name,
            Model model){


        model.addAttribute(
                "streams",
                service.searchStreams(name)
        );


        addCounts(model);


        return "dashboard";
    }





    @GetMapping("/status")
    public String statusFilter(
            @RequestParam String value,
            Model model){


        model.addAttribute(
                "streams",
                service.getStreamsByStatus(value)
        );


        addCounts(model);


        return "dashboard";
    }





    @GetMapping("/add")
    public String addPage(){

        return "add-stream";

    }





    @PostMapping("/add-stream")
    public String addStream(Stream stream){


        service.addStream(stream);


        return "redirect:/";

    }





    private void addCounts(Model model){


        model.addAttribute(
                "total",
                service.getTotalCount()
        );


        model.addAttribute(
                "online",
                service.getOnlineCount()
        );


        model.addAttribute(
                "offline",
                service.getOfflineCount()
        );

    }


}