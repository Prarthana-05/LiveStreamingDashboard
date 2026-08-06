package com.prarthana.livestreamingdashboard.controller;


import java.util.List;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import com.prarthana.livestreamingdashboard.entity.Stream;
import com.prarthana.livestreamingdashboard.service.StreamService;


@RestController
@RequestMapping("/streams")
@RequiredArgsConstructor
public class StreamController {


    private final StreamService service;



    @GetMapping
    public List<Stream> getStreams(){

        return service.getAllStreams();

    }



    @PostMapping
    public Stream addStream(@RequestBody Stream stream){

        return service.addStream(stream);

    }



    @GetMapping("/search")
    public List<Stream> searchStreams(
            @RequestParam String name){

        return service.searchStreams(name);

    }


}