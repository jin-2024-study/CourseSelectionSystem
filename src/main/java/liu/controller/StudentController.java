package liu.controller;

import liu.entity.Student;
import liu.service.FileService;
import liu.service.StudentService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生管理控制器类
 * 提供学生信息的增删改查、搜索、分页、导出等功能
 * 需要管理员权限才能访问
 * 
 * @author Liu
 * @version 1.0
 * @since 2025
 */
@Controller
@RequestMapping("/students")
@PreAuthorize("hasRole('ADMIN')")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private FileService fileService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * 数据绑定初始化器
     * 配置日期格式转换器，支持yyyy-MM-dd格式的日期输入
     * 
     * @param binder Web数据绑定器
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /**
     * 显示学生列表页面（支持搜索和分页）
     * 
     * @param keyword 搜索关键字（可选），支持按学号、姓名、学院搜索
     * @param page 当前页码，默认为1
     * @param model 模型对象，用于向视图传递数据
     * @return 学生列表页面视图名称
     */
    @GetMapping
    public String list(@RequestParam(value = "keyword", required = false) String keyword,
                      @RequestParam(value = "page", defaultValue = "1") int page,
                      Model model) {
        List<Student> allStudents = studentService.getAllStudents();
        
        List<Student> filteredStudents = allStudents;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            filteredStudents = allStudents.stream()
                .filter(student -> {
                    boolean matches = student.getStudent_number().toLowerCase().contains(keyword.toLowerCase()) ||
                                    student.getStudent_name().toLowerCase().contains(keyword.toLowerCase()) ||
                                    student.getCollege().toLowerCase().contains(keyword.toLowerCase());
                    return matches;
                })
                .collect(Collectors.toList());
        }
        
        int pageSize = 5;
        int totalStudents = filteredStudents.size();
        int totalPages = (int) Math.ceil((double) totalStudents / pageSize);
        
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        
        int offset = (page - 1) * pageSize;
        List<Student> pageStudents = filteredStudents.stream()
            .skip(offset)
            .limit(pageSize)
            .collect(Collectors.toList());
        
        model.addAttribute("students", pageStudents);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalCount", totalStudents);
        model.addAttribute("hasResults", totalStudents > 0);
        model.addAttribute("keyword", keyword);
        model.addAttribute("hasNext", page < totalPages);
        model.addAttribute("hasPrevious", page > 1);
        
        return "student/list";
    }

    /**
     * 显示学生详细信息页面
     * 
     * @param id 学生ID
     * @param model 模型对象，用于向视图传递学生信息
     * @return 学生详情页面视图名称，如果学生不存在则重定向到列表页面
     */
    @GetMapping("/{id}")
    public String view(@PathVariable Integer id, Model model) {
        Student student = studentService.getStudentById(id);
        if (student == null) {
            return "redirect:/students?error=Student not found";
        }
        model.addAttribute("student", student);
        return "student/view";
    }

    /**
     * 显示添加学生的表单页面
     * 
     * @param model 模型对象，用于向视图传递空的学生对象
     * @return 添加学生表单页面视图名称
     */
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("student", new Student());
        return "student/add";
    }

    /**
     * 处理添加学生的POST请求
     * 
     * @param student 要添加的学生对象，使用@Valid进行数据验证
     * @param bindingResult 数据绑定结果，包含验证错误信息
     * @param photo 上传的学生照片文件（可选）
     * @param redirectAttributes 重定向属性，用于传递消息
     * @return 成功时重定向到学生列表页面，失败时返回添加表单页面
     */
    @PostMapping("/add")
    @Transactional
    public String add(@Valid @ModelAttribute Student student,
                     BindingResult bindingResult,
                     @RequestParam(value = "photo", required = false) MultipartFile photo,
                     RedirectAttributes redirectAttributes,
                     HttpServletRequest request) {
        
        if (bindingResult.hasErrors()) {
            return "student/add";
        }
        
        try {
            System.out.println("===== 开始添加学生流程 =====");
            System.out.println("收到的学生对象: " + student);
            System.out.println("请求内容类型: " + request.getContentType());
            System.out.println("请求方法: " + request.getMethod());
            System.out.println("请求参数名: " + java.util.Collections.list(request.getParameterNames()));
            
            // 检查是否是multipart请求
            if (request instanceof org.springframework.web.multipart.MultipartHttpServletRequest) {
                org.springframework.web.multipart.MultipartHttpServletRequest multipartRequest = 
                    (org.springframework.web.multipart.MultipartHttpServletRequest) request;
                System.out.println("这是一个multipart请求");
                System.out.println("文件参数名: " + multipartRequest.getFileNames());
                System.out.println("所有文件: " + multipartRequest.getFileMap());
            } else {
                System.out.println("这不是一个multipart请求！");
            }
            
            System.out.println("MultipartFile photo 对象: " + photo);
            System.out.println("photo != null: " + (photo != null));
            if (photo != null) {
                System.out.println("photo.isEmpty(): " + photo.isEmpty());
                System.out.println("photo.getSize(): " + photo.getSize());
                System.out.println("photo.getOriginalFilename(): " + photo.getOriginalFilename());
                System.out.println("photo.getContentType(): " + photo.getContentType());
            }
            System.out.println("是否有照片: " + (photo != null && !photo.isEmpty()));
            
            if (photo != null && !photo.isEmpty()) {
                System.out.println("照片信息: " + photo.getOriginalFilename() + ", 大小: " + photo.getSize());
                
                // 方案1：先插入学生，再保存照片，再更新
                System.out.println("--- 执行方案1：先插入再更新 ---");
                
                // 步骤1：先插入学生基本信息获取ID
                System.out.println("步骤1：插入学生基本信息");
                boolean insertResult = studentService.insertStudent(student);
                System.out.println("插入结果: " + insertResult + ", 获得ID: " + student.getStudent_id());
                
                if (!insertResult || student.getStudent_id() <= 0) {
                    throw new RuntimeException("学生基本信息插入失败，ID: " + student.getStudent_id());
                }
                
                // 步骤2：保存照片
                System.out.println("步骤2：保存照片，使用学生ID: " + student.getStudent_id());
                String photoPath = fileService.saveStudentPhoto(student.getStudent_id(), photo);
                System.out.println("照片保存路径: " + photoPath);
                
                // 步骤3：更新学生的照片路径
                System.out.println("步骤3：更新学生照片路径");
                student.setPhoto_path(photoPath);
                boolean updateResult = studentService.updateStudent(student);
                System.out.println("更新结果: " + updateResult);
                
                if (!updateResult) {
                    throw new RuntimeException("照片路径更新失败");
                }
                
                // 验证最终结果
                Student finalStudent = studentService.getStudentById(student.getStudent_id());
                System.out.println("最终验证结果: " + finalStudent);
                
            } else {
                System.out.println("--- 执行无照片流程 ---");
                // 没有照片，直接插入
                boolean insertResult = studentService.insertStudent(student);
                System.out.println("插入结果: " + insertResult + ", 获得ID: " + student.getStudent_id());
                
                if (!insertResult) {
                    throw new RuntimeException("学生信息插入失败");
                }
                
                // 验证插入结果
                Student verifyStudent = studentService.getStudentById(student.getStudent_id());
                System.out.println("验证查询结果: " + verifyStudent);
            }
            
            System.out.println("===== 学生添加流程完成 =====");
            redirectAttributes.addFlashAttribute("success", "学生添加成功！");
            return "redirect:/students";
            
        } catch (Exception e) {
            System.err.println("===== 添加学生失败 =====");
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "添加失败: " + e.getMessage());
            return "redirect:/students/add";
        }
    }

    /**
     * 显示编辑学生信息的表单页面
     * 
     * @param id 要编辑的学生ID
     * @param model 模型对象，用于向视图传递学生信息
     * @return 编辑学生表单页面视图名称，如果学生不存在则重定向到列表页面
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Student student = studentService.getStudentById(id);
        if (student == null) {
            return "redirect:/students?error=Student not found";
        }
        model.addAttribute("student", student);
        return "student/edit";
    }

    /**
     * 处理更新学生信息的POST请求
     * 
     * @param id 要更新的学生ID
     * @param student 更新后的学生对象，使用@Valid进行数据验证
     * @param bindingResult 数据绑定结果，包含验证错误信息
     * @param photo 上传的新学生照片文件（可选）
     * @param redirectAttributes 重定向属性，用于传递消息
     * @param model 模型对象，用于在验证失败时传递数据
     * @return 成功时重定向到学生列表页面，失败时返回编辑表单页面
     */
    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id,
                      @Valid @ModelAttribute Student student,
                      BindingResult bindingResult,
                      @RequestParam(value = "photo", required = false) MultipartFile photo,
                      RedirectAttributes redirectAttributes,
                      Model model) {
        

        if (bindingResult.hasErrors()) {
            System.out.println("Errors: " + bindingResult.getAllErrors());
        }
        
        if (bindingResult.hasErrors()) {
            // 重新加载学生信息以显示错误页面
            Student existingStudent = studentService.getStudentById(id);
            if (existingStudent != null) {
                model.addAttribute("student", existingStudent);
            }
            redirectAttributes.addFlashAttribute("error", "表单验证失败，请检查输入的数据");
            return "student/edit";
        }
        
        try {
            Student existingStudent = studentService.getStudentById(id);
            if (existingStudent == null) {
                redirectAttributes.addFlashAttribute("error", "Student not found");
                return "redirect:/students";
            }
            
            System.out.println("Existing Student: " + existingStudent);
            
            // 处理照片上传
            if (photo != null && !photo.isEmpty()) {
                System.out.println("开始上传新照片: " + photo.getOriginalFilename());
                
                // 如果已有照片，先删除原照片
                if (existingStudent.getPhoto_path() != null && !existingStudent.getPhoto_path().isEmpty()) {
                    System.out.println("准备删除原照片: " + existingStudent.getPhoto_path());
                    boolean deleted = fileService.deleteFile(existingStudent.getPhoto_path());
                    System.out.println("原照片删除结果: " + (deleted ? "成功" : "失败"));
                }
                
                // 保存新照片
                String photoPath = fileService.saveStudentPhoto(id, photo);
                System.out.println("新照片保存完成，路径: " + photoPath);
                student.setPhoto_path(photoPath);
            } else {
                System.out.println("未上传新照片，保持原有路径: " + existingStudent.getPhoto_path());
                student.setPhoto_path(existingStudent.getPhoto_path());
            }
            
            // 设置学生ID
            student.setStudent_id(id);

            // 执行更新
            boolean result = studentService.updateStudent(student);
            System.out.println("Update result: " + result);
            
            if (result) {
                redirectAttributes.addFlashAttribute("success", "学生信息更新成功！");
            } else {
                redirectAttributes.addFlashAttribute("error", "更新失败，请重试");
            }
            
            return "redirect:/students";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "更新失败: " + e.getMessage());
            return "redirect:/students/" + id + "/edit";
        }
    }

    /**
     * 处理删除学生的POST请求
     * 
     * @param id 要删除的学生ID
     * @param redirectAttributes 重定向属性，用于传递操作结果消息
     * @return 重定向到学生列表页面
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("message", "Student deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete: " + e.getMessage());
        }
        return "redirect:/students";
    }

    /**
     * 处理学生搜索请求
     * 将搜索参数重定向到列表页面，支持中文关键字的URL编码
     * 
     * @param keyword 搜索关键字
     * @param model 模型对象
     * @return 重定向到带搜索参数的学生列表页面
     */
    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        try {

            
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8.toString());

            String redirectUrl = "redirect:/students?keyword=" + encodedKeyword;
            System.out.println("Redirect URL: " + redirectUrl);

            return redirectUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/students?keyword=" + keyword;
        }
    }

    /**
     * 导出学生信息为Excel文件
     * 生成包含所有学生信息的Excel工作表，支持浏览器下载
     * 
     * @param response HTTP响应对象，用于设置下载文件的响应头和输出流
     * @throws IOException 文件操作异常
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<Student> students = studentService.getAllStudents();
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Student Information");
        
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 4000);
        
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(headerStyle);
        
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(dataStyle);
        
        int rowNum = 0;
        
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Student Information");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        
        rowNum++;
        
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Student Number", "Student Name", "College", "Enrollment Date"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        for (Student student : students) {
            Row dataRow = sheet.createRow(rowNum++);
            
            dataRow.createCell(0).setCellValue(student.getStudent_number());
            dataRow.createCell(1).setCellValue(student.getStudent_name());
            dataRow.createCell(2).setCellValue(student.getCollege());
            dataRow.createCell(3).setCellValue(student.getEnrollment_date() != null ? 
                new SimpleDateFormat("yyyy-MM-dd").format(student.getEnrollment_date()) : "");
            
            for (int i = 0; i < 4; i++) {
                dataRow.getCell(i).setCellStyle(dataStyle);
            }
        }
        
        String fileName = "students_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + 
                          URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()) + "\"");
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }



    /**
     * 为Excel单元格样式设置边框
     * 
     * @param style 要设置边框的单元格样式
     */
    private void setBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }
}