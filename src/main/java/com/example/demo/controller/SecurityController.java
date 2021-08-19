package com.example.demo.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.DataFiles;
import com.example.demo.model.SiteUser;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.SiteUserRepository;
import com.example.demo.service.FileService;
import com.example.demo.util.Role;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class SecurityController {
	private final FileService FileService;
    private final SiteUserRepository userRepository;
    private final FileRepository FileRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String showList(Authentication loginUser, Model model) {
        model.addAttribute("username", loginUser.getName());
        model.addAttribute("role", loginUser.getAuthorities());
        return "user";
    }

    @GetMapping("/admin/list")
    public String showAdminList(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "list";
    }

    @GetMapping("/register")
    public String register(@ModelAttribute("user") SiteUser user) {
        return "register";
    }

    @GetMapping("/admin/edit/{id}")
    public String editSiteUser(@PathVariable Long id, Model model) {
    	model.addAttribute("user",userRepository.findById(id));
    	return "adminEdit";
    	}
    
    @PostMapping("/admin/edit")
    public String editSiteUser(@Validated @ModelAttribute("user") SiteUser user,BindingResult result) {

        if (result.hasErrors()) {
            return "adminEdit";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.isAdmin()) {
            user.setRole(Role.ADMIN.name());
        } else {
            user.setRole(Role.USER.name());
        }
        userRepository.save(user);

        return "redirect:/admin/list";
    }
   
    @GetMapping("/admin/delete/{id}")
    public String deleteSiteUser(@PathVariable Long id) {
    	userRepository.deleteById(id);
    	return "redirect:/admin/list";
    }
    
    @GetMapping("/admin/adminRedister")
    public String adminRedister(@ModelAttribute("user") SiteUser user) {
        return "adminRedister";
    }
    
    @PostMapping("/admin/adminRedister")
    public String adminRedister(@Validated @ModelAttribute("user") SiteUser user,BindingResult result) {

        if (result.hasErrors()) {
            return "adminRedister";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.isAdmin()) {
            user.setRole(Role.ADMIN.name());
        } else {
            user.setRole(Role.USER.name());
        }
        userRepository.save(user);

        return "redirect:/admin/list";
    }
    
    @GetMapping("/filelist")
    public String filelist(Model model) {
    	model.addAttribute("files", FileRepository.findAll());
        return "filelist";
    }
    
    @GetMapping("/fileadd")//ファイルデータ登録画面へ遷移
    public String fileadd(@ModelAttribute DataFiles file) {
    	return "fileadd";
    }
    
    @PostMapping("/add")//ファイル登録処理
    public String add(@RequestParam("upload_file") MultipartFile uploadFile){
        //1件追加
    	if(uploadFile.isEmpty()) {
    		return "filelist";
    	}
    	try {
    		FileService.save(uploadFile);
    	}catch(Exception e) {
    		System.err.println(e);
    	}
    	
    	return "redirect:/filelist"; //一覧画面へ遷移
    }
    @GetMapping("/download/{id}")//ファイルダウンロード処理
    public String download(@PathVariable Long id, HttpServletResponse response) {
    	byte[] buff = FileRepository.getById(id).getFileObj();
    	ByteArrayInputStream bis = new ByteArrayInputStream(buff);
    	String filename = FileRepository.getById(id).getFilename();
    	//ファイルダウンロードの設定を実施
        //ファイルの種類は指定しない
        response.setContentType("application/octet-stream");
        response.setHeader("Cache-Control", "private");
        response.setHeader("Pragma", "");
        response.setHeader("Content-Disposition"
                ,"attachment;filename=\"" + filename + "\"");
    	try {
			IOUtils.copy(bis, response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    @PostMapping("/register")
    public String process(@Validated @ModelAttribute("user") SiteUser user,
            BindingResult result) {

        if (result.hasErrors()) {
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.isAdmin()) {
            user.setRole(Role.ADMIN.name());
        } else {
            user.setRole(Role.USER.name());
        }
        userRepository.save(user);

        return "redirect:/login?register";
    }
}
