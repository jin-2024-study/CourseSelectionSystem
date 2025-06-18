package liu.controller;

import liu.entity.CourseSelection;
import liu.entity.CourseSelectionList;
import liu.entity.Course;
import liu.entity.Student;
import liu.service.CourseSelectionService;
import liu.service.CourseSelectionListService;
import liu.service.CourseService;
import liu.service.StudentService;
import liu.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @description: 选课管理控制器
 * @author: liu jin
 * @create: 2025-04-18 19:37
 **/

@Controller
@RequestMapping("/courseSelection")
public class CourseSelectionController {

    @Autowired
    private StudentService studentService;
    
    @Autowired
    private CourseSelectionService courseSelectionService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private CourseSelectionListService courseSelectionListService;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 选课系统入口 - 根据用户角色重定向到不同页面
     */
    @GetMapping
    public String courseSelectionHome(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {
        // 检查当前用户角色
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (isAdmin) {
            // 管理员重定向到学生列表页面
            return "redirect:/courseSelection/admin/list";
        } else {
            // 普通用户显示学生选择页面
            return showStudentSelectionWithPagination(keyword, page, model);
        }
    }
    
    /**
     * 显示学生选择页面（支持搜索和分页）
     */
    private String showStudentSelectionWithPagination(String keyword, int page, Model model) {
        try {
            // 每页显示3个学生
            int pageSize = 3;
            int offset = (page - 1) * pageSize;
            
            List<Student> allStudents;
            int totalCount;
            
            // 根据关键词搜索或获取所有学生
            if (keyword != null && !keyword.trim().isEmpty()) {
                allStudents = studentService.getAllStudents().stream()
                        .filter(student -> 
                            (student.getStudent_number() != null && student.getStudent_number().contains(keyword.trim())) ||
                            (student.getStudent_name() != null && student.getStudent_name().contains(keyword.trim()))
                        )
                        .collect(java.util.stream.Collectors.toList());
                totalCount = allStudents.size();
            } else {
                allStudents = studentService.getAllStudents();
                totalCount = allStudents.size();
            }
            
            // 分页处理
            List<Student> students = allStudents.stream()
                    .skip(offset)
                    .limit(pageSize)
                    .collect(java.util.stream.Collectors.toList());
            
            // 计算分页信息
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);
            
            if (students.isEmpty() && totalCount == 0) {
                if (keyword != null && !keyword.trim().isEmpty()) {
                    model.addAttribute("error", "没有找到匹配的学生记录");
                } else {
                    model.addAttribute("error", "系统中还没有学生记录，请联系管理员添加学生信息");
                }
            } else if (students.isEmpty() && page > 1) {
                // 如果当前页没有数据但总数据不为0，重定向到第一页
                return "redirect:/courseSelection?keyword=" + (keyword != null ? keyword : "") + "&page=1";
            }
            
            model.addAttribute("students", students);
            model.addAttribute("keyword", keyword);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalCount", totalCount);
            model.addAttribute("hasResults", totalCount > 0);
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                model.addAttribute("message", "搜索到 " + totalCount + " 个匹配的学生");
            } else {
                model.addAttribute("message", "请选择您的学生身份进行选课操作");
            }
            
            return "courseSelection/student_selection";
            
        } catch (Exception e) {
            model.addAttribute("error", "获取学生信息失败: " + e.getMessage());
            return "courseSelection/student_selection";
        }
    }
    
    /**
     * 显示所有学生选课信息的列表页面 - 仅管理员可访问（支持搜索和分页）
     */
    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {
        
        try {
            // 每页显示10条记录
            int pageSize = 10;
            int offset = (page - 1) * pageSize;
            
            List<Student> allStudents;
            int totalCount;
            
            // 根据关键词搜索或获取所有学生
            if (keyword != null && !keyword.trim().isEmpty()) {
                allStudents = studentService.findStudentAndCourseInfo().stream()
                        .filter(student -> 
                            (student.getStudent_number() != null && student.getStudent_number().contains(keyword.trim())) ||
                            (student.getStudent_name() != null && student.getStudent_name().contains(keyword.trim()))
                        )
                        .collect(java.util.stream.Collectors.toList());
                totalCount = allStudents.size();
            } else {
                allStudents = studentService.findStudentAndCourseInfo();
                totalCount = allStudents.size();
            }
            
            // 分页处理
            List<Student> students = allStudents.stream()
                    .skip(offset)
                    .limit(pageSize)
                    .collect(java.util.stream.Collectors.toList());
            
            // 计算分页信息
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);
            
            if (students.isEmpty() && totalCount == 0) {
                if (keyword != null && !keyword.trim().isEmpty()) {
                    model.addAttribute("error", "没有找到匹配的学生记录");
                } else {
                    model.addAttribute("error", "系统中还没有学生记录");
                }
            } else if (students.isEmpty() && page > 1) {
                // 如果当前页没有数据但总数据不为0，重定向到第一页
                String redirectUrl = "redirect:/courseSelection/admin/list?page=1";
                if (keyword != null && !keyword.trim().isEmpty()) {
                    redirectUrl += "&keyword=" + keyword;
                }
                return redirectUrl;
            }
            
            model.addAttribute("students", students);
            model.addAttribute("keyword", keyword);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalCount", totalCount);
            model.addAttribute("hasResults", totalCount > 0);
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                model.addAttribute("message", "搜索到 " + totalCount + " 个匹配的学生");
            }
            
            return "courseSelection/list";
            
        } catch (Exception e) {
            model.addAttribute("error", "获取学生信息失败: " + e.getMessage());
            return "courseSelection/list";
        }
    }
    
    /**
     * 查看指定学生的选课详情页面，重定向到筛选页面
     */
    @GetMapping("/students/{studentId}")
    public String viewStudentSelectionCourses(
            @PathVariable("studentId") Integer studentId, 
            @RequestParam(value = "academicYear", required = false) String academicYear,
            @RequestParam(value = "semester", required = false) String semester,
            Model model) {
        
        // 检查权限：管理员可以查看任何学生，普通用户只能查看自己
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            // 普通用户只能访问自己的信息，这里需要根据当前登录用户获取对应的学生ID
            // 暂时允许访问，后续可以添加用户-学生关联逻辑
        }
        
        return filterBySemester(studentId, academicYear, semester, model, null);
    }
    
    /**
     * 根据学号或姓名关键字搜索学生选课信息 (新的GET请求处理方法)
     */
    @GetMapping("/search")
    public String searchStudents(@RequestParam("keyword") String keyword, Model model) {
        System.out.println("开始搜索，关键词: " + keyword);
        
        List<Student> students = null;
        
        try {
            // 获取所有学生信息
            List<Student> allStudents = studentService.getAllStudents();
            
            // 根据关键词过滤学生列表，同时考虑学号和姓名的匹配
            if (keyword != null && !keyword.trim().isEmpty()) {
                students = allStudents.stream()
                        .filter(student -> 
                            (student.getStudent_number() != null && student.getStudent_number().contains(keyword)) ||
                            (student.getStudent_name() != null && student.getStudent_name().contains(keyword))
                        )
                        .collect(java.util.stream.Collectors.toList());
                
                System.out.println("找到" + students.size() + "个匹配的学生");
            } else {
                students = allStudents;
                System.out.println("未提供搜索关键词，返回所有学生");
            }
            
            // 加载每个学生的选课信息
            for (Student student : students) {
                List<CourseSelection> selections = courseSelectionService.getCourseSelectionsByStudentId(student.getStudent_id());
                student.setCourseSelections(selections);
            }
            
        } catch (Exception e) {
            System.err.println("搜索过程中发生错误: " + e.getMessage());
            e.printStackTrace();
            students = java.util.Collections.emptyList();
        }
        
        model.addAttribute("students", students);
        model.addAttribute("keyword", keyword);
        
        return "courseSelection/search_result";
    }

    /**
     * 显示添加选课记录的表单页面，支持预填充学年和学期
     */
    @GetMapping("/students/{studentId}/add")
    public String showAddForm(
            @PathVariable("studentId") Integer studentId,
            @RequestParam(value = "academicYear", required = false) String academicYear,
            @RequestParam(value = "semester", required = false) String semester,
            Model model) {
        
        // 检查权限：管理员可以为任何学生添加选课，普通用户只能为自己添加选课
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            // 普通用户只能为自己添加选课，这里需要根据当前登录用户获取对应的学生ID
            // 暂时允许访问，后续可以添加用户-学生关联逻辑
        }
        
        if (studentId != null) {
            // 如果提供了学生ID，则获取该学生信息
            Student student = studentService.getStudentById(studentId);
            if (student != null) {
                model.addAttribute("student", student);
            }
        } else {
            // 否则获取所有学生
            List<Student> students = studentService.getAllStudents();
            model.addAttribute("students", students);
        }
        
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        
        CourseSelection courseSelection = new CourseSelection();
        // 如果提供了学年和学期，则设置默认值
        if (academicYear != null && !academicYear.isEmpty()) {
            courseSelection.setAcademic_year(academicYear);
        }
        if (semester != null && !semester.isEmpty()) {
            courseSelection.setSemester(semester);
        }
        model.addAttribute("courseSelection", courseSelection);
        
        // 添加用户角色信息到模型
        model.addAttribute("isAdmin", isAdmin);
        
        return "courseSelection/add";
    }
    
    /**
     * 处理添加选课记录的表单提交，并直接返回筛选结果
     */
    @PostMapping("/students/{studentId}/add")
    public String addCourseSelection(
            @PathVariable("studentId") Integer studentId,
            @RequestParam("academic_year") String academicYear,
            @RequestParam("semester") String semester,
            @Valid @ModelAttribute("courseSelection") CourseSelection courseSelection,
            BindingResult bindingResult,
            @RequestParam("course_ids") Integer[] courseIds,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // 获取学生信息（用于通知）
        Student student = studentService.getStudentById(studentId);
        
        // 如果验证有错误，重新显示表单
        if (bindingResult.hasErrors()) {
            List<Student> students = studentService.getAllStudents();
            List<Course> courses = courseService.getAllCourses();
            
            model.addAttribute("students", students);
            model.addAttribute("courses", courses);
            
            // 将验证错误信息添加到模型中
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            String errorMessage = allErrors.stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            model.addAttribute("error", errorMessage);
            
            // 发送选课失败通知
            if (student != null) {
                notificationService.sendCourseSelectionFailedNotification(
                    student, "表单验证失败: " + errorMessage, academicYear, semester);
            }
            
            return "courseSelection/add";
        }
        
        try {
            // 1. 设置选课记录的其他属性
            courseSelection.setStudent_id(studentId);
            courseSelection.setAcademic_year(academicYear);
            courseSelection.setSemester(semester);
            
            // 检查是否已选择了相同的课程
            List<CourseSelection> existingSelections = courseSelectionService.getCourseSelectionsByStudentId(studentId);
            for (CourseSelection existing : existingSelections) {
                if (existing.getAcademic_year().equals(academicYear) && existing.getSemester().equals(semester)) {
                    // 获取该选课记录的所有课程
                    List<CourseSelectionList> existingDetails = courseSelectionListService.getListsBySelectionId(existing.getSelection_id());
                    
                    // 检查是否有重复的课程
                    for (Integer newCourseId : courseIds) {
                        Course newCourse = courseService.getCourseById(newCourseId);
                        for (CourseSelectionList detail : existingDetails) {
                            Course existingCourse = courseService.getCourseById(detail.getCourse_id());
                            if (existingCourse.getCourse_code().equals(newCourse.getCourse_code())) {
                                String errorMessage = "课程代码 " + newCourse.getCourse_code() + " 已存在于该学生的选课记录中，不能重复添加相同课程代码";
                                model.addAttribute("error", errorMessage);
                                
                                // 重新加载课程列表
                                List<Course> courses = courseService.getAllCourses();
                                model.addAttribute("courses", courses);
                                model.addAttribute("student", student);
                                
                                // 发送选课失败通知
                                if (student != null) {
                                    notificationService.sendCourseSelectionFailedNotification(
                                        student, "课程重复: " + errorMessage, academicYear, semester);
                                }
                                
                                return "courseSelection/add";
                            }
                        }
                    }
                }
            }
            
            // 2. 保存选课记录
            boolean result = courseSelectionService.insertCourseSelection(courseSelection);
            if (!result) {
                String errorMessage = "添加选课记录失败";
                model.addAttribute("error", errorMessage);
                
                // 重新加载课程列表
                List<Course> courses = courseService.getAllCourses();
                model.addAttribute("courses", courses);
                model.addAttribute("student", student);
                
                // 发送选课失败通知
                if (student != null) {
                    notificationService.sendCourseSelectionFailedNotification(
                        student, "数据库保存失败", academicYear, semester);
                }
                
                return "courseSelection/add";
            }
            
            // 3. 添加选课详情并收集选课的课程
            List<Course> selectedCourses = new ArrayList<>();
            for (Integer courseId : courseIds) {
                CourseSelectionList detail = new CourseSelectionList();
                detail.setSelection_id(courseSelection.getSelection_id());
                detail.setCourse_id(courseId);
                
                boolean detailResult = courseSelectionListService.insertCourseSelectionList(detail);
                if (!detailResult) {
                    String errorMessage = "添加选课详情失败";
                    model.addAttribute("error", errorMessage);
                    
                    // 重新加载课程列表
                    List<Course> courses = courseService.getAllCourses();
                    model.addAttribute("courses", courses);
                    model.addAttribute("student", student);
                    
                    // 发送选课失败通知
                    if (student != null) {
                        notificationService.sendCourseSelectionFailedNotification(
                            student, "选课详情保存失败", academicYear, semester);
                    }
                    
                    return "courseSelection/add";
                }
                
                // 收集课程信息用于成功通知
                Course course = courseService.getCourseById(courseId);
                if (course != null) {
                    selectedCourses.add(course);
                }
            }
            
            // 4. 选课成功，发送成功通知
            if (student != null && !selectedCourses.isEmpty()) {
                notificationService.sendCourseSelectionSuccessNotification(
                    student, selectedCourses, academicYear, semester);
            }
            
            // 操作成功，直接返回带有筛选条件的结果页面
            return filterBySemester(studentId, academicYear, semester, model, redirectAttributes);
            
        } catch (Exception e) {
            String errorMessage = "添加选课记录时发生错误: " + e.getMessage();
            model.addAttribute("error", errorMessage);
            
            // 重新加载课程列表
            List<Course> courses = courseService.getAllCourses();
            model.addAttribute("courses", courses);
            model.addAttribute("student", student);
            
            // 发送选课失败通知
            if (student != null) {
                notificationService.sendCourseSelectionFailedNotification(
                    student, "系统异常: " + e.getMessage(), academicYear, semester);
            }
            
            return "courseSelection/add";
        }
    }

    /**
     * 显示编辑选课记录的表单页面 - 仅管理员可访问
     */
    @GetMapping("/selections/{selectionId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable("selectionId") Integer selectionId, Model model) {
        CourseSelection courseSelection = courseSelectionService.getCourseSelectionById(selectionId);
        if (courseSelection == null) {
            return "redirect:/courseSelection";
        }
        
        // 获取该选课记录所有的课程详情
        List<CourseSelectionList> selectedCourses = courseSelectionListService.getListsBySelectionId(selectionId);
        
        // 确保每个选课详情都加载了完整的课程信息
        for (CourseSelectionList detail : selectedCourses) {
            Course course = courseService.getCourseById(detail.getCourse_id());
            detail.setCourse(course);
        }
        
        // 获取学生信息
        Student student = studentService.getStudentById(courseSelection.getStudent_id());
        
        model.addAttribute("courseSelection", courseSelection);
        model.addAttribute("selectedCourses", selectedCourses);
        model.addAttribute("student", student);
        
        return "courseSelection/edit";
    }
    
    /**
     * 处理编辑选课记录的表单提交 - 仅管理员可访问
     */
    @PostMapping("/selections/{selectionId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editCourseSelection(
            @PathVariable("selectionId") Integer selectionId,
            @RequestParam("student_id") Integer studentId,
            @RequestParam("academic_year") String academicYear,
            @RequestParam("semester") String semester,
            @Valid @ModelAttribute("courseSelection") CourseSelection courseSelection,
            BindingResult bindingResult,
            @RequestParam Map<String, String> formParams,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // 如果验证有错误，重新显示表单
        if (bindingResult.hasErrors()) {
            // 重新加载选课详情
            List<CourseSelectionList> selectedCourses = courseSelectionListService.getListsBySelectionId(selectionId);
            
            // 确保每个选课详情都加载了完整的课程信息
            for (CourseSelectionList detail : selectedCourses) {
                Course course = courseService.getCourseById(detail.getCourse_id());
                detail.setCourse(course);
            }
            
            // 获取学生信息
            Student student = studentService.getStudentById(studentId);
            
            model.addAttribute("selectedCourses", selectedCourses);
            model.addAttribute("student", student);
            
            // 将验证错误信息添加到模型中
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            for (ObjectError error : allErrors) {
                model.addAttribute("error", error.getDefaultMessage());
            }
            
            return "courseSelection/edit";
        }
        
        try {
            // 1. 更新选课记录的基本信息
            courseSelection.setSelection_id(selectionId);
            courseSelection.setStudent_id(studentId);
            courseSelection.setAcademic_year(academicYear);
            courseSelection.setSemester(semester);
            
            boolean result = courseSelectionService.updateCourseSelection(courseSelection);
            if (!result) {
                model.addAttribute("error", "更新选课记录失败");
                return "redirect:/courseSelection/selections/" + selectionId + "/edit";
            }
            
            // 2. 更新每个课程信息
            List<CourseSelectionList> selectedCourses = courseSelectionListService.getListsBySelectionId(selectionId);
            for (CourseSelectionList detail : selectedCourses) {
                // 获取表单中该课程的参数索引
                String index = getIndexFromFormParams(formParams, detail.getList_id());
                if (index != null) {
                    // 更新课程信息
                    Course course = courseService.getCourseById(detail.getCourse_id());
                    course.setCourse_code(formParams.get("courseDetails[" + index + "].course.course_code"));
                    course.setCourse_name(formParams.get("courseDetails[" + index + "].course.course_name"));
                    course.setCourse_type(formParams.get("courseDetails[" + index + "].course.course_type"));
                    course.setCredit_hours(Integer.parseInt(formParams.get("courseDetails[" + index + "].course.credit_hours")));
                    course.setCredits(Float.parseFloat(formParams.get("courseDetails[" + index + "].course.credits")));
                    
                    boolean courseResult = courseService.updateCourse(course);
                    if (!courseResult) {
                        model.addAttribute("error", "更新课程信息失败");
                        return "redirect:/courseSelection/selections/" + selectionId + "/edit";
                    }
                }
            }
            
            // 使用重定向属性传递成功消息
            redirectAttributes.addFlashAttribute("success", "更新选课记录成功");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "更新选课记录时发生错误: " + e.getMessage());
            return "redirect:/courseSelection/selections/" + selectionId + "/edit";
        }
        
        // 对学期参数进行URL编码
        String encodedSemester = "";
        try {
            encodedSemester = URLEncoder.encode(semester, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            // 如果编码失败，使用原始值
            encodedSemester = semester;
        }
        
        // 操作成功，直接返回学生选课页面，带上当前的筛选参数，但不执行重新筛选
        return "redirect:/courseSelection/students/" + studentId + "/filter?academicYear=" + 
               (academicYear != null ? academicYear : "") + "&semester=" + encodedSemester;
    }
    
    private String getIndexFromFormParams(Map<String, String> formParams, int listId) {
        for (String key : formParams.keySet()) {
            if (key.matches("courseDetails\\[\\d+\\]\\.list_id") && formParams.get(key).equals(String.valueOf(listId))) {
                return key.replaceAll("courseDetails\\[(\\d+)\\]\\.list_id", "$1");
            }
        }
        return null;
    }
    
    /**
     * 删除选课记录
     */
    @DeleteMapping("/selections/{selectionId}")
    public String deleteCourseSelection(
            @PathVariable("selectionId") Integer selectionId,
            @RequestParam("studentId") Integer studentId,
            @RequestParam(value = "academicYear", required = false) String academicYear,
            @RequestParam(value = "semester", required = false) String semester,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            // 获取选课记录的学年和学期信息（用于后续筛选）
            CourseSelection courseSelection = null;
            if (academicYear == null || semester == null) {
                courseSelection = courseSelectionService.getCourseSelectionById(selectionId);
                if (courseSelection != null) {
                    academicYear = courseSelection.getAcademic_year();
                    semester = courseSelection.getSemester();
                }
            }
            
            boolean result = courseSelectionService.deleteCourseSelection(selectionId);
            if (result) {
                redirectAttributes.addFlashAttribute("success", "删除选课记录成功");
            } else {
                redirectAttributes.addFlashAttribute("error", "删除选课记录失败");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除选课记录时发生错误: " + e.getMessage());
        }
        
        // 对学期参数进行URL编码
        String encodedSemester = "";
        if (semester != null) {
            try {
                encodedSemester = URLEncoder.encode(semester, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                // 如果编码失败，使用原始值
                encodedSemester = semester;
            }
        }
        
        // 操作完成后，直接返回学生选课页面，带上当前的筛选参数，但不执行重新筛选
        return "redirect:/courseSelection/students/" + studentId + "/filter?academicYear=" + 
               (academicYear != null ? academicYear : "") + "&semester=" + encodedSemester;
    }

    /**
     * 显示编辑单个课程信息的表单页面 - 仅管理员可访问
     */
    @GetMapping("/courses/{listId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditCourseForm(
            @PathVariable("listId") Integer listId,
            @RequestParam("selectionId") Integer selectionId,
            @RequestParam(value = "academicYear", required = false) String academicYear,
            @RequestParam(value = "semester", required = false) String semester,
            Model model) {
        
        CourseSelectionList courseDetail = courseSelectionListService.getCourseSelectionListById(listId);
        if (courseDetail == null) {
            return "redirect:/courseSelection";
        }
        
        // 确保加载完整的课程信息
        Course course = courseService.getCourseById(courseDetail.getCourse_id());
        courseDetail.setCourse(course);
        
        // 获取选课记录信息
        CourseSelection courseSelection = courseSelectionService.getCourseSelectionById(selectionId);
        
        // 如果没有提供学年和学期参数，从选课记录中获取
        if ((academicYear == null || academicYear.isEmpty() || semester == null || semester.isEmpty()) 
                && courseSelection != null) {
            academicYear = courseSelection.getAcademic_year();
            semester = courseSelection.getSemester();
        }
        
        model.addAttribute("courseDetail", courseDetail);
        model.addAttribute("courseSelection", courseSelection);
        model.addAttribute("academicYear", academicYear);
        model.addAttribute("semester", semester);
        
        return "courseSelection/editCourse";
    }
    
    /**
     * 处理编辑单个课程信息的表单提交，更新课程基本信息 - 仅管理员可访问
     */
    @PostMapping("/courses/{listId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editCourse(
            @PathVariable("listId") Integer listId,
            @RequestParam("course_id") Integer courseId,
            @RequestParam("selectionId") Integer selectionId,
            @RequestParam("course_code") String courseCode,
            @RequestParam("course_name") String courseName,
            @RequestParam("course_type") String courseType,
            @RequestParam("credit_hours") Integer creditHours,
            @RequestParam("credits") Float credits,
            @RequestParam(value = "academicYear", required = false) String academicYear,
            @RequestParam(value = "semester", required = false) String semester,
            RedirectAttributes redirectAttributes) {
        
        try {
            System.out.println("收到编辑课程请求：listId=" + listId + ", 课程ID=" + courseId + 
                              ", 学年=" + academicYear + ", 学期=" + semester);
            System.out.println("新的课程信息: 代码=" + courseCode + ", 名称=" + courseName + 
                              ", 类型=" + courseType + ", 学时=" + creditHours + ", 学分=" + credits);
            
            // 获取课程信息
            Course course = courseService.getCourseById(courseId);
            if (course == null) {
                System.err.println("未找到课程信息，课程ID: " + courseId);
                redirectAttributes.addFlashAttribute("error", "未找到课程信息");
                return "redirect:/courseSelection/students/" +
                       courseSelectionService.getCourseSelectionById(selectionId).getStudent_id();
            }
            
            // 获取学生信息（用于通知）
            CourseSelection courseSelection = courseSelectionService.getCourseSelectionById(selectionId);
            Student student = null;
            if (courseSelection != null) {
                student = studentService.getStudentById(courseSelection.getStudent_id());
            }
            
            // 记录原课程信息，便于日志追踪和变更通知
            String originalInfo = String.format("代码=%s, 名称=%s, 类型=%s, 学时=%d, 学分=%.1f", 
                course.getCourse_code(), course.getCourse_name(), course.getCourse_type(), 
                course.getCredit_hours(), course.getCredits());
            System.out.println("修改前的课程信息: " + originalInfo);
            
            // 更新课程信息
            course.setCourse_code(courseCode);
            course.setCourse_name(courseName);
            course.setCourse_type(courseType);
            course.setCredit_hours(creditHours);
            course.setCredits(credits);
            
            String newInfo = String.format("代码=%s, 名称=%s, 类型=%s, 学时=%d, 学分=%.1f", 
                course.getCourse_code(), course.getCourse_name(), course.getCourse_type(), 
                course.getCredit_hours(), course.getCredits());
            System.out.println("修改后的课程信息: " + newInfo);
            
            boolean result = courseService.updateCourse(course);
            if (!result) {
                System.err.println("更新课程信息失败");
                redirectAttributes.addFlashAttribute("error", "更新课程信息失败");
            } else {
                System.out.println("更新课程信息成功");
                redirectAttributes.addFlashAttribute("success", "更新课程信息成功");
                
                // 发送课程变更通知
                if (student != null && courseSelection != null) {
                    String changeDetails = String.format("课程信息已更新\n原信息: %s\n新信息: %s", originalInfo, newInfo);
                    notificationService.sendCourseChangeNotification(
                        student, course, changeDetails, 
                        courseSelection.getAcademic_year(), courseSelection.getSemester());
                }
            }
            
        } catch (Exception e) {
            System.err.println("更新课程信息时发生错误: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "更新课程信息时发生错误: " + e.getMessage());
        }
        
        // 获取学生ID
        int studentId = courseSelectionService.getCourseSelectionById(selectionId).getStudent_id();
        System.out.println("学生ID: " + studentId);
        
        // 如果没有提供学年和学期参数，从选课记录中获取
        if ((academicYear == null || academicYear.isEmpty() || semester == null || semester.isEmpty()) && selectionId != null) {
            CourseSelection courseSelection = courseSelectionService.getCourseSelectionById(selectionId);
            if (courseSelection != null) {
                academicYear = courseSelection.getAcademic_year();
                semester = courseSelection.getSemester();
                System.out.println("从选课记录获取到学年=" + academicYear + ", 学期=" + semester);
            }
        }
        
        // 对学期参数进行URL编码
        String encodedSemester = "";
        if (semester != null) {
            try {
                encodedSemester = URLEncoder.encode(semester, StandardCharsets.UTF_8.toString());
                System.out.println("编码后的学期参数: " + encodedSemester);
            } catch (UnsupportedEncodingException e) {
                // 如果编码失败，使用原始值
                encodedSemester = semester;
                System.err.println("URL编码失败: " + e.getMessage());
            }
        }
        
        // 操作完成后，直接返回学生选课页面，带上当前的筛选参数，但不执行重新筛选
        String redirectUrl = "redirect:/courseSelection/students/" + studentId + "/filter?academicYear=" + 
                           (academicYear != null ? academicYear : "") + "&semester=" + encodedSemester;
        System.out.println("重定向URL: " + redirectUrl);
        return redirectUrl;
    }
    
    /**
     * 删除选课记录中的单个课程 - 仅管理员可访问
     */
    @PostMapping("/courses/{listId}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCourse(
            @PathVariable("listId") Integer listId,
            @RequestParam("selectionId") Integer selectionId,
            @RequestParam("studentId") Integer studentId,
            @RequestParam(value = "academicYear", required = false) String academicYear,
            @RequestParam(value = "semester", required = false) String semester,
            RedirectAttributes redirectAttributes) {
        
        try {
            System.out.println("收到删除课程请求：listId=" + listId + ", selectionId=" + selectionId + 
                              ", studentId=" + studentId + ", 学年=" + academicYear + ", 学期=" + semester);
            
            // 首先确认课程详情是否存在
            CourseSelectionList courseDetail = courseSelectionListService.getCourseSelectionListById(listId);
            if (courseDetail == null) {
                System.err.println("未找到要删除的课程，课程ID: " + listId);
                redirectAttributes.addFlashAttribute("error", "未找到要删除的课程，课程ID: " + listId);
                return "redirect:/courseSelection/students/" + studentId + "/filter?academicYear=" + 
                      (academicYear != null ? academicYear : "") + "&semester=" + 
                      (semester != null ? semester : "");
            }

            // 获取课程信息和学生信息（用于通知）
            Course course = courseService.getCourseById(courseDetail.getCourse_id());
            Student student = studentService.getStudentById(studentId);
            
            // 获取选课记录的学年和学期信息（用于后续筛选和通知）
            CourseSelection courseSelection = null;
            if ((academicYear == null || academicYear.isEmpty() || semester == null || semester.isEmpty()) && selectionId != null) {
                System.out.println("从选课记录获取学年学期信息");
                courseSelection = courseSelectionService.getCourseSelectionById(selectionId);
                if (courseSelection != null) {
                    academicYear = courseSelection.getAcademic_year();
                    semester = courseSelection.getSemester();
                    System.out.println("从选课记录获取到学年=" + academicYear + ", 学期=" + semester);
                }
            } else if (selectionId != null) {
                courseSelection = courseSelectionService.getCourseSelectionById(selectionId);
            }
            
            // 执行删除操作
            System.out.println("正在删除课程明细，ID: " + listId + ", 选课ID: " + selectionId);
            boolean result = courseSelectionListService.deleteCourseSelectionList(listId);
            if (result) {
                System.out.println("删除课程明细成功");
                redirectAttributes.addFlashAttribute("success", "删除课程成功");
                
                // 发送课程删除通知
                if (student != null && course != null) {
                    notificationService.sendCourseDeletionNotification(
                        student, course, academicYear, semester);
                }
            } else {
                System.out.println("删除课程明细失败");
                redirectAttributes.addFlashAttribute("error", "删除课程失败，请检查数据完整性");
            }
        } catch (Exception e) {
            System.err.println("删除课程明细异常: " + e.getMessage());
            e.printStackTrace(); // 打印堆栈信息到控制台
            redirectAttributes.addFlashAttribute("error", "删除课程时发生错误: " + e.getMessage());
        }
        
        // 对学期参数进行URL编码
        String encodedSemester = "";
        if (semester != null) {
            try {
                encodedSemester = URLEncoder.encode(semester, StandardCharsets.UTF_8.toString());
                System.out.println("编码后的学期参数: " + encodedSemester);
            } catch (UnsupportedEncodingException e) {
                // 如果编码失败，使用原始值
                encodedSemester = semester;
                System.err.println("URL编码失败: " + e.getMessage());
            }
        }
        
        // 操作完成后，直接返回学生选课页面，带上当前的筛选参数，但不执行重新筛选
        String redirectUrl = "redirect:/courseSelection/students/" + studentId + "/filter?academicYear=" + 
                           (academicYear != null ? academicYear : "") + "&semester=" + encodedSemester;
        System.out.println("重定向URL: " + redirectUrl);
        return redirectUrl;
    }

    /**
     * 根据学年和学期筛选学生的选课记录，并展示结果
     */
    @GetMapping("/students/{studentId}/filter")
    public String filterBySemester(
            @PathVariable("studentId") Integer studentId,
            @RequestParam(value = "academicYear", required = false) String academicYear,
            @RequestParam(value = "semester", required = false) String semester,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // 检查权限：管理员可以查看任何学生，普通用户只能查看自己
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            // 普通用户只能访问自己的信息
            // 这里需要根据当前登录用户获取对应的学生ID进行验证
            // 暂时允许访问，后续可以添加用户-学生关联逻辑
        }
        
        // 记录请求参数，用于调试
        System.out.println("筛选参数 - 学年: " + academicYear + ", 学期: " + semester);
        
        // 获取学生信息
        Student student = studentService.getStudentById(studentId);
        if (student == null) {
            redirectAttributes.addFlashAttribute("error", "未找到学生信息");
            return "redirect:/courseSelection";
        }
        
        // 获取该学生的所有选课记录
        List<CourseSelection> allSelections = courseSelectionService.getCourseSelectionsByStudentId(studentId);
        
        // 按学年和学期分组
        Map<String, List<CourseSelection>> groupedSelections = new java.util.HashMap<>();
        List<CourseSelection> filteredSelections = new java.util.ArrayList<>();
        
        // 获取所有可用的学年和学期选项
        Set<String> academicYears = new HashSet<>();
        Set<String> semesters = new HashSet<>();
        
        // 首先收集所有学年和学期
        for (CourseSelection selection : allSelections) {
            academicYears.add(selection.getAcademic_year());
            semesters.add(selection.getSemester());
        }
        
        // 仅当用户没有进行任何筛选(URL中没有参数)且学生有选课记录时，使用默认值
        boolean useDefault = (academicYear == null && semester == null) && !allSelections.isEmpty();
        if (useDefault) {
            academicYear = allSelections.get(0).getAcademic_year();
            semester = allSelections.get(0).getSemester();
            System.out.println("使用默认筛选参数 - 学年: " + academicYear + ", 学期: " + semester);
        }
        
        // 根据学年和学期筛选选课记录
        for (CourseSelection selection : allSelections) {
            boolean match = true;
            
            // 如果提供了学年条件且不为空，则进行筛选
            if (academicYear != null && !academicYear.isEmpty()) {
                match = match && academicYear.equals(selection.getAcademic_year());
            }
            
            // 如果提供了学期条件且不为空，则进行筛选
            if (semester != null && !semester.isEmpty()) {
                match = match && semester.equals(selection.getSemester());
            }
            
            // 如果匹配条件，则添加到结果列表
            if (match) {
                filteredSelections.add(selection);
                
                // 构建分组
                String key = selection.getAcademic_year();
                if (!groupedSelections.containsKey(key)) {
                    groupedSelections.put(key, new java.util.ArrayList<>());
                }
                groupedSelections.get(key).add(selection);
            }
        }
        
        // 加载每个选课记录的详细信息
        for (CourseSelection selection : filteredSelections) {
            // 加载选课详情
            List<CourseSelectionList> details = courseSelectionListService.getListsBySelectionId(selection.getSelection_id());
            selection.setCourseSelectionLists(details);
            
            // 确保每个选课详情都加载了完整的课程信息
            for (CourseSelectionList detail : details) {
                Course course = courseService.getCourseById(detail.getCourse_id());
                detail.setCourse(course);
            }
        }
        
        // 填充模型
        model.addAttribute("student", student);
        model.addAttribute("courseSelections", filteredSelections);
        model.addAttribute("groupedSelections", groupedSelections);
        model.addAttribute("academicYears", academicYears);
        model.addAttribute("semesters", semesters);
        model.addAttribute("selectedYear", academicYear);
        model.addAttribute("selectedSemester", semester);
        
        // 添加用户角色信息到模型
        model.addAttribute("isAdmin", isAdmin);
        
        return "courseSelection/studentSelectionCourse";
    }

    /**
     * 添加学年学期 - 仅管理员可访问
     */
    @PostMapping("/students/{studentId}/addSemester")
    @PreAuthorize("hasRole('ADMIN')")
    public String addSemester(
            @PathVariable("studentId") Integer studentId,
            @RequestParam("academicYear") String academicYear,
            @RequestParam("semester") String semester,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("=== 开始处理添加学年学期请求 ===");
        System.out.println("学生ID: " + studentId);
        System.out.println("学年: " + academicYear);
        System.out.println("学期: " + semester);
        
        try {
            // 后端验证学年格式
            if (!isValidAcademicYear(academicYear)) {
                System.out.println("学年格式验证失败: " + academicYear);
                redirectAttributes.addFlashAttribute("error", "学年格式不正确，请输入如\"2025-2026\"的格式");
                return "redirect:/courseSelection/students/" + studentId + "/filter";
            }
            
            // 后端验证学期格式
            if (!isValidSemester(semester)) {
                System.out.println("学期格式验证失败: " + semester);
                redirectAttributes.addFlashAttribute("error", "学期只能是\"第一学期\"或\"第二学期\"");
                return "redirect:/courseSelection/students/" + studentId + "/filter";
            }
            
            System.out.println("验证通过，开始检查重复记录...");
            
            // 检查该学生是否已存在相同的学年学期组合
            List<CourseSelection> existingSelections = courseSelectionService.getCourseSelectionsByStudentId(studentId);
            for (CourseSelection existing : existingSelections) {
                if (existing.getAcademic_year().equals(academicYear) && existing.getSemester().equals(semester)) {
                    System.out.println("发现重复的学年学期记录");
                    redirectAttributes.addFlashAttribute("error", "该学年学期已存在，请选择其他学年学期");
                    return "redirect:/courseSelection/students/" + studentId + "/filter";
                }
            }
            
            // 获取目标学生信息
            Student targetStudent = studentService.getStudentById(studentId);
            if (targetStudent == null) {
                System.out.println("未找到学生，ID: " + studentId);
                redirectAttributes.addFlashAttribute("error", "未找到指定学生");
                return "redirect:/courseSelection/students/" + studentId + "/filter";
            }
            
            System.out.println("找到目标学生: " + targetStudent.getStudent_name());
            
            // 获取当前管理员信息
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String adminName = auth.getName(); // 获取管理员用户名
            System.out.println("当前管理员: " + adminName);
            
            // 创建新的选课记录
            CourseSelection courseSelection = new CourseSelection();
            courseSelection.setStudent_id(studentId);
            courseSelection.setAcademic_year(academicYear);
            courseSelection.setSemester(semester);
            
            System.out.println("开始保存选课记录...");
            boolean result = courseSelectionService.insertCourseSelection(courseSelection);
            if (result) {
                System.out.println("选课记录保存成功，开始发送通知...");
                redirectAttributes.addFlashAttribute("success", "学年学期添加成功");
                
                // 发送单播通知给指定学生和管理员
                try {
                    notificationService.sendNewSemesterNotificationToUser(targetStudent, academicYear, semester, adminName);
                    System.out.println("✅ 新学期开放选课单播通知已发送成功!");
                    System.out.println("  - 学生: " + targetStudent.getStudent_name() + " (" + targetStudent.getStudent_number() + ")");
                    System.out.println("  - 学年: " + academicYear);
                    System.out.println("  - 学期: " + semester);
                    System.out.println("  - 管理员: " + adminName);
                } catch (Exception e) {
                    System.err.println("❌ 发送新学期单播通知失败: " + e.getMessage());
                    e.printStackTrace();
                    // 不影响主要业务流程，仅记录错误
                }
            } else {
                System.out.println("选课记录保存失败");
                redirectAttributes.addFlashAttribute("error", "学年学期添加失败");
            }
            
        } catch (Exception e) {
            System.err.println("添加学年学期异常: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "添加学年学期时发生错误: " + e.getMessage());
        }
        
        System.out.println("=== 添加学年学期请求处理完成 ===");
        
        // 重定向到学生选课页面，显示新添加的学年学期
        return "redirect:/courseSelection/students/" + studentId + "/filter?academicYear=" + 
               academicYear + "&semester=" + java.net.URLEncoder.encode(semester, java.nio.charset.StandardCharsets.UTF_8);
    }
    
    /**
     * 验证学年格式
     */
    private boolean isValidAcademicYear(String academicYear) {
        if (academicYear == null || academicYear.trim().isEmpty()) {
            return false;
        }
        
        // 学年格式必须是YYYY-YYYY，且后一年比前一年大1
        String pattern = "^(\\d{4})-(\\d{4})$";
        if (!academicYear.matches(pattern)) {
            return false;
        }
        
        String[] years = academicYear.split("-");
        try {
            int startYear = Integer.parseInt(years[0]);
            int endYear = Integer.parseInt(years[1]);
            
            // 检查是否是连续的年份
            if (endYear != startYear + 1) {
                return false;
            }
            
            // 检查年份是否合理（比如在1900-2100之间）
            if (startYear < 1900 || startYear > 2100) {
                return false;
            }
            
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 验证学期格式
     */
    private boolean isValidSemester(String semester) {
        return "第一学期".equals(semester) || "第二学期".equals(semester);
    }

    /**
     * 下载学生选课情况Excel文件 - 仅管理员可访问
     */
    @GetMapping("/students/{studentId}/download")
    public void downloadStudentCourses(
            @PathVariable("studentId") Integer studentId,
            @RequestParam(value = "academicYear", required = false) String academicYear,
            @RequestParam(value = "semester", required = false) String semester,
            HttpServletResponse response) throws IOException {
        
        try {
            // 获取学生信息
            Student student = studentService.getStudentById(studentId);
            if (student == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "学生信息不存在");
                return;
            }
            
            // 获取该学生的所有选课记录
            List<CourseSelection> allSelections = courseSelectionService.getCourseSelectionsByStudentId(studentId);
            
            // 按学年和学期筛选选课记录
            List<CourseSelection> filteredSelections = new java.util.ArrayList<>();
            for (CourseSelection selection : allSelections) {
                boolean match = true;
                
                // 如果提供了学年条件且不为空，则进行筛选
                if (academicYear != null && !academicYear.isEmpty()) {
                    match = match && academicYear.equals(selection.getAcademic_year());
                }
                
                // 如果提供了学期条件且不为空，则进行筛选
                if (semester != null && !semester.isEmpty()) {
                    match = match && semester.equals(selection.getSemester());
                }
                
                if (match) {
                    filteredSelections.add(selection);
                }
            }
            
            // 加载每个选课记录的详细信息
            for (CourseSelection selection : filteredSelections) {
                // 加载选课详情
                List<CourseSelectionList> details = courseSelectionListService.getListsBySelectionId(selection.getSelection_id());
                selection.setCourseSelectionLists(details);
                
                // 确保每个选课详情都加载了完整的课程信息
                for (CourseSelectionList detail : details) {
                    Course course = courseService.getCourseById(detail.getCourse_id());
                    detail.setCourse(course);
                }
            }
            
            // 生成Excel文件
            generateStudentCoursesExcel(student, filteredSelections, academicYear, semester, response);
            
        } catch (Exception e) {
            System.err.println("下载学生选课情况Excel异常: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "生成Excel文件失败");
        }
    }
    
    /**
     * 生成学生选课情况Excel文件
     */
    private void generateStudentCoursesExcel(Student student, List<CourseSelection> courseSelections, 
                                           String academicYear, String semester, HttpServletResponse response) 
                                           throws IOException {
        
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        
        // 创建工作表
        Sheet sheet = workbook.createSheet("学生选课情况");
        
        // 设置列宽
        sheet.setColumnWidth(0, 3000);   // 学年
        sheet.setColumnWidth(1, 3000);   // 学期
        sheet.setColumnWidth(2, 4000);   // 课程代码
        sheet.setColumnWidth(3, 6000);   // 课程名称
        sheet.setColumnWidth(4, 3000);   // 课程类型
        sheet.setColumnWidth(5, 3000);   // 学时
        sheet.setColumnWidth(6, 3000);   // 学分
        
        // 创建样式
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
        
        CellStyle summaryStyle = workbook.createCellStyle();
        summaryStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font summaryFont = workbook.createFont();
        summaryFont.setBold(true);
        summaryStyle.setFont(summaryFont);
        summaryStyle.setAlignment(HorizontalAlignment.CENTER);
        summaryStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(summaryStyle);
        
        int rowNum = 0;
        
        // 添加标题信息
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("学生选课情况表");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        
        // 添加学生信息
        rowNum++; // 空行
        Row studentInfoRow1 = sheet.createRow(rowNum++);
        studentInfoRow1.createCell(0).setCellValue("学生姓名：" + student.getStudent_name());
        studentInfoRow1.createCell(2).setCellValue("学号：" + student.getStudent_number());
                 studentInfoRow1.createCell(4).setCellValue("学院：" + student.getCollege());
        
        // 添加筛选条件
        Row filterRow = sheet.createRow(rowNum++);
        String filterInfo = "筛选条件：";
        if (academicYear != null && !academicYear.isEmpty()) {
            filterInfo += "学年=" + academicYear + " ";
        }
        if (semester != null && !semester.isEmpty()) {
            filterInfo += "学期=" + semester;
        }
        if ((academicYear == null || academicYear.isEmpty()) && (semester == null || semester.isEmpty())) {
            filterInfo += "全部学年学期";
        }
        filterRow.createCell(0).setCellValue(filterInfo);
        
        rowNum++; // 空行
        
        // 创建表头
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"学年", "学期", "课程代码", "课程名称", "课程类型", "学时", "学分"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 统计变量
        int totalCourses = 0;
        int requiredCourses = 0;
        int electiveCourses = 0;
        double totalCredits = 0.0;
        int totalHours = 0;
        
        // 添加数据行
        for (CourseSelection selection : courseSelections) {
            if (selection.getCourseSelectionLists() != null) {
                for (CourseSelectionList detail : selection.getCourseSelectionLists()) {
                    Row dataRow = sheet.createRow(rowNum++);
                    
                    dataRow.createCell(0).setCellValue(selection.getAcademic_year());
                    dataRow.createCell(1).setCellValue(selection.getSemester());
                    dataRow.createCell(2).setCellValue(detail.getCourse().getCourse_code());
                    dataRow.createCell(3).setCellValue(detail.getCourse().getCourse_name());
                    dataRow.createCell(4).setCellValue(detail.getCourse().getCourse_type());
                    dataRow.createCell(5).setCellValue(detail.getCourse().getCredit_hours());
                    dataRow.createCell(6).setCellValue(detail.getCourse().getCredits());
                    
                    // 设置样式
                    for (int i = 0; i < 7; i++) {
                        dataRow.getCell(i).setCellStyle(dataStyle);
                    }
                    
                    // 统计
                    totalCourses++;
                    totalCredits += detail.getCourse().getCredits();
                    totalHours += detail.getCourse().getCredit_hours();
                    if ("必修".equals(detail.getCourse().getCourse_type())) {
                        requiredCourses++;
                    } else if ("选修".equals(detail.getCourse().getCourse_type())) {
                        electiveCourses++;
                    }
                }
            }
        }
        
        // 添加汇总信息
        rowNum++; // 空行
        Row summaryHeaderRow = sheet.createRow(rowNum++);
        Cell summaryHeaderCell = summaryHeaderRow.createCell(0);
        summaryHeaderCell.setCellValue("选课情况汇总");
        summaryHeaderCell.setCellStyle(summaryStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 6));
        
        Row summary1 = sheet.createRow(rowNum++);
        summary1.createCell(0).setCellValue("总课程数：" + totalCourses + "门");
        summary1.createCell(2).setCellValue("必修课：" + requiredCourses + "门");
        summary1.createCell(4).setCellValue("选修课：" + electiveCourses + "门");
        
        Row summary2 = sheet.createRow(rowNum++);
        summary2.createCell(0).setCellValue("总学分：" + totalCredits + "分");
        summary2.createCell(2).setCellValue("总学时：" + totalHours + "学时");
        
        // 生成文件名
        String fileName = student.getStudent_name() + "_选课情况";
        if (academicYear != null && !academicYear.isEmpty()) {
            fileName += "_" + academicYear;
        }
        if (semester != null && !semester.isEmpty()) {
            fileName += "_" + semester;
        }
        fileName += "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
        
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + 
                          URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()) + "\"");
        
        // 写入响应
        workbook.write(response.getOutputStream());
        workbook.close();
    }
    
    /**
     * 设置单元格边框
     */
    private void setBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }
} 
