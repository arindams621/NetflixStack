package com.pwc.springboot.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.pwc.springboot.dao.EmployeeDAO;
import com.pwc.springboot.model.Employee;

@RestController
public class MainController {


    @Autowired
    private EmployeeDAO employeeDAO;
 
    @RequestMapping("/")
    @ResponseBody
    public String welcome() {
    	System.out.println("In Controller ");
	        return "Welcome to PwC Employee Management System.";
    }

    
   
    // http://localhost:8080/SomeContextPath/employees
        
	@RequestMapping(value = "/allEmployees", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)    		  
    @ResponseBody
    public List<Employee> getEmployees() {
			
        List<Employee> list = employeeDAO.getAllEmployees();
        System.out.println("(Service Side) Searching all employee records."); 
        return list;
    }
 
    
    // http://localhost:8080/SomeContextPath/employee/{empNo}
	
    @RequestMapping(value = "/searchEmployee/{empNo}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Employee getEmployee(@PathVariable("empNo") String empNo) {
    	System.out.println("(Service Side) Searching one employee record :" +empNo); 
        return employeeDAO.getEmployee(empNo);
    }
 
  
    // http://localhost:8080/SomeContextPath/employee
     @RequestMapping(value = "/addEmployee",  method = RequestMethod.POST,  produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Employee addEmployee(@RequestBody Employee emp) { 
        System.out.println("(Service Side) Creating employee: " + emp.getEmpNo()); 
        return employeeDAO.addEmployee(emp);
    }
 
    
    // http://localhost:8080/SomeContextPath/employee
    @RequestMapping(value = "/updateEmployee",  method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Employee updateEmployee(@RequestBody Employee emp) {    	
    	System.out.println("(Service Side) Editing employee: " + emp.getEmpNo()); 
        return employeeDAO.updateEmployee(emp);
    }
 
  
    // http://localhost:8080/SomeContextPath/employee/{empNo}
    @RequestMapping(value = "/deleteEmployee/{empNo}", method = RequestMethod.DELETE,  produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void deleteEmployee(@PathVariable("empNo") String empNo) {
    	
    	System.out.println("(Service Side) Deleting employee: " + empNo); 
        employeeDAO.deleteEmployee(empNo);
    }
    
//===========================for fall back method==========================================================  
    @HystrixCommand(fallbackMethod="employeeFallback", commandKey="employeeCount",groupKey="employeeCount",
    commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "30000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "4")})         
    @RequestMapping(value = "/employeeCount", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String employeeCount() { 
    	
		if(RandomUtils.nextBoolean()) {
		throw new RuntimeException("Failed......");
	}
    	int count = employeeDAO.getAllEmployees().size();
     	System.out.println("(Service Side) total number of employee : " + count); 
     	String msg= "Total number of employee : " + count; 
     	return  msg;
    }
    
    
    @HystrixCommand(fallbackMethod="employeeFallback", commandKey="employeeOrganizationName",groupKey="employeeOrganizationName",
    commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "30000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "4")})         
    @RequestMapping(value = "/getOrganizationName", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String employeeOrganizationName() { 
    	
		if(RandomUtils.nextBoolean()) {
		throw new RuntimeException("Failed......");
	}
    	return "PwC India Pvt. Ltd.";
    }
    
	public String employeeFallback() {		
		//return "Fall back method initiated.";
		return "Fall back method initiated.";
		}
	
}
