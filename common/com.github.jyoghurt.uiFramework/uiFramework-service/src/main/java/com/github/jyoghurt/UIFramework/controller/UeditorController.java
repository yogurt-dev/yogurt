package com.github.jyoghurt.UIFramework.controller;

import com.github.jyoghurt.UIFramework.ueditor.ActionEnter;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by zhangjl on 2015/12/15.
 */
@RestController
@LogContent(module = "ueditor")
@RequestMapping("/ueditor")
public class UeditorController extends BaseController {
    @Value("${uploadPath}")
    private String uploadPath;
    @Value("${downloadPath}")
    private String downloadPath;
    @RequestMapping(value = "/uploadImg", method = RequestMethod.GET)
    @ResponseBody    public void upload() throws IOException {
        String action = request.getParameter("action");
        if ("config".equals(action)) {
            request.setCharacterEncoding( "utf-8" );
            response.setHeader("Content-Type", "text/html");
            String rootPath = request.getSession().getServletContext().getRealPath("/");
            try {
                String exec = new ActionEnter(request, rootPath,uploadPath,downloadPath,null).exec();
                PrintWriter writer = response.getWriter();
                writer.write(exec);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/uploadImg", method = RequestMethod.POST)
    @ResponseBody
    public void upload(@RequestParam CommonsMultipartFile upfile) throws IOException {
        System.out.print("");
        request.setCharacterEncoding( "utf-8" );
        request.setAttribute("action", "uploadimage");
        response.setHeader("Content-Type", "text/html");
        String rootPath = request.getSession().getServletContext().getRealPath("/");
        String exec = new ActionEnter(request, rootPath,uploadPath,downloadPath,upfile).exec();
        PrintWriter writer = response.getWriter();
        writer.write(exec);
        writer.flush();
        writer.close();
    }
}


