package com.example.demo.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.DataFiles;
import com.example.demo.repository.FileRepository;
@Service
public class FileService {
	private final FileRepository fileRepository;
	
	@Autowired
	public FileService(FileRepository fileRepository) {
		this.fileRepository = fileRepository;
		
	}
	public void save(MultipartFile mfile) throws IOException{
		DataFiles file = new DataFiles();
		file.setFilename(StringUtils.cleanPath(mfile.getOriginalFilename()));
		file.setFileObj(mfile.getBytes());
		fileRepository.save(file);
	}
}
