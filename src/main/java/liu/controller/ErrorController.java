package liu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    
    @GetMapping("/error/404")
    public String handleNotFound() {
        return "error/404";
    }
    
    @GetMapping("/error/500")
    public String handleInternalError() {
        return "error/500";
    }
    
    @RequestMapping("/error")
    public String handleError() {
        return "error/error";
    }
} 