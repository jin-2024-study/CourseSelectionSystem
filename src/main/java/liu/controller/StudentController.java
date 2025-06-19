package liu.controller;

import liu.entity.Student;
import liu.service.FileService;
import liu.service.StudentService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.apache.poi.ss.usermodel.BorderStyle.*;

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
@RequestMapping("students")
@PreAuthorize("hasRole('ADMIN')")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    @Autowired
    private FileService fileService;

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
                .filter(student -> student.getStudent_number().toLowerCase().contains(keyword.toLowerCase()) ||
                                student.getStudent_name().toLowerCase().contains(keyword.toLowerCase()) ||
                                student.getCollege().toLowerCase().contains(keyword.toLowerCase()))
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
            logger.info("===== 开始添加学生流程 =====");
            logger.debug("收到的学生对象: {}", student);
            logger.debug("请求内容类型: {}", request.getContentType());
            logger.debug("请求方法: {}", request.getMethod());
            logger.debug("请求参数名: {}", java.util.Collections.list(request.getParameterNames()));

            // 检查是否是multipart请求
            if (request instanceof org.springframework.web.multipart.MultipartHttpServletRequest multipartRequest) {
                logger.debug("这是一个multipart请求");
                logger.debug("文件参数名: {}", multipartRequest.getFileNames());
                logger.debug("所有文件: {}", multipartRequest.getFileMap());
            } else {
                logger.debug("这不是一个multipart请求！");
            }
            
            logger.debug("MultipartFile photo 对象: {}", photo);
            logger.debug("photo != null: {}", (photo != null));
            if (photo != null) {
                logger.debug("photo.isEmpty(): {}", photo.isEmpty());
                logger.debug("photo.getSize(): {}", photo.getSize());
                logger.debug("photo.getOriginalFilename(): {}", photo.getOriginalFilename());
                logger.debug("photo.getContentType(): {}", photo.getContentType());
            }
            logger.debug("是否有照片: {}", (photo != null && !photo.isEmpty()));
            
            if (photo != null && !photo.isEmpty()) {
                logger.info("照片信息: {}, 大小: {}", photo.getOriginalFilename(), photo.getSize());
                
                // 方案1：先插入学生，再保存照片，再更新
                logger.info("--- 执行方案1：先插入再更新 ---");
                
                // 步骤1：先插入学生基本信息
                logger.debug("步骤1：插入学生基本信息");
                boolean insertResult = studentService.insertStudent(student);
                logger.debug("插入结果: {}, 获得ID: {}", insertResult, student.getStudent_id());
                
                if (!insertResult || student.getStudent_id() <= 0) {
                    throw new RuntimeException("插入学生基本信息失败");
                }
                
                // 步骤2：保存照片
                logger.debug("步骤2：保存照片，使用学生ID: {}", student.getStudent_id());
                String photoPath = fileService.saveStudentPhoto(student.getStudent_id(), photo);
                logger.info("照片保存路径: {}", photoPath);
                
                // 步骤3：更新学生照片路径
                logger.debug("步骤3：更新学生照片路径");
                student.setPhoto_path(photoPath);
                boolean updateResult = studentService.updateStudent(student);
                logger.debug("更新结果: {}", updateResult);
                
                if (!updateResult) {
                    throw new RuntimeException("更新学生照片路径失败");
                }
                
                // 最终验证
                Student finalStudent = studentService.getStudentById(student.getStudent_id());
                logger.debug("最终验证结果: {}", finalStudent);
                
            } else {
                logger.info("--- 执行无照片流程 ---");
                // 无照片直接插入
                boolean insertResult = studentService.insertStudent(student);
                logger.debug("插入结果: {}, 获得ID: {}", insertResult, student.getStudent_id());
                
                if (!insertResult) {
                    throw new RuntimeException("插入学生信息失败");
                }
                
                // 验证插入结果
                Student verifyStudent = studentService.getStudentById(student.getStudent_id());
                logger.debug("验证查询结果: {}", verifyStudent);
            }
            
            logger.info("===== 学生添加流程完成 =====");
            redirectAttributes.addFlashAttribute("success", "学生添加成功");
            return "redirect:/students";
        } catch (Exception e) {
            logger.error("===== 添加学生失败 =====");
            logger.error("错误信息: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "添加学生失败: " + e.getMessage());
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
     * @return 成功时重定向到学生列表页面，失败时返回编辑表单页面
     */
    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id,
                      @Valid @ModelAttribute Student student,
                      BindingResult bindingResult,
                      @RequestParam(value = "photo", required = false) MultipartFile photo,
                      RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            logger.debug("表单验证错误: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("error", "表单验证失败，请检查输入的数据");
            return "redirect:/students/" + id + "/edit";
        }
        
        try {
            Student existingStudent = studentService.getStudentById(id);
            if (existingStudent == null) {
                redirectAttributes.addFlashAttribute("error", "Student not found");
                return "redirect:/students";
            }
            
            logger.debug("现有学生信息: {}", existingStudent);
            
            // 处理照片上传
            if (photo != null && !photo.isEmpty()) {
                logger.info("开始上传新照片: {}", photo.getOriginalFilename());
                
                // 如果已有照片，先删除原照片
                if (existingStudent.getPhoto_path() != null && !existingStudent.getPhoto_path().isEmpty()) {
                    logger.debug("准备删除原照片: {}", existingStudent.getPhoto_path());
                    boolean deleted = fileService.deleteFile(existingStudent.getPhoto_path());
                    logger.debug("原照片删除结果: {}", deleted ? "成功" : "失败");
                }
                
                // 保存新照片
                String photoPath = fileService.saveStudentPhoto(id, photo);
                logger.info("新照片保存完成，路径: {}", photoPath);
                student.setPhoto_path(photoPath);
            } else {
                logger.debug("未上传新照片，保持原有路径: {}", existingStudent.getPhoto_path());
                student.setPhoto_path(existingStudent.getPhoto_path());
            }
            
            // 设置学生ID
            student.setStudent_id(id);

            // 执行更新
            boolean result = studentService.updateStudent(student);
            logger.debug("更新结果: {}", result);
            
            if (result) {
                redirectAttributes.addFlashAttribute("success", "学生信息更新成功！");
            } else {
                redirectAttributes.addFlashAttribute("error", "更新失败，请重试");
            }
            
            return "redirect:/students";
        } catch (Exception e) {
            logger.error("更新学生信息时发生错误: {}", e.getMessage(), e);
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
     * @return 重定向到带搜索参数的学生列表页面
     */
    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword) {
        try {
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String redirectUrl = "redirect:/students?keyword=" + encodedKeyword;
            logger.debug("重定向URL: {}", redirectUrl);
            return redirectUrl;
        } catch (Exception e) {
            logger.error("搜索重定向时发生错误: {}", e.getMessage(), e);
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
                          URLEncoder.encode(fileName, StandardCharsets.UTF_8) + "\"");
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 为Excel单元格样式设置边框
     * 
     * @param style 要设置边框的单元格样式
     */
    private void setBorders(CellStyle style) {
        style.setBorderTop(THIN);
        style.setBorderBottom(THIN);
        style.setBorderLeft(THIN);
        style.setBorderRight(THIN);
    }
}