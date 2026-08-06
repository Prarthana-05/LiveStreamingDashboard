package com.prarthana.livestreamingdashboard.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.prarthana.livestreamingdashboard.entity.Stream;
import com.prarthana.livestreamingdashboard.repository.StreamRepository;


@Service
@RequiredArgsConstructor
public class StreamService {


    private final StreamRepository repository;



    public List<Stream> getAllStreams(){

        return repository.findAll();

    }



    public Stream addStream(Stream stream){

        return repository.save(stream);

    }



    public List<Stream> searchStreams(String name){

        return repository.findByStreamNameContainingIgnoreCase(name);

    }



    public long getTotalCount(){

        return repository.count();

    }



    public long getOnlineCount(){

        return repository.countByStatus("online");

    }



    public long getOfflineCount(){

        return repository.countByStatus("offline");

    }



    public List<Stream> getStreamsByStatus(String status){

        return repository.findByStatus(status);

    }

}